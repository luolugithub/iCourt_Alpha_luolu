package com.icourt.alpha.widget.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class SFileNameFilter implements InputFilter {

    // 特殊字符不能作为资料库名称：'\\', '/', ':', '*', '?', '"', '<', '>', '|', '\b', '\t'
    Pattern pattern = Pattern.compile("[\\\\|/|:|：|*|?|？|\"|“|”|<|>|\\||\t]", Pattern.CASE_INSENSITIVE);

    /**
     * @param source 为即将输入的字符串。source
     * @param start  source的start
     * @param end    endsource的end start为0，end也可理解为source长度了
     * @param dest   dest输入框中原来的内容，dest
     * @param dstart 要替换或者添加的起始位置，即光标所在的位置
     * @param dend   要替换或者添加的终止始位置，若为选择一串字符串进行更改，则为选中字符串 最后一个字符在dest中的位置。
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return pattern.matcher(source).replaceAll("");
    }
}
