package com.icourt.alpha.entity.event;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/15
 * version 1.0.0
 */
public class OverTimingRemindEvent {
    public static final int ACTION_TIMING_REMIND_NO_REMIND = 100;
    public static final int ACTION_SHOW_TIMING_REMIND = 101;
    public static final int ACTION_SYNC_BUBBLE_CLOSE_TO_SERVER = 102;

    public int action;
    public long timeSec;

    public OverTimingRemindEvent(int action) {
        this.action = action;
    }

    public OverTimingRemindEvent(int action, long timeSec) {
        this(action);
        this.timeSec = timeSec;
    }
}