package com.icourt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sun.rmi.runtime.Log;

/**
 * Created by zhaodanyang on 2017/9/26.
 */

public class CalendarTest {

    public static void main(String[] args) throws Exception {


        System.out.print("======输出时间：" + String.format(Locale.CHINA, "%02d:%02d", 100, 1));

//        Date date1 = new Date(2015, 11, 31);
//        Date date2 = new Date(2016, 11, 31);
//
//        System.out.print("是不是同一周" + isSameWeekDates(date1, date2));


//        try {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));
//            while (cal.getTimeInMillis() < System.currentTimeMillis()) {
//                cal.add(Calendar.DAY_OF_YEAR, 1);
//                System.out.print("\n--时间--:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTimeInMillis()));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        long currWeekStartTime = getCurrWeekStartTime();
//        System.out.print("\n--时间--:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currWeekStartTime));
//
//        long currWeekEndTime = getCurrWeekEndTime();
//        System.out.print("\n--时间--:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currWeekEndTime));

    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int month1 = cal1.get(Calendar.MONTH);
        int month2 = cal2.get(Calendar.MONTH);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        } else if (1 == subYear && month2 == Calendar.DECEMBER) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        } else if (-1 == subYear && month1 == Calendar.DECEMBER) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取本周的开始时间 毫秒
     *
     * @return
     */
    public static long getCurrWeekStartTime() {
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+:08:00"));
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return currentDate.getTimeInMillis();
    }


    /* 获取本周的开始时间 毫秒
     * @return
     */
    public static long getCurrWeekEndTime() {
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+:08:00"));
        // currentDate.setFirstDayOfWeek(Calendar.SUNDAY);
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return currentDate.getTime().getTime();
    }


}
