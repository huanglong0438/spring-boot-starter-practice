package com.dc.boynextdoor.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * ServiceConfigurationBinder
 *
 * @title ServiceConfigurationBinder
 * @Description
 * @Author donglongcheng01
 * @Date 2019-12-03
 **/
@Slf4j
public class ServiceConfigurationBinder {

    private static final String PREFIX = BoyNextDoorProperties.PREFIX + ".service";

    private static final Pattern PATH_SPLITTER_PATTERN = Pattern.compile("[.]");

    private final BoyNextDoorProperties properties;

    private final Environment environment;

    private Map<String, BNDConfigService.ServiceConfiguration> serviceConfig;

    private static final Set<String> SUFFIX = new HashSet<String>() {
        {
            add("zookeeper-connect");
            add("zookeeper.connect");
            add("group");
            add("version");
            add("port");
        }
    };

    public ServiceConfigurationBinder(Environment environment, BoyNextDoorProperties properties) {
        this.properties = properties;
        this.environment = environment;
    }

    public Map<String, BNDConfigService.ServiceConfiguration> bind() {
        // comparing是从s中提取出需要被排序的cnt，然后通过比较cnt来排序
        Comparator<String> comparator = Comparator.comparing(s -> {
            int cnt = PATH_SPLITTER_PATTERN.matcher(s).groupCount();
            return s.contains("zookeeper.connect") ? cnt - 1 : cnt;
        });
        comparator.reversed().thenComparing(Comparator.naturalOrder());
        serviceConfig = new TreeMap<>(comparator); // TreeMap，带排序功能的树，底层是红黑树
        // SpringBoot的binder，从environment中获取配置
        Binder binder = Binder.get(environment);
        // 获取PREFIX的配置，然后转成map，然后执行#setServiceConfig设置
        binder.bind(PREFIX, Bindable.mapOf(String.class, String.class))
                .orElseGet(Collections::emptyMap)
                .forEach(this::setServiceConfig);
        return serviceConfig;
    }

    private void setServiceConfig(String key, String value) {
        SUFFIX.stream()
                .filter(key::endsWith)
                .forEach(name -> {
                    // serviceKey.zookeeper-connect - zookeeper-connect，两边做减法，得到serviceKey
                    String serviceKey = key.substring(0, key.length() - name.length() - 1);
                    // 给serviceKey注入name:value的配置
                    if (!serviceConfig.containsKey(serviceKey)) {
                        serviceConfig.put(serviceKey, new BNDConfigService.ServiceConfiguration());
                    }
                    setValue(serviceConfig.get(serviceKey), name, value);
                });
    }

    private void setValue(BNDConfigService.ServiceConfiguration serviceConfiguration, String name, String value) {
        switch (name) {
            case "zookeeper.connect":
            case "zookeeper-connect":
                serviceConfiguration.setZookeeperConnect(value);
                break;
            case "group":
                serviceConfiguration.setGroup(value);
                break;
            case "version":
                serviceConfiguration.setVersion(value);
                break;
            case "port":
                serviceConfiguration.setPort(Integer.parseInt(value));
                break;
            default:
                throw new IllegalArgumentException("illegal argument: " + name);
        }
    }
}
