package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TaskItemAdapter extends BaseArrayRecyclerAdapter<TaskEntity.TaskItemEntity> implements BaseRecyclerAdapter.OnItemChildClickListener, BaseRecyclerAdapter.OnItemChildLongClickListener {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, int position) {
        CheckBox checkBox = holder.obtainView(R.id.task_item_checkbox);
        TextView taskNameView = holder.obtainView(R.id.task_title_tv);
        ImageView startTimmingView = holder.obtainView(R.id.task_item_start_timming);
        TextView projectNameView = holder.obtainView(R.id.task_project_belong_tv);
        TextView timeView = holder.obtainView(R.id.task_time_tv);
        TextView checkListView = holder.obtainView(R.id.task_check_list_tv);
        TextView documentNumView = holder.obtainView(R.id.task_file_num_tv);
        TextView commentNumView = holder.obtainView(R.id.task_comment_num_tv);

        taskNameView.setText(taskItemEntity.name);
        projectNameView.setText(taskItemEntity.taskGroupName);
        timeView.setText("06:22");
        checkListView.setText(taskItemEntity.doneItemTaskCount + "/" + taskItemEntity.itemTaskCount);
        documentNumView.setText(String.valueOf(taskItemEntity.documentCount));
        commentNumView.setText(String.valueOf(taskItemEntity.commentCount));
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }
}
