package com.dc.boynextdoor.remoting.client;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 通过{@link AtomicInteger}来对Client的引用计数，引用为0时才close（借鉴了JVM引用计数法GC的思想）
 *
 * @title ReferenceCountClient
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-26
 **/
public final class ReferenceCountClient implements Client {

    private final Object lock;
    private final Client client;

    private final AtomicInteger refenceCount = new AtomicInteger(0);

    public ReferenceCountClient(Client client, Object lock) {
        this.client = client;
        this.lock = lock;
        refenceCount.incrementAndGet();
    }

    public URI URI() {
        return client.getUri();
    }

    public void close() throws IOException {
        synchronized (lock) {
            if (refenceCount.decrementAndGet() <= 0) {
                client.close();
            }
        }
    }

    public void passiveClose() throws IOException {
        this.close();
    }

    public void connect(URI uri) throws IllegalStateException {
        client.connect(uri);
    }

    public URI getUri() {
        return client.getUri();
    }

    public void transceive(Request request, Callback<Response> callback) throws IllegalStateException {
        client.transceive(request, callback);
    }

    public void incrementAndGetCount() {
        refenceCount.incrementAndGet();
    }

    public boolean isClosed() {
        return client.isClosed();
    }
}
