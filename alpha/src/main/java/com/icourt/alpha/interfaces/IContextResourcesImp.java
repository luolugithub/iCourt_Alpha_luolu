package com.icourt.alpha.interfaces;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Description 定义过时的resource资源获取方式,更安全
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/14
 * version
 */

public interface IContextResourcesImp {

    @NonNull
    int getContextColor(@ColorRes int id);


    @NonNull
    Drawable getContextDrawable(@DrawableRes int id);


    @NonNull
    CharSequence getContextString(@StringRes int id);
}
