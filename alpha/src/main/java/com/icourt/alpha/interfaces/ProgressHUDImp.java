package com.icourt.alpha.interfaces;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * Description  加载对话框定义
 * Company Beijing
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/2/4
 * version
 */

public interface ProgressHUDImp {

    /**
     * 展示加载对话框
     *
     * @param notice
     */
    void showLoadingDialog(@Nullable String notice);

    /**
     * 结束展示对话框
     */
    void dismissLoadingDialog();

    /**
     * 是否正在展示加载对话框
     *
     * @return
     */
    boolean isShowLoading();
}
