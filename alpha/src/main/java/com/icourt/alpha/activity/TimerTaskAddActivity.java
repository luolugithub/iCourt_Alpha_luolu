package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;

import java.io.Serializable;

/**
 * Description  从任务跳转到添加计时
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/10
 * version 2.0.0
 */
public class TimerTaskAddActivity extends BaseTimerAddActivity
        implements
        OnFragmentCallBackListener {

    private static final String KEY_TASKITEMENTITY = "key_taskItemEntity";//用来传递任务实体的key。

    public static void launch(@NonNull Context context, TaskEntity.TaskItemEntity taskItemEntity) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TimerTaskAddActivity.class);
        intent.putExtra(KEY_TASKITEMENTITY, taskItemEntity);
        context.startActivity(intent);
    }

    @Override
    protected String getTimerTitle() {
        TaskEntity.TaskItemEntity taskItemEntity = getTaskItemEntity();
        if (taskItemEntity != null) {
            return taskItemEntity.name;
        }
        return "";
    }

    @Override
    protected String getProjectId() {
        TaskEntity.TaskItemEntity taskItemEntity = getTaskItemEntity();
        if (taskItemEntity != null && taskItemEntity.matter != null) {
            return taskItemEntity.matter.id;
        }
        return null;
    }

    @Override
    protected String getProjectName() {
        TaskEntity.TaskItemEntity taskItemEntity = getTaskItemEntity();
        if (taskItemEntity != null && taskItemEntity.matter != null) {
            return taskItemEntity.matter.name;
        }
        return null;
    }

    @Override
    protected TaskEntity.TaskItemEntity getTaskItemEntity() {
        Serializable serializable = getIntent().getSerializableExtra(KEY_TASKITEMENTITY);
        if (serializable != null && serializable instanceof TaskEntity.TaskItemEntity) {
            return (TaskEntity.TaskItemEntity) serializable;
        }
        return null;
    }

    @Override
    protected void cacheData() {

    }

    @Override
    protected void clearCache() {

    }

}
