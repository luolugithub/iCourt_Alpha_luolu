package com.icourt.alpha.adapter.baseadapter.adapterObserver;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class RefreshViewEmptyObserver extends DataChangeAdapterObserver {

    private RefreshLayout refreshLayout;
    private RecyclerView.Adapter adapter;

    public RefreshViewEmptyObserver(
            @NonNull RefreshLayout refreshLayout,
            @NonNull RecyclerView.Adapter adapter) {
        this.refreshLayout = refreshLayout;
        this.adapter = adapter;
    }

    @CallSuper
    @Override
    protected void updateUI() {
        if (refreshLayout != null) {
            refreshLayout.enableEmptyViewWithAdapter(adapter);
        }
    }
}
