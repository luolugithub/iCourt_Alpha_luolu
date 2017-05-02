package com.icourt.alpha.entity.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class GroupActionEvent {
    public static final int GROUP_ACTION_JOIN = 1;
    public static final int GROUP_ACTION_QUIT = 2;

    @IntDef({GROUP_ACTION_JOIN,
            GROUP_ACTION_QUIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GROUP_ACTION {

    }

    @GROUP_ACTION
    public int action;
    public String groupId;

    public GroupActionEvent(@GROUP_ACTION int action, String groupId) {
        this.action = action;
        this.groupId = groupId;
    }
}
