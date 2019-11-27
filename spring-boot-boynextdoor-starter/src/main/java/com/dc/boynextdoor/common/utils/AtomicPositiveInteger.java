package com.dc.boynextdoor.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicPositiveInteger，封装了 {@link AtomicInteger}，溢出最大值后会复位，因为不复位的话round robin时取余会有问题
 *
 * @title AtomicPositiveInteger
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-12
 **/
public class AtomicPositiveInteger extends Number {

    private final AtomicInteger i;

    public AtomicPositiveInteger() {
        i = new AtomicInteger();
    }

    public AtomicPositiveInteger(int initialValue) {
        i = new AtomicInteger(initialValue);
    }

    /**
     * 用溢出后复位的逻辑替代 {@link AtomicInteger#getAndIncrement()}
     */
    public final int getAndIncrement() {
        // CAS的精髓，死循环等设置成功
        for (; ; ) {
            int current = i.get();
            // 用这句逻辑和compareAndSet替代AtomicInteger的i.getAndIncrement()
            int next = current >= Integer.MAX_VALUE ? 0 : current + 1;
            if (i.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    public final int getAndDecrement() {
        for (; ; ) {
            int current = i.get();
            int next = current <= 0 ? Integer.MAX_VALUE : current - 1;
            if (i.compareAndSet(current, next)) {
                return current;
            }
        }
    }

    @Override
    public int intValue() {
        return i.intValue();
    }

    @Override
    public long longValue() {
        return i.longValue();
    }

    @Override
    public float floatValue() {
        return i.floatValue();
    }

    @Override
    public double doubleValue() {
        return i.doubleValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + i.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AtomicPositiveInteger other = (AtomicPositiveInteger) obj;
        if (!i.equals(other.i)) {
            return false;
        }
        return true;
    }


}
