package com.icourt.alpha.entity.bean;

import android.support.annotation.DrawableRes;

/**
 * Description  item实体模版
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/12
 * version 1.0.0
 */
public interface ItemsEntityImp {

    /**
     * item的标题
     *
     * @return
     */
    CharSequence getItemTitle();

    /**
     * item的类型
     *
     * @return
     */
    int getItemType();

    /**
     * item的icon
     *
     * @return
     */
    @DrawableRes
    int getItemIconRes();

    /**
     * item的网络图片
     *
     * @return
     */
    String getItemIcon();

    /**
     * 是否被选中
     *
     * @return
     */
    boolean isChecked();
}
