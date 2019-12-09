package com.dc.boynextdoor.common.utils;

import java.util.UUID;

/**
 * IdUtils
 *
 * @title IdUtils
 * @Description
 * @Author donglongcheng01
 * @Date 2019-12-02
 **/
public final class IdUtils {

    private IdUtils() {
    }

    public static String genUUID() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        System.out.println(IdUtils.genUUID());
    }
}
