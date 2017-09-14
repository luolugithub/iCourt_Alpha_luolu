package com.icourt.alpha.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.VersionDescAdapter;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.UpdateAppDialogNoticeImp;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ApkUtils;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.Md5Utils;
import com.icourt.alpha.utils.NetUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.utils.UrlUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class BaseAppUpdateActivity extends BaseUmengActivity implements
        UpdateAppDialogNoticeImp {
    public static final String UPDATE_APP_VERSION_KEY = "update_app_version_key";//版本更新版本号
    private AlertDialog updateNoticeDialog;
    private ProgressDialog updateProgressDialog;
    public static final int REQUEST_FILE_PERMISSION = 9999;

    private static final int UPGRADE_STRATEGY_UNCOMPEL_TYPE = 1;//非强制升级
    private static final int UPGRADE_STRATEGY_COMPEL_TYPE = 2;//强制升级

    @Override
    public final boolean hasFilePermission(@NonNull Context context) {
        return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public final void requestFilePermission(@NonNull Context context, int reqCode) {
        reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件写入权限！", reqCode);
    }

    @Override
    public final void checkAppUpdate(@NonNull BaseCallBack<ResEntity<AppVersionEntity>> callBack) {
        if (callBack == null) return;
        getApi().getNewVersionAppInfo()
                .enqueue(callBack);
    }

    @Override
    public final void checkAppUpdate(@NonNull final Context context, final String title) {
        if (context == null) return;
        checkAppUpdate(new SimpleCallBack<AppVersionEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<AppVersionEntity>> call, Response<ResEntity<AppVersionEntity>> response) {
                if (response.body().result == null) return;
                AppVersionEntity appVersionEntity = response.body().result;
                if (!TextUtils.equals(appVersionEntity.appVersion, SpUtils.getInstance().getStringData(UPDATE_APP_VERSION_KEY, ""))) {
                    //如果effectVersion（影响版本号）为null 或者 effectVersion 和本地版本好一致则显示更新对话框
                    if (TextUtils.isEmpty(appVersionEntity.effectVersion) || TextUtils.equals(appVersionEntity.effectVersion, BuildConfig.VERSION_NAME)) {
                        showAppUpdateDialog(getActivity(), response.body().result, title);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResEntity<AppVersionEntity>> call, Throwable t) {
                if (t instanceof HttpException) {
                    HttpException hx = (HttpException) t;
                    if (hx.code() == 401) {
                        showTopSnackBar("fir token 更改");
                        return;
                    }
                }
                super.onFailure(call, t);
            }
        });
    }

    @Override
    public boolean hasLocalApkFile(String url) {
        try {
            String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            String apkPath = ROOTPATH + Md5Utils.md5(url, url) + ".apk";
            File file = new File(apkPath);
            return file.exists();
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public final void showAppUpdateDialog(@NonNull final Context context, @NonNull final AppVersionEntity appVersionEntity, String title) {
        if (isDestroyOrFinishing()) return;
        if (updateNoticeDialog != null && updateNoticeDialog.isShowing()) return;
        if (!shouldUpdate(appVersionEntity)) return;
        showUpdateDescDialog(context, appVersionEntity, false);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(TextUtils.isEmpty(appVersionEntity.versionDesc) ? "有一个新版本,请立即更新吧" : appVersionEntity.versionDesc); //设置内容
//        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (hasFilePermission(context)) {
//                    MobclickAgent.onEvent(context, UMMobClickAgent.dialog_update_btn_click_id);
////                    getUpdateProgressDialog().setMax(appVersionEntity.binary != null ? (int) appVersionEntity.binary.fsize : 1_000);//下载进度条
//                    String updateUrl = UrlUtils.appendParam(appVersionEntity.upgradeUrl, "alphaNewApp", appVersionEntity.appVersion);
//                    showAppDownloadingDialog(getActivity(), updateUrl);
//                } else {
//                    requestFilePermission(context, REQUEST_FILE_PERMISSION);
//                }
//            }
//        });
//        if (shouldForceUpdate(appVersionEntity)) {
//            builder.setCancelable(false);
//        } else {
//            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    SpUtils.getInstance().remove(UPDATE_APP_VERSION_KEY);
//                    SpUtils.getInstance().putData(UPDATE_APP_VERSION_KEY, appVersionEntity.appVersion);
//                    dialog.dismiss();
//                }
//            });
//        }
//        updateNoticeDialog = builder.create();
//        updateNoticeDialog.show();
    }

    /**
     * 显示更新对话框
     *
     * @param appVersionEntity
     * @param isLookDesc
     */
    public void showUpdateDescDialog(@NonNull final Context context, @NonNull final AppVersionEntity appVersionEntity, final boolean isLookDesc) {
        if (appVersionEntity == null) return;

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_update_app_layout);
        TextView lastVersionTv = (TextView) window.findViewById(R.id.last_version_tv);
        TextView uploadTimeTv = (TextView) window.findViewById(R.id.last_version_uploadtime_tv);
        RecyclerView recyclerView = (RecyclerView) window.findViewById(R.id.last_version_content_recyclerview);
        TextView noUpdateTv = (TextView) window.findViewById(R.id.last_version_no_update_tv);
        TextView updateTv = (TextView) window.findViewById(R.id.last_version_update_tv);

        lastVersionTv.setText(appVersionEntity.appVersion);
        uploadTimeTv.setText(DateUtils.getTimeDateFormatYearDot(appVersionEntity.gmtCreate));

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        VersionDescAdapter versionDescAdapter = new VersionDescAdapter();
        recyclerView.setAdapter(versionDescAdapter);
        versionDescAdapter.bindData(true, appVersionEntity.versionDescs);

        if (isLookDesc) {
            noUpdateTv.setVisibility(View.GONE);
            updateTv.setVisibility(View.VISIBLE);
            updateTv.setText("关闭");
        } else {
            noUpdateTv.setVisibility(shouldForceUpdate(appVersionEntity) ? View.GONE : View.VISIBLE);
            updateTv.setVisibility(View.VISIBLE);
        }
        noUpdateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.getInstance().remove(UPDATE_APP_VERSION_KEY);
                SpUtils.getInstance().putData(UPDATE_APP_VERSION_KEY, appVersionEntity.appVersion);
                alertDialog.dismiss();
            }
        });
        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLookDesc) {
                    alertDialog.dismiss();
                } else {
                    if (hasFilePermission(context)) {
                        MobclickAgent.onEvent(context, UMMobClickAgent.dialog_update_btn_click_id);
                        String updateUrl = UrlUtils.appendParam(appVersionEntity.upgradeUrl, "alphaNewApp", appVersionEntity.appVersion);
                        showAppDownloadingDialog(getActivity(), updateUrl);
                    } else {
                        requestFilePermission(context, REQUEST_FILE_PERMISSION);
                    }
                }
            }
        });
    }

    /**
     * 是否强制更新
     * VERSION_CODE 如果大于本地版本 就强制更新
     *
     * @param appVersionEntity
     * @return
     */
    public final boolean shouldForceUpdate(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null && appVersionEntity.upgradeStrategy == UPGRADE_STRATEGY_COMPEL_TYPE;
    }

    /**
     * 是否非强制更新
     *
     * @param appVersionEntity
     * @return
     */
    @Override
    public final boolean shouldUpdate(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null && appVersionEntity.upgradeStrategy == UPGRADE_STRATEGY_UNCOMPEL_TYPE;
    }

    /**
     * 是否有最新版本
     *
     * @param appVersionEntity
     * @return
     */
    public boolean isUpdateApp(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null && !TextUtils.equals(appVersionEntity.appVersion, BuildConfig.VERSION_NAME);
    }

    private ProgressDialog getUpdateProgressDialog() {
        if (updateProgressDialog == null) {
            updateProgressDialog = new ProgressDialog(getContext());
            updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            updateProgressDialog.setCancelable(false);
            updateProgressDialog.setCanceledOnTouchOutside(false);
            updateProgressDialog.setTitle("下载中...");
        }
        return updateProgressDialog;
    }

    @Override
    public final void showAppDownloadingDialog(@NonNull Context context, @NonNull String newVersinApkUrl) {
        startDownloadApk(newVersinApkUrl);
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FILE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showTopSnackBar("文件写入权限被拒绝！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private FileDownloadListener apkDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (totalBytes > 0) {
                getUpdateProgressDialog().setMax(totalBytes);
            }
            getUpdateProgressDialog().setProgress(soFarBytes);
            getUpdateProgressDialog().show();
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (totalBytes > 0) {
                getUpdateProgressDialog().setMax(totalBytes);
            }

            getUpdateProgressDialog().setProgress(soFarBytes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            getUpdateProgressDialog().dismiss();
            if (task != null && !TextUtils.isEmpty(task.getPath())) {
                installApk(new File(task.getPath()));
                FileUtils.deleteFolderOtherFile(new File(getApkSavePath()), new File(task.getPath()));
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            if (NetUtils.hasNetwork(BaseApplication.getApplication())) {
                bugSync("App更新失败", e);
            }
            if (e instanceof FileDownloadHttpException) {
                int code = ((FileDownloadHttpException) e).getCode();
                showTopSnackBar(String.format("%s:%s", code, "下载异常!"));
            } else if (e instanceof FileDownloadOutOfSpaceException) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("存储空间严重不足,去清理?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                showTopSnackBar(String.format("下载异常!" + StringUtils.throwable2string(e)));
            }
            getUpdateProgressDialog().dismiss();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            log("--------->warn");
        }
    };

    private void startDownloadApk(String apkUrl) {
        if (TextUtils.isEmpty(apkUrl)) return;
        if (getUpdateProgressDialog().isShowing()) return;
        if (Environment.isExternalStorageEmulated()) {
            String path = getApkSavePath() + Md5Utils.md5(apkUrl, apkUrl) + ".apk";
            FileDownloader
                    .getImpl()
                    .create(apkUrl)
                    .setPath(path)
                    .setListener(apkDownloadListener).start();
        } else {
            showTopSnackBar("sd卡不可用!");
        }
    }

    /**
     * 获取安装包地址
     *
     * @return
     */
    private String getApkSavePath() {
        StringBuilder pathBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        pathBuilder.append(File.separator);
        pathBuilder.append(ActionConstants.FILE_DOWNLOAD_PATH);
        pathBuilder.append(File.separator);
        pathBuilder.append(ActionConstants.APK_DOWNLOAD_PATH);
        pathBuilder.append(File.separator);
        return pathBuilder.toString();
    }

    /**
     * 安装apk
     *
     * @param apkFile
     */
    protected void installApk(File apkFile) {
        ApkUtils.installApk(getContext(), apkFile);
    }

    private void pauseDownloadApk() {
        if (apkDownloadListener != null) {
            try {
                FileDownloader
                        .getImpl()
                        .pause(apkDownloadListener);
            } catch (Exception e) {
            }
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseDownloadApk();
    }
}
