package com.icourt.alpha.base;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.xrefreshlayout.RefreshaLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  适合recyclerView 列表布局 的activity
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/3
 * version 1.0.0
 */
public abstract class BaseRecyclerActivity<T> extends BaseActivity implements XRefreshView.XRefreshViewListener {

    protected DataChangeAdapterObserver dataChangeAdapterObserver = new DataChangeAdapterObserver() {
        @Override
        protected void updateUI() {
            RefreshaLayout refreshaLayout = getRefreshLayout();
            if (refreshaLayout == null) return;
            refreshaLayout.enableEmptyViewWithAdapter(getRecyclerAdapter());
        }
    };

    @CheckResult
    protected abstract BaseArrayRecyclerAdapter<T> getRecyclerAdapter();

    @CheckResult
    protected abstract RefreshaLayout getRefreshLayout();

    @Override
    protected void initView() {
        super.initView();
        RefreshaLayout refreshLayout = getRefreshLayout();
        if (refreshLayout != null) {
            refreshLayout.setXRefreshViewListener(this);
            refreshLayout.setPullLoadEnable(true);
        }
    }

    @Override
    protected abstract void getData(boolean isRefresh);

    /**
     * 获取分页数据并绑定数据
     *
     * @param isRefresh 是否刷新 true 将会清除adapter中的集合
     * @param call
     */
    public void getPageData(final boolean isRefresh, @NonNull Call<ResEntity<List<T>>> call) {
        if (call != null && !call.isExecuted()) {
            call.enqueue(new SimpleCallBack<List<T>>() {
                @Override
                public void onSuccess(Call<ResEntity<List<T>>> call, Response<ResEntity<List<T>>> response) {
                    BaseArrayRecyclerAdapter<T> recyclerAdapter = getRecyclerAdapter();
                    if (recyclerAdapter != null) {
                        recyclerAdapter.bindData(isRefresh, response.body().result);
                    }
                    RefreshaLayout refreshLayout = getRefreshLayout();
                    if (refreshLayout != null) {
                        refreshLayout.stopRefresh();
                        refreshLayout.stopLoadMore();
                    }
                }

                @Override
                public void onFailure(Call<ResEntity<List<T>>> call, Throwable t) {
                    super.onFailure(call, t);
                    RefreshaLayout refreshLayout = getRefreshLayout();
                    if (refreshLayout != null) {
                        refreshLayout.stopRefresh();
                        refreshLayout.stopLoadMore();
                    }
                }
            });
        }
    }


    @Override
    public void onRefresh() {

    }

    @CallSuper
    @Override
    public void onRefresh(boolean isPullDown) {
        getData(true);
    }

    @CallSuper
    @Override
    public void onLoadMore(boolean isSilence) {
        getData(false);
    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }
}
