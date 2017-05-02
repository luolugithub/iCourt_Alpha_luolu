package com.icourt.alpha.db.dbmodel;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.entity.bean.CustomerEntity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
@RealmClass
public class CustomerDbModel extends RealmObject implements IConvertModel<CustomerEntity> {
    @PrimaryKey
    public String pkid;
    public String contactType;
    public String name;
    public String sex;

    public CustomerDbModel(String pkid, String contactType, String name, String sex) {
        this.pkid = pkid;
        this.contactType = contactType;
        this.name = name;
        this.sex = sex;
    }

    public CustomerDbModel() {
    }

    @Override
    public CustomerEntity convert2Model() {
        return new CustomerEntity(pkid, contactType, name, sex);
    }
}
