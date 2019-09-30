package com.dc.boynextdoor.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * NetUtils
 *
 * @title NetUtils
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-26
 **/
public class NetUtils {

    public static final String ANYHOST = "0.0.0.0";

    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

}
