package com.icourt.alpha.http.consumer;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;

import com.icourt.alpha.http.IDefNotify;
import com.icourt.alpha.http.HttpThrowableUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Description   网络处理异常基类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/28
 * version 2.1.0
 */
public class BaseThrowableConsumer implements Consumer<Throwable>, IDefNotify {

    @CallSuper
    @Override
    public void accept(@NonNull Throwable t) throws Exception {
        HttpThrowableUtils.handleHttpThrowable(this, t);
    }


    /**
     * 系统可能屏蔽通知[典型的华为]
     * 替代为Snackbar
     *
     * @param noticeStr
     */
    @UiThread
    @Override
    public void defNotify(String noticeStr) {
        HttpThrowableUtils.defaultNotify(noticeStr);
    }
}
