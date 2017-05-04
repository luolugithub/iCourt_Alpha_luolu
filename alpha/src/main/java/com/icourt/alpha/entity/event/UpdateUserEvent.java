package com.icourt.alpha.entity.event;

/**
 * Description  更新用户信息event
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class UpdateUserEvent {

    public String user_pic;
    public String user_phone;
    public String user_email;

    public UpdateUserEvent(String user_pic, String user_phone, String user_email) {
        this.user_pic = user_pic;
        this.user_phone = user_phone;
        this.user_email = user_email;
    }
}
