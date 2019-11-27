package com.dc.boynextdoor.remoting.proxy;

import com.dc.boynextdoor.common.Requestor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * RequestorInvocationHandler
 *
 * @title RequestorInvocationHandler
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-27
 **/
public class RequestorInvocationHandler implements InvocationHandler {

    private final Requestor<?> requestor;

    public RequestorInvocationHandler(Requestor<?> requestor) {
        this.requestor = requestor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // todo dlc 继续补充这里，然后梳理总结ServiceReferenceFactoryBean
        return null;
    }
}
