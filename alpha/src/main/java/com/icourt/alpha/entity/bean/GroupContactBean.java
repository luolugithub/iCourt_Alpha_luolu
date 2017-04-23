package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

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
    public String userId;
    public String userName;
    public String name;
    public String phone;
    public String email;
    public String pic;
    public int robot;

    public GroupContactBean() {
    }

    public GroupContactBean(String userId, String userName, String name, String phone, String email, String pic, int robot) {
        this.userId = userId;
        this.userName = userName;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pic = pic;
        this.robot = robot;
    }

    @Override
    public String toString() {
        return "GroupContactBean{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", pic='" + pic + '\'' +
                ", robot=" + robot +
                '}';
    }


    @Override
    public ContactDbModel convert2Model() {
        if (TextUtils.isEmpty(userId)) return null;
        return new ContactDbModel(userId,
                userName,
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
}
