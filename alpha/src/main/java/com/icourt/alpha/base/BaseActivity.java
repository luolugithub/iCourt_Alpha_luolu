package com.icourt.alpha.base;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.http.AlphaApiService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version
 */

public abstract class BaseActivity
        extends BasePermisionActivity
        implements ProgressHUDImp
        , View.OnClickListener {


    protected final BaseActivity getActivity() {
        return this;
    }


    protected final BaseActivity getContext() {
        return this;
    }


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
     * 设置页面标题 固定id R.id.titleContent
     *
     * @param title
     */
    @Override
    public final void setTitle(CharSequence title) {
        super.setTitle(title);
        View titleText = findViewById(R.id.titleContent);
        if (titleText instanceof TextView) {
            ((TextView) titleText).setText(title);
        }
    }

    /**
     * @return 标题中的textView
     */
    @CheckResult
    public TextView getTitleTextView() {
        View titleText = findViewById(R.id.titleContent);
        return (titleText instanceof TextView) ? (TextView) titleText : null;
    }

    /**
     * 设置页面标题 固定id R.id.titleContent
     *
     * @param titleId
     */
    @Override
    public final void setTitle(@StringRes int titleId) {
        super.setTitle(titleId);
        View titleText = findViewById(R.id.titleContent);
        if (titleText instanceof TextView) {
            ((TextView) titleText).setText(getString(titleId));
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
        //TODO
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


    /**
     * 注册事件点击监听⌚
     *
     * @param v
     * @param <V>
     * @return
     */
    @Nullable
    protected final <V extends View> V registerClick(@NonNull V v) {
        if (v != null) {
            v.setOnClickListener(this);
        }
        return v;
    }

    @Nullable
    protected final void registerClick(@IdRes int id) {
        View viewById = findViewById(id);
        if (viewById != null) {
            viewById.setOnClickListener(this);
        }
    }

    @Nullable
    protected final void unregisterClick(@IdRes int id) {
        View viewById = findViewById(id);
        if (viewById != null) {
            viewById.setOnClickListener(null);
        }
    }

    /**
     * 取消事件点击监听⌚
     *
     * @param v
     * @param <V>
     * @return
     */
    @Nullable
    protected final <V extends View> V unregisterClick(@NonNull V v) {
        if (v != null) {
            v.setOnClickListener(null);
        }
        return v;
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


}
