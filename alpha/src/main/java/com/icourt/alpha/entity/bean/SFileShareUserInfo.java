package com.icourt.alpha.entity.bean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/17
 * version 2.1.0
 */
public class SFileShareUserInfo {
    public static class UserInfo {
        public String nickName;
        public String name;
        public String pic;
        public String userId;
    }

    public String share_type;
    public String permission;
    public UserInfo userInfo;
}
