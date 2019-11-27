package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.remoting.client.Client;
import com.dc.boynextdoor.remoting.client.NettyClient;
import com.dc.boynextdoor.remoting.server.NettyServer;
import com.dc.boynextdoor.remoting.server.Server;

/**
 * NettyRemoting
 *
 * @title NettyRemoting
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-26
 **/
public class NettyRemoting implements Remoting {

    @Override
    public Server bind(URI uri) {
        return new NettyServer(uri);
    }

    @Override
    public Client connect(URI uri) throws IllegalStateException {
        Client client = new NettyClient();
        client.connect(uri);
        return client;
    }
}
