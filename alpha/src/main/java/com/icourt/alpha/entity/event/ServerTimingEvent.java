package com.icourt.alpha.entity.event;

import android.support.annotation.StringDef;

import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.utils.StringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description  网络计时同步
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/25
 * version 2.0.0
 */
public class ServerTimingEvent extends TimeEntity.ItemEntity {

    public static final String TIMING_SYNC = "TIMING_SYNC";
    public static final String TIMING_SYNC_START = "TIMING_SYNC_START";
    public static final String TIMING_SYNC_EDIT = "TIMING_SYNC_EDIT";
    public static final String TIMING_SYNC_DELETE = "TIMING_SYNC_DELETE";
    public static final String TIMING_SYNC_CLOSE_BUBBLE = "TIMING_CLOSE_BUBBLE";
    public static final String TIMING_SYNC_NO_REMIND = "TIMING_NO_REMIND";
    public static final String TIMING_SYNC_TOO_LONG = "TIMING_TOO_LONG";

    @StringDef({TIMING_SYNC,
            TIMING_SYNC_START,
            TIMING_SYNC_EDIT,
            TIMING_SYNC_DELETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TIMING_SYNC_SCENE {

    }

    public String id;

    public String object;

    public String type;

    @TIMING_SYNC_SCENE
    public String scene;

    public String clientId;

    public String content;//消息内容，如：计时已经超过20小时，请注意劳逸结合

    /**
     * 是否是同步对象
     *
     * @return
     */
    public boolean isSyncObject() {
        return StringUtils.equalsIgnoreCase(object, "SYNC", false);
    }

    /**
     * 是否是计时同步
     *
     * @return
     */
    public boolean isSyncTimingType() {
        return StringUtils.equalsIgnoreCase(type, "TIMING_SYNC", false);
    }

    /**
     * 是否是显示计时提醒
     *
     * @return
     */
    public boolean isBubbleSync() {
        return StringUtils.equals(type, "BUBBLE_SYNC", false);
    }

    @Override
    public String toString() {
        return "ServerTimingEvent{" +
                "object='" + object + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
