package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionAction;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class CustomerEntity implements IConvertModel<CustomerDbModel>,
        Serializable,
        ISuspensionInterface,
        ISuspensionAction {
    public String suspensionTag;

    public String pkid;
    public String contactType;
    public String name;
    public String sex;
    public String title;
    public String itemSubType;
    public String contactPkid;
    public String impression;

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
    public boolean isShowSuspension() {
        return true;
    }

    @Override
    public String getSuspensionTag() {
        return TextUtils.isEmpty(suspensionTag) ? "#" : suspensionTag;
    }

    @Nullable
    @Override
    public String getTargetField() {
        return name;
    }

    @Override
    public void setSuspensionTag(@NonNull String suspensionTag) {
        this.suspensionTag = suspensionTag;
    }
}
