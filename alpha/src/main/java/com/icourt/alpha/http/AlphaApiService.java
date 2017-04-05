package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-02 14:26
 * <p>
 * 分页公共参数 整形  请大家按照这个【顺序】写
 * @Field("start") int start,
 * @Field("limit") int limit,
 * @Field("maxId") int maxId,
 */
public interface AlphaApiService {


    /**
     * 获取新版本app
     * 文档参考 https://fir.im/docs/version_detection
     *
     * @param url fir地址
     * @return
     */
    @GET
    Call<AppVersionEntity> getNewVersionAppInfo(
            @Url String url);

    /**
     * 获取组详情
     *
     * @param id 组id
     * @return
     */
    @GET("api/v1/auth/groups")
    Call<ResEntity<JsonElement>> getGroups(@Query("id") int id);
}
