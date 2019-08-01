package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.Requestor;

import java.net.URI;
import java.util.List;

/**
 * EndPoint
 *
 * @title EndPoint
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public interface EndPoint {

    // warning：java.net.URI可能存在线程安全问题
    <T> void export(URI uri, T impl) throws IllegalStateException;

    <T> Requestor<T> reference(Class<T> type, URI uri) throws IllegalStateException;

    void destroy();

    void startServer();

    List<URI> getServices();

    Requestor<?> getServiceRequestor(String key);

    boolean isStarted();


}
