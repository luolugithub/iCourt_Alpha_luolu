package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

/**
 * Description  任务创建
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public class TaskCreateActivity extends BaseActivity {

    public static void launch(@NonNull Context context, @NonNull String content, String startTime) {
        if (context == null) return;
        if (TextUtils.isEmpty(content)) return;
        Intent intent = new Intent(context, TaskCreateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("新建任务");
    }
}
