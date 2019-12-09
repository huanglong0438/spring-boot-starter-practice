package com.dc.boynextdoor.registry;

import com.dc.boynextdoor.common.URI;

/**
 * ZookeeperRegistryFactory
 *
 * @title ZookeeperRegistryFactory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-12-06
 **/
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {
    @Override
    protected Registry createRegistry(URI uri) {
        return new ZookeeperRegistry(uri);
    }
}
