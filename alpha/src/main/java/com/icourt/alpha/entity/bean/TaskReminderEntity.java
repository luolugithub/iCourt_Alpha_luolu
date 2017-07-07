package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description  任务提醒实体
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/1
 * version 2.0.0
 */

public class TaskReminderEntity implements Serializable {

    public static final String ALL_DAY = "allday";//全天任务
    public static final String PRECISE = "precise";//有具体时间的任务

    public String taskReminderType;//提醒类型
    public List<String> ruleTime;//设置的时间
    public List<CustomTimeItemEntity> customTime;//自定义时间提醒

    public static class CustomTimeItemEntity implements Serializable {
        String type;//类型
        String prefix;//几天前
        String suffix;//具体时间
    }

}
