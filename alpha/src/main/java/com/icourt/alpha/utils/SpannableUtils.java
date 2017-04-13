package com.icourt.alpha.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-11-18 15:29
 * 处理span的工具类
 */

public class SpannableUtils {
    private SpannableUtils() {
    }


    /**
     * 设置文本中某些文字的颜色
     *
     * @param originalText    原文
     * @param ma              指定着色文本的匹配器
     * @param foregroundColor 制定着色文本的颜色
     * @return
     * @see ForegroundColorSpan
     */
    public static SpannableString getTextForegroundColorSpan(
            CharSequence originalText,
            Matcher ma,
            int foregroundColor) {
        if (TextUtils.isEmpty(originalText)) {
            return null;
        }
        if (ma == null || !ma.find()) {
            return new SpannableString(originalText);
        }
        SpannableString spannableString = new SpannableString(originalText);
        do {
            try {
                //循环改变所有的颜色
                spannableString.setSpan(
                        new ForegroundColorSpan(foregroundColor),
                        ma.start(),
                        ma.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                break;
            }
        } while (ma.find());
        return spannableString;
    }

    /**
     * 为指定范围(start - end eg:1-5)的文本着色
     *
     * @param originalText    原文
     * @param startIndex      着色开始位置
     * @param endIndex        着色结束位置
     * @param foregroundColor 着色的颜色
     * @return
     */
    public static SpannableString getTextForegroundColorSpan(
            CharSequence originalText,
            int startIndex,
            int endIndex,
            int foregroundColor) {
        if (TextUtils.isEmpty(originalText) || startIndex < 0
                || endIndex > originalText.length() || startIndex >= endIndex) {
            return null;
        }
        SpannableString spannableString = new SpannableString(originalText);
        spannableString.setSpan(new ForegroundColorSpan(foregroundColor),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 为原文中指定文字着色
     *
     * @param originalText    原文
     * @param targetText      指定着色的文字
     * @param foregroundColor 着色的颜色
     * @return
     */
    public static SpannableString getTextForegroundColorSpan(CharSequence originalText, String targetText, int foregroundColor) {
        if (TextUtils.isEmpty(targetText)) {
            return null;
        }
        Pattern pattern = Pattern.compile(targetText);
        Matcher matcher = pattern.matcher(originalText);
        return getTextForegroundColorSpan(originalText, matcher, foregroundColor);
    }

    /**
     * 为textView 原文中指定文字着色
     *
     * @param textView        文本控件
     * @param originalText    原文
     * @param targetText      指定着色的文字
     * @param foregroundColor 着色的颜色
     * @return
     */
    public static void setTextForegroundColorSpan(TextView textView, CharSequence originalText, String targetText, int foregroundColor) {
        if (textView == null) return;
        textView.setText(getTextForegroundColorSpan(originalText, targetText, foregroundColor));
    }

    /**
     * 设置字符串中数字的颜色 包含+ %
     *
     * @param originalText 原文
     * @param numTextColor 原文中数字着色的文字
     * @return
     */
    public static SpannableString getTextNumColor(CharSequence originalText, int numTextColor) {
        if (TextUtils.isEmpty(originalText)) return null;
        String NUM_PATTERN = "[0-9]+|\\+|\\%";
        Pattern pattern = Pattern.compile(NUM_PATTERN);
        Matcher matcher = pattern.matcher(originalText);
        return getTextForegroundColorSpan(originalText, matcher, numTextColor);
    }
}
