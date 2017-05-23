package com.icourt.alpha.entity.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Description 选择团队模型
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/22
 * version 2.0.0
 */

public class SelectGroupBean implements Serializable{

    public String groupId;
    @SerializedName(value = "groupName", alternate = {"groupname"})
    public String groupName;

}
