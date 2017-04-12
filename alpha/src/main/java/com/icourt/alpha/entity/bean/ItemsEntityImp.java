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

    /*public CharSequence itemTitle;
    public int ItemType;
    public int ItemIconRes;
    public String ItemIcon;

    public ItemsEntity(CharSequence itemTitle, int itemIconRes) {
        this.itemTitle = itemTitle;
        ItemIconRes = itemIconRes;
    }

    public ItemsEntity(CharSequence itemTitle, int itemType, int itemIconRes) {
        this.itemTitle = itemTitle;
        ItemType = itemType;
        ItemIconRes = itemIconRes;
    }*/
    /**
     * item的标题
     *
     * @return
     */
    public abstract CharSequence getItemTitle();

    /**
     * item的类型
     *
     * @return
     */
    public abstract int getItemType();

    /**
     * item的icon
     *
     * @return
     */
    @DrawableRes
    public abstract int getItemIconRes();

    /**
     * item的网络图片
     *
     * @return
     */
    public abstract String getItemIcon();
}
