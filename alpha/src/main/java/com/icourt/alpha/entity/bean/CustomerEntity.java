package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class CustomerEntity extends IndexPinYinActionEntity implements IConvertModel<CustomerDbModel>, Serializable {

    public String pkid;
    public String contactType;
    public String name;
    public String sex;

    public CustomerEntity() {
    }

    public CustomerEntity(String pkid, String contactType, String name, String sex) {
        this.pkid = pkid;
        this.contactType = contactType;
        this.name = name;
        this.sex = sex;
    }

    @Override
    public CustomerDbModel convert2Model() {
        if (!TextUtils.isEmpty(pkid)) {
            return new CustomerDbModel(pkid, contactType, name, sex);
        }
        return null;
    }


    @Override
    public String getTarget() {
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return "#";
    }

    @Override
    public String getSuspensionTag() {
        String suspensionTag = super.getSuspensionTag();
        return TextUtils.isEmpty(suspensionTag) ? "" : suspensionTag;
    }
}
