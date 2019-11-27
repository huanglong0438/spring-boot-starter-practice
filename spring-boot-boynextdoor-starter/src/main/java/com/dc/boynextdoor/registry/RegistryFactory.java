package com.dc.boynextdoor.registry;

import com.dc.boynextdoor.common.URI;

/**
 * 注册中心工厂类
 *
 * @title RegistryFactory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-04
 **/
public interface RegistryFactory {

    /**
     * 获取注册中心
     *
     * @param uri 注册中心地址
     * @return 返回注册中心
     */
    Registry getRegistry(URI uri);

}
