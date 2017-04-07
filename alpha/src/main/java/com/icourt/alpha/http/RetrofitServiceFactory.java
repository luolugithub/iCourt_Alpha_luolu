package com.icourt.alpha.http;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/29
 * version
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

