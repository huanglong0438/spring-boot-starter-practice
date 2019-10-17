package com.dc.boynextdoor.ext;

/**
 * 默认的选择策略，从callerLoader、contextLoader、systemLoader中选择最低的
 *
 * @title DefaultClassLoadStrategy
 * @Description 默认的选择策略，从callerLoader、contextLoader、systemLoader中选择最低的
 * @Author donglongcheng01
 * @Date 2019-10-04
 **/
public class DefaultClassLoadStrategy implements IClassLoadStrategy {

    @Override
    public ClassLoader getClassLoader(ClassLoadContext ctx) {
        if (ctx == null) {
            throw new RuntimeException("ctx is null");
        }

        final ClassLoader callerLoader = ctx.getCallerClass().getClassLoader();
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

        ClassLoader result;

        // caller、context、system总是用子ClassLoader
        if (isChild(contextLoader, callerLoader)) {
            result = callerLoader;
        } else if (isChild(callerLoader, contextLoader)) {
            result = contextLoader;
        } else {
            result = contextLoader;
        }

        final ClassLoader systemLoader = ClassLoader.getSystemClassLoader();

        if (isChild(result, systemLoader)) {
            result = systemLoader;
        }

        return result;
    }

    /**
     * 确认loader1和loader2的父子关系
     *
     * @return loader1是loader2的爸爸吗，是的话返回true，否则返回false
     */
    private static boolean isChild(final ClassLoader loader1, ClassLoader loader2) {
        if (loader1 == loader2)
            return true;
        if (loader2 == null) // 认为loader2是bootstrap loader，最高
            return false;
        if (loader1 == null) // 认为loader1是bootstrap loader，最高
            return true;

        // loader2向上找爸爸，找到loader1，则返回true
        for (; loader2 != null; loader2 = loader2.getParent()) {
            if (loader2 == loader1)
                return true;
        }

        return false;
    }
}
