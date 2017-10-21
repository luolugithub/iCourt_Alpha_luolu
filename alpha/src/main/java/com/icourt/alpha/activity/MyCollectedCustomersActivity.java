package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 我关注的客户
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class MyCollectedCustomersActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyCollectedCustomersActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    CustomerAdapter customerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collected_customers);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.task_my_attention);
        recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_user, R.string.client_not_collected);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(customerAdapter = new CustomerAdapter());
        customerAdapter.setOnItemClickListener(this);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }

        });

        refreshLayout.autoRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(
                getApi().getCustomers(0, 100000, 1),
                new SimpleCallBack<List<CustomerEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<CustomerEntity>>> call, Response<ResEntity<List<CustomerEntity>>> response) {
                        stopRefresh();
                        customerAdapter.bindData(isRefresh, response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<CustomerEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        CustomerEntity customerEntity = (CustomerEntity) adapter.getItem(adapter.getRealPos(position));
        if (!TextUtils.isEmpty(customerEntity.contactType)) {
            //公司
            if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "C")) {
                CustomerCompanyDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, true);
            } else if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "P")) {
                CustomerPersonDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, true);
            }
        }
    }
}
