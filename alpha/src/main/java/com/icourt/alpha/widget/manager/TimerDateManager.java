package com.icourt.alpha.widget.manager;

import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Description 用来统一管理计时列表的时间筛选、过滤的管理类
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/11
 * version
 */

public class TimerDateManager {


    public static final int START_YEAR = 2015;

    /**
     * 返回计时列表筛选的开始日期（起始时间是2015年1月1日）
     *
     * @return
     */
    public static Calendar getStartDate() {
        //起始时间是2015年1月1日
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(Calendar.YEAR, START_YEAR);
        instance.set(Calendar.MONTH, Calendar.JANUARY);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance;
    }

    /**
     * 获取所有的周数据（从2015年1月1日开始，到当前日期所有的周）
     *
     * @return
     */
    public static List<TimingSelectEntity> getWeekData() {
        List<TimingSelectEntity> dayList = new ArrayList<>();//显示日期的list
        Calendar instance = getStartDate();
        //当前周的开始时间
        long weekStartTime = DateUtils.getWeekStartTime(instance.getTimeInMillis());
        //当前周的结束时间
        long weekEndTime = DateUtils.getWeekEndTime(instance.getTimeInMillis());

        while (weekStartTime < System.currentTimeMillis()) {
            TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
            timingSelectEntity.startTimeMillis = weekStartTime;
            timingSelectEntity.endTimeMillis = weekEndTime;
            timingSelectEntity.startTimeStr = DateUtils.getyyyy_MM_dd(weekStartTime);
            timingSelectEntity.endTimeStr = DateUtils.getyyyy_MM_dd(weekEndTime);
            dayList.add(timingSelectEntity);
            instance.clear();
            instance.setTimeInMillis(weekEndTime + 1);
            weekStartTime = instance.getTimeInMillis();
            weekEndTime = DateUtils.getWeekEndTime(instance.getTimeInMillis());
        }
        return dayList;
    }

    /**
     * 获取所有的月数据（从2015年1月1日开始，到当前日期所有的月）
     *
     * @return
     */
    public static List<TimingSelectEntity> getMonthData() {
        List<TimingSelectEntity> dayList = new ArrayList<>();//显示日期的list
        Calendar instance = getStartDate();
        //当前月的开始时间
        long monthStartTime = DateUtils.getMonthStartTime(instance.getTimeInMillis());
        //当前月的结束时间
        long monthEndTime = DateUtils.getMonthEndTime(instance.getTimeInMillis());

        while (monthStartTime < System.currentTimeMillis()) {
            TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
            timingSelectEntity.startTimeMillis = monthStartTime;
            timingSelectEntity.endTimeMillis = monthEndTime;
            timingSelectEntity.startTimeStr = DateUtils.getyyyy_MM_dd(monthStartTime);
            timingSelectEntity.endTimeStr = DateUtils.getyyyy_MM_dd(monthEndTime);
            dayList.add(timingSelectEntity);
            instance.add(Calendar.MONTH, 1);//月+1
            monthStartTime = instance.getTimeInMillis();
            monthEndTime = DateUtils.getMonthEndTime(instance.getTimeInMillis());
        }
        return dayList;
    }


    /**
     * 获取所有的年数据（从2015年1月1日开始，到当前日期所有的年）
     *
     * @return
     */
    public static List<TimingSelectEntity> getYearData() {
        List<TimingSelectEntity> dayList = new ArrayList<>();//显示日期的list
        Calendar instance = getStartDate();
        //当前年的开始时间
        long yearStartTime = DateUtils.getYearStartTime(instance.getTimeInMillis());
        //当前年的结束时间
        long yearEndTime = DateUtils.getYearEndTime(instance.getTimeInMillis());

        while (yearStartTime < System.currentTimeMillis()) {
            TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
            timingSelectEntity.startTimeMillis = yearStartTime;
            timingSelectEntity.endTimeMillis = yearEndTime;
            timingSelectEntity.startTimeStr = DateUtils.getyyyy_MM_dd(yearStartTime);
            timingSelectEntity.endTimeStr = DateUtils.getyyyy_MM_dd(yearEndTime);
            dayList.add(timingSelectEntity);
            instance.add(Calendar.YEAR, 1);//年+1
            yearStartTime = instance.getTimeInMillis();
            yearEndTime = DateUtils.getYearEndTime(instance.getTimeInMillis());
        }
        return dayList;
    }

}
