package com.icourt.alpha.utils;

import android.text.TextUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/27
 * version 1.0.0
 */
public class UrlUtils {

    /**
     * 获取url 参数
     * https://alphalawyer.cn/ilaw/api/v2/file/download?sFileId=64880&token=xxx&width=480;
     *
     * @param key
     * @return
     */
    public static String getParam(String key, String url) {
        if (!TextUtils.isEmpty(key)) {
            if (!TextUtils.isEmpty(url)) {
                String urlKey = key + "=";
                int startParamIndex = url.lastIndexOf(urlKey);
                if (startParamIndex > 0) {
                    int end = url.indexOf("&", startParamIndex);
                    if (end > 0) {
                        return url.substring(startParamIndex + urlKey.length(), end);
                    } else {//最后一个
                        return url.substring(startParamIndex + urlKey.length());
                    }
                }
            }
        }
        return null;
    }

    /**
     * 移除某个参数
     *
     * @param key
     * @param url
     * @return
     */
    public static String removeParam(String key, String url) {
        String value = getParam(key, url);
        if (!TextUtils.isEmpty(value)) {
            return url.replace(String.format("%s=%s", key, value), "");
        }
        return url;
    }
}
