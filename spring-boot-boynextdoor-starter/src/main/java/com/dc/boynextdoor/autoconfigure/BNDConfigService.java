package com.dc.boynextdoor.autoconfigure;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * BNDConfigService
 * todo dlc bnd和SpringConfig的联动
 *
 * @title BNDConfigService
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
@Slf4j
public class BNDConfigService {

    private final BoyNextDoorProperties properties;

    private String ipaddr;

    public BNDConfigService(BoyNextDoorProperties properties, Environment environment) {
        this.properties = properties;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    }

    /**
     * 获取service的group，计划要支持每个服务自定义的，目前都用统一的
     *
     * @param serviceInterfaceName 计划要支持每个服务自定义的，目前都用统一的
     * @return service的group
     */
    private String getGroup(String serviceInterfaceName) {
        return properties.getDefaultGroup();
    }

    /**
     * 获取本地机器的ip
     */
    public String getIpaddr() {
        if (ipaddr == null) {
            synchronized (this) {
                try {
                    InetAddress address = InetAddress.getLocalHost();
                    ipaddr = address.getHostAddress();
                } catch (UnknownHostException e) {
                    throw new IllegalStateException("get local address error", e);
                }
            }
        }
        return ipaddr;
    }

    public Integer getPort(String serviceInterfaceName) {
        return properties.getDefaultPort();
    }

    /**
     * 根据{@code com.dc.XXXService}获取配置的接口，封装了里面的getXXX方法
     *
     * @param path com.dc.XXXService
     * @return 封装了一个service ref需要的配置（zk地址以及后面拼接的路径）
     */
    public ServiceConfiguration getServiceConfiguration(String path) {
        ServiceConfiguration configuration = new ServiceConfiguration();
        configuration.setGroup(getGroup(path));
        configuration.setPort(getPort(path));
        configuration.setVersion("1.0.0"); // 暂时写死
        configuration.setZookeeperConnect(getServiceZookeeperConnect(path));
        return configuration;
    }

    private String getServiceZookeeperConnect(String serviceInterfaceName) {
        return properties.getZookeeperConnect();
    }

    @Data
    public static class ServiceConfiguration {
        private String group;
        private Integer port;
        private String zookeeperConnect;
        private String version;
    }

}
