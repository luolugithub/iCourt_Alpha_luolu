package com.icourt.alpha.http.callback;

import android.support.annotation.CallSuper;

import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SFileTokenUtils;

import retrofit2.Call;
import retrofit2.HttpException;

/**
 * Description  sfile token 403重试
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public abstract class SFileCallBack<T> extends SimpleCallBack2<T> {
    private final int MAX_RETRY_COUNT = 10;//最大重试10次 避免死循环
    private int retryCount;

    @CallSuper
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof HttpException
                && ((HttpException) t).code() == 403) {
            if (LoginInfoUtils.isUserLogin()
                    && retryCount < MAX_RETRY_COUNT) {
                SFileTokenUtils.syncServerSFileToken();
                retryCount++;
            }
        } else {
            super.onFailure(call, t);
        }
    }
}
