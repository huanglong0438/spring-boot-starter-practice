package com.dc.boynextdoor.core;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;
import org.springframework.util.Assert;

/**
 * Requestor的抽象类
 *
 * @title AbstractRequestor
 * @Description Requestor的抽象类，定义了Requestor的一些基本的行为，标准的设计模式
 * @Author donglongcheng01
 * @Date 2019-10-10
 **/
public abstract class AbstractRequestor<T> implements Requestor<T> {

    /**
     * 待处理的接口
     */
    private final Class<T> iface;

    /**
     * 待处理的uri
     */
    private final URI uri;

    private volatile boolean available = true;

    private volatile boolean destroyed = false;

    public AbstractRequestor(Class<T> iface, URI uri) {
        Assert.notNull(iface, "service interface cannot be null");
        Assert.notNull(uri, "service uri cannnot be null");
        this.iface = iface;
        this.uri = uri;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public Class<T> getInterface() {
        return iface;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public void destroy() {
        if (isDestroyed()) {
            return;
        }
        destroyed = true;
        setAvailable(false);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String toString() {
        return "AbstractRequestor{" +
                "iface=" + iface +
                ", uri=" + uri +
                '}';
    }
}
