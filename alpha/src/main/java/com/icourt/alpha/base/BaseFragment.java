package com.icourt.alpha.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.AlphaApiService;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Description
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/28
 * version
 */

public abstract class BaseFragment
        extends Fragment
        implements ProgressHUDImp
        , View.OnClickListener,
        INotifyFragment {

    protected View rootView;

    /**
     * 如果当前的父亲不是手机窗体上的时候,移除掉
     *
     * @param v
     * @param <V>
     * @return
     */
    protected final <V extends View> V removeParent(@NonNull V v) {
        if (v != null) {
            ViewParent parent = v.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(v);
            }
        }
        return v;
    }

    /**
     * 初始化布局 标准方法 主动调用
     */
    protected abstract void initView();

    /**
     * 获取数据 标准方法 非主动调用
     *
     * @param isRefresh 是否刷新
     */
    protected void getData(boolean isRefresh) {

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
     * 获取fragment的根布局
     *
     * @return
     */
    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }


    /**
     * 是否应该填充布局
     *
     * @return
     */
    protected final boolean shouldAddView() {
        return rootView == null;
    }

    /***
     *  解决oncreateview调用多次
     * @param layoutId
     * @param container
     * @return
     */
    protected final View onCreateView(@LayoutRes int layoutId, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceStater) {
        if (shouldAddView()) {
            rootView = inflater.inflate(layoutId, container, false);
        }
        removeParent(rootView);
        return rootView;
    }


    /**
     * 是否已经初始化过
     */
    private boolean isAlreadyInit;

    /**
     * 是否已经初始化过
     *
     * @return
     */
    public boolean isAlreadyInit() {
        return isAlreadyInit;
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAlreadyInit) {
            isAlreadyInit = true;
            initView();
        }
    }


    /**
     * 查找控件
     *
     * @param id
     * @return
     */
    @Nullable
    protected View findViewById(@IdRes int id) {
        return rootView != null ? rootView.findViewById(id) : null;
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

    private KProgressHUD progressHUD;

    /**
     * 获取 菊花加载对话框
     *
     * @return
     */
    private KProgressHUD getSvProgressHUD() {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        return progressHUD;
    }

    /***
     *  展示加载对话框
     * @param notice
     */
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

    @Override
    public void onClick(View v) {

    }
    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament,int type,Bundle bundle) {

    }

    /**
     * 添加或者显示碎片
     *
     * @param targetFragment  将要添加／显示的fragment
     * @param currentFragment 正在显示的fragment
     * @param containerViewId 替换的viewid
     * @return 当前执行显示的fragment
     */
    protected final Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
        FragmentManager fm = getChildFragmentManager();
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

    /**
     * @return 登陆uid
     */
    @Nullable
    @CheckResult
    protected final String getLoginUserId() {
        return LoginInfoUtils.getLoginUserId();
    }

}
