package com.dc.boynextdoor.autoconfigure.exporting;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.net.URI;

/**
 * ServiceExporterRegisterBean
 *
 * @title ServiceExporterRegisterBean
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
@Slf4j
public class ServiceExporterRegisterBean {

    private boolean exported = true;

    private URI uri;

    @PostConstruct
    public void init() {
        uri = getUri();
    }

    private URI getUri() {
        return null;
    }

    public void register() {
        // todo 注册到zk
        log.info("registered to zk.");
        exported = true;
    }

}
