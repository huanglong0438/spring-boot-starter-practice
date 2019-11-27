package com.dc.boynextdoor.cluster;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.core.directory.Directory;

/**
 * 快速失败Cluster，快速失败的逻辑在 {@link FailfastClusterRequestor}
 *
 * @title FailfastCluster
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public class FailfastCluster implements Cluster {

    public final static String NAME = "failfast";

    /**
     * 生成一个 {@link FailfastClusterRequestor}，快速失败requestor，用来处理请求
     */
    @Override
    public <T> Requestor<T> merge(Directory<T> directory) {
        return new FailfastClusterRequestor<>(directory);
    }
}
