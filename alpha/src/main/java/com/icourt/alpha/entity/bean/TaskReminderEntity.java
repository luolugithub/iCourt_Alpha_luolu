package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Description  任务提醒实体
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/1
 * version 2.0.0
 */

public class TaskReminderEntity implements Serializable, Cloneable {

    public static final String ALL_DAY = "allday";//全天任务
    public static final String PRECISE = "precise";//有具体时间的任务

    public String taskReminderType;//任务提醒类型。 allday: 全天任务；precise: 特定到期的任务；
    /**
     * 按规则设置的提醒时间集合。
     * 全天任务：
     * ODB，当天（9:00)；
     * 1DB，一天前（9:00)；
     * 2DB，两天前（9:00)；
     * 1WB，一周前（9:00)；
     * 特定到期任务：
     * 0MB，任务到期时；
     * 5MB，5分钟前；
     * 10MB：10分钟前；
     * 30MB，半小时前；
     * 1HB，1小时前；
     * 2HB，2小时前；
     * 1DB，一天前；
     * 2DB，两天前
     */
    public List<String> ruleTime;//设置的时间
    public List<CustomTimeItemEntity> customTime;//自定义时间提醒

    public static class CustomTimeItemEntity implements Serializable {
        public String point;//自定义设置的提醒时间点    "05:44"
        public String unit;//自定义设置的提醒时间单位,参数：day，天；hour，小时；minute，分钟； ,
        public String unitNumber;//自定义设置的提醒时间单位数量

        @Override
        public String toString() {
            return "CustomTimeItemEntity{" +
                    "point='" + point + '\'' +
                    ", unit='" + unit + '\'' +
                    ", unitNumber='" + unitNumber + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CustomTimeItemEntity) {
                CustomTimeItemEntity entity = (CustomTimeItemEntity) obj;
                return TextUtils.equals(point, entity.point) && TextUtils.equals(unit, entity.unit) && TextUtils.equals(unitNumber, entity.unitNumber);
            } else {
                return false;
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (TaskReminderEntity) super.clone();
    }

    @Override
    public String toString() {
        return "TaskReminderEntity{" +
                "taskReminderType='" + taskReminderType + '\'' +
                ", ruleTime=" + ruleTime +
                ", customTime=" + customTime +
                '}';
    }
}
