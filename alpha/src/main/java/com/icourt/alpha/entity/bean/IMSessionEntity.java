package com.icourt.alpha.entity.bean;

import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class IMSessionEntity {

    public Team team;//群对象 与contactBean互斥
    public RecentContact recentContact;
    public IMBodyEntity customIMBody;//自定义消息体 请提前解析
    public GroupContactBean contactBean;//联系人实体对象

    public IMSessionEntity(Team team, RecentContact recentContact, IMBodyEntity customIMBody, GroupContactBean contactBean) {
        this.team = team;
        this.recentContact = recentContact;
        this.customIMBody = customIMBody;
        this.contactBean = contactBean;
    }

    @Override
    public String toString() {
        return "IMSessionEntity{" +
                "team=" + team +
                ", recentContact=" + recentContact +
                ", customIMBody=" + customIMBody +
                ", contactBean=" + contactBean +
                '}';
    }
}
