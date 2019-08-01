package com.dc.boynextdoor.common;

import java.net.URI;
import java.util.Map;

/**
 * Request
 *
 * @title Request
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public interface Request {

    URI getUri();

    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameters();

    Map<String, Object> getAttachments();

    Object getAttachment(String key);

    void setAttachment(Map<String, Object> attachments);

    void setAttachment(String key, Object value);

    public String getId();

}
