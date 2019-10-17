package com.dc.boynextdoor.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存每个impl类包含的method，防止总是调用反射导致的性能问题
 *
 * @title MethodCache
 * @Description 缓存每个impl类包含的method，防止总是调用反射导致的性能问题
 * @Author donglongcheng01
 * @Date 2019-10-11
 **/
public class MethodCache {

    /**
     * 缓存impl实现类 com.dc.xxxServiceImpl.getXXX --> getXXX(method)
     */
    private static final ConcurrentHashMap<String, Method> implMethodName2Method = new ConcurrentHashMap<>();

    /**
     * 缓存接口类 com.dc.xxxService.getXXX --> getXXX(method)
     */
    private static final ConcurrentHashMap<String, Method> ifaceMethodName2Method = new ConcurrentHashMap<>();


    /**
     * 注册interface和impl类的所有方法，注册的意思就是缓存到本地的ConcurrentHashMap
     *
     * @param iface 接口类
     * @param impl 实现类
     */
    public static void registerMethod(Class<?> iface, Object impl) {
        for (Method method : impl.getClass().getMethods()) {
            method.setAccessible(true);
            implMethodName2Method.putIfAbsent(iface.getName() + "." + method.getName(), method);
        }

        for (Method method : iface.getMethods()) {
            ifaceMethodName2Method.putIfAbsent(iface.getName() + "." + method.getName(), method);
        }

    }

    public static Method getMethod(String serviceName, String methodName) {
        return implMethodName2Method.get(serviceName + "." + methodName);
    }

    public static Method getMethod(Class<?> iface, String methodName) {
        return ifaceMethodName2Method.get(iface.getName() + "." + methodName);
    }

    /**
     * 获取所有 xxxService --> [method1, method2 ...]
     */
    public static Map<String, List<String>> getAllServiceMethodName2Method() {
        if (MapUtils.isEmpty(ifaceMethodName2Method)) {
            return Maps.newConcurrentMap();
        }
        Map<String, List<String>> ret = Maps.newHashMap();
        for (String methodName : ifaceMethodName2Method.keySet()) {
            int index = methodName.lastIndexOf(".");
            if (index > 0 && index < methodName.length()) {
                String iface = methodName.substring(0, index);
                String method = methodName.substring(index + 1);
                List<String> list = ret.computeIfAbsent(iface, k -> Lists.newArrayList());
                list.add(method);
            }
        }
        return ret;
    }

}
