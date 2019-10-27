package com.dc.boynextdoor.ext;

import com.dc.boynextdoor.common.URI;

/**
 * DarkClassLoaderResolver
 *
 * @title DarkClassLoaderResolver
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-04
 **/
public final class DarkClassLoaderResolver {

    private static final CallerResolver CALLER_RESOLVER;

    private static IClassLoadStrategy classLoadStrategy;

    static {
        try {
            CALLER_RESOLVER = new CallerResolver();
        } catch (SecurityException e) {
            throw new RuntimeException("DarkClassLoaderResolver: could not create CallerResolver: " + e);
        }
        classLoadStrategy = new DefaultClassLoadStrategy(); // 采用默认的选择ClassLoader选择策略（选最低的）
    }

    /**
     * 定位到VanEndPoint，然后根据{@code classLoadStrategy}策略获取ClassLoader
     *
     * @return
     */
    public static synchronized ClassLoader getClassLoader() {
        return getClassLoader(0);
    }

    static synchronized ClassLoader getClassLoader(int callerOffset) {
        Class caller = getCallerClass(callerOffset);
        ClassLoadContext classLoadContext = new ClassLoadContext(caller);
        // 用VanEndPoint的ClassLoader
        return classLoadStrategy.getClassLoader(classLoadContext);
    }

    /**
     * <p>CALLER_RESOLVER.getClassContext获取调用栈（getCallerClass -> getClassLoader -> loaderClass），
     * <p>获取的过程通过自定义{@code SecurityManager}子类绕过安全检查
     * <p>然后后面是偏移量，刚好定位到{@link com.dc.boynextdoor.remoting.VanEndPoint#export(URI, Object)}
     *
     * @param offset 偏移量
     * @return 返回VanEndPoint调用类
     */
    private static Class getCallerClass(int offset) {
        return CALLER_RESOLVER.getClassContext()[CALL_CONTEXT_OFFSET + offset];
    }

    private static final int CALL_CONTEXT_OFFSET = 3;


    /**
     * <p>CallerResolver继承自SecurityManager
     * <p>覆写{@link #getClassContext()}方法获取调用栈，防止被SecurityManager给权限拦截了
     */
    private static final class CallerResolver extends SecurityManager {
        protected Class[] getClassContext() {
            return super.getClassContext();
        }

    }

    private DarkClassLoaderResolver() {
        // non-instantiable
    }
}
