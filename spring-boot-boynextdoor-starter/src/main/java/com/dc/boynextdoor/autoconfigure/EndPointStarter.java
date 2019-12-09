package com.dc.boynextdoor.autoconfigure;

import com.dc.boynextdoor.autoconfigure.exporting.ServiceExporterRegisterBean;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.remoting.EndPoint;
import com.dc.boynextdoor.remoting.VanEndPoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * EndPointStarter，实现了ApplicationListener，在Spring加载完成后会执行主流程，挨个注册服务
 *
 * @title EndPointStarter
 * @Description EndPointStarter，实现了ApplicationListener，在Spring加载完成后会执行主流程，挨个注册服务
 * @Author donglongcheng01
 * @Date 2019-07-23
 **/
@Slf4j
public class EndPointStarter implements ApplicationListener<ApplicationReadyEvent>, BeanFactoryAware, DisposableBean {

    private ListableBeanFactory beanFactory;

    @Autowired
    private BoyNextDoorProperties boyNextDoorProperties;

    /**
     * <pre>
     * Spring发生ApplicationReadyEvent事件时，做出响应
     * 1. 启动netty server，包括在childHandler里登记{@link VanEndPoint#export(URI, Object)}注册好的handler
     * 2. 把service注册到zk
     * </pre>
     */
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // EndPoint就是nettyServer
        EndPoint endPoint = TypeLocator.getInstance().getInstanceOfType(EndPoint.class);
        if (endPoint.isStarted()) {
            return;
        }

        // 服务端的所有service
        Map<String, ServiceExporterRegisterBean> services =
                beanFactory.getBeansOfType(ServiceExporterRegisterBean.class);

        if (MapUtils.isEmpty(services)) {
            return;
        }

        // 核心：启动netty server
        log.info("starting boy next door...");
        endPoint.startServer();

        if (boyNextDoorProperties.getRegistration() != null && !boyNextDoorProperties.getRegistration()) {
            log.info("boyNextDoorProperties registration is false");
            return;
        }

        // 核心：把服务端的service注册到zk
        log.info("registering services...");
        services.forEach((serviceName, registerBean) -> registerBean.register());


    }

    public void destroy() throws Exception {
        EndPoint endPoint = TypeLocator.getInstance().getInstanceOfType(VanEndPoint.class);
        if (endPoint.isStarted()) {
            endPoint.destroy();
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    public void setBoyNextDoorProperties(BoyNextDoorProperties boyNextDoorProperties) {
        this.boyNextDoorProperties = boyNextDoorProperties;
    }
}
