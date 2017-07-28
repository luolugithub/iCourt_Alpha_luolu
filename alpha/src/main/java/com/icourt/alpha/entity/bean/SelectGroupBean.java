package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Description 选择团队模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/22
 * version 2.0.0
 */

public class SelectGroupBean implements Serializable {

    @SerializedName(value = "groupId", alternate = {"id"})
    public String groupId;
    @SerializedName(value = "groupName", alternate = {"groupname","name"})
    public String groupName;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        SelectGroupBean groupBean = (SelectGroupBean) obj;
        if (TextUtils.equals(this.groupId, groupBean.groupId)) {
            return true;
        }
        return false;
    }
}
