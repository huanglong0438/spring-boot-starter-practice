package com.dc.boynextdoor.common;

import java.net.URI;

/**
 * Requestor
 *
 * @title Requestor
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public interface Requestor<T> {

    Class<T> getInterface();

    URI getUri();

    boolean isAvailable();

    void request(Request request, Callback<T> callback) throws IllegalStateException;

    void destroy();

}
