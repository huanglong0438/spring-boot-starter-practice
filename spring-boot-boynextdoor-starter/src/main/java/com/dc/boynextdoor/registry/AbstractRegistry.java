package com.dc.boynextdoor.registry;

import com.dc.boynextdoor.common.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * {@link Registry} 的抽象实现类，为serviceReference登记listener，然后启动新线程执行注册的具体实现（写zk）
 *
 * @title AbstractRegistry
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-04
 **/
public abstract class AbstractRegistry implements Registry {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegistry.class);

    private final ConcurrentMap<String, Set<NotifyListener>> subscribed = new ConcurrentHashMap<>();

    private final Executor registryExecutor =
            Executors.newFixedThreadPool(4,
                    new CustomizableDaemonThreadFactory("registryAsyncWorker-"));

    private final ConcurrentMap<String, Boolean> subscribedRunStatusMap = new ConcurrentHashMap<>();

    /**
     * 根据uri把服务的listener注册上去，例如zookeeper实现类，listener会监听zk事件，实时更新本地缓存的server ip
     *
     * @param uri      就是一个serviceReference
     * @param listener 一个监听器，如zk事件的listener
     */
    @Override
    public void subscribe(URI uri, NotifyListener listener) {
        Assert.notNull(uri, "subscribe uri is null");
        Assert.notNull(listener, "subscribe listener is null");

        String fullString = uri.toFullString();
        Set<NotifyListener> listeners = subscribed.get(fullString);
        if (listeners == null) {
            // 没有就初始化，写时拷贝技术，适用于读多写少：https://www.jianshu.com/p/afc6e0ae08b0
            subscribed.putIfAbsent(fullString, new CopyOnWriteArraySet<>());
            listeners = subscribed.get(fullString);
        }
        listeners.add(listener);

        // 单独启线程后台注册listener
        registryExecutor.execute(() -> {
            Boolean running = subscribedRunStatusMap.get(fullString);
            if (running == null || !running) {
                // 【注意】subscribedRunStatusMap.get和put之间存在短时间线程不安全，因为影响不大，容忍了
                // 只有在两个serviceReference的uri一样的情况下有一定几率会撞车，影响就是重新注册了一遍
                subscribedRunStatusMap.put(fullString, Boolean.TRUE);
                // doXXX，子类具体的实现方法，
                // 这个方法可能会hang住，所以才通过running来判断，如果已经在running则不再执行，防止了线程泄漏
                doSubscribe(uri, listener);
                LOGGER.info("reference subscribe success. uri");
                subscribedRunStatusMap.put(fullString, Boolean.FALSE);
            } else {
                LOGGER.warn("has another thread subscribe this uri");
            }
        });
    }

    protected abstract void doSubscribe(URI uri, NotifyListener listener);

    static class CustomizableDaemonThreadFactory extends CustomizableThreadFactory {
        public CustomizableDaemonThreadFactory(String threadNamePrefix) {
            super(threadNamePrefix);
            super.setDaemon(true);
        }
    }
}
