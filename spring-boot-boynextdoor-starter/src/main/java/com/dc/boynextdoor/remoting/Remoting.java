package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.remoting.client.Client;
import com.dc.boynextdoor.remoting.server.Server;

/**
 * 就是一个工厂模式的工厂，可以获取server or client
 *
 * @title Remoting
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-26
 **/
public interface Remoting {

    /**
     * 获取server
     */
    Server bind(URI uri);

    /**
     * 获取client
     */
    Client connect(URI uri) throws IllegalStateException;

}
