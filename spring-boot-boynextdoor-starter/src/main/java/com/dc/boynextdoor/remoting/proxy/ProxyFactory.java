package com.dc.boynextdoor.remoting.proxy;

import com.dc.boynextdoor.common.Requestor;

/**
 * ProxyFactory
 *
 * @title ProxyFactory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-06
 **/
public interface ProxyFactory {

    <T> T getProxy(Requestor<T> requestor) throws Exception;

    <T> T getAsyncProxy(Requestor<?> requestor, Class<T> asynInterface) throws Exception;

}
