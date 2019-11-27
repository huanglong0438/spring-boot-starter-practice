package com.dc.boynextdoor.cluster.loadbalancer;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AbstractLoadBalancer
 *
 * @title AbstractLoadBalancer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public abstract class AbstractLoadBalancer implements LoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLoadBalancer.class);

    public String getName() {
        try {
            return getClass().getSimpleName();
        } catch (Exception e) {
            // 可能是个匿名内部类
            return getClass().getName();
        }
    }

    @Override
    public <T> Requestor<T> select(List<Requestor<T>> requestors, Request request) {
        String loadBalanceName = getName();
        if (CollectionUtils.isEmpty(requestors)) {
            LOGGER.debug("requestor is empty");
            return null;
        }
        Requestor<T> result = null;

        if (requestors.size() == 1) {
            result = requestors.get(0);
        } else {
            result = doSelect(requestors, request);
        }
        LOGGER.debug("LoadBalancer:{}, before:{},after:{}", loadBalanceName, requestors, result);
        return result;
    }

    /**
     * 子类实现具体的均衡策略
     */
    protected abstract <T> Requestor<T> doSelect(List<Requestor<T>> requestors, Request request);


}
