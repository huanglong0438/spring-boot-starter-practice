package com.dc.boynextdoor.core.directory;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;

import java.util.List;

/**
 * Directory
 *
 * @title Directory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public interface Directory<T> {

    Class<T> getInterface();

    /**
     * 核心方法，其它方法都是扯淡，列出这个request可以调用的Requestor（即下游provider）
     */
    List<Requestor<T>> list(Request request);

    URI getUri();

    URI getDIrectoryUri();

    boolean isAvailable();

    void destroy();

}
