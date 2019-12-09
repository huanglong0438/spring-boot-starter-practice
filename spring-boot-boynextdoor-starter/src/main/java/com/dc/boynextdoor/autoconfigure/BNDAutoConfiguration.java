package com.dc.boynextdoor.autoconfigure;

import com.dc.boynextdoor.autoconfigure.exporting.ServiceExportingRegister;
import com.dc.boynextdoor.autoconfigure.reference.ServiceReferenceRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * 整体的Application容器配置，匹配com.dc.autoconfigure.enabled配置，如果为true，则该配置生效
 *
 * @title BNDAutoConfiguration
 * @Description
 * @Author donglongcheng01
 * @Date 2019-07-23
 **/
@Configuration
@ConditionalOnProperty(
        prefix = "com.dc.autoconfigure",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Import({ServiceReferenceRegistrar.class, ServiceExportingRegister.class})
@AutoConfigureAfter(BNDConfigAutoConfiguration.class)
public class BNDAutoConfiguration {

    /**
     * 解析BoyNextDoorProperties，解析配置的服务
     */
    @Bean
    public BNDConfigService bndConfigService(BoyNextDoorProperties properties, Environment environment) {
        return new BNDConfigService(properties, environment);
    }

    /**
     * 一个{@link ApplicationListener}，在Spring容器启动后执行相关代码启动nettyServer
     */
    @Bean
    public EndPointStarter endPointStarter(BoyNextDoorProperties properties) {
        EndPointStarter starter = new EndPointStarter();
        starter.setBoyNextDoorProperties(properties);
        return starter;
    }

}
