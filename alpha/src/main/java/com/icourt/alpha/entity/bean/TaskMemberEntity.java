package com.icourt.alpha.entity.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Description  任务查看对象
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public class TaskMemberEntity implements Serializable {
    @SerializedName(value = "userId", alternate = {"memberPkId"})
    public String userId;
    @SerializedName(value = "userName", alternate = {"name"})
    public String userName;
    @SerializedName(value = "userPic", alternate = {"pic"})
    public String userPic;
}
