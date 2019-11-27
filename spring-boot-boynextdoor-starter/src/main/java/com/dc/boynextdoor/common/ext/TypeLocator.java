package com.dc.boynextdoor.common.ext;

import com.dc.boynextdoor.remoting.Filter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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

    /**
     * {@code baseClass}在同一个包下是否有名为{@code name}的子类
     *
     * @param baseClass 基类，如Filter.class
     * @param name 子类名称，如ServerContext
     * @param <T> 泛型
     * @return ServerContextFilter是否存在
     */
    public <T> boolean hasExtension(Class<T> baseClass, String name) {
        try {
            T instance = getInstanceOfType(baseClass, name);
            return instance != null;
        } catch (Exception e) {
            log.error("error while loading Class: " + name + " of " + baseClass.getName());
            return false;
        }
    }

    /**
     * 获取{@code baseClass}的名为{@code name}的子类
     * <p>要求：子类和基类必须在同一个包下
     *
     * @param baseClass 基类，如Filter.class
     * @param name 子类名称，如ServerContext
     * @param <T> 泛型
     * @return ServerContextFilter是否存在
     */
    public <T> T getInstanceOfType(Class<T> baseClass, String name) {
        try {
            String className = name + baseClass.getSimpleName();
            String originClassName = baseClass.getName();
            int lastDotIndex = originClassName.lastIndexOf(".");
            String newClassName = originClassName.substring(0, lastDotIndex) + "." + className;
            return (T) getInstanceOfType(Class.forName(newClassName));
        } catch (Exception e) {
            log.error("error while loading Class: " + name + " of " + baseClass.getName());
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(TypeLocator.getInstance().hasExtension(Filter.class, "ServerContext"));
    }
}
