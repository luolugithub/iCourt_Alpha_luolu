package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

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
public class MyCollectedCustomersActivity extends BaseActivity {

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
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
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
        setTitle("我关注的");
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.my_center_null_atme_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(customerAdapter = new CustomerAdapter());
        customerAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, customerAdapter));
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
        refreshLayout.setNoticeEmptyText("暂无关注联系人");
        refreshLayout.setNoticeEmptyImage(R.mipmap.icon_placeholder_user);


        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().getCustomers(0, 100000, 1)
                .enqueue(new SimpleCallBack<List<CustomerEntity>>() {
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
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }
}
