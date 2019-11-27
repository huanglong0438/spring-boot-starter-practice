package com.dc.boynextdoor.core.directory;

import com.dc.boynextdoor.cluster.router.Router;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Directory的抽象实现类
 *
 * @title AbstractDirectory
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-20
 **/
public abstract class AbstractDirectory<T> implements Directory<T> {

    protected static Logger LOGGER = LoggerFactory.getLogger(AbstractDirectory.class);

    private final URI serviceUri;

    private final URI directoryUri;

    protected volatile boolean destroyed = false;

    protected volatile List<Router> routers;

    public AbstractDirectory(URI serviceUri, URI directoryUri) {
        Assert.notNull(serviceUri, "service uri is null");
        this.serviceUri = serviceUri;
        this.directoryUri = directoryUri;
        // 初始化routers
        buildRouters();
    }

    /**
     * Directory的【核心】方法，根据request路由出合适的requestor
     *
     * @param request 请求
     * @return 选出的合适的requestor列表
     */
    @Override
    public List<Requestor<T>> list(Request request) {
        if (destroyed) {
            throw new IllegalStateException("Directory already destroyed. uri: " + getUri());
        }
        // 执行子类的doList方法找到这个request的所有requestor
        List<Requestor<T>> requestors = doList(request);
        // 通过router根据uri路由选择requestor
        for (Router router : routers) {
            requestors = router.route(requestors, getUri(), request);
        }
        return requestors;
    }


    private void buildRouters() {
        routers = routers == null ? Lists.newArrayList() : Lists.newArrayList(routers);
        String routerKey = serviceUri.getParameter("router");
        // 目前暂只支持按Tag方式路由
        String defaultRouter = "Tags";
        routers.add(TypeLocator.getInstance().getInstanceOfType(
                Router.class, StringUtils.isEmpty(routerKey) ? defaultRouter : routerKey));
    }

    @Override
    public URI getUri() {
        return serviceUri;
    }

    @Override
    public URI getDIrectoryUri() {
        return directoryUri;
    }

    protected abstract List<Requestor<T>> doList(Request request);

    @Override
    public void destroy() {
        destroyed = true;
    }
}
