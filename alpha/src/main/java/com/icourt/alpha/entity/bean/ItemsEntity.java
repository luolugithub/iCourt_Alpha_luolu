package com.icourt.alpha.entity.bean;

/**
 * Description  item实体模版
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/12
 * version 1.0.0
 */
public class ItemsEntity implements ItemsEntityImp {

    public CharSequence itemTitle;
    public int itemType;
    public int itemIconRes;
    public String itemIcon;

    public ItemsEntity(CharSequence itemTitle, int itemIconRes) {
        this.itemTitle = itemTitle;
        this.itemIconRes = itemIconRes;
    }

    public ItemsEntity(CharSequence itemTitle, int itemType, int itemIconRes) {
        this.itemTitle = itemTitle;
        this.itemType = itemType;
        this.itemIconRes = itemIconRes;
    }

    @Override
    public CharSequence getItemTitle() {
        return itemTitle;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    @Override
    public int getItemIconRes() {
        return itemIconRes;
    }

    @Override
    public String getItemIcon() {
        return itemIcon;
    }
}
