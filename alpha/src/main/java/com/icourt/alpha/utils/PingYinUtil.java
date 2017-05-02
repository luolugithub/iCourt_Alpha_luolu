package com.icourt.alpha.utils;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pinyin4android.PinyinUtil;

/**
 * Description
 * 汉字处理工具类 结合pinyin4j.jar
 * Company  Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/7/10
 * version
 */
public class PingYinUtil {
    /**
     *
     汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
     数字：[0x30,0x39]（或十进制[48, 57]）
     小写字母：[0x61,0x7a]（或十进制[97, 122]）
     大写字母：[0x41,0x5a]（或十进制[65, 90]）
     */

    /**
     * 汉字转换成拼音
     *
     * @param context
     * @param hanzi
     * @return
     */
    @Nullable
    @CheckResult
    public static final String getPingYin(Context context, String hanzi) {
        if (context != null && !TextUtils.isEmpty(hanzi)) {
            return PinyinUtil.toPinyin(context, hanzi);
        }
        return null;
    }

    /**
     * 提取汉字的第一个字母
     *
     * @param hanzi
     * @return
     */
    @Nullable
    @CheckResult
    public static final char getFirstUpperLetter(Context context, String hanzi) {
        if (context != null && !TextUtils.isEmpty(hanzi)) {
            String pinyin = getPingYin(context, hanzi);
            if (!TextUtils.isEmpty(pinyin)) {
                return pinyin.toUpperCase().charAt(0);
            }
        }
        return 0;
    }

    /**
     * 提取汉字的第一个字母
     *
     * @param hanzi
     * @return
     */
    @Nullable
    @CheckResult
    public static final char getIndexUpperLetter(Context context, String hanzi, int index) {
        String pinyin = getPingYin(context, hanzi);
        if (!TextUtils.isEmpty(pinyin) && index < pinyin.length()) {
            return pinyin.toUpperCase().charAt(index);
        }
        return 0;
    }

    /**
     * 是否是大写字母
     *
     * @param c
     * @return
     */
    public static final boolean isUpperLetter(char c) {
        return c >= 65 && c <= 90;
    }


    /**
     * 在大写字母中的index
     * [65, 90]
     *
     * @param upperLetter 大写字母
     * @return
     */
    public static final int indexOfUpperUpperLetter(char upperLetter) {
        if (isUpperLetter(upperLetter)) {
            return upperLetter % 65;
        }
        return -1;
    }

}
