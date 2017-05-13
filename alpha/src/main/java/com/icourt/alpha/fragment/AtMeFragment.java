package com.icourt.alpha.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.MyAtedActivity;
import com.icourt.alpha.adapter.MyAtedAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/13
 * version 1.0.0
 */
public class AtMeFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;

    public static AtMeFragment newInstance() {
        return new AtMeFragment();
    }


    MyAtedAdapter myAtedAdapter;
    private int pageIndex = 1;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyAtedActivity.class);
        context.startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_at_me, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.my_center_null_atme_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myAtedAdapter = new MyAtedAdapter());
        myAtedAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, myAtedAdapter));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 0;
        }
        getChatApi().getAtMeMsg(pageIndex, ActionConstants.DEFAULT_PAGE_SIZE)
                .enqueue(new SimpleCallBack<List<IMMessageCustomBody>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                        myAtedAdapter.bindData(isRefresh, response.body().result);
                        stopRefresh();
                        pageIndex += 1;
                        enableLoadMore(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<IMMessageCustomBody>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
