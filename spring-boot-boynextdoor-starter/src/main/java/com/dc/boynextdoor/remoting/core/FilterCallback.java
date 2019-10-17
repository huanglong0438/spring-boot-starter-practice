package com.dc.boynextdoor.remoting.core;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.remoting.Filter;

/**
 * FilterCallback
 *
 * @title FilterCallback
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-15
 **/
public class FilterCallback implements Callback {

    private Callback<Response> callback;

    private Filter filter;

    public FilterCallback(Callback<Response> callback, Filter filter) {
        this.callback = callback;
        this.filter = filter;
    }

    @Override
    public void handleResult(Object result) {

    }

    @Override
    public void handleError(Throwable error) {

    }
}
