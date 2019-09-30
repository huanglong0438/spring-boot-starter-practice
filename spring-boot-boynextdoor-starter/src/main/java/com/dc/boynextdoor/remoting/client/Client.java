package com.dc.boynextdoor.remoting.client;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;

import java.io.IOException;

/**
 * bnd客户端
 *
 * @title Client
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
public interface Client {

    void connect(URI uri) throws IllegalStateException;

    void close() throws IOException;

    void passiveClose() throws IOException;

    URI getUri();

    void transceive(Request request, Callback<Response> callback) throws IllegalStateException;

    boolean isClosed();

}
