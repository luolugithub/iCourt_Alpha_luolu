package com.icourt.alpha.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.AlphaApiService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.interfaces.IContextResourcesImp;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Description  基类封装
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version 1.0.0
 */

public abstract class BaseActivity
        extends BasePermisionActivity
        implements ProgressHUDImp
        , View.OnClickListener, IContextResourcesImp {

    public static final String KEY_ACTIVITY_RESULT = "ActivityResult";

    /**
     * @return 上下文
     */
    protected final BaseActivity getActivity() {
        return this;
    }


    /**
     * @return 上下文
     */
    protected final BaseActivity getContext() {
        return this;
    }


    /**
     * 初始化布局 标准方法 非被动调用与回调[DataBinding更加自由 ] 请主动调用
     */
    @CallSuper
    protected void initView() {
        registerClick(R.id.titleBack);
        registerClick(R.id.titleAction);
        registerClick(R.id.titleAction2);
    }

    /**
     * 设置页面标题 固定id R.id.titleContent
     *
     * @param title
     */
    @Override
    public final void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
        TextView titleTextView = getTitleTextView();
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    /**
     * @return 标题中的textView
     */
    @Nullable
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
        TextView titleTextView = getTitleTextView();
        if (titleTextView != null) {
            titleTextView.setText(getString(titleId));
        }
    }

    /**
     * @return title返回/取消的view
     */
    @Nullable
    @CheckResult
    public final View getTitleBackView() {
        return findViewById(R.id.titleBack);
    }

    /**
     * @return 标题返回的image
     */
    @Nullable
    @CheckResult
    public final ImageView getTitleBackImage() {
        View titleBackImage = getTitleBackView();
        return (titleBackImage instanceof ImageView) ? (ImageView) titleBackImage : null;
    }

    /**
     * @return 标题返回／取消的TextView
     */
    @Nullable
    @CheckResult
    public final CheckedTextView getTitleBackTextView() {
        View titleBackTextView = getTitleBackView();
        return (titleBackTextView instanceof CheckedTextView) ? (CheckedTextView) titleBackTextView : null;
    }

    /**
     * @return 标题操作图片按钮
     */
    @Nullable
    @CheckResult
    public ImageView getTitleActionImage() {
        View titleActionImage = findViewById(R.id.titleAction);
        return (titleActionImage instanceof ImageView) ? (ImageView) titleActionImage : null;
    }

    /**
     * @return 标题操作图片按钮 第二个
     */
    @Nullable
    @CheckResult
    public ImageView getTitleActionImage2() {
        View titleActionImage = findViewById(R.id.titleAction2);
        return (titleActionImage instanceof ImageView) ? (ImageView) titleActionImage : null;
    }

    /**
     * @return 标题操作TextView
     */
    @Nullable
    @CheckResult
    public TextView getTitleActionTextView() {
        View titleActionText = findViewById(R.id.titleAction);
        return (titleActionText instanceof CheckedTextView) ? (CheckedTextView) titleActionText : null;
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
        ToastUtils.showToast(notice);
    }

    /**
     * Toast提示
     * 缺陷 有的rom 会禁用掉taost 比如huawei rom
     *
     * @param resId
     */
    @UiThread
    protected final void showToast(@StringRes int resId) {
        ToastUtils.showToast(resId);
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

    /**
     * 注册控件的点击监听
     *
     * @param id
     */
    @Nullable
    protected final void registerClick(@IdRes int id) {
        View viewById = findViewById(id);
        if (viewById != null) {
            viewById.setOnClickListener(this);
        }
    }

    /**
     * 取消注册控件的点击监听
     *
     * @param id
     */
    @Nullable
    protected final void unRegisterClick(@IdRes int id) {
        View viewById = findViewById(id);
        if (viewById != null) {
            viewById.setOnClickListener(null);
        }
    }

    /**
     * 设置控件隐藏或者展示
     *
     * @param v
     * @param isVisible
     * @param <V>
     * @return
     */
    @Nullable
    protected final <V extends View> V setViewVisible(@NonNull V v, boolean isVisible) {
        if (v != null) {
            v.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
        return v;
    }

    /**
     * 取消事件点击监听⌚
     *
     * @param v
     * @param <V>
     * @return
     */
    @Nullable
    protected final <V extends View> V unRegisterClick(@NonNull V v) {
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

    @CallSuper
    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
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
        if (isDestroyOrFinishing()) return;
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
     * 发送日志到bugtags上面去
     *
     * @param tag
     * @param log
     */
    protected void postLog2Bugtags(String tag, String log) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            StringBuilder stringBuilder = new StringBuilder(tag);
            stringBuilder.append("\n");
            stringBuilder.append(log);
            stringBuilder.append("\n");
            stringBuilder.append("uid:");
            stringBuilder.append(getLoginUserId());
            Bugtags.sendFeedback(stringBuilder.toString());
        }
    }

    /**
     * activity是否销毁或者即将销毁
     *
     * @return
     */
    protected final boolean isDestroyOrFinishing() {
        return SystemUtils.isDestroyOrFinishing(BaseActivity.this);
    }


    /**
     * 添加或者显示碎片
     *
     * @param targetFragment  将要添加／显示的fragment
     * @param currentFragment 正在显示的fragment
     * @param containerViewId 替换的viewid
     * @return 当前已经显示的fragment
     */
    protected final Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (!targetFragment.isAdded()) { // 如果当前fragment添加，则添加到Fragment管理器中
            if (currentFragment == null) {
                transaction
                        .add(containerViewId, targetFragment)
                        .commit();
            } else {
                transaction.hide(currentFragment)
                        .add(containerViewId, targetFragment)
                        .commit();
            }
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment)
                    .commit();
        }
        return targetFragment;
    }


    /**
     * @return 登陆信息
     */
    @Nullable
    @CheckResult
    protected final AlphaUserInfo getLoginUserInfo() {
        return LoginInfoUtils.getLoginUserInfo();
    }


    /**
     * @return 登陆uid
     */
    @Nullable
    @CheckResult
    protected final String getLoginUserId() {
        return LoginInfoUtils.getLoginUserId();
    }


    /**
     * 清除登陆信息
     */
    protected final void clearLoginUserInfo() {
        LoginInfoUtils.clearLoginUserInfo();
    }

    /**
     * 保存登陆信息
     *
     * @param alphaUserInfo
     */
    protected final void saveLoginUserInfo(AlphaUserInfo alphaUserInfo) {
        LoginInfoUtils.saveLoginUserInfo(alphaUserInfo);
    }

    /**
     * @return 是否登陆
     */
    public boolean isUserLogin() {
        return LoginInfoUtils.isUserLogin();
    }

    /**
     * 获取登陆的token
     *
     * @return
     */
    @Nullable
    @CheckResult
    public String getUserToken() {
        return LoginInfoUtils.getUserToken();
    }

    @Override
    public final int getContextColor(@ColorRes int id) {
        return getContextColor(id, Color.BLACK);
    }

    @Override
    public final int getContextColor(@ColorRes int id, @ColorInt int defaultColor) {
        return SystemUtils.getColor(context, id, defaultColor);
    }

    @Nullable
    @Override
    public Drawable getDrawable(Context context, @DrawableRes int id) {
        return SystemUtils.getDrawable(context, id);
    }

}
