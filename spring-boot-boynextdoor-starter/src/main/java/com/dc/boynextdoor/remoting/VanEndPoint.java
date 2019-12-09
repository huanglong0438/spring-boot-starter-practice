package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.constants.Constants;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.core.ClientRequestor;
import com.dc.boynextdoor.core.FilterManager;
import com.dc.boynextdoor.core.ServerRequestor;
import com.dc.boynextdoor.ext.DarkClassLoader;
import com.dc.boynextdoor.remoting.client.Client;
import com.dc.boynextdoor.remoting.client.ReferenceCountClient;
import com.dc.boynextdoor.remoting.server.NettyServer;
import com.dc.boynextdoor.remoting.server.Server;

import com.dc.boynextdoor.common.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * VanEndPoint（终端） 代理了nettyServer的功能
 *
 * @title VanEndPoint
 * @Description VanEndPoint，封装了nettyServer的功能
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public class VanEndPoint implements EndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(VanEndPoint.class);

    private volatile boolean started;

    /**
     * 懒加载，记录下每个 ip+port -> nettyServer 映射（一般就一个）
     */
    private final ConcurrentMap<String, Server> serverMap = new ConcurrentHashMap<String, Server>();

    /**
     * 懒加载，记录每个 normal:com.dc.boynextdoor.Billy:1.0.0 --> URI 映射
     */
    private final ConcurrentMap<String, URI> servicesMap = new ConcurrentHashMap<>();

    /**
     * 登记每个service的requestor（filter链） eg. com.dc.XXXService --> filters
     */
    private final ConcurrentMap<String, Requestor<?>> serviceRequestorMap = new ConcurrentHashMap<String, Requestor<?>>();

    /**
     *
     */
    private final ClientManager clientManager = new ClientManager();

    /**
     * 各种登记，登记给netty，登记到一个map（endPointStarter最后会统一放到zk上）
     * <p>1. Server端构造filter责任链
     * <p>2. 登记uri的实现类为impl，登记到netty的handler里（处理请求），登记到这里的map里（注册到zk）
     *
     * @param uri  从uri中获取接口信息
     * @param impl 实现类
     * @param <T>  实现类的泛型（XXXService）
     * @throws IllegalStateException
     */
    @Override
    public <T> void export(URI uri, T impl) throws IllegalStateException {
        try {
            Class<T> interfaceClass = DarkClassLoader.loaderClass(uri.getParameter(Constants.INTERFACE_KEY));
            // 1. 根据接口和实现类，构造ServerRequestor处理请求
            Requestor<T> requestor = new ServerRequestor(interfaceClass, uri, impl);
            // 2. 按顺序构造默认要过filter（设置上下文，做各种监控）
            Requestor<T> filterRequestor = FilterManager.buildReuqestorChain(requestor,
                    uri.getParameter(Constants.SERVER_FILTER_KEY), Constants.DEFAULT_FILTERS);
            Server server = initServer(uri);
            // 3. 把自己这个service注册到server上，其实就是注册到netty的handler里登记的map里 serviceKey --> requestor
            server.registerService(filterRequestor);
            URI oldUri = servicesMap.putIfAbsent(uri.getServiceKey(), uri);
            if (oldUri != null) {
                // 这个报错说明这个服务端服务粗心被重复写了两次
                throw new IllegalStateException("uri: " + oldUri + " already exported");
            }
            serviceRequestorMap.put(uri.getServiceKey(), requestor);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

    }

    /**
     * <p>懒加载serverMap，一个ip:port唯一确定一个server
     * <p>一般就一个
     *
     * @param uri van协议的uri
     * @return 一个server（nettyServer）
     */
    private Server initServer(URI uri) {
        String key = uri.getHost() + uri.getPort();
        Server server = serverMap.get(key);
        if (server == null) {
            // 这个锁住的代码块整个是一个get-and-set操作，这个一步是整个需要锁起来的
            synchronized (this) {
                server = serverMap.get(key);
                if (server == null) {
                    Server newServer = new NettyServer(uri);
                    server = serverMap.putIfAbsent(key, newServer);
                    if (server == null) {
                        server = newServer;
                    }
                }
            }
        }
        return server;
    }

    /**
     * uri to requestor，
     * 根据uri --> nettyClient --> ClientRequestor，最后再包装上责任链处理各种杂事(统计,校验...)
     */
    @Override
    public <T> Requestor<T> reference(Class<T> type, URI uri) throws IllegalStateException {
        Requestor<T> requestor = new ClientRequestor<T>(type, uri, clientManager.getClient(uri));
        // todo 这里的filters是错的，是server的，不是client的
        return FilterManager.buildReuqestorChain(requestor,
                "", "clientcontext,activelimit,clientmonitor,clientfceye");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void startServer() {
        started = true;
        // 一个ip:port的server就启动一次，一般也就一个
        for (Server server : serverMap.values()) {
            server.start();
        }

    }

    @Override
    public List<URI> getServices() {
        return null;
    }

    @Override
    public Requestor<?> getServiceRequestor(String key) {
        return null;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    /**
     * 封装了uri --> ReferenceCountClient(对client的计数引用)，
     * client的管理器，在getClient的时候计数加一，但是返回的引用还是原来的
     */
    static class ClientManager {
        /**
         * uri --> ReferenceCountClient(对client的计数引用)
         */
        private final ConcurrentMap<String, ReferenceCountClient> referenceClientMap = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, Object> referenceLock = new ConcurrentHashMap<>();

        /**
         * 在getClient的时候计数加一，但是返回的引用还是原来的（借鉴了JVM引用计数法思想）
         */
        private Client getClient(URI uri) {
            String key = uri.getAddress();
            referenceLock.putIfAbsent(key, new Object());
            Object lock = referenceLock.get(key);

            synchronized (lock) {
                ReferenceCountClient client = referenceClientMap.get(key);
                if (client != null) {
                    if (!client.isClosed()) { // 原来有client，并且正常运行着，则引用计数+1，然后返回
                        client.incrementAndGetCount();
                        return client;
                    }
                    // 原来有client，但是已经关闭了，则remove掉
                    referenceClientMap.remove(key);
                }

                ReferenceCountClient newClient = new ReferenceCountClient(initClient(uri), lock);
                client = referenceClientMap.putIfAbsent(key, newClient);
                if (client == null) {
                    // put succeeded, use new value
                    client = newClient;
                } else {
                    LOGGER.error("error while get Client, old client is exists!!");
                }
                return client;
            }
        }

        /**
         * 通过Remoting（一种工厂）获取Client，实际就是new Client，然后client.connect
         */
        private Client initClient(URI uri) {
            Remoting remoting = TypeLocator.getInstance().getInstanceOfType(Remoting.class, "Netty");
            return remoting.connect(uri);
        }

        public void destroy() {
            for (Map.Entry<String, Object> entry : referenceLock.entrySet()) {
                String key = entry.getKey();
                Object lock = entry.getValue();
                synchronized (lock) {
                    ReferenceCountClient client = referenceClientMap.get(key);
                    try {
                        client.close();
                    } catch (Throwable throwable) {
                        LOGGER.warn(throwable.getMessage(), throwable);
                    }
                }
            }
        }
    }

}
