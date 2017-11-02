package com.icourt.alpha.utils;

import android.support.annotation.StringDef;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Description 时间格式化类，如果有新的时间格式，添加DateStyle枚举的类型，不要再添加方法了。
 * Company Beijing icourt
 *
 * @author zhaodanyang
 *         E-mail:zhaodanyang@icourt.cc
 *         date createTime：17/11/1
 *         version 2.2.1
 */
public class DateUtils {
    /**
     * 年月
     */
    public static final String DATE_YYYYMM_STYLE1 = "yyyy年MM月";
    /**
     * 年月日
     */
    public static final String DATE_YYYYMMDD_STYLE1 = "yyyy-MM-dd";
    public static final String DATE_YYYYMMDD_STYLE2 = "yyyy年MM月dd日";
    public static final String DATE_YYYYMMDD_STYLE3 = "yyyy.MM.dd";
    public static final String DATE_YYYYMMDD_STYLE4 = "yyyy/MM/dd";
    /**
     * 年月日时分
     */
    public static final String DATE_YYYYMMDD_HHMM_STYLE1 = "yyyy-MM-dd HH:mm";
    public static final String DATE_YYYYMMDD_HHMM_STYLE2 = "yyyy年MM月dd日 HH:mm";
    public static final String DATE_YYYYMMDD_HHMM_STYLE3 = "yyyy/MM/dd HH:mm";
    /**
     * 月日
     */
    public static final String DATE_MMDD_STYLE1 = "MM月dd日";
    public static final String DATE_MMDD_STYLE2 = "MM/dd";
    /**
     * 月日时分
     */
    public static final String DATE_MMDD_HHMM_STYLE1 = "MM-dd HH:mm";
    public static final String DATE_MMDD_HHMM_STYLE2 = "MM月dd日 HH:mm";
    public static final String DATE_MMDD_HHMM_STYLE3 = "MM/dd HH:mm";
    /**
     * 时分
     */
    public static final String DATE_HHMM_STYLE1 = "HH:mm";


    @StringDef({DATE_YYYYMM_STYLE1,
            DATE_YYYYMMDD_STYLE1,
            DATE_YYYYMMDD_STYLE2,
            DATE_YYYYMMDD_STYLE3,
            DATE_YYYYMMDD_STYLE4,
            DATE_YYYYMMDD_HHMM_STYLE1,
            DATE_YYYYMMDD_HHMM_STYLE2,
            DATE_YYYYMMDD_HHMM_STYLE3,
            DATE_MMDD_STYLE1,
            DATE_MMDD_STYLE2,
            DATE_MMDD_HHMM_STYLE1,
            DATE_MMDD_HHMM_STYLE2,
            DATE_MMDD_HHMM_STYLE3,
            DATE_HHMM_STYLE1})
    @Retention(RetentionPolicy.SOURCE)
    @interface DateStyle {

    }

