package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TaskItemAdapter extends BaseArrayRecyclerAdapter<TaskEntity.TaskItemEntity> {
    TimeEntity.ItemEntity itemEntity;

    public void setItemEntity(TimeEntity.ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

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

        if (taskItemEntity.matter != null) {
            if (taskItemEntity.parentFlow != null) {
                projectNameView.setText(taskItemEntity.matter.name + " - " + taskItemEntity.parentFlow.name);
            } else {
                if (!TextUtils.isEmpty(taskItemEntity.parentName))
                    projectNameView.setText(taskItemEntity.matter.name + " - " + taskItemEntity.parentName);
                else
                    projectNameView.setText(taskItemEntity.matter.name);
            }
        }

        if (taskItemEntity.timingSum > 0) {
            timeView.setVisibility(View.VISIBLE);
            timeView.setText(getHm(taskItemEntity.timingSum));
        } else {
            timeView.setVisibility(View.INVISIBLE);
        }
        if (taskItemEntity.itemCount > 0) {
            checkListView.setVisibility(View.VISIBLE);
            checkListView.setText(taskItemEntity.doneItemCount + "/" + taskItemEntity.itemCount);
        } else {
            checkListView.setVisibility(View.INVISIBLE);
        }
        if (taskItemEntity.attachmentCount > 0) {
            documentNumView.setVisibility(View.VISIBLE);
            documentNumView.setText(String.valueOf(taskItemEntity.attachmentCount));
        } else {
            documentNumView.setVisibility(View.INVISIBLE);
        }
        if (taskItemEntity.commentCount > 0) {
            commentNumView.setVisibility(View.VISIBLE);
            commentNumView.setText(String.valueOf(taskItemEntity.commentCount));
        } else {
            commentNumView.setVisibility(View.INVISIBLE);
        }
        if (taskItemEntity.attendeeUsers != null) {
            TaskUsersAdapter usersAdapter;
            if (recyclerView.getLayoutManager() == null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                layoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutManager);
                usersAdapter = new TaskUsersAdapter();
                recyclerView.setAdapter(usersAdapter);
            }
            usersAdapter = (TaskUsersAdapter) recyclerView.getAdapter();
            usersAdapter.bindData(true, taskItemEntity.attendeeUsers);
        }

        if (taskItemEntity.isTiming) {
            startTimmingView.setImageResource(R.drawable.orange_side_dot_bg);
            startTimmingView.setTag(R.drawable.orange_side_dot_bg);
        } else {
            startTimmingView.setImageResource(R.mipmap.icon_start_20);
            startTimmingView.setTag(R.mipmap.icon_start_20);
        }
        checkBox.setChecked(taskItemEntity.state);
        holder.bindChildClick(checkBox);
        holder.bindChildClick(startTimmingView);
    }

    public String getHm(long times) {
        times /= 1000;
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        return String.format("%02d:%02d", hour, minute);
    }
}
