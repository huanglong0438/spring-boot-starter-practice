package com.dc.boynextdoor.remoting.client;

import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.remoting.core.RpcRequest;
import org.junit.Test;

/**
 * NettyClientTest
 *
 * @title NettyClientTest
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
public class NettyClientTest {

    @Test
    public void testSendRequest() {
        URI uri = new URI("van", "", "",
                "127.0.0.1", 8888, "/dlc/test", null);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName("dlctest");
        rpcRequest.setId("666");
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setParameters(new Object[]{"param"});
        NettyClient nettyClient = new NettyClient(uri);
        nettyClient.connect(uri);
        nettyClient.transceive(rpcRequest, null);
    }

}
