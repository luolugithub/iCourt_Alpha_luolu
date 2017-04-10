package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-08-29 18:39
 * tab 双击刷新
 */
public interface OnTabDoubleClickListener {

    /**
     * @param targetFragment
     * @param v
     * @param bundle
     */
    void onTabDoubleClick(Fragment targetFragment, View v, Bundle bundle);
}
