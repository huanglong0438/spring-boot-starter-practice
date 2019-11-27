package com.dc.boynextdoor.registry;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;

/**
 * NotifyListener
 *
 * @title NotifyListener
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-06
 **/
public interface NotifyListener extends IZkChildListener, IZkDataListener, IZkStateListener {
}
