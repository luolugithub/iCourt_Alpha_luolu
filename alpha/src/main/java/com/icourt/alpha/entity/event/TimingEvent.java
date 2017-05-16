package com.icourt.alpha.entity.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/15
 * version 1.0.0
 */
public class TimingEvent {
    public static final int TIMING_UPDATE_PROGRESS = 1;
    public static final int TIMING_ADD = 2;
    public static final int TIMING_STOP = 3;
    public static final int TIMING_UPDATE_DATA = 4;

    public static final TimingEvent timingSingle = new TimingEvent("", TIMING_ADD);


    @IntDef({TIMING_UPDATE_PROGRESS,
            TIMING_ADD,
            TIMING_STOP,
            TIMING_UPDATE_DATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TIMING_ACTION {

    }

    public String timingId;
    @TIMING_ACTION
    public int action;
    public int timingTimeMillisecond;

    public TimingEvent(String timingId, int action) {
        this.timingId = timingId;
        this.action = action;
    }
}
