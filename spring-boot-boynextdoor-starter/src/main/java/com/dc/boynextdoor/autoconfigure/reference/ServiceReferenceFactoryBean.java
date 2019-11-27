package com.dc.boynextdoor.autoconfigure.reference;

import com.dc.boynextdoor.autoconfigure.BNDConfigService;
import com.dc.boynextdoor.autoconfigure.logging.GenericServiceLogger;
import com.dc.boynextdoor.cluster.Cluster;
import com.dc.boynextdoor.cluster.FailfastCluster;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.constants.Constants;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.core.directory.RegistryDirectory;
import com.dc.boynextdoor.registry.Registry;
import com.dc.boynextdoor.registry.RegistryFactory;
import com.dc.boynextdoor.registry.ZookeeperRegistry;
import com.dc.boynextdoor.remoting.proxy.JdkProxyFactory;
import com.dc.boynextdoor.remoting.proxy.ProxyFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link com.dc.boynextdoor.autoconfigure.annotation.ServiceReference}注解实际生成的代理工厂
 *
 * @title ServiceReferenceFactoryBean
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-27
 **/
public class ServiceReferenceFactoryBean<T> implements FactoryBean<T> {

    private BNDConfigService bndConfigService;

    private Class<T> serviceInterface;

    private BNDConfigService.ServiceConfiguration configuration;

    private T singletonInstance = null;

    /**
     * 【核心】@ServiceReference注解生成FactoryBean单例的代理工厂的过程
     *
     * @return 代理
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        if (singletonInstance == null) {
            configuration = bndConfigService.getServiceConfiguration(serviceInterface.getName());
            // registry工厂
            RegistryFactory registryFactory =
                    (RegistryFactory) TypeLocator.getInstance().getInstanceOfType(ZookeeperRegistry.class);
            String zookeeperConnect = configuration.getZookeeperConnect();
            URI zookeeperUri = URI.valueOf("zookeeper://" + zookeeperConnect);
            // 工厂模式，根据zookeeperUri生产Registry
            Registry registry = registryFactory.getRegistry(zookeeperUri);
            Assert.notNull(registry, "registry factory get Registry failed");
            ProxyFactory proxyFactory = TypeLocator.getInstance().getInstanceOfType(JdkProxyFactory.class);
            URI serviceUri = getServiceUri();

            // RegistryDirectory的核心就是list，当做一个文件夹(directory)，list方法列出可执行的下游provider
            RegistryDirectory<T> directory = new RegistryDirectory<>(serviceInterface, zookeeperUri, serviceUri);
            directory.setRegistry(registry);
            registry.subscribe(serviceUri, directory);
            Cluster cluster = TypeLocator.getInstance().getInstanceOfType(FailfastCluster.class);
            // 这里生成的requestor，日后在执行被代理的Reference的方法时，
            // 会调用directory.list列出provider然后选择一个writeAndFlush
            Requestor<T> requestor = cluster.merge(directory);
            // 自定义的JdkProxyFactory封装了requestor，然后在这之上再封装了打log
            singletonInstance = logged(proxyFactory.getProxy(requestor));
        }
        return singletonInstance;
    }

    /**
     * 为{@code proxy}生成了代理类，包上了一层{@link GenericServiceLogger}的打印日志的AOP，
     * 注意，用到了<b>Spring的AOP</b>功能
     *
     * @param proxy 被代理的对象
     * @return 返回AOP包裹后的对象
     */
    private T logged(T proxy) {
        GenericServiceLogger logger = new GenericServiceLogger(LoggerFactory.getLogger(serviceInterface.getName()));
        org.springframework.aop.framework.ProxyFactory proxyFactory =
                new org.springframework.aop.framework.ProxyFactory(proxy);
        proxyFactory.addAdvice(logger);
        return (T) proxyFactory.getProxy();
    }

    private URI getServiceUri() {
        Map<String, String> params = new HashMap<>();
        // 从配置中心获取version group interface
        params.put(Constants.VERSION_KEY, configuration.getVersion());
        params.put(Constants.GROUP_KEY, configuration.getGroup());
        params.put(Constants.INTERFACE_KEY, serviceInterface.getName());
        String host = bndConfigService.getIpaddr();
        return new URI.Builder("van", host, 0).params(params).build();
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }
}
