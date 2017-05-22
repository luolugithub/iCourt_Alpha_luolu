package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SelectGroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupBean;
import com.icourt.alpha.entity.bean.SelectGroupBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择团队
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/18
 * version 2.0.0
 */

public class GroupSelectActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    List<GroupBean> groupBeenList;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    SelectGroupAdapter selectGroupAdapter;

    public static void launchForResult(@NonNull Activity context, @NonNull List<SelectGroupBean> groupBeenList, int requestCode) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupSelectActivity.class);
        intent.putExtra("groupBeenList", (Serializable) groupBeenList);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("负责团队");
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_user, "暂无负责团队");
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(selectGroupAdapter = new SelectGroupAdapter());
        selectGroupAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, selectGroupAdapter));
        selectGroupAdapter.setOnItemClickListener(this);
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

        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                CustomerPersonCreateActivity.launchSetResultFromGroup(GroupSelectActivity.this, selectGroupAdapter.getSelectedData());
                finish();
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().officeGroupsQuery(getLoginUserInfo().getOfficeId()).enqueue(new SimpleCallBack<List<SelectGroupBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<SelectGroupBean>>> call, Response<ResEntity<List<SelectGroupBean>>> response) {
                stopRefresh();
                selectGroupAdapter.bindData(true, response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<List<SelectGroupBean>>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
            }
        });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        selectGroupAdapter.toggleSelected(position);
    }
}
