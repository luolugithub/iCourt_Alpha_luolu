package com.icourt.alpha.widget.filter;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public interface BaseFilter<T> {

    @Nullable
    @CheckResult
    List<T> filter(@Nullable List<T> data, int type);

    @Nullable
    @CheckResult
    boolean filter(@Nullable List<T> data, T t);

    @Nullable
    @CheckResult
    List<T> filterEmpty(@Nullable List<T> data);
}
