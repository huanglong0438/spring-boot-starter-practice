package com.dc.boynextdoor.cluster.router;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;

import java.util.List;

/**
 * Router
 *
 * @title Router
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-21
 **/
public interface Router extends Comparable<Router> {

//    int PRIORITY_ID_ROUTER = 5;
//
//    int PRIORITY_DYNAMICFLOW_ROUTER = 4;
//
//    int PRIORITY_DARKLAUNCH_ROUTER = 3;
//
//    int PRIORITY_LOGICIDC_ROUTER = 2;
//
//    int PRIORITY_LOCALHOSTPRIOR_ROUTER = 1;

    // 默认情况 先app（tags） 再用户（dynamicflow） 再逻辑机房（LOGICIDC）

    int PRIORITY_LOGICIDC_ROUTER = 90;
    int PRIORITY_DYNAMICFLOW_ROUTER = 80;
    int PRIORITY_TAGS_ROUTER = 70;
    int PRIORITY_ID_ROUTER = 60;
    int PRIORITY_DARKLAUNCH_ROUTER = 50;
    int PRIORITY_LOCALHOSTPRIOR_ROUTER = 10;

    /**
     * 返回router的优先级，用于在多个router存在时确定哪个优先执行
     * 目前的优先级为：id > dynamicflow > darklaunch > logicidc > localhost
     * @return 返回优先级
     */
    int getPriority();

    URI getUri();

    /**
     * 根据消费者uri和请求参数,从一个requestor集合里选出满足条件的子集
     * @param requestors
     * @param uri
     * @param request
     * @return
     */
    <T> List<Requestor<T>> route(List<Requestor<T>> requestors, URI uri, Request request);

}
