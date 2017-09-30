package com.icourt.alpha.base;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;

import com.bugtags.library.Bugtags;
import com.icourt.alpha.R;
import com.icourt.alpha.base.permission.IAlphaPermission;
import com.icourt.alpha.base.permission.IAlphaSelectPhoto;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.http.ApiAlphaService;
import com.icourt.alpha.http.ApiChatService;
import com.icourt.alpha.http.ApiProjectService;
import com.icourt.alpha.http.ApiSFileService;
import com.icourt.alpha.http.IContextCallQueue;
import com.icourt.alpha.http.IContextObservable;
import com.icourt.alpha.http.ResEntityFunction;
import com.icourt.alpha.http.ResEntitySimpleFunction;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.OnDialogFragmentDismissListener;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.ToastUtils;
import com.icourt.api.RequestUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/5
 * version 1.0.0
 */
public abstract class BaseDialogFragment extends DialogFragment
        implements ProgressHUDImp,
        IContextCallQueue,
        IContextObservable,
        View.OnClickListener,
        INotifyFragment,
        LifecycleProvider<FragmentEvent>,
        IAlphaPermission,
        IAlphaSelectPhoto {
    Queue<WeakReference<Call>> contextCallQueue = new ConcurrentLinkedQueue<>();

    public static final String KEY_FRAGMENT_RESULT = "FragmentResult";
    public static final String KEY_FRAGMENT_UPDATE_KEY = "fragment_update_key";
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    protected View rootView;
    private AlertDialog mPermissionAlertDialog;

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }


    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public static void show(DialogFragment fragment,
                            String tag,
                            FragmentTransaction fragmentTransaction) {
        fragment.show(fragmentTransaction, tag);
    }

    public static void show(DialogFragment fragment,
                            String tag,
                            FragmentManager fragmentManager) {
        fragment.show(fragmentManager, tag);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
        if (mPermissionAlertDialog != null && mPermissionAlertDialog.isShowing()) {
            mPermissionAlertDialog.dismiss();
        }
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        cancelAllCall();
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    @CallSuper
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    /**
     * 通知父容器刷新
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (getParentFragment() instanceof OnDialogFragmentDismissListener) {
            ((OnDialogFragmentDismissListener) getParentFragment())
                    .onDialogFragmentDismiss(this);
        } else if (getActivity() instanceof OnDialogFragmentDismissListener) {
            ((OnDialogFragmentDismissListener) getActivity())
                    .onDialogFragmentDismiss(this);
        }
        super.onDismiss(dialog);
    }

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
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
            }
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
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
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
                    .setDimAmount(0.5f)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        return progressHUD;
    }

    @UiThread
    public void showLoadingDialog(@StringRes int noticeId) {
        showLoadingDialog(getString(noticeId));
    }

    /***
     *  展示加载对话框
     * @param notice
     */
    @Override
    public void showLoadingDialog(@Nullable String notice) {
        if (isDetached()) return;
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
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {

    }

    @Nullable
    @Override
    public Bundle getFragmentData(int type, @Nullable Bundle inBundle) {
        return null;
    }

    /**
     * 添加或者显示碎片
     *
     * @param targetFragment  将要添加／显示的fragment
     * @param currentFragment 正在显示的fragment
     * @param containerViewId 替换的viewid
     * @return 当前执行显示的fragment
     */
    protected Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (!targetFragment.isAdded()) { // 如果当前fragment添加，则添加到Fragment管理器中
            if (currentFragment == null) {
                transaction
                        .add(containerViewId, targetFragment)
                        .commitAllowingStateLoss();
            } else {
                transaction.hide(currentFragment)
                        .add(containerViewId, targetFragment)
                        .commitAllowingStateLoss();
            }
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment)
                    .commitAllowingStateLoss();
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

    /**
     * 同步bug到bugtags
     *
     * @param tag
     * @param log
     */
    protected void bugSync(String tag, String log) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) {
            try {
                StringBuilder stringBuilder = new StringBuilder(tag);
                stringBuilder.append("\n");
                stringBuilder.append("page:" + getClass().getSimpleName());
                stringBuilder.append("\n");
                stringBuilder.append(log);
                stringBuilder.append("\n");
                stringBuilder.append("loginUserInfo:\n" + getLoginUserInfo());
                Bugtags.sendFeedback(stringBuilder.toString());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同步bug到bugtags
     *
     * @param tag
     * @param throwable
     */
    protected void bugSync(String tag, Throwable throwable) {
        if (!TextUtils.isEmpty(tag) && throwable != null) {
            bugSync(tag, StringUtils.throwable2string(throwable));
        }
    }

    /**
     * 加入队列
     *
     * @param call
     * @param callback
     * @param <T>
     * @return
     */
    @Override
    public <T> Call<T> callEnqueue(@NonNull Call<T> call, Callback<T> callback) {
        if (isDetached()) return null;
        if (call != null) {
            contextCallQueue.offer(new WeakReference<Call>(call));
            return RequestUtils.callEnqueue(call, callback);
        }
        return call;
    }

    /**
     * 取消当前页面所有请求
     */
    @Override
    public void cancelAllCall() {
        while (contextCallQueue.peek() != null) {
            WeakReference<Call> poll = contextCallQueue.poll();
            if (poll != null) {
                RequestUtils.cancelCall(poll.get());
            }
        }
    }

    /**
     * 取消单个请求
     *
     * @param call
     * @param <T>
     */
    @Override
    public <T> void cancelCall(@NonNull Call<T> call) {
        for (WeakReference<Call> poll : contextCallQueue) {
            if (poll != null && call == poll.get()) {
                contextCallQueue.remove(poll);
                break;
            }
        }
        RequestUtils.cancelCall(call);
    }


    @Override
    public <T> Observable<T> sendObservable(Observable<? extends ResEntity<T>> observable) {
        if (observable != null) {
            return observable
                    .map(new ResEntitySimpleFunction<T>())
                    .compose(this.<T>bindToLifecycle());
        }
        return null;
    }

    @Override
    public <T> Observable<? extends ResEntity<T>> sendObservable2(Observable<? extends ResEntity<T>> observable) {
        if (observable != null) {
            return observable
                    .map(new ResEntityFunction<ResEntity<T>>())
                    .compose(this.<ResEntity<T>>bindToLifecycle());
        }
        return null;
    }

    @Override
    public <T extends ResEntity> Observable<T> sendObservable3(Observable<T> observable) {
        if (observable != null) {
            return observable
                    .map(new ResEntityFunction<T>())
                    .compose(this.<T>bindToLifecycle());
        }
        return null;
    }
    /**
     * 容易出现状态丢失
     *
     * @param transaction
     * @param tag
     * @return
     */
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            return super.show(transaction, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            bugSync("DialogFragment Transaction show Exception", e);
        }
        return -1;
    }

    /**
     * 容易出现状态丢失
     *
     * @param manager
     * @param tag
     */
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            bugSync("DialogFragment manager show Exception", e);
        }
    }

    /**
     * 不建议用这个
     *
     * @link{ "dismissAllowingStateLoss()" }
     */
    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            bugSync("DialogFragment dismiss Exception", e);
            try {
                dismissAllowingStateLoss();
            } catch (Throwable ex) {
                ex.printStackTrace();
                bugSync("DialogFragment dismissAllowingStateLoss Exception", ex);
            }
        }
    }

    /**
     * 检查权限
     *
     * @param permission
     * @return
     */
    protected boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)// Permission was added in API Level 16
        {
            return ContextCompat.checkSelfPermission(getActivity(), permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * 检查权限
     *
     * @param permissions
     * @return
     */
    protected boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)// Permission was added in API Level 16
        {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(getActivity(), permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param permission
     * @param rationaleId
     * @param requestCode
     */
    protected void reqPermission(final String permission, @StringRes int rationaleId, final int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), getString(rationaleId),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    protected void reqPermission(final String permission, String rationale, final int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }


    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mPermissionAlertDialog = builder.show();
    }

    @Override
    public boolean checkCameraPermission() {
        return checkPermission(Manifest.permission.CAMERA);
    }

    @Override
    public boolean checkAcessFilePermission() {
        return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void requestCameraPermission() {
        reqPermission(Manifest.permission.CAMERA, R.string.permission_rationale_camera, PERMISSION_REQ_CODE_CAMERA);
    }

    @Override
    public void requestAcessFilePermission() {
        reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.permission_rationale_storage, PERMISSION_REQ_CODE_ACCESS_FILE);
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_CODE_CAMERA:
                if (grantResults != null
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    SnackbarUtils.showTopSnackBar(
                            getActivity(),
                            getString(R.string.permission_denied_camera));
                }
                break;
            case PERMISSION_REQ_CODE_ACCESS_FILE:
                if (grantResults != null
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    SnackbarUtils.showTopSnackBar(
                            getActivity(),
                            getString(R.string.permission_denied_storage));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void checkAndSelectMutiPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        if (checkAcessFilePermission()) {
            selectMutiPhotos(onHanlderResultCallback);
        } else {
            requestAcessFilePermission();
        }
    }

    @Override
    public void selectMutiPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        FunctionConfig config = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(9)
                .build();
        GalleryFinal.openGalleryMuti(REQ_CODE_GALLERY_MUTI, config, onHanlderResultCallback);
    }

    @Override
    public void checkAndSelectSingleFromPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        if (checkAcessFilePermission()) {
            selectSingleFromPhotos(onHanlderResultCallback);
        } else {
            requestAcessFilePermission();
        }
    }

    @Override
    public void selectSingleFromPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableEdit(false)
                .setEnableCrop(false)
                .build();
        GalleryFinal.openGallerySingle(REQ_CODE_GALLERY_SINGLE, config, onHanlderResultCallback);
    }

    @Override
    public void selectFromCamera(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableEdit(false)
                .setEnableCrop(false)
                .build();
        GalleryFinal.openCamera(REQ_CODE_CAMERA, config, onHanlderResultCallback);
    }

    @Override
    public void checkAndSelectFromCamera(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback) {
        if (checkCameraPermission()) {
            selectFromCamera(onHanlderResultCallback);
        } else {
            requestCameraPermission();
        }
    }
}
