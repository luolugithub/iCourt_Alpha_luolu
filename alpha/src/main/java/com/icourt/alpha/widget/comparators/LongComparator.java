package com.icourt.alpha.widget.comparators;


import java.util.Comparator;

/**
 * 返回值分为负数正数和0
 * <p>
 * 负数代表左值小于右值，排在上面
 * <p>
 * 正数代表左值大于右值，排在下面
 * <p>
 * 0代表左值等于右值，排在上面
 * <p>
 * 可以这样理解：排序就是比较谁大谁小，将小的放在前面，大的放在后面。例如当返回负数的时候，表明第一个数应该排在第二个数的上面。
 */

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public class LongComparator implements Comparator<Long> {

    public ORDER order;

    public LongComparator(ORDER order) {
        this.order = order;
    }

    @Override
    public int compare(Long o1, Long o2) {
        if (o1 != null
                && o2 != null) {
            if (order == ORDER.DESC) {
                return o2.compareTo(o1);
            } else {
                return o1.compareTo(o2);
            }
        }
        return 0;
    }
}
