package com.dc.boynextdoor.ext;

/**
 * <p>替代ClassLoader
 *
 * @title DarkClassLoader
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-04
 **/
public class DarkClassLoader {

    public static Class loaderClass(final String name) throws ClassNotFoundException {
        final ClassLoader loader = DarkClassLoaderResolver.getClassLoader(1);
        return Class.forName(name, false, loader);
    }

}
