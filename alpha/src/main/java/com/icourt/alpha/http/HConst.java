package com.icourt.alpha.http;


import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.utils.SpUtils;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-04-15 14:14
 */
public class HConst {

    public static String cookie = "";

    /**
     * 网络缓存最大值
     */
    public static final int CACHE_MAXSIZE = 1024 * 1024 * 30;

    /**
     * 网络缓存保存时间
     */
    public static final int TIME_CACHE = 60 * 60; // 一小时

    /**
     * 接口请求超时时间
     */
    public static final int SOCKET_TIME_OUT = 30_000;

    /**
     * 接口响应超时时间  目前服务器压力大
     */
    public static final int SOCKET_RESPONSE_TIME_OUT = 30_000;


    /**
     * 允许http日志
     */
    public static boolean HTTP_LOG_ENABLE = BuildConfig.IS_DEBUG;


    // Android 渠道
    public static final String OS_TYPE = "1";


    public static final String WX_APPID = "wxa3502404446fe64e";//微信appid
    public static final String WX_APPSECRET = "609cb731d035eeea37431b9d11115078";//微信AppSecret


}
