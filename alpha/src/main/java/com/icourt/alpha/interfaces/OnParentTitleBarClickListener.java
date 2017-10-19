package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/8
 * version 2.1.0
 */
public interface OnParentTitleBarClickListener {

    /**
     * 返回
     *
     * @param parent
     * @param v
     * @return
     */
    boolean onParentTitleBack(Object parent, View v, int type, @Nullable Bundle bundle);

    /**
     * 点击标题
     *
     * @param parent
     * @param v
     * @return
     */
    void onParentTitleClick(Object parent, View v, int type, @Nullable Bundle bundle);

    /**
     * 动作按钮1
     *
     * @param parent
     * @param v
     * @return
     */
    void onParentTitleActionClick(Object parent, View v, int type, @Nullable Bundle bundle);

    /**
     * 动作按钮2
     *
     * @param parent
     * @param v
     * @return
     */
    void onParentTitleActionClick2(Object parent, View v, int type, @Nullable Bundle bundle);

}
