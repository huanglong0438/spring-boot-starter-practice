package com.dc.boynextdoor.cluster.loadbalancer;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.utils.AtomicPositiveInteger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * RoundRobinLoadBalancer，负载均衡轮询算法
 *
 * @title RoundRobinLoadBalancer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-12
 **/
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    private final ConcurrentMap<String, AtomicPositiveInteger> service2Index = new ConcurrentHashMap<>();

    @Override
    protected <T> Requestor<T> doSelect(List<Requestor<T>> requestors, Request request) {
        String key = requestors.get(0).getUri().getServiceKey() + "." + request.getMethodName();
        AtomicPositiveInteger index = service2Index.get(key);
        if (index == null) {
            service2Index.putIfAbsent(key, new AtomicPositiveInteger());
            index = service2Index.get(key);
        }
        return requestors.get(index.getAndIncrement() % requestors.size());
    }
}
