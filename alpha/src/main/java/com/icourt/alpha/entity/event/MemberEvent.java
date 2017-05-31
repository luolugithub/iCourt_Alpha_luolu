package com.icourt.alpha.entity.event;

import com.netease.nimlib.sdk.msg.constant.NotificationType;

import java.util.ArrayList;

/**
 * Description  成员变化
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/31
 * version 1.0.0
 */
public class MemberEvent {


    public String sessionId;
    public NotificationType notificationType;
    /**
     * 被操作的成员帐号列表
     */
    public ArrayList<String> targets;

    public MemberEvent(String sessionId, NotificationType notificationType, ArrayList<String> targets) {
        this.sessionId = sessionId;
        this.notificationType = notificationType;
        this.targets = targets;
    }
}
