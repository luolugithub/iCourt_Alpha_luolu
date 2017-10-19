package com.icourt.alpha.http.callback;

import android.support.annotation.CallSuper;

import com.icourt.alpha.http.exception.ResponseException;

import retrofit2.Call;
import retrofit2.Response;


/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-04-20 18:38
 */
public abstract class SimpleCallBack2<T> extends BaseCallBack<T> {

    @CallSuper
    @Override
    protected void dispatchHttpSuccess(Call<T> call, Response<T> response) {
        if (response.body() != null) {
            onSuccess(call, response);
        } else {
            onFailure(call, new ResponseException(-1, "响应为null"));
        }
    }

}
