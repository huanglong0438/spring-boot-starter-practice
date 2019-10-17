package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.constants.Constants;
import com.dc.boynextdoor.core.FilterManager;
import com.dc.boynextdoor.core.ServerRequestor;
import com.dc.boynextdoor.ext.DarkClassLoader;
import com.dc.boynextdoor.remoting.server.NettyServer;
import com.dc.boynextdoor.remoting.server.Server;

import com.dc.boynextdoor.common.URI;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * VanEndPoint 代理了nettyServer的功能
 *
 * @title VanEndPoint
 * @Description VanEndPoint，封装了nettyServer的功能
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public class VanEndPoint implements EndPoint {

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

    @Override
    public <T> Requestor<T> reference(Class<T> type, URI uri) throws IllegalStateException {
        return null;
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

}
