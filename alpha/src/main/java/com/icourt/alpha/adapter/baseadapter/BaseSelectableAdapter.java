package com.icourt.alpha.adapter.baseadapter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import com.asange.recyclerviewadapter.SelectableBaseAdapter;
import com.icourt.alpha.base.BaseActionHelper;
import com.icourt.alpha.http.ApiAlphaService;
import com.icourt.alpha.http.ApiChatService;
import com.icourt.alpha.http.ApiProjectService;
import com.icourt.alpha.http.ApiSFileService;
import com.icourt.alpha.interfaces.ProgressHUDImp;
import com.zhaol.refreshlayout.interfaces.IDataEmptyAdapter;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/7
 * version 2.1.0
 */
public abstract class BaseSelectableAdapter<T extends com.asange.recyclerviewadapter.SelectableEntity> extends SelectableBaseAdapter<T>  implements ProgressHUDImp,IDataEmptyAdapter{
    final BaseActionHelper baseActionHelper = new BaseActionHelper();

    public BaseSelectableAdapter(@NonNull List<T> data) {
        super(data);
    }

    public BaseSelectableAdapter() {
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        baseActionHelper.attachContext(recyclerView.getContext());
        super.onAttachedToRecyclerView(recyclerView);
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        baseActionHelper.detachedContext();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    @UiThread
    public void showLoadingDialog(@Nullable String notice) {
        baseActionHelper.showLoadingDialog(notice);
    }

    @Override
    public int getRealAdapterCount() {
        if (getData().isEmpty()) {
            return 0;
        }
        return getData().size();
    }

    @Override
    @UiThread
    public void dismissLoadingDialog() {
        baseActionHelper.dismissLoadingDialog();
    }


    /**
     * 加载对话框是否展示中
     *
     * @return
     */
    @Override
    public boolean isShowLoading() {
        return baseActionHelper.isShowLoading();
    }

    /**
     * Toast提示
     * 缺陷 有的rom 会禁用掉taost 比如huawei rom
     *
     * @param notice
     */
    @UiThread
    protected void showToast(@NonNull CharSequence notice) {
        baseActionHelper.showToast(notice);
    }

    /**
     * 顶部的snackBar
     *
     * @param notice
     */
    @UiThread
    protected void showTopSnackBar(@NonNull CharSequence notice) {
        baseActionHelper.showTopSnackBar(notice);
    }

    /**
     * 底部的snackBar android默认在底部
     *
     * @param notice
     */
    @UiThread
    protected void showBottomSnackBar(@NonNull CharSequence notice) {
        baseActionHelper.showBottomSnackBar(notice);
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiAlphaService getApi() {
        return baseActionHelper.getApi();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiChatService getChatApi() {
        return baseActionHelper.getChatApi();
    }

    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiProjectService getProjectApi() {
        return baseActionHelper.getProjectApi();
    }


    /**
     * 接口 http通信
     *
     * @return
     */
    @NonNull
    protected final ApiSFileService getSFileApi() {
        return baseActionHelper.getSFileApi();
    }
}
