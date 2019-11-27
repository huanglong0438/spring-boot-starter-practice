package com.dc.boynextdoor.cluster;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.core.directory.Directory;

/**
 * Cluster
 *
 * @title Cluster
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public interface Cluster {

    /**
     * merge requestors成为一个总的requestor
     */
    <T> Requestor<T> merge(Directory<T> directory);

}
