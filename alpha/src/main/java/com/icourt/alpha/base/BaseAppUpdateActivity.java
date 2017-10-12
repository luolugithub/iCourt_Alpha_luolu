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
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.UpdateAppDialogNoticeImp;
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
    private static final String CUSTOM_APK_JOINT_NAME = "alphaNewApp";//自定义apk name拼接字符串 :为确保每次url不同
    private AlertDialog updateNoticeDialog;
    private ProgressDialog updateProgressDialog;
    public static final int REQUEST_FILE_PERMISSION = 9999;

    private static final int UPGRADE_STRATEGY_NO_TYPE = -1;//无更新
    private static final int UPGRADE_STRATEGY_UNCOMPEL_TYPE = 1;//非强制升级
    private static final int UPGRADE_STRATEGY_COMPEL_TYPE = 2;//强制升级

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseDownloadApk();
    }

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
                // TODO: 17/9/15    测试下载url
                appVersionEntity.upgradeUrl = "https://devbox.alphalawyer.cn/seafhttp/files/37ef8760-6e41-419c-96bf-8c720e6f216a/alpha-BaiDu-innertest-v2.0.4_2017-09-06%2014%3A13.apk";
                if (!TextUtils.equals(appVersionEntity.appVersion, SpUtils.getInstance().getStringData(UPDATE_APP_VERSION_KEY, ""))) {
                    //upgradeStrategy!=-1则显示更新对话框
                    if (appVersionEntity.upgradeStrategy != -1) {
                        showAppUpdateDialog(getActivity(), appVersionEntity, title);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResEntity<AppVersionEntity>> call, Throwable t) {
                showTopSnackBar(t.getMessage());
                bugSync("检查最新版本失败", t);
                super.onFailure(call, t);
            }
        });
    }

    @Override
    public boolean hasLocalApkFile(String url) {
        try {
            String apkPath = String.format("%s/%s.apk", getApkSavePath(), Md5Utils.md5(url, url));
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
        showUpdateDescDialog(context, appVersionEntity, false);
    }

    /**
     * 显示更新对话框
     *
     * @param appVersionEntity
     * @param isLookDesc
     */
    public void showUpdateDescDialog(@NonNull final Context context, @NonNull final AppVersionEntity appVersionEntity, final boolean isLookDesc) {
        if (appVersionEntity == null) return;

        updateNoticeDialog = new AlertDialog.Builder(this).create();
        updateNoticeDialog.show();
        Window window = updateNoticeDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setContentView(R.layout.dialog_update_app_layout);
        TextView titleTv = (TextView) window.findViewById(R.id.last_version_title_tv);
        RecyclerView recyclerView = (RecyclerView) window.findViewById(R.id.last_version_content_recyclerview);
        TextView noUpdateTv = (TextView) window.findViewById(R.id.last_version_no_update_tv);
        TextView updateTv = (TextView) window.findViewById(R.id.last_version_update_tv);


        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        VersionDescAdapter versionDescAdapter = null;
        HeaderFooterAdapter headerFooterAdapter = new HeaderFooterAdapter<>(
                versionDescAdapter = new VersionDescAdapter());
        addHeaderView(headerFooterAdapter, recyclerView, appVersionEntity);
        addFooterView(headerFooterAdapter, recyclerView);
        recyclerView.setAdapter(headerFooterAdapter);
        versionDescAdapter.bindData(true, appVersionEntity.versionDescs);

        updateNoticeDialog.setCancelable(shouldUpdate(appVersionEntity));
        if (isLookDesc) {
            noUpdateTv.setVisibility(View.GONE);
            updateTv.setVisibility(View.VISIBLE);
            updateTv.setText("关闭");
            titleTv.setText("更新日志");
        } else {
            noUpdateTv.setVisibility(shouldForceUpdate(appVersionEntity) ? View.GONE : View.VISIBLE);
            updateTv.setVisibility(View.VISIBLE);
            titleTv.setText("发现新版本");
        }
        noUpdateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.getInstance().remove(UPDATE_APP_VERSION_KEY);
                SpUtils.getInstance().putData(UPDATE_APP_VERSION_KEY, appVersionEntity.appVersion);
                updateNoticeDialog.dismiss();
            }
        });
        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLookDesc) {
                    updateNoticeDialog.dismiss();
                } else {
                    if (hasFilePermission(context)) {
                        MobclickAgent.onEvent(context, UMMobClickAgent.dialog_update_btn_click_id);
                        String updateUrl = UrlUtils.appendParam(appVersionEntity.upgradeUrl, CUSTOM_APK_JOINT_NAME, appVersionEntity.appVersion);
                        showAppDownloadingDialog(getActivity(), updateUrl);
                    } else {
                        requestFilePermission(context, REQUEST_FILE_PERMISSION);
                    }
                }
            }
        });
    }

    /**
     * 添加底部view
     *
     * @param headerFooterAdapter
     * @param recyclerView
     */
    private void addFooterView(HeaderFooterAdapter headerFooterAdapter, RecyclerView recyclerView) {
        View footerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_update_dialog_list_layout, recyclerView);
        TextView footerTv = (TextView) footerView.findViewById(R.id.footer_textview);
        headerFooterAdapter.addFooter(footerView);
        footerTv.setText("升级是小阿的信仰，我们下次见！");
    }

    /**
     * 添加头部view
     *
     * @param headerFooterAdapter
     * @param recyclerView
     * @param appVersionEntity
     */
    private void addHeaderView(HeaderFooterAdapter headerFooterAdapter, RecyclerView recyclerView, AppVersionEntity appVersionEntity) {
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_update_dialog_list_layout, recyclerView);
        TextView lastVersionTv = (TextView) headerView.findViewById(R.id.last_version_tv);
        TextView uploadTimeTv = (TextView) headerView.findViewById(R.id.last_version_uploadtime_tv);
        headerFooterAdapter.addHeader(headerView);
        lastVersionTv.setText(appVersionEntity.appVersion);
        uploadTimeTv.setText(DateUtils.getTimeDateFormatYearDot(appVersionEntity.gmtModified));
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
        return appVersionEntity != null && appVersionEntity.upgradeStrategy != UPGRADE_STRATEGY_COMPEL_TYPE;
    }

    /**
     * 是否有最新版本
     *
     * @param appVersionEntity
     * @return
     */
    public boolean isUpdateApp(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null
                && !TextUtils.equals(appVersionEntity.appVersion, BuildConfig.VERSION_NAME)
                && appVersionEntity.upgradeStrategy != UPGRADE_STRATEGY_NO_TYPE;
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
            if (updateNoticeDialog.isShowing()) updateNoticeDialog.dismiss();
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
        }
    };

    private void startDownloadApk(String apkUrl) {
        if (TextUtils.isEmpty(apkUrl)) return;
        log("apkUrl ----  " + apkUrl);
        if (getUpdateProgressDialog().isShowing()) return;
        if (Environment.isExternalStorageEmulated()) {
            String path = String.format("%s/%s.apk", getApkSavePath(), Md5Utils.md5(apkUrl, apkUrl));
            log("path ----  " + path);
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
        pathBuilder.append(DownloadConfig.FILE_DOWNLOAD_APK_DIR);
        log("pathBuilder ----  " + pathBuilder.toString());
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

}
