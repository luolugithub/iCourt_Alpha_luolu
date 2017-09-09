package com.icourt.alpha.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;

import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;

/**
 * Description  文档参考 https://fir.im/docs/version_detection
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/5
 * version 1.0.0
 */
public interface UpdateAppDialogNoticeImp {

    /**
     * 是否有文件写入权限
     *
     * @param context
     * @return
     */
    boolean hasFilePermission(@NonNull Context context);

    /**
     * 请求文件写入权限
     *
     * @param context
     * @param reqCode 请求码
     * @return
     */
    void requestFilePermission(@NonNull Context context, int reqCode);

    /**
     * 检查更新
     *
     * @param callBack
     */
    void checkAppUpdate(@NonNull BaseCallBack<ResEntity<AppVersionEntity>> callBack);

    /**
     * 检查更新 有更新 将自动调用showAppUpdateDialog(CharSequence newVersinInfo)
     *
     * @param context
     */
    void checkAppUpdate(@NonNull Context context);

    /**
     * app 更新提示
     *
     * @param context
     * @param appVersionEntity 新版本特性描述
     */
    void showAppUpdateDialog(@NonNull Context context, @NonNull AppVersionEntity appVersionEntity);

    /**
     * 本地是否包含apk文件
     *
     * @param url
     * @return
     */
    boolean hasLocalApkFile(String url);

    /**
     * 是否应该更新
     *
     * @param appVersionEntity
     * @return
     */
    boolean shouldUpdate(@NonNull AppVersionEntity appVersionEntity);

    /**
     * 新版本下载对话框
     *
     * @param context
     * @param newVersinApkUrl 新版本下载地址
     */
    void showAppDownloadingDialog(@NonNull Context context, @NonNull String newVersinApkUrl);
}
