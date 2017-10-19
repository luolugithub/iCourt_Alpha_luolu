package com.icourt.alpha.http.observer;

import android.support.annotation.CallSuper;

import com.icourt.alpha.http.IDefNotify;
import com.icourt.alpha.http.HttpThrowableUtils;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/28
 * version 2.1.0
 */
public abstract class BaseObserver<T> implements Observer<T>, IDefNotify {

    @Override
    public void onSubscribe(@NonNull Disposable disposable) {

    }

    @CallSuper
    @Override
    public void onError(@NonNull Throwable throwable) {
        HttpThrowableUtils.handleHttpThrowable(this, throwable);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void defNotify(String noticeStr) {
        HttpThrowableUtils.defaultNotify(noticeStr);
    }
}
