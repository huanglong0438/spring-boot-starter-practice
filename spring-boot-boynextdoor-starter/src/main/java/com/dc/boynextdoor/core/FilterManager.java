package com.dc.boynextdoor.core;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.remoting.Filter;
import com.dc.boynextdoor.remoting.core.FilterCallback;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * FilterManager
 *
 * @title FilterManager
 * @Description 用来生成处理请求的责任链，把filter封装成requestor（todo 感觉没必要，待优化）
 * @Author donglongcheng01
 * @Date 2019-10-14
 **/
@Slf4j
public class FilterManager {

    /**
     * <pre>
     * filter从后往前构造链,
     *  bndtest -> server(原始requestor)
     *  remoteaccess -> bndtest
     *  ...
     *
     * 最后的结果是 servercontext -> executelimit -> servermonitor -> remoteaccess -> bndtest -> server
     * </pre>
     *
     * @param requestor      serverRequestor
     * @param reqeustFilters 请求中要求的filter
     * @param defaultFilters 默认要过的filter
     * @param <T>            泛型
     * @return requestor链的头部
     */
    public static <T> Requestor<T> buildReuqestorChain(
            Requestor<T> requestor, String reqeustFilters, String defaultFilters) {
        // 所有需要过的filter
        List<String> names =
                Lists.newArrayList("ServerContext,ExecuteLimit,ServerMonitor,RemoteAccess,BndTest"
                        .split(","));
        Requestor<T> last = requestor;
        List<Filter> filters = new ArrayList<>();
        for (String name : names) {
            // 先判断该filter是否存在
            if (TypeLocator.getInstance().hasExtension(Filter.class, name)) {
                Filter filter = TypeLocator.getInstance().getInstanceOfType(Filter.class, name);
                // 把filter都加进来
                filters.add(filter);
            }
        }
        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                // [Java 局部内部类访问局部变量为什么必须加final关键字](https://blog.csdn.net/dazhaoDai/article/details/83097017)
                final Filter filter = filters.get(i);
                final Requestor<T> next = last;
                last = generateRequestor(filter, requestor, next);
            }
        }
        return last;
    }

    /**
     * 根据filter构造requestor，requestor的作用是封装了filter（感觉没啥用），filter在执行完后会next requestor继续链式走下去
     *
     * @param filter    当前需要被封装的filter
     * @param requestor 就是原始的ServerRequestor
     * @param next      这个requestor执行完后需要调用的下一个requestor
     * @param <T>       泛型
     * @return 构造好的当前requestor
     */
    private static <T> Requestor<T> generateRequestor(Filter filter, Requestor<T> requestor, Requestor<T> next) {
        return new Requestor<T>() {
            @Override
            public Class<T> getInterface() {
                return requestor.getInterface();
            }

            @Override
            public URI getUri() {
                return requestor.getUri();
            }

            @Override
            public boolean isAvailable() {
                return requestor.isAvailable();
            }

            @Override
            public void destroy() {
                requestor.destroy();
            }

            @Override
            public String toString() {
                return requestor.toString();
            }

            @Override
            public void request(Request request, Callback<Response> callback) throws IllegalStateException {
                filter.request(next, request, new FilterCallback(callback, filter));
            }
        };
    }

}
