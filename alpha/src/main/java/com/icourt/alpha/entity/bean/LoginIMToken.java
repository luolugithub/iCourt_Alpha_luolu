package com.icourt.alpha.entity.bean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class LoginIMToken {
    public String imToken;
    public String accid;

    @Override
    public String toString() {
        return "LoginIMToken{" +
                "imToken='" + imToken + '\'' +
                ", accid='" + accid + '\'' +
                '}';
    }
}
