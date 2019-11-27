package com.dc.boynextdoor.cluster.loadbalancer;

import com.dc.boynextdoor.cluster.loadbalancer.AbstractLoadBalancer;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;

import java.util.List;

/**
 * 只选第一个
 *
 * @title OnlyFirstLoadBalancer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public class OnlyFirstLoadBalancer extends AbstractLoadBalancer {

    public static final String NAME = "onlyFirst";

    @Override
    protected <T> Requestor<T> doSelect(List<Requestor<T>> requestors, Request request) {
        return requestors.get(0);
    }
}
