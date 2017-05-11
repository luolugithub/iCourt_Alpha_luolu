package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
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
        RecyclerView recyclerView = holder.obtainView(R.id.tasl_member_recyclerview);

        taskNameView.setText(taskItemEntity.name);
        projectNameView.setText(taskItemEntity.parentName);
        timeView.setText(DateUtils.getTimeDurationDate(taskItemEntity.timingSum));
        checkListView.setText(taskItemEntity.doneItemCount + "/" + taskItemEntity.itemCount);
        documentNumView.setText(String.valueOf(taskItemEntity.attachmentCount));
        commentNumView.setText(String.valueOf(taskItemEntity.commentCount));
        if (taskItemEntity.attendeeUserEntities != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            layoutManager.setReverseLayout(true);
            recyclerView.setLayoutManager(layoutManager);
            TaskUsersAdapter usersAdapter = new TaskUsersAdapter();
            usersAdapter.bindData(false, taskItemEntity.attendeeUserEntities);
        }
        holder.bindChildClick(checkBox);
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }

    @Override
    public boolean onItemChildLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

        return false;
    }
}
