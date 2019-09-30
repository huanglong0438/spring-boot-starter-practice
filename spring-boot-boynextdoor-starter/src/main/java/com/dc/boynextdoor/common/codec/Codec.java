package com.dc.boynextdoor.common.codec;

/**
 * Codec
 *
 * @title Codec
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-26
 **/
public interface Codec {

    public <T> T decode(Class<T> clazz, byte[] bytes) throws Exception;

    public <T> byte[] encode(Class<T> clazz, T object) throws Exception;

}
