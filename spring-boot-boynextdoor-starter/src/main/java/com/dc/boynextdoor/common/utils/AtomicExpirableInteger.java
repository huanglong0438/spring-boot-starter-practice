package com.dc.boynextdoor.common.utils;

import java.util.concurrent.atomic.AtomicReference;

/**
 * AtomicExpirableInteger，会过期的AtomicInteger，如果设置的时候发现已经距离上次记录的时间戳过去了很久（过期了），则从头开始计数
 *
 * @title AtomicExpirableInteger
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-25
 **/
public class AtomicExpirableInteger extends Number {

    /**
     * 最大存活时间（单位：ms）
     */
    private final long maxLifeTime;

    /**
     * 初始值，默认0
     */
    private final int initialValue;

    private final AtomicReference<ValueTimeStamp> atomicReference;

    private static class ValueTimeStamp {

        private final int value;

        private final long timestamp;

        public ValueTimeStamp(int value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    public AtomicExpirableInteger(long maxLifeTime) {
        this(0, maxLifeTime);
    }

    public AtomicExpirableInteger(int initialValue, long maxLifeTime) {
        long initialTimeStamp = System.currentTimeMillis();
        this.maxLifeTime = maxLifeTime;
        this.initialValue = initialValue;
        this.atomicReference = new AtomicReference<ValueTimeStamp>(new ValueTimeStamp(initialValue, initialTimeStamp));
    }

    public int get() {
        return addAndGet(0);
    }

    private int addAndGet(int delta) {
        // 这个while(true)本身也是CAS操作的一环
        while (true) {
            int current = atomicReference.get().value;
            long recentTimeStamp = atomicReference.get().timestamp;
            int next;
            long newTimeStamp = recentTimeStamp;
            long recentTimeSpan = System.currentTimeMillis() - recentTimeStamp;
            if (recentTimeSpan > maxLifeTime) {
                // 当前时间 - 最近的一次访问时间 > 最大存活时间，说明过期了
                // 从initialValue，从头开始计数
                next = initialValue + delta;
                newTimeStamp = System.currentTimeMillis();
            } else {
                // 没过期，继续在current的基础上累加
                next = current + delta;
            }
            // CAS操作设置
            if (compareAndSet(current, next, recentTimeStamp, newTimeStamp)) {
                return next;
            }
        }
    }

    private boolean compareAndSet(int expectedReference, int newReference, long expectedTimeStamp, long newTimeStamp) {
        ValueTimeStamp current = atomicReference.get();
        return expectedReference == current.value
                && expectedTimeStamp == current.timestamp
                && ((newReference == current.value && newTimeStamp == current.timestamp)
                || atomicReference.compareAndSet(current, new ValueTimeStamp(newReference, newTimeStamp)));
    }


    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }
}