    /**
     * 获取聊天的时间格式化 简写版
     *
     * @param milliseconds
     * @return
     */
    public static String getFormatChatTimeSimple(long milliseconds) {
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

        SimpleDateFormat timeformatter24 = new SimpleDateFormat(DATE_HHMM_STYLE1, Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);
        if (!currentTime.before(todaybegin)) {
            dataString = timeStringBy24;
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天";
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天";
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat(DATE_YYYYMMDD_STYLE1, Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }
        return dataString;
    }

    /**
     * 获取聊天的时间格式化
     *
     * @param milliseconds
     * @return
     */
    public static String getFormatChatTime(long milliseconds) {
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

        SimpleDateFormat timeformatter24 = new SimpleDateFormat(DATE_HHMM_STYLE1, Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);
        if (!currentTime.before(todaybegin)) {
            dataString = timeStringBy24;
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天 " + timeStringBy24;
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天 " + timeStringBy24;
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime) + " " + timeStringBy24;
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat(DATE_YYYYMMDD_HHMM_STYLE1, Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }
        return dataString;
    }

    /**
     * 格式1
     * http://wiki.alphalawyer.cn/pages/viewpage.action?pageId=1773098
     * 注意:别轻易修改
     * 文档地址:http://wiki.alphalawyer.cn/pages/viewpage.action?pageId=1773098
     * 获取标准的时间格式化:
     * 对近期时间点敏感、显示区域有限
     * 1:  t < 60 分钟：x分钟前（x = 1～59）
     * 2:  1 小时 ≤ t < 24 小时：x小时前（x = 1～23）
     * 3:  24 小时 ≤ t ≤ 前一天零点：昨天
     * 4:  前一天零点 < t ≤ 24*5 小时：x天前（x = 2～5）
     * 5:  t > 24*5 小时：yyyy-mm-dd
     *
     * @param milliseconds
     * @return
     */
    public static final String getStandardSimpleFormatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (isOverToday(milliseconds)) {//1.未来
            sdf.applyPattern(DATE_YYYYMMDD_HHMM_STYLE1);
            return sdf.format(milliseconds);
        } else if (isToday(milliseconds)) {//2.今天
            long distanceMilliseconds = System.currentTimeMillis() - milliseconds;
            if (distanceMilliseconds < TimeUnit.HOURS.toMillis(1)) {//3.x分钟前
                long distanceSeconds = TimeUnit.MILLISECONDS.toMinutes(distanceMilliseconds);
                if (distanceMilliseconds < 0) {
                    sdf.applyPattern(DATE_YYYYMMDD_HHMM_STYLE1);
                    return sdf.format(milliseconds);
                } else if (distanceSeconds == 0) {
                    return "刚刚";
                } else {
                    return String.format("%s分钟前", TimeUnit.MILLISECONDS.toMinutes(distanceMilliseconds));
                }
            } else {//4.x小时前
                return String.format("%s小时前", TimeUnit.MILLISECONDS.toHours(distanceMilliseconds));
            }
        } else if (isYesterday(milliseconds)) {
            return "昨天";//5.昨天
        } else {
            int todayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTimeInMillis(milliseconds);

            int targetDayOfYear = targetCalendar.get(Calendar.DAY_OF_YEAR);
            long distanceDayInt = todayOfYear - targetDayOfYear;

            long distanceMilliseconds = System.currentTimeMillis() - milliseconds;
            long distanceDay = TimeUnit.MILLISECONDS.toDays(distanceMilliseconds);
            //避免相差年份的问题
            if (distanceDay < 10 && distanceDayInt <= 5) {//x天前（x = 2～5）
                return String.format("%s天前", distanceDayInt);
            } else {//yyyy-mm-dd
                sdf.applyPattern(DATE_YYYYMMDD_STYLE1);
                return sdf.format(milliseconds);
            }
        }
    }

