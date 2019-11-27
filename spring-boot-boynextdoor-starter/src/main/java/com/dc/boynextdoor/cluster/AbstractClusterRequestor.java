package com.dc.boynextdoor.cluster;

import com.dc.boynextdoor.cluster.loadbalancer.LoadBalancer;
import com.dc.boynextdoor.cluster.loadbalancer.RoundRobinLoadBalancer;
import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.core.directory.Directory;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * AbstractClusterRequestor
 *
 * @title AbstractClusterRequestor
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-08
 **/
public abstract class AbstractClusterRequestor<T> implements Requestor<T> {

    protected final Directory<T> directory;

    private volatile boolean destroyed = false;

    private LoadBalancer reselectLaodBalancer;

    public AbstractClusterRequestor(Directory<T> directory) {
        this(directory, directory.getUri());
    }

    public AbstractClusterRequestor(Directory<T> directory, URI uri) {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        this.directory = directory;
        this.reselectLaodBalancer = TypeLocator.getInstance().getInstanceOfType(RoundRobinLoadBalancer.class);
    }

    public Class<T> getInterface() {
        return directory.getInterface();
    }

    public URI getUri() {
        return directory.getUri();
    }

    public boolean isAvailable() {
        return directory.isAvailable();
    }

    public void destroy() {
        directory.destroy();
        destroyed = true;
    }

    /**
     * 从{@code requestors}中根据负载均衡策略{@code loadBalancer}选择合适的requestor
     *
     * @param loadBalancer 负载均衡策略
     * @param request      Rpc请求
     * @param requestors   各种requestor处理器
     * @param selected     已选择过的，用来进行负载均衡策略
     * @return 选中的requestor
     */
    protected Requestor<T> select(LoadBalancer loadBalancer, Request request,
                                  List<Requestor<T>> requestors, List<Requestor<T>> selected) {
        if (CollectionUtils.isEmpty(requestors)) {
            return null;
        }
        if (requestors.size() == 1) {
            return requestors.get(0);
        }
        // 两个requestor，则直接走轮询
        if (requestors.size() == 2 && CollectionUtils.isNotEmpty(selected)) {
            return selected.get(0) == requestors.get(0) ? requestors.get(1) : requestors.get(0);
        }
        // 通过loadBalancer进行负载均衡
        return loadBalancer.select(requestors, request);
    }

    protected void checkWhetherDestoryed() {

        if (destroyed) {
            throw new IllegalStateException("requestor for " + getInterface() + " is destroyed, cannot be invoked");
        }
    }

    @Override
    public String toString() {
        return getInterface() + " -> " + getUri().toString();
    }

    /**
     * 【重点】传说中的no provider available就是出自这里
     * <p>检查是否有requestor可以处理
     *
     * @param requestors 处理请求的requestor，每个requestor就是一个provider
     * @param request    RpcRequest
     */
    protected void checkRequestors(List<Requestor<T>> requestors, Request request) {
        if (CollectionUtils.isEmpty(requestors)) {
            throw new IllegalStateException("Failed to invoke the method "
                    + request.getMethodName() + " in the service "
                    + getInterface().getSimpleName() + ". No provider available");
        }
    }

    /**
     * 抽象类的request实现，封装了获取provider和负载均衡的抽象操作
     *
     * @param request 请求
     * @param callback 返回结果的回调
     */
    public void request(Request request, Callback<Response> callback) {
        checkWhetherDestoryed();
        // directory就是zk，从zk上获取到所有的provider
        List<Requestor<T>> requestors = directory.list(request);
        LoadBalancer loadBalancer = getLoadBalancer(request, requestors);
        doRequest(request, requestors, loadBalancer, callback);

    }

    /**
     * 交给子类实现requestor的实际处理细节
     */
    protected abstract void doRequest(
            Request request, List<Requestor<T>> requestors, LoadBalancer loadBalancer, Callback<Response> callback);

    private LoadBalancer getLoadBalancer(Request request, List<Requestor<T>> requestors) {
        // 简单版，只支持RoundRobin轮询负载均衡模式
        return TypeLocator.getInstance().getInstanceOfType(RoundRobinLoadBalancer.class);
    }

}
