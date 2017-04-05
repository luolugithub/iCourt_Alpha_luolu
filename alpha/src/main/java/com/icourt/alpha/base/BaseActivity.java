package com.icourt.alpha.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.AlphaApiService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.interfaces.UpdateAppDialogNoticeImp;
import com.icourt.alpha.interfaces.callback.AppUpdateCallBack;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version
 */

public abstract class BaseActivity
        extends BasePermisionActivity
        implements ProgressHUDImp
        , View.OnClickListener
        , UpdateAppDialogNoticeImp {


    protected final BaseActivity getActivity() {
        return this;
    }


    protected final BaseActivity getContext() {
        return this;
    }


    private AlertDialog updateNoticeDialog;
    private static final int REQUEST_FILE_PERMISSION = 9999;


    /**
     * 初始化布局 标准方法 非被动调用与回调[DataBinding更加自由 ] 请主动调用
     */
    @CallSuper
    protected void initView() {
        View titleBack = findViewById(R.id.titleBack);
        if (titleBack != null) {
            titleBack.setOnClickListener(this);
        }
    }

    /**
     * 获取数据 标准方法 请主动调用
     *
     * @param isRefresh 是否刷新
     */
    protected void getData(boolean isRefresh) {
    }

    private KProgressHUD progressHUD;

    /**
     * 获取 菊花加载对话框
     *
     * @return
     */
    @NonNull
    private KProgressHUD getSvProgressHUD() {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(getContext())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        return progressHUD;
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final AlphaApiService getApi() {
        return RetrofitServiceFactory.provideAlphaService();
    }


    /**
     * Toast提示
     * 缺陷 有的rom 会禁用掉taost 比如huawei rom
     *
     * @param notice
     */
    @UiThread
    protected final void showToast(@NonNull CharSequence notice) {

    }

    /**
     * Toast提示
     * 缺陷 有的rom 会禁用掉taost 比如huawei rom
     *
     * @param resId
     */
    @UiThread
    protected final void showToast(@StringRes int resId) {
        this.showToast(getString(resId));
    }

    /**
     * 顶部的snackBar
     *
     * @param notice
     */
    @UiThread
    protected final void showTopSnackBar(@NonNull CharSequence notice) {
        SnackbarUtils.showTopSnackBar(getActivity(), notice);
    }

    /**
     * 顶部的snackBar
     *
     * @param resId
     */
    @UiThread
    protected final void showTopSnackBar(@StringRes int resId) {
        this.showTopSnackBar(getString(resId));
    }

    /**
     * 底部的snackBar android默认在底部
     *
     * @param notice
     */
    @UiThread
    protected final void showBottomSnackBar(@NonNull CharSequence notice) {
        SnackbarUtils.showBottomSnack(getActivity(), notice);
    }

    /**
     * 底部的snackBar android默认在底部
     *
     * @param resId
     */
    @UiThread
    protected final void showBottomSnackBar(@StringRes int resId) {
        this.showBottomSnackBar(getString(resId));
    }

    @CallSuper
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 展示加载对话框
     *
     * @param id
     */
    @UiThread
    public final void showLoadingDialog(@StringRes int id) {
        this.showLoadingDialog(getString(id));
    }

    /***
     *  展示加载对话框
     * @param notice
     */
    @UiThread
    @Override
    public void showLoadingDialog(@Nullable String notice) {
        KProgressHUD currSVProgressHUD = getSvProgressHUD();
        currSVProgressHUD.setLabel(notice);
        if (!currSVProgressHUD.isShowing()) {
            currSVProgressHUD.show();
        }
    }

    /**
     * 取消加载对话框
     */
    @UiThread
    @Override
    public void dismissLoadingDialog() {
        if (isShowLoading()) {
            progressHUD.dismiss();
        }
    }

    /**
     * 加载对话框是否展示中
     *
     * @return
     */
    @Override
    public boolean isShowLoading() {
        return progressHUD != null && progressHUD.isShowing();
    }


    /**
     * 日志输出
     *
     * @param log 日志内容
     */
    public void log(String log) {
        LogUtils.d(log);
    }

    /**
     * activity是否销毁或者即将销毁
     *
     * @return
     */
    protected final boolean isDestroyOrFinishing() {
        return SystemUtils.isDestroyOrFinishing(BaseActivity.this);
    }

    @Override
    public final boolean hasFilePermission(@NonNull Context context) {
        return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public final void requestFilePermission(@NonNull Context context, int reqCode, String rationale) {
        reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, rationale, reqCode);
    }

    @Override
    public final void checkAppUpdate(@NonNull BaseCallBack<AppVersionEntity> callBack) {

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
    public final void showAppUpdateDialog(@NonNull final Context context, @NonNull final AppVersionEntity appVersionEntity) {
        if (isDestroyOrFinishing()) return;
        //if (updateNoticeDialog != null && updateNoticeDialog.isShowing()) return;
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
                    requestFilePermission(context, REQUEST_FILE_PERMISSION, "我们需要文件写入权限");
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
        builder.show();
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

    @Override
    public final void showAppDownloadingDialog(@NonNull Context context, @NonNull String newVersinApkUrl) {

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
}
