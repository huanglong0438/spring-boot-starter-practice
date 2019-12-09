package com.dc.boynextdoor.registry;

import com.dc.boynextdoor.common.URI;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * todo dlc 补充zookeeper的实现细节
 *
 * @title ZookeeperRegistry
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-04
 **/
public class ZookeeperRegistry extends AbstractRegistry {

    public ZookeeperRegistry(URI uri) {
    }

    @Override
    protected void doSubscribe(URI uri, NotifyListener listener) {

    }

    @Override
    public void register(URI uri) {

    }

    @Override
    public void unregister(URI uri) {

    }

    @Override
    public void unsubscribe(URI uri, NotifyListener listener) {

    }

    @Override
    public List<URI> lookup(URI uri) {
        return null;
    }

    @Override
    public List<URI> localLookup(URI uri) {
        return null;
    }

    @Override
    public void updateLocal(String key, List<URI> requestorUris) {

    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public List<String> lookupAll(String username, String passwd) {
        return null;
    }

    @Override
    public List<URI> lookup(String serviceKey, String username, String passwd) {
        return null;
    }
}
