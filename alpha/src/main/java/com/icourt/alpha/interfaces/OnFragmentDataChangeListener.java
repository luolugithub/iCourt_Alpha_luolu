package com.icourt.alpha.interfaces;

import android.support.v4.app.Fragment;

/**
 * Description  fragment中适配器数据发生改变监听器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/19
 * version 2.1.0
 */
public interface OnFragmentDataChangeListener {
    /**
     * @param fragment
     * @param type     扩展类型
     * @param o        数据对象 一般是list
     */
    void onFragmentDataChanged(Fragment fragment, int type, Object o);
}
