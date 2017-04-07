package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FlowerAdapter;
import com.icourt.alpha.adapter.recycleradapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.dbmodel.FlowerModel;
import com.icourt.alpha.db.dbservice.FlowerDbService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/6
 * version 1.0.0
 */
public class DemoRealmActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemChildClickListener {


    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, DemoRealmActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.bt_deleteall)
    Button btDeleteall;
    @BindView(R.id.bt_queryall)
    Button btQueryall;
    @BindView(R.id.et_insert)
    EditText etInsert;
    @BindView(R.id.bt_insert)
    Button btInsert;
    @BindView(R.id.et_query)
    EditText etQuery;
    @BindView(R.id.bt_query)
    Button btQuery;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    FlowerAdapter flowerAdapter;
    FlowerDbService flowerDbService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_realm);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("realm demo");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(flowerAdapter = new FlowerAdapter());
        flowerAdapter.setOnItemChildClickListener(this);
        flowerDbService = new FlowerDbService();
        flowerAdapter.bindData(true, flowerDbService.queryAll());
    }

    @OnClick({R.id.bt_insert, R.id.bt_query, R.id.bt_deleteall, R.id.bt_queryall})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_insert:
                if (TextUtils.isEmpty(etInsert.getText())) {
                    showTopSnackBar("名称为null");
                    return;
                }
                flowerDbService.insertOrUpdate(
                        new FlowerModel(String.valueOf(SystemClock.elapsedRealtime()), etInsert.getText().toString()));
                flowerAdapter.bindData(true, flowerDbService.queryAll());
                break;
            case R.id.bt_query:
                if (TextUtils.isEmpty(etQuery.getText())) {
                    showTopSnackBar("名称为null");
                    return;
                }
                flowerAdapter.bindData(true, flowerDbService.contains(etQuery.getText().toString()));
                break;
            case R.id.bt_deleteall:
                flowerDbService.deleteAll();
                flowerAdapter.clearData();
                break;
            case R.id.bt_queryall:
                flowerAdapter.bindData(true, flowerDbService.queryAll());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FlowerModel item = flowerAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.bt_flower_del:
                if (item != null) {
                    flowerDbService.delete(item);
                    flowerAdapter.bindData(true, flowerDbService.queryAll());
                }
                break;
            case R.id.bt_flower_update:
                if (item != null) {
                    item = flowerDbService.insertOrUpdate(new FlowerModel(item.pk, item.name + SystemClock.elapsedRealtime()));
                    flowerAdapter.updateItem(position, item);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flowerDbService.releaseService();
    }
}
