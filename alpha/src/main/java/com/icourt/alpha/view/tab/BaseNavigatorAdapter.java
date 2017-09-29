package com.icourt.alpha.view.tab;

import android.support.annotation.Nullable;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/24
 * version 2.1.0
 */
public abstract class BaseNavigatorAdapter extends CommonNavigatorAdapter {

    @Nullable
    public abstract CharSequence getTitle(int index);
}
