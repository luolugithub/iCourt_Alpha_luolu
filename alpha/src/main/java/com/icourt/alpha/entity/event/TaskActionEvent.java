package com.icourt.alpha.entity.event;

import android.support.annotation.IntDef;

import com.icourt.alpha.entity.bean.TaskEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskActionEvent {

    public static final int TASK_DELETE_ACTION = 1;//删除任务的广播
    public static final int TASK_UPDATE_DESC_ACTION = 2;
    public static final int TASK_REFRESG_ACTION = 3;
    public static final int TASK_UPDATE_NAME_ACTION = 4;
    public static final int TASK_UPDATE_PROJECT_ACTION = 5;
    public static final int TASK_UPDATE_ITEM = 6;
    public static final int TASK_ADD_ITEM_ACITON = 7;
    public static final int TASK_PROJECT_END_OPERATE = 8;//项目里已完成任务列表的任务进行操作了，发送通知，项目下面的任务列表需要进行刷新操作。

    @IntDef({TASK_DELETE_ACTION,
            TASK_UPDATE_DESC_ACTION,
            TASK_REFRESG_ACTION,
            TASK_UPDATE_NAME_ACTION,
            TASK_UPDATE_PROJECT_ACTION,
            TASK_UPDATE_ITEM,
            TASK_ADD_ITEM_ACITON,
            TASK_PROJECT_END_OPERATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TASK_ACTION {

    }

    @TASK_ACTION
    public int action;
    public String id;
    public String desc;
    public TaskEntity.TaskItemEntity entity;
    public String projectId;

    public TaskActionEvent(@TASK_ACTION int action, String id, String desc) {
        this.action = action;
        this.id = id;
        this.desc = desc;
    }

    public TaskActionEvent(@TASK_ACTION int action) {
        this.action = action;
    }

    public TaskActionEvent(int action, String projectId) {
        this.action = action;
        this.projectId = projectId;
    }

    public TaskActionEvent(int action, TaskEntity.TaskItemEntity entity) {
        this.action = action;
        this.entity = entity;
    }
}
