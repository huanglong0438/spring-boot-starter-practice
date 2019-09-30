package com.dc.boynextdoor.common.constants;

import java.util.regex.Pattern;

/**
 * Constants
 *
 * @title Constants
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-26
 **/
public final class Constants {

    /**
     * 全局采用逗号分隔
     */
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    /**
     * URI里指定默认值,比如有key,那么DEFAULT_KEY_PREFIX+key指定的值就是该key的默认值
     */
    public static final String DEFAULT_KEY_PREFIX = "default.";

    /**
     * 服务分组key
     */
    public static final String GROUP_KEY = "group";

    /**
     * URI的路径key
     */
    public static final String PATH_KEY = "path";

    /**
     * 服务接口key
     */
    public static final String INTERFACE_KEY = "interface";

    /**
     * 服务版本key
     */
    public static final String VERSION_KEY = "version";

    /**
     * 注册中心key
     */
    public static final String REGISTRY_KEY = "registry";
    /**
     * rpc调用的超时时间key
     */
    public static final String CALL_TIMEOUT_KEY = "call.timeout";

    /**
     * 默认rpc调用超时5分钟
     */
    public static final long DEFAULT_CALL_TIMEOUT = 5 * 60 * 1000;

    /**
     * 服务简单key
     */
    public static final String INTERFACE_SIMPLE_KEY = "interface.simple";

    private Constants() {
    }


}
