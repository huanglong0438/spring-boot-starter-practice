package com.dc.boynextdoor.remoting.core;

import com.dc.boynextdoor.common.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * RpcResponse
 *
 * @title RpcResponse
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
public final class RpcResponse implements Response {
    private String id;

    private Object exception;

    private Object result;

    private Map<String, Object> attachments;

    public RpcResponse() {
    }

    public RpcResponse(String id, Object result, Throwable exception) {
        this.id = id;
        this.result = result;
        // if (error != null && error instanceof Throwable)
        // ((Throwable) error).initCause(null);
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public String getId() {
        return id;
    }

    public Throwable getException() {
        return (Throwable) exception;
    }

    public Object recreate() throws Throwable {
        if (exception != null && exception instanceof Throwable) {
            throw (Throwable) exception;
        }
        return result;
    }

    public boolean hasException() {
        return exception != null;
    }

    @Override
    public String toString() {
        return "RpcResponse [result=" + result + ", exception=" + exception + "]";
    }

    public void setException(Throwable exp) {
        this.exception = exp;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public Object getAttachment(String key) {
        if (attachments == null) {
            return null;
        }
        return attachments.get(key);
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments == null ? new HashMap<String, Object>() : attachments;
    }

    public void setAttachment(String key, Object value) {
        if (attachments == null) {
            attachments = new HashMap<String, Object>();
        }
        attachments.put(key, value);
    }

}
