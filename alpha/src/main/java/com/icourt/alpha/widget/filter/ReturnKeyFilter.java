package com.icourt.alpha.widget.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * @author youxuan  E-mail:xuanyouwu@163.com
 * @version 2.2.1
 * @Description 过滤回车键键tab键[尤其是粘贴]
 * @Company Beijing icourt
 * @date createTime：2017/11/6
 */
public class ReturnKeyFilter implements InputFilter {
    
    private static final String patternStr = "[\n|\t]";
    private static final Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);

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
