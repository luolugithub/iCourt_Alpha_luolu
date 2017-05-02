package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionAction;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.io.Serializable;

/**
 * Description  联系人模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class GroupContactBean
        implements IConvertModel<ContactDbModel>, Serializable, ISuspensionInterface, ISuspensionAction {

    public String suspensionTag;

    public String accid;
    @SerializedName(value = "userId", alternate = "user_id")
    public String userId;
    public String name;
    public String phone;
    public String email;
    public String pic;
    public int robot;

    public GroupContactBean() {
    }

    public GroupContactBean(String accid, String userId, String name, String phone, String email, String pic, int robot) {
        this.accid = accid;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pic = pic;
        this.robot = robot;
    }

    @Override
    public String toString() {
        return "GroupContactBean{" +
                "suspensionTag='" + suspensionTag + '\'' +
                ", accid='" + accid + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", pic='" + pic + '\'' +
                ", robot=" + robot +
                '}';
    }

    @Override
    public ContactDbModel convert2Model() {
        if (TextUtils.isEmpty(accid)) return null;
        return new ContactDbModel(accid, userId,
                null,
                name,
                phone,
                email,
                pic,
                robot);
    }

    @Override
    public boolean isShowSuspension() {
        return true;
    }

    @NonNull
    @Override
    public String getSuspensionTag() {
        return TextUtils.isEmpty(suspensionTag) ? "#" : suspensionTag;
    }

    @Override
    public String getTargetField() {
        return name;
    }

    @Override
    public void setSuspensionTag(@NonNull String suspensionTag) {
        this.suspensionTag = suspensionTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final GroupContactBean other = (GroupContactBean) o;
        return TextUtils.equals(this.accid, other.accid);
    }
}
