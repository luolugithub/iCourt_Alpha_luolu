package com.icourt.alpha.db.dbmodel;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.entity.bean.GroupContactBean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Description  联系人数据库模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ContactDbModel extends RealmObject
        implements IConvertModel<GroupContactBean> {

    @PrimaryKey
    public String userId;
    public String userName;
    public String name;
    public String phone;
    public String email;
    public String pic;
    public int robot;

    public ContactDbModel(String userId, String userName, String name, String phone, String email, String pic, int robot) {
        this.userId = userId;
        this.userName = userName;
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
        return new GroupContactBean(userId,
                userName,
                name,
                phone,
                email,
                pic,
                robot);
    }
}
