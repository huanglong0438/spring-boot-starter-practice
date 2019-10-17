package com.dc.boynextdoor.core;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.remoting.core.RpcRequest;
import com.dc.boynextdoor.remoting.core.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Server端的处理人
 *
 * @title ServerRequestor
 * @Description Server端的处理人
 * @Author donglongcheng01
 * @Date 2019-10-10
 **/
@Slf4j
public class ServerRequestor<T> extends AbstractRequestor<T> {

    private T impl;

    public ServerRequestor(Class<T> iface, URI uri, T impl) {
        super(iface, uri);
        this.impl = impl;

        MethodCache.registerMethod(iface, impl);
    }


    @Override
    public void request(Request request, Callback<Response> callback) throws IllegalStateException {
        RpcRequest rpcRequest = (RpcRequest) request;
        Method method = MethodCache.getMethod(getInterface(), rpcRequest.getMethodName());
        if (method == null) {
            callback.handleError(new IllegalArgumentException("No such Service: " + getUri().getServiceKey()));
            return;
        }
        try {
            Object returnValue = method.invoke(impl, request.getParameters());
            callback.handleResult(new RpcResponse(rpcRequest.getId(), returnValue, null));
        } catch (Throwable e) {
            // 如果被调用的impl实现方法抛了业务异常，则在这里处理
            log.warn("invoke impl method error.", e);
            callback.handleResult(new RpcResponse(rpcRequest.getId(), null, e));
        }
    }
}
