package com.icourt.api;

import android.content.Context;

import com.icourt.api.impl.IRetrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-02 14:26
 */
public class BaseClient implements IRetrofit {

    private Retrofit mRetrofit;

    @Override
    public void attachBaseUrl(OkHttpClient client, String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                //.addConverterFactory(ProtoConverterFactory.create())//适合数据同步
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    @Override
    public <T> T createService(Class<T> clz) {
        return mRetrofit.create(clz);
    }

    @Override
    public void destory() {
        mRetrofit = null;
    }
}
