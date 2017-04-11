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

    public Team team;
    public RecentContact recentContact;

    public IMSessionEntity(Team team, RecentContact recentContact) {
        this.team = team;
        this.recentContact = recentContact;
    }
}
