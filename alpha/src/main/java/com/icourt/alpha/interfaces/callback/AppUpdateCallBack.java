package com.icourt.alpha.interfaces.callback;

import android.text.TextUtils;

import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.exception.ResponseException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public abstract class AppUpdateCallBack extends BaseCallBack<AppVersionEntity> {

    @Override
    protected final void dispatchHttpSuccess(Call<AppVersionEntity> call, Response<AppVersionEntity> response) {
        if (response.body() != null) {
            if (!TextUtils.isEmpty(response.body().upgradeUrl)) {
                onSuccess(call, response);
            } else {
                onFailure(call, new ResponseException(-100, "下载地址为null"));
            }
        } else {
            onFailure(call, new ResponseException(-1, "响应为null"));
        }
    }
}
