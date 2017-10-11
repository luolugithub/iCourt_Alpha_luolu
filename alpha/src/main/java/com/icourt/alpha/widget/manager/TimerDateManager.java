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


    /**
     * 返回计时列表筛选的开始日期（起始时间是2015年1月1日）
     *
     * @return
     */
    public static Calendar getStartDate() {
        //起始时间是2015年1月1日
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(Calendar.YEAR, 2015);
        instance.set(Calendar.MONTH, Calendar.JANUARY);
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance;
    }

    /**
     * 获取所有的周数据（从2015年1月1日开始，到当前日期所在周）
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

}
