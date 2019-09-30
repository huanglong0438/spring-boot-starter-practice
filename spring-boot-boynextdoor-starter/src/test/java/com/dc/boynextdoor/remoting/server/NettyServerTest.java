package com.dc.boynextdoor.remoting.server;

import com.dc.boynextdoor.common.URI;
import org.junit.Test;

/**
 * NettyServerTest
 *
 * @title NettyServerTest
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-12
 **/
public class NettyServerTest {

    @Test
    public void testStart() {
        URI uri = new URI("van", null, null,
                "0.0.0.0", 8888, "/fuck/you", null);
        NettyServer server = new NettyServer(uri);
        server.start();
    }

}
