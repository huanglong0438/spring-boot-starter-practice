package com.dc.boynextdoor.remoting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>相当于跨线程版本的ThreadLocal，
 * <p>ThreadLocal本身是每个线程一个ThreadLocal-->value
 * <p>这里是每个requestId一个ConcurrentHashMap（key --> value）
 *
 * @title RpcLocalContext
 * @Description 相当于跨线程版本的ThreadLocal，ThreadLocal本身是每个线程一个ThreadLocal-->value
 * @Author donglongcheng01
 * @Date 2019-10-11
 **/
public class RpcLocalContext {

    /**
     * requestId --> RpcLocalContext，RpcLocalContext保存着
     */
    private static final ConcurrentHashMap<String, RpcLocalContext> CROSS_THREAD_CONTEXT = new ConcurrentHashMap<>();

    /**
     * <p>以requestId为key，从ConcurrentHashMap里获取RpcLocalContext
     * <p>相当于{@link ThreadLocal#get()} todo ThreadLocal源码走一遍
     */
    public static RpcLocalContext getContext(String requestId) {
        RpcLocalContext context = CROSS_THREAD_CONTEXT.get(requestId);
        if (context == null) {
            RpcLocalContext newContext = new RpcLocalContext();
            context = CROSS_THREAD_CONTEXT.putIfAbsent(requestId, newContext);
            if (context == null) {
                context = newContext;
            }
        }
        return context;
    }

    public static void removeContext(String requestId) {
        CROSS_THREAD_CONTEXT.remove(requestId);
    }

    private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) values.get(key);
    }

    public RpcLocalContext set(String key, Object value) {
        if (value == null) {
            values.remove(key);
        } else {
            values.put(key, value);
        }
        return this;
    }

    public void saveRpcContext() {
        Map<String, Object> store = new HashMap<>();
//        store.putAll(RpcContext.getContext().get());
//        set()
    }

}
