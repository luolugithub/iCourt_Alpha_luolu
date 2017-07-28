package com.icourt.alpha.entity.event;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/10
 * version 1.0.0
 */
public class MessageEvent {

    public static final int ACTION_MSG_CANCEL_COLLECT = 1;
    public int action;
    public long msgId;

    public MessageEvent(int action, long msgId) {
        this.action = action;
        this.msgId = msgId;
    }
}
