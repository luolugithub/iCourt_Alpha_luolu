package com.icourt.alpha.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.interfaces.UpdateAppDialogNoticeImp;
import com.icourt.alpha.interfaces.callback.AppUpdateCallBack;
import com.icourt.alpha.utils.Md5Utils;
import com.icourt.alpha.utils.StringUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;

import java.io.File;

import retrofit2.Call;
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
    private AlertDialog updateNoticeDialog;
    private ProgressDialog updateProgressDialog;
    private static final int REQUEST_FILE_PERMISSION = 9999;

    @Override
    public final boolean hasFilePermission(@NonNull Context context) {
        return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public final void requestFilePermission(@NonNull Context context, int reqCode) {
        reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件写入权限！", reqCode);
    }

    @Override
    public final void checkAppUpdate(@NonNull BaseCallBack<AppVersionEntity> callBack) {
        if (callBack == null) return;
        getApi().getNewVersionAppInfo(BuildConfig.APK_UPDATE_URL)
                .enqueue(callBack);
    }

    @Override
    public final void checkAppUpdate(@NonNull final Context context) {
        if (context == null) return;
        getApi().getNewVersionAppInfo(BuildConfig.APK_UPDATE_URL)
                .enqueue(new AppUpdateCallBack() {
                    @Override
                    public void onSuccess(Call<AppVersionEntity> call, Response<AppVersionEntity> response) {
                        showAppUpdateDialog(getActivity(), response.body());
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
    public final void showAppUpdateDialog(@NonNull final Context context, @NonNull final AppVersionEntity appVersionEntity) {
        if (isDestroyOrFinishing()) return;
        if (updateNoticeDialog != null && updateNoticeDialog.isShowing()) return;
        if (!shouldUpdate(appVersionEntity)) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("更新提醒")
                .setMessage(TextUtils.isEmpty(appVersionEntity.changelog) ? "有一个新版本,请立即更新吧" : appVersionEntity.changelog); //设置内容
        builder.setPositiveButton("前往更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (hasFilePermission(context)) {
                    showAppDownloadingDialog(getActivity(), appVersionEntity.install_url);
                } else {
                    requestFilePermission(context, REQUEST_FILE_PERMISSION);
                }
            }
        });
        if (shouldForceUpdate(appVersionEntity)) {
            builder.setCancelable(false);
        } else {
            builder.setNegativeButton("下次更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        updateNoticeDialog = builder.create();
        updateNoticeDialog.show();
    }

    /**
     * 是否强制更新
     * VERSION_CODE 如果大于本地版本 就强制更新
     *
     * @param appVersionEntity
     * @return
     */
    public final boolean shouldForceUpdate(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null && appVersionEntity.version > BuildConfig.VERSION_CODE;
    }

    @Override
    public final boolean shouldUpdate(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null
                && !TextUtils.equals(appVersionEntity.versionShort, BuildConfig.VERSION_NAME);
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
            getUpdateProgressDialog().setMax(totalBytes);
            getUpdateProgressDialog().setProgress(soFarBytes);
            getUpdateProgressDialog().show();
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            getUpdateProgressDialog().setMax(totalBytes);
            getUpdateProgressDialog().setProgress(soFarBytes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            getUpdateProgressDialog().dismiss();
            if (task != null && !TextUtils.isEmpty(task.getPath())) {
                installApk(new File(task.getPath()));
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            /*if (NetworkUtils.isConnected(BaseApplication.getApplication())) {
                uplaodLog(e);
            }*/
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
            String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            FileDownloader
                    .getImpl()
                    .create(apkUrl)
                    .setPath(ROOTPATH + Md5Utils.md5(apkUrl, apkUrl) + ".apk")
                    .setListener(apkDownloadListener).start();
        } else {
            showTopSnackBar("sd卡不可用!");
        }
    }

    /**
     * 安装apk
     *
     * @param apkFile
     */
    protected void installApk(File apkFile) {
        if (apkFile != null && apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
            getContext().startActivity(intent);
        }
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
