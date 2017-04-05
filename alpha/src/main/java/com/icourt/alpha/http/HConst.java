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

    //开发服务器
    public static final String BASE_DEBUG_URL = "https://dev.alphalawyer.cn/";

    //测试服务器
    public static final String BASE_TEST_URL = "https://test.alphalawyer.cn/";

    //正式服务器
    public static final String BASE_PREVIEW_URL = "https://pre.alphalawyer.cn／";

    //正式服务器
    public static final String BASE_RELEASE_URL = "https://alphalawyer.cn/";


    public static String BASE_HTTP = BASE_DEBUG_URL;

    /**
     * 动态切换 url
     * 有效期 是同一个版本
     */
    public static final String DYNAMIC_SWITCHING_BASE_URL = String.format("%s_%s", BuildConfig.VERSION_NAME, "baseUrl");

    static {
        switch (BuildConfig.URL_CONFIG) {
            case 0:
                BASE_HTTP = BASE_DEBUG_URL;
                break;
            case 1:
                BASE_HTTP = BASE_TEST_URL;
                break;
            case 2:
                BASE_HTTP = BASE_PREVIEW_URL;
                break;
            case 3:
                BASE_HTTP = BASE_RELEASE_URL;
                break;
        }
        /*try {
            //开发者动态改变的地址
            String userBaseUrl = SpUtils.getInstance().getStringData(DYNAMIC_SWITCHING_BASE_URL, "");
            if (TextUtils.equals(userBaseUrl, BASE_DEBUG_URL)
                    || TextUtils.equals(userBaseUrl, BASE_PREVIEW_URL)
                    || TextUtils.equals(userBaseUrl, BASE_RELEASE_URL)) {
                BASE_HTTP = userBaseUrl;
                if (TextUtils.equals(userBaseUrl, BASE_DEBUG_URL)) {
                    BASE_SHARE_HTTP = BASE_DEBUG_URL;
                } else if (TextUtils.equals(userBaseUrl, BASE_PREVIEW_URL)) {
                    BASE_SHARE_HTTP = BASE_PREVIEW_URL;
                } else if (TextUtils.equals(userBaseUrl, BASE_RELEASE_URL)) {
                    BASE_SHARE_HTTP = BASE_RELEASE_SHARE_URL;
                }
            }
        } catch (Exception e) {
        }*/


        //BASE_HTTP = "http://next.tcmmooc.com/";
    }


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
