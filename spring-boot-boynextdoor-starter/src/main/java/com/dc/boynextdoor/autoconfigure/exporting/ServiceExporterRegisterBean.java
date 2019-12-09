package com.dc.boynextdoor.autoconfigure.exporting;

import com.dc.boynextdoor.autoconfigure.BNDConfigService;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.constants.Constants;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.registry.Registry;
import com.dc.boynextdoor.registry.RegistryFactory;
import com.dc.boynextdoor.registry.ZookeeperRegistryFactory;
import com.dc.boynextdoor.remoting.EndPoint;
import com.dc.boynextdoor.remoting.VanEndPoint;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * RPC服务的provider
 *
 * @title ServiceExporterRegisterBean
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
@Slf4j
public class ServiceExporterRegisterBean {

    private boolean exported = true;

    private URI uri;

    private Class<?> serviceInterface;

    private Object target;

    private BNDConfigService bndConfigService;

    private BNDConfigService.ServiceConfiguration configuration;

    @PostConstruct
    public void init() {
        uri = getUri();

        EndPoint endPoint = TypeLocator.getInstance().getInstanceOfType(VanEndPoint.class);

        endPoint.export(uri, target);
    }

    private URI getUri() {
        configuration = bndConfigService.getServiceConfiguration(serviceInterface.getName());
        Map<String, String> params = Maps.newHashMap();
        params.put(Constants.VERSION_KEY, configuration.getVersion());
        params.put(Constants.GROUP_KEY, configuration.getGroup());
        params.put(Constants.INTERFACE_KEY, serviceInterface.getName());

        return new URI.Builder("van", bndConfigService.getIpaddr(), configuration.getPort())
                .params(params).build();
    }

    public void register() {
        RegistryFactory registryFactory =
                TypeLocator.getInstance().getInstanceOfType(ZookeeperRegistryFactory.class);
        Registry registry = registryFactory.getRegistry(URI.valueOf("van://" + configuration.getZookeeperConnect()));
        registry.register(uri);
        log.info("registered to zk.");
        exported = true;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public BNDConfigService getBndConfigService() {
        return bndConfigService;
    }

    public void setBndConfigService(BNDConfigService bndConfigService) {
        this.bndConfigService = bndConfigService;
    }

    public BNDConfigService.ServiceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BNDConfigService.ServiceConfiguration configuration) {
        this.configuration = configuration;
    }
}
