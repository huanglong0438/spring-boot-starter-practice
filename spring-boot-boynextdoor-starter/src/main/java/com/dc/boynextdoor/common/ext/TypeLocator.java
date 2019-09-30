package com.dc.boynextdoor.common.ext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 用来根据Class定位到单例的类，这个类存在的意义就是因为不想依赖Spring，但是又想有Spring的获取bean的功能
 *
 * @title TypeLocator
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
public class TypeLocator {

    private static final ConcurrentMap<Class<?>, Object> CLASS_SINGLETON = new ConcurrentHashMap<>();

    private static TypeLocator instance = null;

    @SuppressWarnings("unchecked")
    public <T> T getInstanceOfType(Class<T> clazz) {
        // todo obj可能不是T类型，待补充
        try {
            T instance = (T) CLASS_SINGLETON.get(clazz);
            if (instance == null) {
                CLASS_SINGLETON.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) CLASS_SINGLETON.get(clazz);
            }
            return instance;
        } catch (Throwable throwable) {
            throw new IllegalStateException("class [" + clazz + "] could not be instantiated.", throwable);
        }
    }

    /**
     * 单例模式-懒汉模式-线程安全的双重检测
     *
     * @return
     */
    public static TypeLocator getInstance() {
        if (instance == null) {
            synchronized (TypeLocator.class) {
                if (instance == null) {
                    instance = new TypeLocator();
                }
            }
        }
        return instance;
    }

}
