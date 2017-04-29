package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/28
 * version 1.0.0
 */
public final class IMMessageExtBody {
    //文件消息 file
    public String repo_id;
    public String path;
    public String name;
    public long size;


    //钉消息 pin
    public String id;
    public boolean pin;


    //@消息 AT
    public List<String> users;
    public boolean is_all;

    //系统辅助消息
    public String type;
    public String content;

    //链接图文消息 link
    public String title;
    public String thumb;
    public String desc;
    public String url;

    private IMMessageExtBody(List<String> users, boolean is_all) {
        this.users = users;
        this.is_all = is_all;
    }

    public IMMessageExtBody(String id, boolean pin) {
        this.id = id;
        this.pin = pin;
    }

    /**
     * 构建 扩展的at消息
     *
     * @param users
     * @param is_all
     * @return
     */
    public static IMMessageExtBody createAtExtBody(List<String> users, boolean is_all) {
        return new IMMessageExtBody(users, is_all);
    }

    /**
     * 构建 扩展的钉消息
     *
     * @param isPing
     * @param pingMsgId
     * @return
     */
    public static IMMessageExtBody createDingExtBody(boolean isPing, String pingMsgId) {
        return new IMMessageExtBody(pingMsgId, isPing);
    }


    public IMMessageExtBody() {
    }
}
