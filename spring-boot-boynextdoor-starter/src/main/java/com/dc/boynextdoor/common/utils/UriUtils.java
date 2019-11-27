package com.dc.boynextdoor.common.utils;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * UriUtils
 *
 * @title UriUtils
 * @Description
 * @Author donglongcheng01
 * @Date 2019-11-21
 **/
public final class UriUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(UriUtils.class);

    private UriUtils() {
    }

    /**
     * URI List to String
     *
     * @param list
     * @return
     */
    public static String listUriToString(List<URI> list) {
        try {
            if (list == null || list.isEmpty()) {
                return "[]";
            }
            String ret = "";
            for (URI uri : list) {
                if (uri != null) {
                    ret += "," + uri.getAddress();
                }
            }
            if (StringUtils.isBlank(ret)) {
                return "[]";
            }
            return "[" + ret.substring(1) + "]";
        } catch (Exception e) {
            LOGGER.error("listUriToString error, Don't worry,it do not affect business!", e);
            return "listUriToString error";
        }
    }

    /**
     * Requestor list to String
     *
     * @param requestors
     * @return
     */
    public static <S> String listRequestorToString(List<Requestor<S>> requestors) {
        try {
            if (requestors == null || requestors.isEmpty()) {
                return "[]";
            }
            List<URI> list = new ArrayList<URI>(requestors.size());
            for (Requestor<S> requestor : requestors) {
                list.add(requestor.getUri());
            }
            String str = UriUtils.listUriToString(list);
            return str;
        } catch (Exception e) {
            LOGGER.error("listRequestorToString error, Don't worry,it do not affect business!", e);
            return "listRequestorToString error";
        }
    }


}
