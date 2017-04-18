package com.icourt.alpha.interfaces;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

/**
 * Description 定义过时的resource资源获取方式
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/14
 * version
 */

public interface IContextResourcesImp {

    int getContextColor(@ColorRes int id);

    int getContextColor(@ColorRes int id, @ColorInt int defaultColor);

    @Nullable
    Drawable getDrawable(Context context, @DrawableRes int id);

}
