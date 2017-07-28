package com.icourt.alpha.interfaces;

import com.icourt.alpha.entity.bean.TaskEntity;

import java.util.List;

public interface OnTasksChangeListener {
    /**
     * 任务批量改变
     *
     * @param taskItemEntities
     */
    void onTasksChanged(List<TaskEntity.TaskItemEntity> taskItemEntities);

    /**
     * 单个任务改变
     *
     * @param taskItemEntity
     */
    void onTaskChanged(TaskEntity.TaskItemEntity taskItemEntity);
}