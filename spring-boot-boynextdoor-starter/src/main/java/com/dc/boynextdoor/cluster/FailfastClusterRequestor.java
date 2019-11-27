package com.dc.boynextdoor.cluster;

import com.dc.boynextdoor.cluster.loadbalancer.LoadBalancer;
import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.core.directory.Directory;
import com.dc.boynextdoor.remoting.RpcLocalContext;

import java.util.List;

/**
 * 快速失败的requestor，快速失败的逻辑在 {@link FailfastClusterCallback} 对异常结果的处理方式
 *
 * @title FailfastClusterRequestor
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public class FailfastClusterRequestor<T> extends AbstractClusterRequestor<T> {

    public FailfastClusterRequestor(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected void doRequest(
            Request request, List<Requestor<T>> requestors, LoadBalancer loadBalancer, Callback<Response> callback) {
        // 检查requestors是否为空，判断是否还有provider
        checkRequestors(requestors, request);
        Requestor<T> requestor = select(loadBalancer, request, requestors, null);
        requestor.request(request, new FailfastClusterCallback(callback));

    }

    /**
     * 快速失败Requestor的核心，handleResult时遇到exception就直接失败报错了（而不会自动进行重试）
     */
    class FailfastClusterCallback implements Callback<Response> {

        private final Callback<Response> chainedCallback;

        FailfastClusterCallback(Callback<Response> chainedCallback) {
            this.chainedCallback = chainedCallback;
        }

        @Override
        public void handleResult(Response result) {
            if (result.hasException()) {
                RpcLocalContext.removeContext(result.getId());
            }
            chainedCallback.handleResult(result);
        }

        @Override
        public void handleError(Throwable error) {

        }
    }

}
