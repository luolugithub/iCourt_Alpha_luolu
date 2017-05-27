package com.icourt.alpha.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static cn.finalteam.toolsfinal.DateUtils.date;

public class DateUtils {

    public static String getTimeShowString(long milliseconds, boolean abbreviate) {
        String dataString;
        String timeStringBy24;

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todaybegin = todayStart.getTime();
        Date yesterdaybegin = new Date(todaybegin.getTime() - 3600 * 24 * 1000);
        Date preyesterday = new Date(yesterdaybegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todaybegin)) {
            dataString = "今天";
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天";
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天";
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }

        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);

        if (abbreviate) {
            if (!currentTime.before(todaybegin)) {
                return getTodayTimeBucket(currentTime);
            } else {
                return dataString;
            }
        } else {
            return dataString + " " + timeStringBy24;
        }
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
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date
     * @return
     */
    public static String getTodayTimeBucket(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat timeformatter0to11 = new SimpleDateFormat("KK:mm", Locale.getDefault());
        SimpleDateFormat timeformatter1to12 = new SimpleDateFormat("hh:mm", Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 5) {
            return "凌晨 " + timeformatter0to11.format(date);
        } else if (hour >= 5 && hour < 12) {
            return "上午 " + timeformatter0to11.format(date);
        } else if (hour >= 12 && hour < 18) {
            return "下午 " + timeformatter1to12.format(date);
        } else if (hour >= 18 && hour < 24) {
            return "晚上 " + timeformatter1to12.format(date);
        }
        return "";
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 获得当前时间的毫秒数
     * <p>
     * 详见{@link System#currentTimeMillis()}
     *
     * @return
     */
    public static long millis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取时长 00:11
     * bug
     *
     * @param milliseconds
     * @return
     */
    @Deprecated
    public static String getTimeDurationDate(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(milliseconds);
    }


    /**
     * 获取时长 00:11
     *
     * @param milliseconds
     * @return
     */
    public static String getHHmmss(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(milliseconds);
    }

    /**
     * 获取时长 00:11
     *
     * @param milliseconds
     * @return
     */
    public static String getHHmm(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(milliseconds);
    }

    /**
     * 获取日期 yyyy年MM月dd日
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeDateFormatYear(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        return formatter.format(milliseconds);
    }

    /**
     * 获取日期 MM月dd日
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeDate(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        return formatter.format(milliseconds);
    }

    /**
     * 获取日期 MM月dd日 HH:mm
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeDateFormatMm(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");
        return formatter.format(milliseconds);
    }

    /**
     * @param milliseconds
     * @return
     */
    public static String getyyyyMMdd(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        return formatter.format(milliseconds);
    }

    /**
     * @param milliseconds
     * @return
     */
    public static String getyyyyMMddHHmm(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(milliseconds);
    }

    /**
     * @param milliseconds
     * @return
     */
    public static String getMMMdd(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        try {
            return formatter.format(milliseconds);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return String.valueOf(milliseconds);
    }

    /**
     * 获得天数差
     *
     * @param begin
     * @param end
     * @return
     */
    public static long getDayDiff(Date begin, Date end) {
        long day = 1;
        if (end.getTime() < begin.getTime()) {
            day = -1;
        } else if (end.getTime() == begin.getTime()) {
            day = 0;
        } else {
            day += (end.getTime() - begin.getTime()) / (24 * 60 * 60 * 1000);
        }
        return day;
    }

    /**
     * 获取今天开始时间 毫秒
     *
     * @return
     */
    public static long getTodayStartTime() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime().getTime();
    }

    /**
     * 获取今天开始时间 毫秒
     *
     * @return
     */
    public static long getTodayEndTime() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTime().getTime();
    }

    /**
     * 获取本周的开始时间 毫秒
     *
     * @return
     */
    public static long getCurrWeekStartTime() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return currentDate.getTime().getTime();
    }


    /* 获取本周的开始时间 毫秒
     * @return
     */
    public static long getCurrWeekEndTime() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return currentDate.getTime().getTime();
    }


    public static boolean isToday(long millis) {
        Calendar current = Calendar.getInstance();
        Calendar todayStart = Calendar.getInstance();    //今天
        todayStart.set(Calendar.YEAR, current.get(Calendar.YEAR));
        todayStart.set(Calendar.MONTH, current.get(Calendar.MONTH));
        todayStart.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);

        Calendar todayEnd = Calendar.getInstance();    //今天
        todayEnd.set(Calendar.YEAR, current.get(Calendar.YEAR));
        todayEnd.set(Calendar.MONTH, current.get(Calendar.MONTH));
        todayEnd.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);

        return millis / 1000 >= todayStart.getTimeInMillis() / 1000
                && millis / 1000 <= todayEnd.getTimeInMillis() / 1000;
    }

    /**
     * 是明天
     *
     * @param millis
     * @return
     */
    public static boolean isOverToday(long millis) {
        Calendar todayEnd = Calendar.getInstance();    //今天
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return millis / 1000 >= todayEnd.getTimeInMillis() / 1000;
    }


    /**
     * @param millis 毫秒
     * @return
     */
    public static boolean isYesterday(long millis) {
        Calendar today = Calendar.getInstance();    //今天
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天
        yesterday.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        return millis / 1000 < today.getTimeInMillis() / 1000
                && millis / 1000 >= yesterday.getTimeInMillis() / 1000;
    }
}
