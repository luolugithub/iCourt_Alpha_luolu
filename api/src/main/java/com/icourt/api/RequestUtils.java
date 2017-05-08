package com.icourt.api;

import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class RequestUtils {

    /**
     * 构建文本请求体
     *
     * @param text
     * @return
     */
    public static RequestBody createTextBody(String text) {
        return RequestBody
                .create(MediaType.parse("text/plain"),
                        TextUtils.isEmpty(text) ? "" : text);
    }

    /**
     * 构建图片请求体
     *
     * @param file
     * @return
     */
    public static RequestBody createImgBody(File file) {
        if (file != null && file.exists()) {
            return RequestBody.create(MediaType.parse("image/*"), file);
        }
        return null;
    }

    /**
     * 构建媒体流请求体
     *
     * @param file
     * @return
     */
    public static RequestBody createStreamBody(File file) {
        if (file != null && file.exists()) {
            return RequestBody.create(MediaType.parse("application/octet-stream"), file);
        }
        return null;
    }


    /**
     * 构建表单请求体
     *
     * @param file
     * @return
     */
    public static RequestBody createFormBody(File file) {
        if (file != null && file.exists()) {
            return RequestBody.create(MediaType.parse("multipart/form-data"), file);
        }
        return null;
    }

    /**
     * 构建json请求体
     *
     * @param json
     * @return
     */
    public static RequestBody createJsonBody(String json) {
        return RequestBody
                .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                        TextUtils.isEmpty(json) ? "" : json);
    }
}
