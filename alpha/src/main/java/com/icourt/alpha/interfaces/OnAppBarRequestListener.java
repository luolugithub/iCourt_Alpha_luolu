package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/15
 * version 1.0.0
 */
public interface OnAppBarRequestListener {

    AppBarLayout getAppBarLayout(Fragment fragment, int type, Bundle bundle);

    void onRequestAppBarLayoutExpand(boolean expand, boolean animate);
}
