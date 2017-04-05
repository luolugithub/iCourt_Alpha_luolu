package com.icourt.alpha.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.CallSuper;

/**
 * Description activity生命周期日志
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public class ActivityLifecycleLogCallbacks
        implements Application.ActivityLifecycleCallbacks {

    @CallSuper
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        LogUtils.d("===========>onActivityCreated:" + activity + " savedInstanceState:" + savedInstanceState);
    }

    @CallSuper
    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.d("===========>onActivityStarted:" + activity);
    }

    @CallSuper
    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.d("===========>onActivityResumed:" + activity);
    }

    @CallSuper
    @Override
    public void onActivityPaused(Activity activity) {
        LogUtils.d("===========>onActivityPaused:" + activity);
    }

    @CallSuper
    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.d("===========>onActivityStopped:" + activity);
    }

    @CallSuper
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LogUtils.d("===========>onActivitySaveInstanceState:" + activity);
    }

    @CallSuper
    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.d("===========>onActivityDestroyed:" + activity);
    }
}
