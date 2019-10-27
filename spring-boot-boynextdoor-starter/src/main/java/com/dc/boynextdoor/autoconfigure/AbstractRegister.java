package com.dc.boynextdoor.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.util.Collection;

/**
 * AbstractRegister
 *
 * @title AbstractRegister
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-18
 **/
public class AbstractRegister implements ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {

    /**
     * 容器
     */
    protected BeanFactory beanFactory;

    /**
     * 从xml或注解加载bean的
     */
    protected ResourceLoader resourceLoader;

    /**
     * 负责获取配置的
     */
    protected Environment environment;

    /**
     * {@code @SpringBootApplication -> @EnableAutoConfiguration
     * -> @AutoConfigurationPackage -> @Import(AutoConfigurationPackages.Registrar.class)}
     *
     * <p>{@link AutoConfigurationPackages#get(BeanFactory)}从beanFactory中获取带注解的包
     *
     * @return
     */
    protected Collection<String> getBasePackages() {
        return AutoConfigurationPackages.get(beanFactory);
    }

    protected Class<?> getClass(String beanClassName) {
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new BeanInitializationException("error create bean with class: " + beanClassName, e);
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
