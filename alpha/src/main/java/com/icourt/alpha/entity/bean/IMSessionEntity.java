package com.icourt.alpha.entity.bean;

import com.google.gson.annotations.Expose;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class IMSessionEntity {

    @Expose(serialize = false, deserialize = false)
    public RecentContact recentContact;
    public IMMessageCustomBody customIMBody;//自定义消息体 请提前解析

    /**
     * 是否是机器人
     *
     * @return
     */
    public boolean isRobot() {
        if (recentContact != null) {
            return recentContact.getMsgType() == MsgTypeEnum.custom
                    && recentContact.getAttachment() != null;
        }
        return false;
    }

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
