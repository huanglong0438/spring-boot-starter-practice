package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.Requestor;
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
     * 懒加载，记录下每个 ip+port -> nettyServer 映射
     */
    private final ConcurrentMap<String, Server> serverMap = new ConcurrentHashMap<String, Server>();

    @Override
    public <T> void export(URI uri, T impl) throws IllegalStateException {

        Server server = initServer(uri);

    }

    /**
     * 懒加载serverMap，一个ip:port唯一确定一个server
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
