package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.Response;

/**
 * Filter
 *
 * @title Filter
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-15
 **/
public interface Filter extends Callback<Response> {

    /**
     * 责任链的模式中的每一个filter，都会过一次request方法
     *
     * @param requestor 责任链
     * @param request 请求
     * @param response 返回
     * @throws IllegalStateException 处理过程中可能的异常
     */
    void request(Requestor<?> requestor, Request request, Callback<Response> response) throws IllegalStateException;

}
