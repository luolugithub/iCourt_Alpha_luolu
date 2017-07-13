package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/8
 * version 2.0.0
 */

public class ReminderItemEntity implements Serializable {

    public String timeKey;//0DB,1DB,5MB,1HB...
    public String timeValue;//一天前，1小时前，一周前...

    public TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity;//自定义提醒

    @Override
    public String toString() {
        return "ReminderItemEntity{" +
                "timeKey='" + timeKey + '\'' +
                ", timeValue='" + timeValue + '\'' +
                ", customTimeItemEntity=" + customTimeItemEntity +
                '}';
    }
}
