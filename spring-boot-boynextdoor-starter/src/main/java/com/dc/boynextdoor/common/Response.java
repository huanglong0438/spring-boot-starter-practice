package com.dc.boynextdoor.common;

import java.util.Map;

/**
 * Response
 *
 * @title Response
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
public interface Response {

    /**
     * Has exception.
     *
     * @return has exception.
     */
    boolean hasException();

    /**
     * Get invoke result.
     *
     * @return result if has exception throw it.
     * @throws Throwable
     */
    Object getResult();

    /**
     * Get exception.
     *
     * @return exception if no exception return null.
     */
    Throwable getException();

    /**
     * Recreate.
     */
    Object recreate() throws Throwable;

    /**
     * req id
     * @return
     */
    String getId();

    void setException(Throwable exp);

    /**
     * get attachments.
     *
     * @return attachments.
     */
    Map<String, Object> getAttachments();

    /**
     * get attachment by key.
     *
     * @return attachment value.
     */
    Object getAttachment(String key);

    /**
     * set attachments
     * @param attachments
     */
    void setAttachments(Map<String, Object> attachments);

    public void setAttachment(String key, Object value);

}
