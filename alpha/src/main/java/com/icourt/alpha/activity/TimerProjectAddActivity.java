package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;

import butterknife.ButterKnife;

/**
 * Description  从项目界面添加计时
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/10
 * version 2.0.0
 */
public class TimerProjectAddActivity extends BaseTimerAddActivity
        implements
        OnFragmentCallBackListener {

    private static final String KEY_PROJECT_ID = "key_project_id";//用来传递项目id的key。
    private static final String KEY_PROJECT_NAME = "key_project_name";//用来传递项目名称的key。

    public static void launch(@NonNull Context context, @NonNull String projectId, @NonNull String projectName) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TimerProjectAddActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        intent.putExtra(KEY_PROJECT_NAME, projectName);
        context.startActivity(intent);
    }

    @Override
    protected String getTimerTitle() {
        return "";
    }

    @Override
    protected String getProjectId() {
        return getIntent().getStringExtra(KEY_PROJECT_ID);
    }

    @Override
    protected String getProjectName() {
        return getIntent().getStringExtra(KEY_PROJECT_NAME);
    }

    @Override
    protected TaskEntity.TaskItemEntity getTaskItemEntity() {
        return null;
    }

    @Override
    protected void cacheData() {

    }

    @Override
    protected void clearCache() {

    }
}
