package com.dc.boynextdoor.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

/**
 * BNDConfigService
 *
 * @title BNDConfigService
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
@Slf4j
public class BNDConfigService {

    private final BoyNextDoorProperties properties;

    public BNDConfigService(BoyNextDoorProperties properties, Environment environment) {
        this.properties = properties;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    }
}
