package com.icourt.alpha.widget.comparators;


import android.text.TextUtils;

import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.util.Comparator;

/**
 * Description  按ABCD...WYZ#排序  注意为空 注意jdk1.7排序bug 注意手机的本土语言
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class PinyinComparator<T extends ISuspensionInterface> implements Comparator<T> {
    ChinaComparator chinaComparator = new ChinaComparator();

    @Override
    public int compare(T t1, T t2) {
        if (t1 != null && t2 != null) {
            if (TextUtils.equals(t1.getSuspensionTag(), t2.getSuspensionTag())) {
                return chinaComparator.compare(t1.getTargetField(), t2.getTargetField());
            } else if (TextUtils.equals(t1.getSuspensionTag(), "#")) {
                return 1;
            } else if (TextUtils.equals(t2.getSuspensionTag(), "#")) {
                return -1;
            } else {
                return chinaComparator.compare(t1.getSuspensionTag(), t2.getSuspensionTag());
            }
        }
        return 0;
    }
}
