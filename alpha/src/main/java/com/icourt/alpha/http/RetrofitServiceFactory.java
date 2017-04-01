package com.icourt.alpha.http;


/**
 * Created by Administrator on 2016/4/27.
 */
public class RetrofitServiceFactory {

    public static AlphaApiService alphaApiService;

    public static AlphaApiService provideAlphaService() {
        if (alphaApiService == null) {
            alphaApiService = AlphaClient.getInstance().createService(AlphaApiService.class);
        }
        return alphaApiService;
    }
}

