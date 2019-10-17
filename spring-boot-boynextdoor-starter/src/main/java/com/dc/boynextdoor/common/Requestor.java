package com.dc.boynextdoor.common;

/**
 * Requestor
 *
 * @title Requestor
 * @Description 就是责任链模式，其中的一环
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public interface Requestor<T> {

    Class<T> getInterface();

    URI getUri();

    boolean isAvailable();

    /**
     * 核心方法，责任链中的一环，处理请求request并且通过callback回调返回结果
     *
     * @param request 请求
     * @param callback 返回结果的回调
     * @throws IllegalStateException
     */
    void request(Request request, Callback<Response> callback) throws IllegalStateException;

    void destroy();

}
