package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.DemoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
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

    /**
     * @param context
     */
    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, DemoActivity.class);
        intent.setAction("getCourse");
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
        setTitle("xrefreshDemo");
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
        getApi().getData(5)
                .enqueue(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {

                    }

                });

        getApi().getPageData(6)
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {

                    }
                });
    }


}
