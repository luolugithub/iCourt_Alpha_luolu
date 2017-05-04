package com.icourt.alpha.widget.comparators;


import java.util.Comparator;

/**
 * Description  实体带Long字段比较器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public class LongFieldEntityComparator<T extends ILongFieldEntity> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        if (o1 != null
                && o1.getCompareLongField() != null
                && o2 != null
                && o2.getCompareLongField() != null) {
            if (order == ORDER.DESC) {
                return o2.getCompareLongField().compareTo(o1.getCompareLongField());
            } else {
                return o1.getCompareLongField().compareTo(o2.getCompareLongField());
            }
        }
        return 0;
    }

    public enum ORDER {
        ASC, DESC;
    }

    public ORDER order;

    public LongFieldEntityComparator(ORDER order) {
        this.order = order;
    }

}
