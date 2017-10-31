package com.icourt.alpha.interfaces.callback;

import android.text.TextUtils;

import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.http.httpmodel.ResEntity;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public abstract class AppUpdateCallBack extends BaseCallBack<ResEntity<AppVersionEntity>> {

    @Override
    protected final void dispatchHttpSuccess(Call<ResEntity<AppVersionEntity>> call, Response<ResEntity<AppVersionEntity>> response) {
        if (response.body() != null&&response.body().result!=null) {
            if (!TextUtils.isEmpty(response.body().result.upgradeUrl)) {
                onSuccess(call, response);
            } else {
                onFailure(call, new ResponseException(-100, "下载地址为null"));
            }
        } else {
            onFailure(call, new ResponseException(-1, "响应为null"));
        }
    }
}
