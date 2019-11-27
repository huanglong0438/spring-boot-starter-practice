package com.dc.boynextdoor.registry;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * todo dlc
 *
 * @title ZookeeperRegistry
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-04
 **/
public class ZookeeperRegistry extends AbstractRegistry {

    @Override
    public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
        return null;
    }

    @Override
    public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

    }

    @Override
    public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

    }

    @Override
    public void rebind(String name, Remote obj) throws RemoteException, AccessException {

    }

    @Override
    public String[] list() throws RemoteException, AccessException {
        return new String[0];
    }
}
