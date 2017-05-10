package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public interface INotifyFragment {

    /**
     * 通知fragment更新
     *
     * @param targetFrgament
     * @param type
     * @param inBundle
     */
    void notifyFragmentUpdate(Fragment targetFrgament, int type, @Nullable Bundle inBundle);

    /**
     * 获取fragment选中的数据
     *
     * @param type
     * @param inBundle
     * @return
     */
    @CheckResult
    @Nullable
    Bundle getFragmentData(int type, @Nullable Bundle inBundle);
}
