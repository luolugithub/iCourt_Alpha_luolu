package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.DateUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/8
 * version 1.0.0
 */

public class TaskSimpleAdapter extends BaseArrayRecyclerAdapter<TaskEntity.TaskItemEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_simple_task;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, int position) {
        ImageView task_item_checkbox = holder.obtainView(R.id.task_item_checkbox);
        TextView task_name_tv = holder.obtainView(R.id.task_name_tv);
        TextView task_desc_tv = holder.obtainView(R.id.task_desc_tv);
        task_name_tv.setText(taskItemEntity.name);
        if (taskItemEntity.state)//已完成
        {
            task_desc_tv.setText(String.format("%s %s",
                    DateUtils.get23Hour59MinFormat(taskItemEntity.updateTime)
                    , getProjectTaskGroupInfo(taskItemEntity)));
        } else {
            task_desc_tv.setText(String.format("%s %s",
                    DateUtils.get23Hour59MinFormat(taskItemEntity.dueTime)
                    , getProjectTaskGroupInfo(taskItemEntity)));
        }
    }

    /**
     * 获取 项目 任务组等信息的组合
     *
     * @param taskItemEntity
     * @return
     */
    private String getProjectTaskGroupInfo(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
            if (taskItemEntity.matter != null) {
                if (taskItemEntity.parentFlow != null) {
                    if (!TextUtils.isEmpty(taskItemEntity.parentFlow.name))
                        return taskItemEntity.matter.name + " - " + taskItemEntity.parentFlow.name;
                    else
                        return taskItemEntity.matter.name;
                } else {
                    if (!TextUtils.isEmpty(taskItemEntity.parentName))
                        return taskItemEntity.matter.name + " - " + taskItemEntity.parentName;
                    else
                        return taskItemEntity.matter.name;
                }
            }
        }
        return "未指定所属项目";
    }
}
