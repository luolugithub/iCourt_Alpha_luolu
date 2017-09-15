package com.icourt.alpha.widget.comparators;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

/**
 * Description 中国字符比较
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class ChinaComparator implements Comparator<String> {
    public ORDER order;

    public ChinaComparator(ORDER order) {
        this.order = order;
    }

    public ChinaComparator() {
        this.order = ORDER.ASC;
    }

    Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

    @Override
    public int compare(String t0, String t1) {
        int result = 0;
        if (null != t0 && null != t1) {
            CollationKey c1 = cmp.getCollationKey(t0);
            CollationKey c2 = cmp.getCollationKey(t1);
            result = cmp.compare(c1.getSourceString(), c2.getSourceString());
        } else if (null == t0) {
            result = 1;
        } else if (null == t1) {
            result = -1;
        }
        if (order == ORDER.DESC) {
            return -result;
        }
        return result;
    }
}
