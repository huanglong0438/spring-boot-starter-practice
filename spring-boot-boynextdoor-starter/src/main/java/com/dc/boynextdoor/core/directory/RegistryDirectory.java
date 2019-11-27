package com.dc.boynextdoor.core.directory;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.registry.NotifyListener;
import com.dc.boynextdoor.registry.Registry;
import com.dc.boynextdoor.remoting.EndPoint;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * RegistryDirectory，就是一个zk directory，核心方法就一个list，通过zk ls命令列出所有provider，然后通过某种负载均衡策略列出来
 * <p>参考 https://www.jianshu.com/p/d6de2d21d744
 *
 * @title RegistryDirectory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public class RegistryDirectory<T> extends AbstractDirectory implements NotifyListener {

    private static final Logger LOG = LoggerFactory.getLogger(RegistryDirectory.class);

    private static final ConcurrentMap<String, RegistryDirectory> REGISTRY_DIRECTORYS = new ConcurrentHashMap<>();

    /**
     * 这个service的serviceKey. eg: normal:com.dc.XXXService
     */
    private final String serviceKey;

    /**
     * 这个service的接口类. eg: XXXService.class
     */
    private final Class<T> serviceType;

    /**
     * zookeeperRegistry
     */
    private volatile Registry registry;

    private final List<Requestor<T>> requestors = new CopyOnWriteArrayList<>();

    private volatile boolean initialized = false;

    /**
     * 构造方法
     *
     * @param serviceType  Reference的interface，如XXXService
     * @param directoryUri zookeeper://22.222.222.22:3333/registry/xxxservice
     * @param serviceUri   van://333.333.333.333:4444/...
     */
    public RegistryDirectory(Class<T> serviceType, URI directoryUri, URI serviceUri) {
        super(directoryUri, serviceUri);
        Assert.notNull(serviceType, "service type is null.");
        Assert.hasText(serviceUri.getServiceKey(), "serviceKey is null.");
        this.serviceType = serviceType;
        this.serviceKey = serviceUri.getServiceKey();

        // 将本zk路径和serviceKey关联登记
        RegistryDirectory old = REGISTRY_DIRECTORYS.putIfAbsent(serviceKey, this);
        if (old != null) {
            throw new IllegalStateException("duplicated service key found, service=" + serviceKey
                    + ", the other one is [" + old + "]");
        }
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    protected List<Requestor<T>> doList(Request request) {
        initMetadata();
        return requestors;
    }

    private void initMetadata() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) { // double check lock
                    List<URI> requestorUris = null;
                    URI uri = getUri();
                    if (registry.isAvailable()) { // zk server ok
                        try {
                            requestorUris = registry.lookup(uri); // 从登记处查询，uri -> zk path -> provider van uris
                        } catch (Exception e) {
                            LOG.warn("registry lookup  uri null, this:{},uri:{}", this.toString(), uri);
                        }
                        if (CollectionUtils.isEmpty(requestorUris)) {
                            // zk server ok, but have no list
                            // maybe provider restart and failed to write on zk path. try local cache
                            LOG.warn("registry lookup empty uri ,Requestor use local cache ,this:{},uri:{}",
                                    this.toString(), uri);
                            requestorUris = registry.localLookup(uri);
                            LOG.warn("service [{}] using local requestor list : {}", uri, requestorUris);
                        }
                    } else { // zk server not available, try local cache
                        LOG.warn("registry lookup empty uri ,Requestor use local cache ,this:{},uri:{}",
                                this.toString(), uri);
                        requestorUris = registry.localLookup(uri);
                        LOG.warn("service [{}] using local requestor list : {}", uri, requestorUris);
                    }
                    // 查漏补缺
                    refreshRequestor(requestorUris);
                    initialized = true;
                }
            }
        }
    }

    /**
     * refresh，对requestors查漏补缺（即干掉失效的provider，补上新增的provider）
     *
     * @param requestorUris provider的uris
     */
    private synchronized void refreshRequestor(List<URI> requestorUris) {
        if (CollectionUtils.isEmpty(requestorUris) && CollectionUtils.isNotEmpty(requestors)) {
            // 从zk上没查到uri，用最近的server list
            for (Requestor<T> requestor : requestors) {
                requestorUris.add(requestor.getUri());
            }
            return;
        }
        // uri -> requestor，【核心】，van://ip:port --> requestor对象
        Map<String, Requestor<T>> newUriRequestorMap = toRequestors(requestorUris);
        Map<String, Requestor<T>> oldUriRequestorMap = asMap(requestors);
        // 根据requestorUris更新本地缓存
        registry.updateLocal(getUri().getServiceKey(), requestorUris);
        // 干掉失效的provider
        try {
            destroyUnusedRequestors(oldUriRequestorMap, newUriRequestorMap);
        } catch (Exception e) {
            LOG.warn("destroyUnusedRequestors error. this:{}", this.toString(), e);
        }
        // 补充新增的provider
        for (Requestor<T> requestor : newUriRequestorMap.values()) {
            if (!requestors.contains(requestor)) {
                requestors.add(requestor);
            }
        }
        Collections.shuffle(requestors);
    }

    private synchronized void refreshRequestor() {
        if (!initialized) {
            return;
        }
        List<URI> requestorUris = registry.lookup(getUri());
        refreshRequestor(requestorUris);
    }

    private void destroyUnusedRequestors(Map<String, Requestor<T>> oldMap, Map<String, Requestor<T>> newMap) {
        if (newMap == null || oldMap == null) {
            LOG.warn("oldMap is null or newMap is null");
            return;
        }

        // check deleted requestor
        List<String> deleted = new ArrayList<String>();
        Collection<Requestor<T>> newRequestors = newMap.values();
        for (Map.Entry<String, Requestor<T>> entry : oldMap.entrySet()) {
            if (!newRequestors.contains(entry.getValue())) {
                deleted.add(entry.getKey());
            }
        }
        for (String uri : deleted) {
            if (uri != null) {
                Requestor<T> requestor = oldMap.remove(uri);
                if (requestor != null) {
                    destroyRequestor(requestor);
                }
            }
        }
    }

    private void destroyRequestor(Requestor<T> requestor) {
        try {
            // 先从requestors中移除，再真的destroy
            boolean success = requestors.remove(requestor);
            if (!success) {
                LOG.error("error while remove requestor, this={}, uri={}", this, requestor.getUri());
            }
            requestor.destroy();
            LOG.info("Directory[{}] destroy requestor[{}] with uri={}", this, requestor, requestor.getUri());
        } catch (Throwable t) {
            LOG.warn("Directory[{}] fail to destroy requestor[{}] with uri={}",
                    this, requestor, requestor.getUri(), t);
        }
    }

    private Map<String, Requestor<T>> toRequestors(List<URI> requestorUris) {
        Map<String, Requestor<T>> newUriRequestorMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(requestorUris)) {
            return newUriRequestorMap;
        }
        Map<String, Requestor<T>> map = asMap(requestors);
        for (URI providerUri : requestorUris) {
            String key = providerUri.toFullString();
            Requestor<T> requestor = map.get(key);
            if (requestor == null) {
                EndPoint endPoint = TypeLocator.getInstance().getInstanceOfType(EndPoint.class, "Van");
                // endPoint就是一个nettyServer，
                requestor = endPoint.reference(serviceType, providerUri);
            }
            newUriRequestorMap.put(key, requestor);
        }
        return newUriRequestorMap;
    }


    private Map<String, Requestor<T>> asMap(List<Requestor<T>> requestors) {
        Map<String, Requestor<T>> map = Maps.newHashMap();
        for (Requestor<T> requestor : requestors) {
            map.put(requestor.getUri().toFullString(), requestor);
        }
        return map;
    }

    @Override
    public Class getInterface() {
        return serviceType;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    /**
     * 对于zk的所有刷新变更事件的处理，都是刷新requestor列表
     */
    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        refreshRequestor();
    }

    @Override
    public void handleDataChange(String s, Object o) throws Exception {
        refreshRequestor();
    }

    @Override
    public void handleDataDeleted(String s) throws Exception {
        refreshRequestor();
    }

    @Override
    public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
        refreshRequestor();
    }

    @Override
    public void handleNewSession() throws Exception {
        refreshRequestor();
    }
}
