package com.icourt.alpha.utils;

import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.SimpleCallBack2;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  alpha用户换sfile token
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class SFileTokenUtils {
    private static final String KEY_TOKEN = "KEY_SFILE_TOKEN_%s";

    /**
     * 获取alpha用户对应的sfile token
     *
     * @return
     */
    public static final String getSFileToken() {
        String loginUserId = LoginInfoUtils.getLoginUserId();
        return SpUtils.getInstance().getStringData(String.format(KEY_TOKEN, loginUserId), "");
    }

    /**
     * 获取alpha用户对应的sfile token
     *
     * @param alphaUserId
     * @return
     */
    public static final String getSFileToken(String alphaUserId) {
        return SpUtils.getInstance().getStringData(String.format(KEY_TOKEN, alphaUserId), "");
    }

    /**
     * 保存alpha用户对应的sfile token
     *
     * @param sFileToken
     */
    public static final void putSFileToken(String sFileToken) {
        String loginUserId = LoginInfoUtils.getLoginUserId();
        SpUtils.getInstance().putData(String.format(KEY_TOKEN, loginUserId), sFileToken);
    }

    /**
     * 保存alpha用户对应的sfile token
     *
     * @param alphaUserId
     * @param sFileToken
     */
    public static final void putSFileToken(String alphaUserId, String sFileToken) {
        SpUtils.getInstance().putData(String.format(KEY_TOKEN, alphaUserId), sFileToken);
    }

    /**
     * 同步 alpha用户对应的sfile token
     */
    public static final void syncServerSFileToken() {
        RetrofitServiceFactory
                .getAlphaApiService()
                .documentTokenQuery()
                .enqueue(new SimpleCallBack2<SFileTokenEntity<String>>() {
                    @Override
                    public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                        putSFileToken(response.body().authToken);
                        AlphaClient.setSFileToken(response.body().authToken);
                    }
                });
    }
}
