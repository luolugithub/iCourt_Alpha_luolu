package com.icourt.alpha.entity.event;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/15
 * version 1.0.0
 */
public class OverTimingRemindEvent {
    public static final int STATUS_TIMING_REMIND_CLOSE = 2;
    public static final int STATUS_TIMING_REMIND_HIDE = 3;

    public int action;
    public int status;

    public OverTimingRemindEvent(int status) {
        this.status = status;
    }
}