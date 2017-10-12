package com.icourt.alpha.adapter.baseadapter.adapterObserver;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class RefreshViewEmptyObserver extends DataChangeAdapterObserver {

    private EmptyRecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public RefreshViewEmptyObserver(
            @NonNull EmptyRecyclerView recyclerView,
            @NonNull RecyclerView.Adapter adapter) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
    }

    @CallSuper
    @Override
        protected void updateUI() {
        if (recyclerView != null) {
            recyclerView.checkIfEmpty();
        }
    }
}
