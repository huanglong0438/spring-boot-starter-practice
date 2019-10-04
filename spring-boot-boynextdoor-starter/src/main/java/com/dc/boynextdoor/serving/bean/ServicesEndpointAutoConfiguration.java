package com.dc.boynextdoor.serving.bean;

import com.dc.boynextdoor.autoconfigure.annotation.ServiceExported;
import com.dc.boynextdoor.autoconfigure.annotation.ServiceReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration注解用来表示这个类是SpringBoot版的xml文件
 *
 * @title ServicesEndpointAutoConfiguration
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-30
 **/
@Configuration
@ConditionalOnClass({ServiceExported.class, ServiceReference.class})
public class ServicesEndpointAutoConfiguration {

    

}
