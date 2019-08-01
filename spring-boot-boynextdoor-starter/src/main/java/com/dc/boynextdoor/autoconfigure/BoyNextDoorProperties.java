package com.dc.boynextdoor.autoconfigure;

import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BoyNextDoorProperties
 *
 * @title BoyNextDoorProperties
 * @Description
 * @Author donglongcheng01
 * @Date 2019-07-23
 **/
@ConfigurationProperties(prefix = BoyNextDoorProperties.PREFIX)
@Data
public class BoyNextDoorProperties {

    static final String PREFIX = "com.dc.boynextdoor";

    private static final int RANGE_PORT_START = 50000;
    private static final int RANGE_PORT_END = 60000;

    // todo 后面考虑换成Eruka
    private String zookeeperConnect;

    private Boolean registration = true;

    private Integer defaultPort = RandomUtils.nextInt(RANGE_PORT_START, RANGE_PORT_END);

}
