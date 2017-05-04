package com.icourt.alpha.entity.event;

/**
 * Description  会话免打扰事件
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public class NoDisturbingEvent {
    public boolean isNoDisturbing;
    public String id;

    public NoDisturbingEvent(boolean isNoDisturbing, String id) {
        this.isNoDisturbing = isNoDisturbing;
        this.id = id;
    }
}
