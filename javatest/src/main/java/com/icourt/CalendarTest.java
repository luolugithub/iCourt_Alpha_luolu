package com.icourt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import sun.rmi.runtime.Log;

/**
 * Created by zhaodanyang on 2017/9/26.
 */

public class CalendarTest {

    public static void main(String[] args) throws Exception {

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

        long currWeekStartTime = getCurrWeekStartTime();
        System.out.print("\n--时间--:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currWeekStartTime));

        long currWeekEndTime = getCurrWeekEndTime();
        System.out.print("\n--时间--:" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currWeekEndTime));

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
