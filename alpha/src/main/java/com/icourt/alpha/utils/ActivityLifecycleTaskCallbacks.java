package com.icourt.alpha.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Description  Activity堆栈存储
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public class ActivityLifecycleTaskCallbacks
        extends ActivityLifecycleLogCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        AppManager.getAppManager().removeActivity(activity);
    }
}
