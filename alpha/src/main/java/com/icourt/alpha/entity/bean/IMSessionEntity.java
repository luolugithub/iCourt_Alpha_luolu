package com.icourt.alpha.entity.bean;

import com.netease.nimlib.sdk.msg.model.RecentContact;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class IMSessionEntity {

    public RecentContact recentContact;
    public IMMessageCustomBody customIMBody;//自定义消息体 请提前解析

    public boolean isNotDisturb;//是否消息免打扰

    public IMSessionEntity(RecentContact recentContact, IMMessageCustomBody customIMBody) {
        this.recentContact = recentContact;
        this.customIMBody = customIMBody;
    }

    @Override
    public String toString() {
        return "IMSessionEntity{" +
                ", recentContact=" + recentContact +
                ", customIMBody=" + customIMBody +
                '}';
    }
}
