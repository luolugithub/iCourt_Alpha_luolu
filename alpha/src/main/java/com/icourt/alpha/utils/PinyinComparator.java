package com.icourt.alpha.utils;


import android.text.TextUtils;

import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.util.Comparator;

/**
 * Description  按ABCD...WYZ#排序
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class PinyinComparator<T extends ISuspensionInterface> implements Comparator<T> {

    @Override
    public int compare(T t1, T t2) {
        if (t1 != null && t2 != null) {
            if (TextUtils.equals(t1.getSuspensionTag(), t2.getSuspensionTag())) {
                if (TextUtils.equals(t1.getSuspensionTag(), "#")
                        && !TextUtils.isEmpty(t1.getTargetField())
                        && !TextUtils.isEmpty(t2.getTargetField())) {
                    return t1.getTargetField().compareToIgnoreCase(t2.getTargetField());
                }
                return 0;
            } else if (TextUtils.equals(t1.getSuspensionTag(), "#")) {
                return 1;
            } else if (TextUtils.equals(t2.getSuspensionTag(), "#")) {
                return -1;
            } else {
                return t1.getSuspensionTag().compareToIgnoreCase(t2.getSuspensionTag());
            }
        }
        return 0;
    }
}
