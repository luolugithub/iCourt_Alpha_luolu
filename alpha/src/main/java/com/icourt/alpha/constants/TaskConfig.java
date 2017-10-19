package com.icourt.alpha.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/10/19
 * version 2.1.1
 */
public class TaskConfig {

    /**
     * 任务标题名字最大长度
     */
    public static final int TASK_NAME_MAX_LENGTH = 200;

    /**
     * 任务描述最大长度
     */
    public static final int TASK_DESC_MAX_LENGTH = 3000;

    /**
     * 任务的状态：
     * -1-全部任务；
     * 0-未完成；
     * 1-已完成；
     * 3-已删除。
     */
    public static final int TASK_STATETYPE_ALL = -1;
    public static final int TASK_STATETYPE_UNFINISH = 0;
    public static final int TASK_STATETYPE_FINISHED = 1;
    public static final int TASK_STATETYPE_DELETED = 3;

    @IntDef({TASK_STATETYPE_ALL,
            TASK_STATETYPE_UNFINISH,
            TASK_STATETYPE_FINISHED,
            TASK_STATETYPE_DELETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskStateType {
    }

    /**
     * 转换
     *
     * @param taskType
     * @return
     */
    @TaskStateType
    public static int convert2TaskStateType(int taskType) {
        switch (taskType) {
            case TASK_STATETYPE_ALL:
                return TASK_STATETYPE_ALL;
            case TASK_STATETYPE_UNFINISH:
                return TASK_STATETYPE_UNFINISH;
            case TASK_STATETYPE_FINISHED:
                return TASK_STATETYPE_FINISHED;
            case TASK_STATETYPE_DELETED:
                return TASK_STATETYPE_DELETED;
            default:
                return TASK_STATETYPE_ALL;
        }
    }
}
