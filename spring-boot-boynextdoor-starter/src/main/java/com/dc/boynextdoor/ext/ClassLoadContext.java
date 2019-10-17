package com.dc.boynextdoor.ext;

/**
 * ClassLoadContext
 *
 * @title ClassLoadContext
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-04
 **/
public class ClassLoadContext {

    private final Class mCaller;

    public final Class getCallerClass() {
        return mCaller;
    }

    ClassLoadContext(Class mCaller) {
        this.mCaller = mCaller;
    }
}
