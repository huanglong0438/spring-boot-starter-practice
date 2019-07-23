package com.dc.boynextdoor.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * EndPointStarter
 *
 * @title EndPointStarter
 * @Description
 * @Author donglongcheng01
 * @Date 2019-07-23
 **/
@Slf4j
public class EndPointStarter implements ApplicationListener<ApplicationReadyEvent>, BeanFactoryAware, DisposableBean {

    private ListableBeanFactory beanFactory;

    @Autowired
    private BoyNextDoorProperties boyNextDoorProperties;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    public void destroy() throws Exception {

    }

    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

    }
}
