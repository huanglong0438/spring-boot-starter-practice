package com.dc.boynextdoor.cluster.loadbalancer;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;

import java.util.List;

/**
 * LoadBalancer
 *
 * @title LoadBalancer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public interface LoadBalancer {

    /**
     * 负载均衡，从{@code requestors}中选择一个
     */
    <T> Requestor<T> select(List<Requestor<T>> requestors, Request request);

}
