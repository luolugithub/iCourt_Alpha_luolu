package com.icourt.alpha.http.callback;

import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.http.httpmodel.ResEntity;

import retrofit2.Call;
import retrofit2.Response;


/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-04-20 18:38
 */
public abstract class SimpleCallBack<T> extends BaseCallBack<ResEntity<T>> {

    @CallSuper
    @Override
    protected void dispatchHttpSuccess(Call<ResEntity<T>> call, Response<ResEntity<T>> response) {
        if (response.body() != null && response.body().succeed) {
            onSuccess(call, response);
        } else {
            onFailure(call,
                    response.body() != null ?
                            new ResponseException(-2, TextUtils.isEmpty(response.body().message) ? "succeed=false;message=null" : response.body().message)
                            : new ResponseException(-1, "响应为null"));
        }
    }

}
