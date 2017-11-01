package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.SpUtils;

/**
 * Description  计时详情
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/10
 * version 2.0.0
 */
public class TimerAddActivity extends BaseTimerAddActivity
        implements
        OnFragmentCallBackListener {

    /**
     * 以下常量是用来缓存添加计时的相关数据
     */
    private static final String CACHE_NAME = "cache_name";

    public static void launch(@NonNull Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TimerAddActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected String getTimerTitle() {
        return SpUtils.getTemporaryCache().getStringData(CACHE_NAME, "");
    }

    @Override
    protected String getProjectId() {
        return null;
    }

    @Override
    protected String getProjectName() {
        return null;
    }

    @Override
    protected TaskEntity.TaskItemEntity getTaskItemEntity() {
        return null;
    }

    /**
     * 缓存数据
     */
    @Override
    protected void cacheData() {
        SpUtils.getTemporaryCache().putData(CACHE_NAME, timeNameTv.getText().toString());
    }

    /**
     * 至于普通模式会：清除历史记录
     */
    @Override
    protected void clearCache() {
        SpUtils.getTemporaryCache().remove(CACHE_NAME);
    }
}
