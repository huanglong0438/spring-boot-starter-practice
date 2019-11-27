package com.dc.boynextdoor.remoting.proxy;

import com.dc.boynextdoor.common.Requestor;

import java.lang.reflect.Proxy;

/**
 * 用JDK的反射包里的代理方法{@code Proxy.newProxyInstance}生成代理对象
 *
 * @title JdkProxyFactory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-06
 **/
public class JdkProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> T getProxy(Requestor<T> requestor) throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[] { requestor.getInterface() }, new RequestorInvocationHandler(requestor));
    }

    @Override
    public <T> T getAsyncProxy(Requestor<?> requestor, Class<T> asynInterface) throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[] { asynInterface }, new RequestorInvocationHandler(requestor));
    }
}
