package com.icourt.alpha.entity.event;

/**
 * Description  置顶事件
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public class SetTopEvent {
    public boolean isSetTop;
    public String id;

    public SetTopEvent(boolean isSetTop, String id) {
        this.isSetTop = isSetTop;
        this.id = id;
    }
}
