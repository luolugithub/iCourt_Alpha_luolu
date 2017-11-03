package com.icourt.alpha.interfaces.callback;

import android.text.TextUtils;

import com.icourt.alpha.entity.bean.AppVersionFirEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.exception.ResponseException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：2017/11/3
 * version 2.2.1
 */
public abstract class AppUpdateByFirCallBack extends BaseCallBack<AppVersionFirEntity> {

    @Override
    protected final void dispatchHttpSuccess(Call<AppVersionFirEntity> call, Response<AppVersionFirEntity> response) {
        if (response.body() != null) {
            if (!TextUtils.isEmpty(response.body().install_url)) {
                onSuccess(call, response);
            } else {
                onFailure(call, new ResponseException(-100, "下载地址为null"));
            }
        } else {
            onFailure(call, new ResponseException(-1, "响应为null"));
        }
    }
}
