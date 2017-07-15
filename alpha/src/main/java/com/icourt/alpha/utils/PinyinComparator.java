package com.icourt.alpha.utils;


import android.text.TextUtils;

import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 * Description  按ABCD...WYZ#排序  注意为空 注意jdk1.7排序bug 注意手机的本土语言
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class PinyinComparator<T extends ISuspensionInterface> implements Comparator<T> {
    Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

    @Override
    public int compare(T t1, T t2) {
        if (t1 != null && t2 != null) {
            if (TextUtils.equals(t1.getSuspensionTag(), t2.getSuspensionTag())) {
                if (null != t1.getTargetField()
                        && null != t2.getTargetField()) {
                    CollationKey c1 = cmp.getCollationKey(t1.getTargetField());
                    CollationKey c2 = cmp.getCollationKey(t2.getTargetField());
                    return cmp.compare(c1.getSourceString(), c2.getSourceString());
                } else if (null == t1.getTargetField()
                        && null == t2.getTargetField()) {
                    return 0;
                } else if (t1.getTargetField() == null) {
                    return 1;
                } else if (t2.getTargetField() == null) {
                    return -1;
                }
                return 0;
            } else if (TextUtils.equals(t1.getSuspensionTag(), "#")) {
                return 1;
            } else if (TextUtils.equals(t2.getSuspensionTag(), "#")) {
                return -1;
            } else {
                if (null != t1.getSuspensionTag()
                        && null != t2.getSuspensionTag()) {
                    CollationKey c1 = cmp.getCollationKey(t1.getSuspensionTag());
                    CollationKey c2 = cmp.getCollationKey(t2.getSuspensionTag());
                    return cmp.compare(c1.getSourceString(), c2.getSourceString());
                } else if (null == t1.getSuspensionTag()
                        && null == t2.getSuspensionTag()) {
                    return 0;
                } else if (t1.getSuspensionTag() == null) {
                    return 1;
                } else if (t2.getSuspensionTag() == null) {
                    return -1;
                }
                return t1.getSuspensionTag().compareToIgnoreCase(t2.getSuspensionTag());
            }
        }
        return 0;
    }
}
