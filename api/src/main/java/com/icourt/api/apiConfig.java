package com.icourt.api;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-02 14:26
 */
public interface apiConfig {
    /**
     * 接口请求超时时间
     */
    int SOCKET_TIME_OUT = 30_000;

    /**
     * 接口响应超时时间  目前服务器压力大
     */
    int SOCKET_RESPONSE_TIME_OUT = 30_000;
}
