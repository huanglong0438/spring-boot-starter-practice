package com.dc.boynextdoor.registry;

import com.dc.boynextdoor.common.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AbstractRegistryFactory
 *
 * @title AbstractRegistryFactory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-19
 **/
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegistryFactory.class);

    private static final ConcurrentMap<String, Registry> REGISTRIES = new ConcurrentHashMap<>();

    public static synchronized void destroyAll() {
        LOGGER.info("Close all registries " + getRegistries());
        for (Registry registry : getRegistries()) {
            try {
                registry.destroy();
            } catch (Throwable e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        REGISTRIES.clear();
    }

    private static Collection<Registry> getRegistries() {
        return Collections.unmodifiableCollection(REGISTRIES.values());
    }

    /**
     * 根据uri获取Registry，通过【double check locking】确保多线程下的安全性
     */
    @Override
    public Registry getRegistry(URI uri) {
        String key = uri.toIdentityString();
        Registry registry = REGISTRIES.get(key);
        if (registry == null) {
            synchronized (this) {
                registry = REGISTRIES.get(key);
                if (registry == null) {
                    Registry newRegistry = createRegistry(uri);
                    registry = REGISTRIES.putIfAbsent(key, newRegistry);
                    if (registry == null) {
                        registry = newRegistry;
                    }
                }
            }
        }
        return registry;
    }

    protected abstract Registry createRegistry(URI uri);

}
