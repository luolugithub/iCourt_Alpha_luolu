package com.icourt.alpha.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-05-05 10:38
 */
public class StringUtils {

    /**
     * 将异常转换成字符串
     *
     * @param throwable
     * @return
     */
    public static String throwable2string(Throwable throwable) {
        String throwableString = null;
        try {
            StringWriter mStringWriter = new StringWriter();
            PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
            throwable.printStackTrace(mPrintWriter);
            mPrintWriter.close();
            throwableString = mStringWriter.toString();
        } catch (Exception e) {
        }
        return throwableString;
    }

    /**
     * 比较
     *
     * @param a
     * @param b
     * @param emptyEquals
     * @return
     */
    public static final boolean equals(String a, String b, boolean emptyEquals) {
        if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b)) {
            return emptyEquals;
        }
        return TextUtils.equals(a, b);
    }

    /**
     * 比较
     *
     * @param a
     * @param b
     * @param emptyEquals
     * @return
     */
    public static final boolean equalsIgnoreCase(String a, String b, boolean emptyEquals) {
        if (TextUtils.isEmpty(a) && TextUtils.isEmpty(b)) {
            return emptyEquals;
        }
        if (!TextUtils.isEmpty(a)) {
            return a.equalsIgnoreCase(b);
        }
        return false;
    }

    /**
     * 转化成大写
     *
     * @param list
     * @return
     */
    @NonNull
    public static final List<String> convert2Uppers(List<String> list) {
        List<String> strings = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (!TextUtils.isEmpty(s)) {
                    strings.add(s.toUpperCase());
                } else {
                    strings.add(s);
                }
            }
        }
        return strings;
    }

    /**
     * 转化成小写
     *
     * @param list
     * @return
     */
    @NonNull
    public static final List<String> convert2Lower(List<String> list) {
        List<String> strings = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (!TextUtils.isEmpty(s)) {
                    strings.add(s.toLowerCase());
                } else {
                    strings.add(s);
                }
            }
        }
        return strings;
    }

    /**
     * 查询集合中是否包括该字符串(大小写不区分)
     * 后期优化时间复杂度
     *
     * @param list
     * @param data
     * @return
     */
    public static final boolean containsIgnoreCase(List<String> list, String data) {
        if (!TextUtils.isEmpty(data) && list != null) {
            return convert2Lower(list).contains(data.toLowerCase());
        }
        return false;
    }

}
