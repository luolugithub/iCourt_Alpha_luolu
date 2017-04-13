package com.icourt.alpha.utils;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/13
 * version 1.0.0
 */
public class IMUtils {

    /**
     * 是否是图片
     *
     * @param url
     * @return
     */
    public static final boolean isPIC(String url) {
        if (!TextUtils.isEmpty(url)) {
            int pointIndex = url.lastIndexOf(".");
            if (pointIndex >= 0 && pointIndex < url.length()) {
                String fileSuffix = url.substring(pointIndex, url.length());
                return getPICSuffixs().contains(fileSuffix);
            }
        }
        return false;
    }

    /**
     * 图片后缀
     *
     * @return
     */
    public static final List<String> getPICSuffixs() {
        return Arrays.asList(".png", ".jpg", ".gif", ".jpeg", ".PNG", ".JPG", ".GIF", ".JPEG");
    }
}
