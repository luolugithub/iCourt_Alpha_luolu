package com.icourt.alpha.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/9
 * version 2.1.0
 */
public class TimingConfig {
    public static final int TIMING_QUERY_BY_DAY = 0;
    public static final int TIMING_QUERY_BY_WEEK = 1;
    public static final int TIMING_QUERY_BY_MONTH = 2;
    public static final int TIMING_QUERY_BY_YEAR = 3;

    @IntDef({TIMING_QUERY_BY_DAY,
            TIMING_QUERY_BY_WEEK,
            TIMING_QUERY_BY_MONTH,
            TIMING_QUERY_BY_YEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TIMINGQUERYTYPE {
    }

    /**
     * 转换
     *
     * @param timingQueryType
     * @return
     */
    @TIMINGQUERYTYPE
    public static final int convert2timingQueryType(int timingQueryType) {
        switch (timingQueryType) {
            case TIMING_QUERY_BY_DAY:
                return TIMING_QUERY_BY_DAY;
            case TIMING_QUERY_BY_WEEK:
                return TIMING_QUERY_BY_WEEK;
            case TIMING_QUERY_BY_MONTH:
                return TIMING_QUERY_BY_MONTH;
            case TIMING_QUERY_BY_YEAR:
                return TIMING_QUERY_BY_YEAR;
            default:
                return TIMING_QUERY_BY_DAY;
        }
    }

    /**
     * 计时标题最大长度
     */
    public static final int TIMING_NAME_MAX_LENGTH=200;

}
