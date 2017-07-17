package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/12/22
 * version
 */

public interface OnPageFragmentChangeListener {

    /**
     * 设置选中的fragment
     * @param frgament
     * @param pos
     * @param bundle
     */
    void onPageFragmentSelected(Fragment frgament, int pos, Bundle bundle);
}
