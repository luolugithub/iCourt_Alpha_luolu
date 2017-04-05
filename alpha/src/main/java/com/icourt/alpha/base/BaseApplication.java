package com.icourt.alpha.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.http.HConst;
import com.icourt.alpha.utils.ActivityLifecycleTaskCallbacks;
import com.icourt.alpha.utils.AppManager;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.logger.AndroidLogAdapter;
import com.icourt.alpha.utils.logger.LogLevel;
import com.icourt.alpha.utils.logger.Logger;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version
 */

public class BaseApplication extends MultiDexApplication {

    {// 友盟登陆/分享初始化
        PlatformConfig.setWeixin(HConst.WX_APPID, HConst.WX_APPSECRET);
        //PlatformConfig.setQQZone("1104872033", "lLB4ODaOnpLNzIxD");
        Config.isJumptoAppStore = false; //其中qq 微信会跳转到下载界面进行下载，其他应用会跳到应用商店进行下载
        Log.LOG = BuildConfig.IS_DEBUG;//umeng sdk日志跟踪
    }

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleTaskCallbacks());
        UMShareAPI.get(this);
        initLogger();
    }

    public static BaseApplication getApplication() {
        return baseApplication;
    }

    /**
     * 初始化比较友好的日志工具
     */
    private void initLogger() {
        Logger.init("logger")                 // default PRETTYLOGGER or use just init()
                .methodCount(0)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(BuildConfig.IS_DEBUG ? LogLevel.FULL : LogLevel.NONE)        // default LogLevel.FULL
                .methodOffset(0)                // default 0
                .logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
    }

}
