package com.dc.boynextdoor.remoting.server;

import com.dc.boynextdoor.common.Requestor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * NettyServer
 *
 * @title NettyServer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
@Slf4j
public class NettyServer implements Server {

    public NettyServer(URI uri) {
        // 从uri中取出
    }

    @Override
    public int getPort() {
        return 0;
    }

    /**
     * 核心：启动netty服务
     */
    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public void join() throws InterruptedException {

    }

    @Override
    public void registerService(Requestor<?> requestor) {

    }

    @Override
    public void unregisterService(Requestor<?> requestor) {

    }
}
