package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.base.BaseActivity;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/11
 * version 2.0.0
 */

public class TaskDetailActivity extends BaseActivity {

    public static void launch(@NonNull Context context, @NonNull String taskId) {
        if (context == null) return;
        if (TextUtils.isEmpty(taskId)) return;
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("taskId", taskId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
