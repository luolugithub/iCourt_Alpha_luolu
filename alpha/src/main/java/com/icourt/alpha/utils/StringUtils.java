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
     * 判断是否为空 去掉 空格
     *
     * @param text
     * @return
     */
    public static final boolean isEmpty(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return true;
        }
        return text.toString().trim().length() <= 0;
    }

    /**
     * 统计到字符长度
     *
     * @param text
     * @return
     */
    public static final int length(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        return text.toString().length();
    }

    /**
     * 是否超过指定长度
     *
     * @param text
     * @param len
     * @return
     */
    public static final boolean isOverLength(CharSequence text, int len) {
        return length(text) > len;
    }

    /**
     * 获取截取后 并添加省略号的文本
     *
     * @param text
     * @param maxNum
     * @return
     */
    public static String getEllipsizeText(String text, int maxNum) {
        if (!TextUtils.isEmpty(text)
                && maxNum > 0
                && text.length() > maxNum) {
            return text.substring(0, maxNum).concat("...");
        }
        return text;
    }

    /**
     * 获取小写
     *
     * @param text
     * @return
     */
    public static String toLowerCase(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.toLowerCase();
        }
        return text;
    }

    /**
     * 获取大写
     *
     * @param text
     * @return
     */
    public static String toUpperCase(String text) {
        if (!TextUtils.isEmpty(text)) {
            return text.toUpperCase();
        }
        return text;
    }

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
     * 是否包含
     *
     * @param a
     * @param key
     * @return
     */
    public static final boolean containsIgnoreCase(String a, String key) {
        if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(key)) {
            return a.toLowerCase().contains(key.toLowerCase());
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
