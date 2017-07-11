package com.icourt.alpha.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description  任务提醒
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/7
 * version 2.0.0
 */

public class TaskReminderUtils {

    /**
     * 全天任务
     */
    public static final Map<String, String> alldayMap = new LinkedHashMap<String, String>() {
        {
            put("ODB", "当天（9:00)");
            put("1DB", "一天前（9:00)");
            put("2DB", "两天前（9:00)");
            put("1WB", "一周前（9:00)");
        }
    };

    /**
     * 特定到期任务
     */
    public static final Map<String, String> preciseMap = new LinkedHashMap<String, String>() {
        {
            put("0MB", "任务到期时");
            put("5MB", "5分钟前");
            put("10MB", "10分钟前");
            put("30MB", "半小时前");
            put("1HB", "1小时前");
            put("2HB", "2小时前");
            put("1DB", "一天前");
            put("2DB", "两天前");
        }
    };

    /**
     * 提醒时间单位
     */
    public static final Map<String, String> unitMap = new LinkedHashMap<String, String>() {
        {
            put("day", "天");
            put("hour", "小时");
            put("minute", "分钟");
        }
    };
}
