package com.icourt.alpha.entity.event;

import android.support.annotation.IntDef;

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

    public static final int TASK_DELETE_ACTION = 1;
    public static final int TASK_UPDATE_ACTION = 2;
    public static final int TASK_REFRESG_ACTION = 3;

    @IntDef({TASK_DELETE_ACTION,
            TASK_UPDATE_ACTION, TASK_REFRESG_ACTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TASK_ACTION {

    }

    @TASK_ACTION
    public int action;
    public String id;
    public String desc;

    public TaskActionEvent(@TASK_ACTION int action, String id, String desc) {
        this.action = action;
        this.id = id;
        this.desc = desc;
    }

    public TaskActionEvent(@TASK_ACTION int action) {
        this.action = action;
    }
}
