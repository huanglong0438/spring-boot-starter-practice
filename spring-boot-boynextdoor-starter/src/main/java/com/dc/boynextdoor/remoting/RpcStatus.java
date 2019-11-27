package com.dc.boynextdoor.remoting;

import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.constants.Constants;
import com.dc.boynextdoor.common.utils.AtomicExpirableInteger;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * RpcStatus
 *
 * @title RpcStatus
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-22
 **/
public class RpcStatus {

    public static final RpcStatus PROVIDER_STATUS = new RpcStatus(Long.MAX_VALUE);

    public static final RpcStatus CONSUMER_STATUS = new RpcStatus(Long.MAX_VALUE);

    private static final ConcurrentMap<String, RpcStatus> SERVICE_STATUS = Maps.newConcurrentMap();

    private static final ConcurrentMap<String, ConcurrentMap<String, RpcStatus>> METHOD_STATUS =
            Maps.newConcurrentMap();

    private final AtomicExpirableInteger expirableFailed;


    public static RpcStatus getStatus(URI uri) {
        String uriKey = uri.toIdentityString();
        RpcStatus status = SERVICE_STATUS.get(uriKey);
        if (status == null) {
            synchronized (RpcStatus.class) {
                if (status == null) { // 用烂了的double check lock
                    RpcStatus newStatus = new RpcStatus(Constants.DEFAULT_STATUS_EXPIRE_TIME);
                    status = SERVICE_STATUS.putIfAbsent(uriKey, newStatus);
                    if (status == null) {
                        status = newStatus;
                    }
                }
            }
        }
        return status;
    }

    public static void cleaRpcStatus(URI uri) {
        String uriKey = uri.toIdentityString();
        synchronized (RpcStatus.class) {
            // 同步清除掉该uri的service和method数据记录
            SERVICE_STATUS.remove(uriKey);
            METHOD_STATUS.remove(uriKey);
        }
    }

    private RpcStatus(long expireTime) {
        expirableFailed = new AtomicExpirableInteger(expireTime);
    }

    public int getExpireFailed() {
        return expirableFailed.get();
    }


}
