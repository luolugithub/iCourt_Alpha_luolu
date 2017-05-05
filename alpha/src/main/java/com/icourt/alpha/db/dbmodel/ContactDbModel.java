package com.icourt.alpha.db.dbmodel;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.entity.bean.GroupContactBean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Description  联系人数据库模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
@RealmClass
public class ContactDbModel extends RealmObject
        implements IConvertModel<GroupContactBean> {

    @PrimaryKey
    public String accid;

    public String userId;
    public String userName;
    public String name;
    public String title;
    public String phone;
    public String email;
    public String pic;
    public int robot;

    public int extInt1;
    public int extInt2;
    public int extInt3;
    public int extInt4;

    public int extString1;
    public int extString2;
    public int extString3;
    public int extString4;

    public boolean extBoolean1;
    public boolean extBoolean2;

    public ContactDbModel(String accid, String userId, String userName, String name, String title, String phone, String email, String pic, int robot) {
        this.accid = accid;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.pic = pic;
        this.robot = robot;
    }

    public ContactDbModel() {
    }


    @Override
    public GroupContactBean convert2Model() {
        return new GroupContactBean(accid, userId,
                name,
                title,
                phone,
                email,
                pic,
                robot);
    }

    @Override
    public String toString() {
        return "ContactDbModel{" +
                "accid='" + accid + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", pic='" + pic + '\'' +
                ", robot=" + robot +
                ", extInt1=" + extInt1 +
                ", extInt2=" + extInt2 +
                ", extInt3=" + extInt3 +
                ", extInt4=" + extInt4 +
                ", extString1=" + extString1 +
                ", extString2=" + extString2 +
                ", extString3=" + extString3 +
                ", extString4=" + extString4 +
                ", extBoolean1=" + extBoolean1 +
                ", extBoolean2=" + extBoolean2 +
                '}';
    }
}
