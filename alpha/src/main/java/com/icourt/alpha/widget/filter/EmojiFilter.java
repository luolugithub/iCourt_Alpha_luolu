package com.icourt.alpha.widget.filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import com.icourt.alpha.utils.EmojiTools;

/**
 * Description emoji过滤器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/7
 * version 2.1.0
 */
public class EmojiFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return replaceEmoji(source, "");
    }

    /**
     * 是否包含emoji
     *
     * @param source
     * @return
     */
    public static final boolean containEmoji(CharSequence source) {
        if (TextUtils.isEmpty(source)) {
            return false;
        }
        return EmojiTools.containsEmoji(source.toString());
    }

    /**
     * 替换emoji
     *
     * @param source
     * @return
     */
    public static final String replaceEmoji(CharSequence source, String replace) {
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        return EmojiTools.filterEmoji(replace);
    }
}
