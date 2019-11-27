package com.dc.boynextdoor.registry;

import com.dc.boynextdoor.common.URI;

import java.util.List;

/**
 * 参考：https://segmentfault.com/a/1190000016598069
 * <p>同java的 {@link java.rmi.registry.Registry}，翻译为 登记处
 * <p>提供服务注册与服务获取。即Server端向Registry注册服务，比如地址、端口等一些信息，Client端从Registry获取远程对象的一些信息，如地址、端口等，然后进行远程调用。
 *
 * @title Registry
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-19
 **/
public interface Registry {

    void register(URI uri);

    void unregister(URI uri);

    void subscribe(URI uri, NotifyListener listener);

    void unsubscribe(URI uri, NotifyListener listener);

    List<URI> lookup(URI uri);

    List<URI> localLookup(URI uri);

    void updateLocal(String key, List<URI> requestorUris);

    URI getUri();

    boolean isAvailable();

    void destroy();

    List<String> lookupAll(String username, String passwd);

    List<URI> lookup(String serviceKey, String username, String passwd);

}
