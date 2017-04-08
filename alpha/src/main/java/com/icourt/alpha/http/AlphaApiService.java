package com.icourt.alpha.http;

import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.httpmodel.ResEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
     * demo 数据获取[非分页]
     *
     * @param id
     * @return
     */
    @GET("api/v1/auth/groups")
    Call<ResEntity<String>> getData(@Query("id") int id);


    /**
     * demo 分页数据获取
     *
     * @param id
     * @return
     */
    @GET("api/v1/auth/groups")
    Call<ResEntity<List<String>>> getPageData(@Query("id") int id);

}
