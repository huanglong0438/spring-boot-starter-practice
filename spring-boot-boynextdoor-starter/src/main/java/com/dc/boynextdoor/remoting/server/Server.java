package com.dc.boynextdoor.remoting.server;

import com.dc.boynextdoor.common.Requestor;

/**
 * Server for RPC
 *
 * @title Server
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public interface Server {

    int getPort();

    /**
     * 核心：启动服务
     */
    void start();

    void close();

    void join() throws InterruptedException;

    void registerService(Requestor<?> requestor);

    void unregisterService(Requestor<?> requestor);

}
