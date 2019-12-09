package com.dc.boynextdoor.remoting.core;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Response;

/**
 * TransceiverCallback
 *
 * @title TransceiverCallback
 * @Description
 * @Author donglongcheng01
 * @Date 2019-12-02
 **/
public class TransceiverCallback<T> implements Callback<Response> {

    private final Callback<T> callback;

    public static <R> TransceiverCallback<R> getInstance(Callback<R> callback) {
        return new TransceiverCallback<>(callback);
    }

    public TransceiverCallback(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void handleResult(Response result) {

    }

    @Override
    public void handleError(Throwable error) {

    }
}
