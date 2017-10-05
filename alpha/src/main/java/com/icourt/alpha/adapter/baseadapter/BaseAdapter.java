package com.icourt.alpha.adapter.baseadapter;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import com.icourt.alpha.http.ApiAlphaService;
import com.icourt.alpha.http.ApiChatService;
import com.icourt.alpha.http.ApiProjectService;
import com.icourt.alpha.http.ApiSFileService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

/**
 * Description  适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/4
 * version 2.1.0
 */
public abstract class BaseAdapter<T>
        extends com.asange.recyclerviewadapter.BaseRecyclerAdapter<T>
        implements ProgressHUDImp {
    protected RecyclerView recyclerView;
    private KProgressHUD progressHUD;

    public BaseAdapter(@Nullable List<T> data) {
        super(data);
    }

    public BaseAdapter() {
        this(new ArrayList<T>());
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = null;
        dismissLoadingDialog();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    @UiThread
    public void showLoadingDialog(@Nullable String notice) {
        if (recyclerView == null) return;
        KProgressHUD currSVProgressHUD = getSvProgressHUD();
        currSVProgressHUD.setLabel(notice);
        if (!currSVProgressHUD.isShowing()) {
            currSVProgressHUD.show();
        }
    }

    @Override
    @UiThread
    public void dismissLoadingDialog() {
        try {
            if (isShowLoading()) {
                progressHUD.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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
                    .create(recyclerView.getContext())
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
    protected void showToast(@NonNull CharSequence notice) {
        ToastUtils.showToast(notice);
    }

    /**
     * 顶部的snackBar
     *
     * @param notice
     */
    @UiThread
    protected void showTopSnackBar(@NonNull CharSequence notice) {
        if (recyclerView == null) return;
        if (recyclerView.getContext() instanceof Activity) {
            SnackbarUtils.showTopSnackBar((Activity) recyclerView.getContext(), notice);
        } else {
            SnackbarUtils.showTopSnackBar(recyclerView, notice);
        }
    }

    /**
     * 底部的snackBar android默认在底部
     *
     * @param notice
     */
    @UiThread
    protected void showBottomSnackBar(@NonNull CharSequence notice) {
        if (recyclerView == null) return;
        if (recyclerView.getContext() instanceof Activity) {
            SnackbarUtils.showBottomSnack((Activity) recyclerView.getContext(), notice);
        } else {
            SnackbarUtils.showBottomSnack(recyclerView, notice);
        }
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiAlphaService getApi() {
        return RetrofitServiceFactory.getAlphaApiService();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiChatService getChatApi() {
        return RetrofitServiceFactory.getChatApiService();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiProjectService getProjectApi() {
        return RetrofitServiceFactory.getProjectApiService();
    }


    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiSFileService getSFileApi() {
        return RetrofitServiceFactory.getSFileApiService();
    }
}
