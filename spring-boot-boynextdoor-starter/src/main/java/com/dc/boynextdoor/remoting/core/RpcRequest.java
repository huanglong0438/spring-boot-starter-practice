package com.dc.boynextdoor.remoting.core;

import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.URI;

import java.util.HashMap;
import java.util.Map;

/**
 * RpcRequest
 *
 * @title RpcRequest
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-26
 **/
public class RpcRequest implements Request {

    /**
     * RPC请求的id
     */
    private String id;

    /**
     * RPC方法的名称
     */
    private String methodName;

    /**
     * RPC方法的参数
     */
    private Object[] parameters;

    /**
     * RPC方法的参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 需要附带到RPC请求中的信息（sessonid，上游ip，各种上下文）
     */
    private Map<String, Object> attachments;

    /**
     * RPC请求的uri
     */
    private URI uri;

    public RpcRequest() {
    }

    public RpcRequest(String methodName, Object[] parameters, Class<?>[] parameterTypes, URI uri) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
        this.uri = uri;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Map<String, Object> getAttachments() {
        return attachments;
    }

    @Override
    public Object getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public void setAttachment(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    @Override
    public void setAttachment(String key, Object value) {
        if (this.attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put(key, value);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
