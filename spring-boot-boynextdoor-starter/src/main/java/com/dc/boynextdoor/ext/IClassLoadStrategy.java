package com.dc.boynextdoor.ext;

/**
 * IClassLoadStrategy
 *
 * @title IClassLoadStrategy
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-04
 **/
public interface IClassLoadStrategy {

    /**
     * 根据ClassLoadContext选择一个ClassLoader
     *
     * @see DarkClassLoaderResolver#getClassLoader()
     */
    ClassLoader getClassLoader(ClassLoadContext ctx);
}
