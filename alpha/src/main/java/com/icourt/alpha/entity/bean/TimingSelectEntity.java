package com.icourt.alpha.entity.bean;

import com.icourt.alpha.utils.DateUtils;

import java.io.Serializable;

/**
 * * Description  用来划分周的时间
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：17/9/21
 * version 2.1.1
 */

public class TimingSelectEntity implements Serializable {

    public String startTimeStr;//开始日期： eg：2017-01－11

    public String endTimeStr;//结束日期： eg：2017-01－11

    public long startTimeMillis;//周的开始时间毫秒数

    public long endTimeMillis;//周的结束时间毫秒数

    private String year;

    @Override
    public String toString() {
        return formatDate(startTimeMillis) + " - " + formatDate(endTimeMillis);
    }


    /**
     * 返回时间区间所在的年份
     *
     * @return
     */
    public String getYear() {
        return String.valueOf(DateUtils.getYear(endTimeMillis));
    }

    /**
     * 格式化时间
     *
     * @param milliseconds
     * @return
     */
    private String formatDate(long milliseconds) {
        return DateUtils.getFormatDate(milliseconds, DateUtils.DATE_MMDD_STYLE1);
    }
}
