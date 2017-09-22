package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.icourt.alpha.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * * Description  用来划分周的时间
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：17/9/21
 * version 2.1.1
 */

public class WeekDateEntity {

    public long startTimeMillios;//周的开始时间毫秒数

    public long endTimeMillios;//周的结束时间毫秒数

    private String year;

    @Override
    public String toString() {
        return formatDate(startTimeMillios) + " - " + formatDate(endTimeMillios);
    }


    /**
     * 返回时间区间所在的年份
     *
     * @return
     */
    public String getYear() {
        String formatStr = "yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr, Locale.CHINA);
        return formatter.format(endTimeMillios);
    }

    /**
     * 格式化时间
     *
     * @param milliseconds
     * @return
     */
    private String formatDate(long milliseconds) {
        String formatStr = "MM月dd日";
        if (!TextUtils.isEmpty(formatStr)) {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr, Locale.CHINA);
            try {
                return formatter.format(milliseconds);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
