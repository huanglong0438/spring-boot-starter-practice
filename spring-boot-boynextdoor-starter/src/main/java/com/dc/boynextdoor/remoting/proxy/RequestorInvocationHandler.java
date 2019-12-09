package com.dc.boynextdoor.remoting.proxy;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.remoting.core.CallFuture;
import com.dc.boynextdoor.remoting.core.RpcRequest;
import com.dc.boynextdoor.remoting.core.TransceiverCallback;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * RequestorInvocationHandler
 *
 * @title RequestorInvocationHandler
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-27
 **/
@Slf4j
public class RequestorInvocationHandler implements InvocationHandler {

    private final Requestor<?> requestor;

    public RequestorInvocationHandler(Requestor<?> requestor) {
        this.requestor = requestor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if ((parameterTypes.length > 0) && (parameterTypes[parameterTypes.length - 1] != null) &&
                    Callback.class.isAssignableFrom(parameterTypes[parameterTypes.length - 1])) {
                // 异步
                Object[] finalArgs = Arrays.copyOf(args, args.length - 1);
                Class<?>[] finalParameterTpes = Arrays.copyOf(parameterTypes, parameterTypes.length - 1);
                Callback<?> callback = (Callback<?>) args[args.length - 1];
                Map<String, String> map = Maps.newHashMap();
                map.put("async", "true");
                RpcRequest rpcRequest = new RpcRequest(requestor.getUri().addParameters(map),
                        method.getName(), finalParameterTpes, finalArgs);
                // 异步请求的callback用TransceiverCallback
                requestor.request(rpcRequest, TransceiverCallback.getInstance(callback));
                return null;
            } else {
                // 同步
                CallFuture<Response> callFuture = null;
                RpcRequest request = null;
                callFuture = new CallFuture<>();
                Map<String, String> map = Maps.newHashMap();
                map.put("async", "false");
                request = new RpcRequest(requestor.getUri().addParameters(map),
                        method.getName(), parameterTypes, args);
                // 同步请求就用普通的callFuture
                requestor.request(request, callFuture);
                return callFuture.get().recreate();
            }
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            throw e.getCause();
        }
    }
}
