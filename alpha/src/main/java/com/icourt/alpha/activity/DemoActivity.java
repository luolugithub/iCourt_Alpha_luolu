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
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.DemoEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/3/30
 * version
 */

public class DemoActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xrefreshview;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, DemoActivity.class);
        context.startActivity(intent);
    }

    DemoAdapter demoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(demoAdapter = new DemoAdapter());

        xrefreshview.setPullLoadEnable(true);
        xrefreshview.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getdata(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getdata(false);
            }
        });
        xrefreshview.setAutoRefresh(true);
        xrefreshview.startRefresh();

        testhttp();
    }

    private void getdata(final boolean isrefresh) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //模拟数据
                List<DemoEntity> demoEntityList = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    demoEntityList.add(new DemoEntity("name_" + i, new Random().nextInt(50) + 10));
                }
                demoAdapter.bindData(isrefresh,demoEntityList);
                xrefreshview.stopRefresh();
                xrefreshview.stopLoadMore();
            }
        }, 500);

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
