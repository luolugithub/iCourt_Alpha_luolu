package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.DemoAdapter;
import com.icourt.alpha.adapter.recycleradapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.recycleradapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseRecyclerActivity;
import com.icourt.alpha.entity.bean.DemoEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.xrefreshlayout.RefreshaLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/30
 * version
 */

public class DemoActivity extends BaseRecyclerActivity<com.icourt.alpha.entity.bean.DemoEntity> {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    RefreshaLayout xrefreshview;
    DemoAdapter demoAdapter;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, DemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(demoAdapter = new DemoAdapter());
        demoAdapter.registerAdapterDataObserver(dataChangeAdapterObserver);
        xrefreshview.setNoticeEmpty(R.mipmap.icon_placeholder_project, "这里的内容为空啦...");
        xrefreshview.setAutoRefresh(true);
        xrefreshview.startRefresh();

        testhttp();
    }

    @Override
    protected BaseArrayRecyclerAdapter<DemoEntity> getRecyclerAdapter() {
        return demoAdapter;
    }

    @Override
    protected RefreshaLayout getRefreshLayout() {
        return xrefreshview;
    }

    @Override
    public void getData(final boolean isRefresh) {

        //方式1:分页数据获取并填充
        // getPageData(isRefresh, null);


        // 方式2:自己写
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRefresh && new Random().nextInt(10) % 2 == 0) {
                    demoAdapter.clearData();
                } else {
                    //模拟数据
                    List<DemoEntity> demoEntityList = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        demoEntityList.add(new DemoEntity("name_" + i, new Random().nextInt(50) + 10));
                    }
                    demoAdapter.bindData(isRefresh, demoEntityList);
                }
                xrefreshview.stopRefresh();
                xrefreshview.stopLoadMore();
            }
        }, 1500);
    }


    public void testhttp() {
        getApi().getGroups(5)
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {

                    }
                });
    }


}
