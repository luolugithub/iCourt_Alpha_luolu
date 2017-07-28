package com.icourt.alpha.view.recyclerviewDivider;

import android.support.annotation.NonNull;

/**
 * 介绍：分类悬停的接口
 */
public interface ISuspensionInterface  extends ISuspensionAction{
    /**
     * 是否需要显示悬停title
     */
    boolean isShowSuspension();


    /**
     * 悬停的title
     *
     * @return
     */
    @NonNull
    String getSuspensionTag();



}