    /**
     * 格式2
     * http://wiki.alphalawyer.cn/pages/viewpage.action?pageId=1773098
     * 注意:别轻易修改
     * 文档地址:http://wiki.alphalawyer.cn/pages/viewpage.action?pageId=1773098
     * 获取标准的时间格式化:
     * <p>
     * 对近期时间点敏感、对具体时间点要求高、显示区域充裕
     * 通常不带有社交属性
     * <p>
     * 1. t < 60 分钟：x分钟前（x = 1～59）
     * 2. 1 小时 ≤ t < 24 小时：x小时前（x = 1～23）
     * 3. 24 小时 ≤ t ≤ 前一天零点：昨天 + hh:mm
     * 4. t > 前一天零点：yyyy-mm-dd + hh:mm
     *
     * @param milliseconds
     * @return
     */
    public static final String getStandardFormatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        if (isOverToday(milliseconds)) {//1.未来
            sdf.applyPattern(DATE_YYYYMMDD_HHMM_STYLE1);
            return sdf.format(milliseconds);
        } else if (isToday(milliseconds)) {//2.今天
            long distanceMilliseconds = System.currentTimeMillis() - milliseconds;
            if (distanceMilliseconds < TimeUnit.HOURS.toMillis(1)) {//3.x分钟前
                long distanceSeconds = TimeUnit.MILLISECONDS.toMinutes(distanceMilliseconds);
                if (distanceMilliseconds < 0) {
                    sdf.applyPattern(DATE_YYYYMMDD_HHMM_STYLE1);
                    return sdf.format(milliseconds);
                } else if (distanceSeconds == 0) {
                    return "刚刚";
                } else {
                    return String.format("%s分钟前", TimeUnit.MILLISECONDS.toMinutes(distanceMilliseconds));
                }
            } else {//4.x小时前
                return String.format("%s小时前", TimeUnit.MILLISECONDS.toHours(distanceMilliseconds));
            }
        } else if (isYesterday(milliseconds)) {
            sdf.applyPattern("昨天 HH:mm");
            return sdf.format(milliseconds);
        } else {
            int todayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTimeInMillis(milliseconds);

            int targetDayOfYear = targetCalendar.get(Calendar.DAY_OF_YEAR);
            long distanceDayInt = todayOfYear - targetDayOfYear;//相差的天 不是间隔的时间/每天的毫秒

            long distanceMilliseconds = System.currentTimeMillis() - milliseconds;
            long distanceDay = TimeUnit.MILLISECONDS.toDays(distanceMilliseconds);
            //避免相差年份的问题
            if (distanceDay < 10 && distanceDayInt <= 5) {//x天前（x = 2～5）
                return String.format("%s天前", distanceDayInt);
            } else {//yyyy-mm-dd
                sdf.applyPattern(DATE_YYYYMMDD_HHMM_STYLE1);
                return sdf.format(milliseconds);
            }
        }
    }

    /**
     * 获取格式化的日期
     *
     * @param millisSecond 毫秒
     * @param dateStyle    指定的那几种日期类型
     * @return
     */
    public static String getFormatDate(long millisSecond, @DateStyle String dateStyle) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateStyle, Locale.CHINA);
            return dateFormat.format(millisSecond);
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("DateUtils.getFormatDate()方法异常", StringUtils.throwable2string(e));
        }
        return "";
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        long timeMillis1 = date1.getTime();
        long timeMillis2 = date2.getTime();
        if (getWeekStartTime(timeMillis1) == getWeekStartTime(timeMillis2) &&
                getWeekEndTime(timeMillis1) == getWeekEndTime(timeMillis2)) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个时间戳是否在同一年
     *
     * @param timeMillis1
     * @param timeMillis2
     * @return
     */
    public static boolean isSameYear(long timeMillis1, long timeMillis2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(timeMillis1);
        calendar2.setTimeInMillis(timeMillis2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date
     * @return
     */
    @Deprecated
    public static String getTodayTimeBucket(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat timeformatter0to11 = new SimpleDateFormat("KK:mm", Locale.getDefault());
        SimpleDateFormat timeformatter1to12 = new SimpleDateFormat(DATE_HHMM_STYLE1, Locale.getDefault());
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
     * 根据日期获得星期
     *
     * @param millis
     * @return
     */
    public static String getWeekOfDateFromZ(long millis) {
        String[] weekDaysName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
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
     * 获取日期
     * 如果是今年，返回：MM月dd日
     * 如果不是今年，返回：yyyy年MM月dd日
     *
     * @param milliseconds 毫秒
     * @return
     */
    public static String getTimeDate(long milliseconds) {
        String formatStr = null;
        if (isThisYear(milliseconds)) {
            formatStr = DATE_MMDD_STYLE1;
        } else {
            formatStr = DATE_YYYYMMDD_STYLE2;
        }
        if (!TextUtils.isEmpty(formatStr)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
                return formatter.format(milliseconds);
            } catch (Exception e) {
                e.printStackTrace();
                BugUtils.bugSync("DateUtils.getTimeDate()方法异常", StringUtils.throwable2string(e));
                return "";
            }
        }
        return "";
    }

    /**
     * 获取日期
     * 如果是今年，返回：MM月dd日 HH:mm；
     * 如果不是今年，返回：yyyy年MM月dd日 HH:mm
     *
     * @param milliseconds 毫秒
     * @return
     */
    public static String getTimeDateFormatMm(long milliseconds) {
        String formatStr = null;
        if (isThisYear(milliseconds)) {
            formatStr = DATE_MMDD_HHMM_STYLE2;
        } else {
            formatStr = DATE_YYYYMMDD_HHMM_STYLE2;
        }
        if (!TextUtils.isEmpty(formatStr)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
                return formatter.format(milliseconds);
            } catch (Exception e) {
                e.printStackTrace();
                BugUtils.bugSync("DateUtils.getTimeDateFormatMm()方法异常", StringUtils.throwable2string(e));
                return "";
            }
        }
        return "";
    }

    /**
     * 获取日期
     * 如果是今年，返回：MM/dd HH:mm
     * 如果不是今年，返回：yyyy/MM/dd HH:mm
     *
     * @param milliseconds 毫秒
     * @return
     */
    public static String getTimeDateFormatXMm(long milliseconds) {
        String formatStr = null;
        if (isThisYear(milliseconds)) {
            formatStr = DATE_MMDD_HHMM_STYLE3;
        } else {
            formatStr = DATE_YYYYMMDD_HHMM_STYLE3;
        }
        if (!TextUtils.isEmpty(formatStr)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
                return formatter.format(milliseconds);
            } catch (Exception e) {
                e.printStackTrace();
                BugUtils.bugSync("DateUtils.getTimeDateFormatXMm()方法异常", StringUtils.throwable2string(e));
                return "";
            }
        }
        return "";
    }

    /**
     * 返回日期
     * 如果是今年，返回：MM/dd
     * 如果不是今年，返回：yyyy/MM/dd
     *
     * @param milliseconds 毫秒
     * @return
     */
    public static String getMMXdd(long milliseconds) {
        String formatStr = null;
        if (isThisYear(milliseconds)) {
            formatStr = DATE_MMDD_STYLE2;
        } else {
            formatStr = DATE_YYYYMMDD_STYLE4;
        }
        if (!TextUtils.isEmpty(formatStr)) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
                return formatter.format(milliseconds);
            } catch (Exception e) {
                e.printStackTrace();
                BugUtils.bugSync("DateUtils.getMMXdd()方法异常", StringUtils.throwable2string(e));
                return "";
            }
        }
        return "";
    }

    /**
     * 获得天数差（这里返回的是两个时间戳差距超过24小时的倍数）
     *
     * @param begin 毫秒
     * @param end   毫秒
     * @return
     */
    public static long getDayDiff(long begin, long end) {
        long day = 1;
        if (end < begin) {
            day = -1;
        } else if (end == begin) {
            day = 0;
        } else {
            day += (end - begin) / (24 * 60 * 60 * 1000);
        }
        return day;
    }

    /**
     * endMillis比startMillis多的天数（比如"2017年10月11日08:00"比"2017年10月10日19:00"多一天，并不是按照是否差距24小时的倍数来计算的）
     * 注意：endMillis要大于startMillis
     *
     * @param startMillis 毫秒
     * @param endMillis   毫秒
     * @return
     */
    public static int differentDays(long startMillis, long endMillis) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(startMillis);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(endMillis);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            //不同年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    //闰年
                    timeDistance += 366;
                } else {
                    //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {//同一年
            return day2 - day1;
        }
    }

    /**
     * 获取今天开始时间 毫秒
     *
     * @return
     */
    public static long getTodayStartTime() {
        return getDayStartTime(System.currentTimeMillis());
    }

    /**
     * 获取今天开始时间 毫秒
     *
     * @return
     */
    public static long getTodayEndTime() {
        return getDayEndTime(System.currentTimeMillis());
    }

    /**
     * 获取时间戳所在天的开始时间
     *
     * @param timeMillis
     * @return
     */
    public static long getDayStartTime(long timeMillis) {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTimeInMillis(timeMillis);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTimeInMillis();
    }

    /**
     * 获取时间戳所在天的结束时间
     *
     * @param timeMillis
     * @return
     */
    public static long getDayEndTime(long timeMillis) {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTimeInMillis(timeMillis);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return currentDate.getTimeInMillis();
    }

    /**
     * 获取本周的开始时间 毫秒
     *
     * @return
     */
    public static long getCurrWeekStartTime() {
        return getWeekStartTime(System.currentTimeMillis());
    }

    /**
     * 获取本周的开始时间 毫秒
     *
     * @return
     */
    public static long getCurrWeekEndTime() {
        return getWeekEndTime(System.currentTimeMillis());
    }

    /**
     * 获取周的开始时间
     *
     * @param timeMillis
     * @return
     */
    public static long getWeekStartTime(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int d;
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//如果是周日，则在当前日期上减去6天，就是周一了
            d = -6;
        } else {//如果不是周日，周一的起始值是减去今天所对应周几，得出这周的第一天。
            d = Calendar.MONDAY - calendar.get(Calendar.DAY_OF_WEEK);
        }
        //所在周开始日期
        calendar.add(Calendar.DAY_OF_WEEK, d);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取周的结束时间
     *
     * @param timeMillis
     * @return
     */
    public static long getWeekEndTime(long timeMillis) {
        return getWeekStartTime(timeMillis) + TimeUnit.DAYS.toMillis(7) - 1;
    }

    /**
     * 判断是不是今天
     *
     * @param millis 毫秒
     * @return
     */
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
     * 今天以后的时间
     *
     * @param millis 毫秒
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
     * 判断是否是昨天
     *
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

    /**
     * 判断是否是今年
     *
     * @param millis 毫秒
     * @return
     */
    public static boolean isThisYear(long millis) {
        Calendar otherYear = Calendar.getInstance();
        otherYear.setTimeInMillis(millis);
        Calendar thisYear = Calendar.getInstance();
        thisYear.setTimeInMillis(millis());

        return otherYear.get(Calendar.YEAR) == thisYear.get(Calendar.YEAR);
    }

    /**
     * 23:59:59 不显示  xx月xx日 hh：mm
     *
     * @param millis 毫秒
     * @return
     */
    public static String get23Hour59Min(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if ((hour == 23 && minute == 59 && second == 59)) {
            return getTimeDate(millis);
        } else {
            return getTimeDateFormatMm(millis);
        }
    }

    /**
     * 23:59:59 不显示 xx/xx hh：mm
     *
     * @param millis 毫秒
     * @return
     */
    public static String get23Hour59MinFormat(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if ((hour == 23 && minute == 59 && second == 59)) {
            return getMMXdd(millis);
        } else {
            return getTimeDateFormatXMm(millis);
        }
    }

    /**
     * 根据小时：分钟 获取时间戳
     *
     * @param hour
     * @param min
     * @return
     */
    public static long getMillByHourmin(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取日期和星期的组合
     *
     * @param millis 毫秒
     * @return
     */
    public static String getMMddWeek(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_MMDD_STYLE1);
        String format = formatter.format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        StringBuilder builder = new StringBuilder(format);
        builder.append(" ");
        switch (day) {
            case Calendar.SUNDAY:
                builder.append("周日");
                break;
            case Calendar.MONDAY:
                builder.append("周一");
                break;
            case Calendar.TUESDAY:
                builder.append("周二");
                break;
            case Calendar.WEDNESDAY:
                builder.append("周三");
                break;
            case Calendar.THURSDAY:
                builder.append("周四");
                break;
            case Calendar.FRIDAY:
                builder.append("周五");
                break;
            case Calendar.SATURDAY:
                builder.append("周六");
                break;
            default:
                break;

        }
        return builder.toString();
    }

    /**
     * 根据提供的年月日获取该月份的第一天
     *
     * @param year
     * @param monthOfYear
     * @return
     */
    public static Date getSupportBeginDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     *
     * @param year
     * @param monthOfYear
     * @return
     */
    public static Date getSupportEndDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return cal.getTime();
    }

    /**
     * 根据提供的年月日获取该月份的第一天
     *
     * @param year
     * @return
     */
    public static Date getSupportBeginDayofYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     *
     * @param year
     * @return
     */
    public static Date getSupportEndDayofYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        return cal.getTime();
    }

    /**
     * 获取当前时间的时间戳（秒数为0）
     * 比如：当前时间为12:10:30，返回的是12:10:00的时间戳
     *
     * @param timeMillis 毫秒
     * @return
     */
    public static long getFormatMillis(long timeMillis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timeMillis);
        int currentYear = instance.get(Calendar.YEAR);
        int currentMonth = instance.get(Calendar.MONTH);
        int currentDay = instance.get(Calendar.DAY_OF_MONTH);
        int currentHour = instance.get(Calendar.HOUR_OF_DAY);
        int currentMinute = instance.get(Calendar.MINUTE);
        //记录当前时间，精确到分钟，秒数置为0。
        instance.set(currentYear, currentMonth, currentDay, currentHour, currentMinute, 0);
        return instance.getTimeInMillis();
    }

    /**
     * 获取时间戳所在月份的第一天的起始时间
     *
     * @param millis 毫秒
     * @return
     */
    public static long getMonthStartTime(long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    /**
     * 获取时间戳所在月份的最后一天的最后一秒
     *
     * @param millis 毫秒
     * @return
     */
    public static long getMonthEndTime(long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        return instance.getTimeInMillis();
    }

    /**
     * 获取时间戳所在年的第一天的起始时间
     *
     * @param millis 毫秒
     * @return
     */
    public static long getYearStartTime(long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        instance.set(Calendar.MONTH, Calendar.JANUARY);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    /**
     * 获取时间戳所在年份的最后一天最后一秒
     *
     * @param millis 毫秒
     * @return
     */
    public static long getYearEndTime(long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        instance.set(Calendar.MONTH, Calendar.DECEMBER);
        instance.set(Calendar.DAY_OF_MONTH, 31);
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        return instance.getTimeInMillis();
    }

    /**
     * 时间格式化 秒 --> 时：分：秒
     *
     * @param seconds 秒
     * @return
     */
    public static String getHHmmss(long seconds) {
        long hour = seconds / 3600;
        long minute = seconds % 3600 / 60;
        long second = seconds % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 时间格式化：秒 --> 时：分
     *
     * @param seconds 秒
     * @return
     */
    public static String getHHmm(long seconds) {
        long hour = seconds / 3600;
        long minute = seconds % 3600 / 60;
        if (minute < 0) {
            minute = 0;
        }
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    /**
     * 将计时时间毫秒数转换为"时:分"的样式，和getHHmm()方法区别，如果秒数大于0，会多加一分钟（如：1小时11分20秒 返回 01小时12分）
     *
     * @param timesMillis 毫秒
     * @return
     */
    public static String getHHmmIntegral(long timesMillis) {
        timesMillis /= 1000;
        long hour = timesMillis / 3600;
        long minute = timesMillis % 3600 / 60;
        long second = timesMillis % 60;
        if (second >= 1) {
            minute += 1;
        }
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    /**
     * 获取计时的样式（比如：20:12:08）
     *
     * @param timeSeconds 秒
     * @return
     */
    public static String getTimingStr(long timeSeconds) {
        long hour = timeSeconds / 3600;
        long minute = timeSeconds % 3600 / 60;
        long second = timeSeconds % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 获取时间戳所在的年份
     *
     * @param timeMillis 毫秒
     * @return
     */
    public static int getYear(long timeMillis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(timeMillis);
        return instance.get(Calendar.YEAR);
    }

    /**
     * 获取当月的第一天
     *
     * @return
     */
    public static String getCurrentMonthFirstDay() {
        try {
            SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_YYYYMMDD_STYLE1);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.getTime();
            return dateFormater.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("DateUtils.getCurrentMonthFirstDay()方法异常", StringUtils.throwable2string(e));
        }
        return "";
    }

    /**
     * 获取当月的最后一天
     *
     * @return
     */
    public static String getCurrentMonthLastDay() {
        try {
            SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_YYYYMMDD_STYLE1);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH,
                    cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            return dateFormater.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("DateUtils.getCurrentMonthFirstDay()方法异常", StringUtils.throwable2string(e));
        }
        return "";
    }
}
