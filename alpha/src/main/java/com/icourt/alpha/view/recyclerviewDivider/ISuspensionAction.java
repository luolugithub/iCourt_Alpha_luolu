package com.icourt.alpha.view.recyclerviewDivider;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public interface ISuspensionAction {

    /**
     * 获取目标字段
     *
     * @return
     */
    @CheckResult
    @Nullable
    String getTargetField();

    /**
     * 设置悬停的title
     *
     * @param suspensionTag
     */
    void setSuspensionTag(String suspensionTag);
}
