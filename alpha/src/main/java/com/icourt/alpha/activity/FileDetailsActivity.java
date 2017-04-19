package com.icourt.alpha.activity;

import android.content.Context;
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

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ImUserMessageDetailAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.IMStringWrapEntity;

import java.io.Serializable;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/18
 * version 1.0.0
 */
public class FileDetailsActivity extends BaseActivity {

    private static final String KEY_FILE_INFO = "key_file_info";
    IMStringWrapEntity item;
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
    ImUserMessageDetailAdapter imUserMessageDetailAdapter;


    public static void launch(@NonNull Context context, IMStringWrapEntity imFileEntity) {
        if (context == null) return;
        if (imFileEntity == null) return;
        Intent intent = new Intent(context, FileDetailsActivity.class);
        intent.putExtra(KEY_FILE_INFO, imFileEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("详情");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("跳转");
        }
        Serializable serializableExtra = getIntent().getSerializableExtra(KEY_FILE_INFO);
        if (serializableExtra instanceof IMStringWrapEntity) {
            item = (IMStringWrapEntity) serializableExtra;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(imUserMessageDetailAdapter = new ImUserMessageDetailAdapter(getUserToken()));
            imUserMessageDetailAdapter.bindData(true, Arrays.asList(item));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                //TODO 跳转到聊天
                showTopSnackBar("未完成");
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
