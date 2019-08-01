package com.dc.boynextdoor.common;

/**
 * Callback
 *
 * @title Callback
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public interface Callback<T> {

    void handleResult(T result);

    void handleError(Throwable error);

}
