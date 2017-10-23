package com.icourt.alpha.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.icourt.alpha.http.ApiAlphaService;
import com.icourt.alpha.http.ApiChatService;
import com.icourt.alpha.http.ApiProjectService;
import com.icourt.alpha.http.ApiSFileService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Description  网络,对话框
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/7
 * version 2.1.0
 */
public class BaseActionHelper implements ProgressHUDImp {

    private Context actionContext;
    private KProgressHUD progressHUD;

    @Nullable
    protected Context getActionContext() {
        return actionContext;
    }

    public BaseActionHelper() {
    }

    public BaseActionHelper(@NonNull Context context) {
        attachContext(context);
    }

    @UiThread
    public void attachContext(@NonNull Context context) {
        this.actionContext = context;
    }

    @UiThread
    public void detachedContext() {
        this.actionContext = null;
        dismissLoadingDialog();
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            if (isShowLoading()) {
                progressHUD.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void showLoadingDialog(@Nullable String notice) {
        if (getActionContext() == null) return;
        KProgressHUD currSVProgressHUD = getSvProgressHUD();
        currSVProgressHUD.setLabel(notice);
        if (!currSVProgressHUD.isShowing()) {
            currSVProgressHUD.show();
        }
    }

    /**
     * 获取 菊花加载对话框
     *
     * @return
     */
    @NonNull
    protected final KProgressHUD getSvProgressHUD() {
        if (progressHUD == null) {
            progressHUD = KProgressHUD
                    .create(getActionContext())
                    .setDimAmount(0.5f)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        return progressHUD;
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
     * Toast提示
     * 缺陷 有的rom 会禁用掉taost 比如huawei rom
     *
     * @param notice
     */
    @UiThread
    public void showToast(@NonNull CharSequence notice) {
        ToastUtils.showToast(notice);
    }

    /**
     * 顶部的snackBar
     *
     * @param notice
     */
    @UiThread
    public void showTopSnackBar(@NonNull CharSequence notice) {
        if (getActionContext() == null) return;
        if (getActionContext() instanceof Activity) {
            SnackbarUtils.showTopSnackBar((Activity) getActionContext(), notice);
        }
    }

    /**
     * 底部的snackBar android默认在底部
     *
     * @param notice
     */
    @UiThread
    public void showBottomSnackBar(@NonNull CharSequence notice) {
        if (getActionContext() == null) return;
        if (getActionContext() instanceof Activity) {
            SnackbarUtils.showBottomSnack((Activity) getActionContext(), notice);
        }
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    public final ApiAlphaService getApi() {
        return RetrofitServiceFactory.getAlphaApiService();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    public final ApiChatService getChatApi() {
        return RetrofitServiceFactory.getChatApiService();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    public final ApiProjectService getProjectApi() {
        return RetrofitServiceFactory.getProjectApiService();
    }


    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    public final ApiSFileService getSFileApi() {
        return RetrofitServiceFactory.getSFileApiService();
    }

}
