package com.dc.boynextdoor.ext;

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
     * todo dlc vanEndPoint#export() -->|加载serveice的类| DarkClassLoader#loaderClass --> DarkClassLoaderResolver
     *
     * @return
     */
    public static synchronized ClassLoader getClassLoader() {
        return null;
    }

    static synchronized ClassLoader getClassLoader(final int callerOffset) {
        return null;
    }


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
