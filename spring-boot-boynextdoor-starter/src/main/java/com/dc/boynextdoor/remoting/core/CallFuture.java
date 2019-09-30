package com.dc.boynextdoor.remoting.core;

import com.dc.boynextdoor.common.Callback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 一个单独的线程去等待Rpc接口的处理结果，通过latch来等待结果的通知
 *
 * @title CallFuture
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
public class CallFuture<T> implements Future<T>, Callback<T> {
    private final CountDownLatch latch = new CountDownLatch(1);

    private final Callback<T> chainedCallback;

    private T result = null;

    private Throwable error = null;

    public CallFuture() {
        this(null);
    }

    /**
     * Creates a CallFuture with a chained Callback which will be invoked when
     * this CallFuture's Callback methods are invoked.
     *
     * @param chainedCallback the chained Callback to set.
     */
    public CallFuture(Callback<T> chainedCallback) {
        this.chainedCallback = chainedCallback;
    }

    /**
     * Sets the RPC response, and unblocks all threads waiting on {@link #get()}
     * or {@link #get(long, TimeUnit)}.
     *
     * @param result the RPC result to set.
     */
    public void handleResult(T result) {
        this.result = result;
        latch.countDown();
        if (chainedCallback != null) {
            chainedCallback.handleResult(result);
        }
    }

    /**
     * Sets an error thrown during RPC execution, and unblocks all threads
     * waiting on {@link #get()} or {@link #get(long, TimeUnit)}.
     *
     * @param error the RPC error to set.
     */
    public void handleError(Throwable error) {
        this.error = error;
        latch.countDown();
        if (chainedCallback != null) {
            chainedCallback.handleError(error);
        }
    }

    /**
     * Gets the value of the RPC result without blocking. Using {@link #get()}
     * or {@link #get(long, TimeUnit)} is usually preferred because these
     * methods block until the result is available or an error occurs.
     *
     * @return the value of the response, or null if no result was returned or
     * the RPC has not yet completed.
     */
    public T getResult() {
        return result;
    }

    /**
     * Gets the error that was thrown during RPC execution. Does not block.
     * Either {@link #get()} or {@link #get(long, TimeUnit)} should be called
     * first because these methods block until the RPC has completed.
     *
     * @return the RPC error that was thrown, or null if no error has occurred
     * or if the RPC has not yet completed.
     */
    public Throwable getError() {
        return error;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public T get() throws InterruptedException, IllegalStateException {
        latch.await();
        if (error != null) {
            if (error instanceof IllegalStateException)
                throw (IllegalStateException) error;
            else
                throw new IllegalStateException("execution exception", error);
        }
        return result;
    }

    public T get(long timeout, TimeUnit unit) throws IllegalStateException {
        try {
            if (latch.await(timeout, unit)) {
                if (error != null) {
                    if (error instanceof IllegalStateException)
                        throw (IllegalStateException) error;
                    else
                        throw new IllegalStateException("call future get exception", error);
                }
                return result;
            } else {
                throw new IllegalStateException("async get time out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("call future is interuptted", e);
        }
    }

    /**
     * Waits for the CallFuture to complete without returning the result.
     *
     * @throws InterruptedException if interrupted.
     */
    public void await() throws InterruptedException {
        latch.await();
    }

    /**
     * Waits for the CallFuture to complete without returning the result.
     *
     * @param timeout the maximum time to wait.
     * @param unit the time unit of the timeout argument.
     * @throws InterruptedException if interrupted.
     * @throws TimeoutException if the wait timed out.
     */
    public void await(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (!latch.await(timeout, unit)) {
            throw new TimeoutException();
        }
    }

    public boolean isDone() {
        return latch.getCount() <= 0;
    }
}
