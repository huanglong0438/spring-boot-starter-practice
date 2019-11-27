package com.dc.boynextdoor.autoconfigure.logging;

import com.google.common.collect.Lists;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * GenericServiceLogger
 *
 * @title GenericServiceLogger
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-27
 **/
public class GenericServiceLogger implements ServiceLogger {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    private final Logger logger;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        try {
            result = methodInvocation.proceed();
        } catch (Throwable t) {
            throw (throwable = t);
        } finally {
            try {
                printLog(methodInvocation, beginTime, throwable, result);
            } catch (Throwable t) {
                logger.warn("error while printLog", t);
            }
        }
        return result;
    }


    private void printLog(MethodInvocation methodInvocation, long beginTime, Throwable throwable, Object result) {
        String messagePattern = "[service: {}][start: {}] [using(ms): {}]";
        List<Object> params = Lists.newArrayList(
                methodInvocation.getMethod(),
                new SimpleDateFormat(DATE_FORMAT).format(beginTime),
                System.currentTimeMillis() - beginTime);
        logger.info(messagePattern, params);
    }

    public GenericServiceLogger(Logger logger) {
        this.logger = logger;
    }
}
