package com.icourt.alpha.view.recyclerviewDivider;

import android.support.annotation.NonNull;

/**
 * Description  {@link ChatItemDecoration}
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/29
 * version 1.0.0
 */
public interface ITimeDividerInterface {
    /**
     * 是否展示时间分割线
     *
     * @param pos
     * @return
     */
    boolean isShowTimeDivider(int pos);

    /**
     * 展示的时间
     *
     * @param pos
     * @return
     */
    @NonNull
    String getShowTime(int pos);


}
