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

public class ProjectActionEvent {

    public static final int PROJECT_DELETE_ACTION = 1;
    public static final int PROJECT_REFRESG_ACTION = 2;

    @IntDef({PROJECT_DELETE_ACTION,
            PROJECT_REFRESG_ACTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PROJEDCT_ACTION {

    }

    public int action;
    public String id;
    public String desc;

    public ProjectActionEvent(@PROJEDCT_ACTION int action) {
        this.action = action;
    }

}
