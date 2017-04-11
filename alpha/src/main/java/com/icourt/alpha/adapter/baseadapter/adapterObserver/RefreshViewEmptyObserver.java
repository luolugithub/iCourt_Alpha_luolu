package com.icourt.alpha.adapter.baseadapter.adapterObserver;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.icourt.alpha.view.xrefreshlayout.RefreshaLayout;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class RefreshViewEmptyObserver extends DataChangeAdapterObserver {

    private RefreshaLayout refreshaLayout;
    private RecyclerView.Adapter adapter;

    public RefreshViewEmptyObserver(
            @NonNull RefreshaLayout refreshaLayout,
            @NonNull RecyclerView.Adapter adapter) {
        this.refreshaLayout = refreshaLayout;
        this.adapter = adapter;
    }

    @Override
    protected void updateUI() {
        if (refreshaLayout != null) {
            refreshaLayout.enableEmptyViewWithAdapter(adapter);
        }
    }
}
