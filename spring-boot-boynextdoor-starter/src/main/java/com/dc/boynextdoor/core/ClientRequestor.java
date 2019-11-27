package com.dc.boynextdoor.core;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.remoting.RpcStatus;
import com.dc.boynextdoor.remoting.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClientRequestor
 *
 * @title ClientRequestor
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-22
 **/
public class ClientRequestor<T> extends AbstractRequestor<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRequestor.class);

    private Client client;

    public ClientRequestor(Class<T> iface, URI uri, Client client) {
        super(iface, uri);
        this.client = client;
        RpcStatus.cleaRpcStatus(getUri());
    }

    @Override
    public boolean isAvailable() {
        int expireFailed = RpcStatus.getStatus(getUri()).getExpireFailed();
        int failedCheck = 3; // 先默认检查设置拉黑阈值3次
        return expireFailed < failedCheck;
    }

    /**
     * 就是执行client.transceive，本质上就是执行netty的channel的writeAndFlush把codec编码的字节码数组送出去
     *
     * @param request 请求
     * @param callback 返回结果的回调
     * @throws IllegalStateException
     */
    @Override
    public void request(Request request, Callback<Response> callback) throws IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("requestor is unavailable: " + this);
        }
        client.transceive(request, callback);
    }
}
