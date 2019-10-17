package com.dc.boynextdoor.remoting;

/**
 * RpcContext
 *
 * @title RpcContext
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-12
 **/
public class RpcContext {

    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>(){
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };



}
