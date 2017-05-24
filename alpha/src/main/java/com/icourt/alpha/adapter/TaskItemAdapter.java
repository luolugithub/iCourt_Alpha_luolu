package com.icourt.alpha.adapter;

import android.graphics.Color;
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
import com.icourt.alpha.utils.DateUtils;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TaskItemAdapter extends BaseArrayRecyclerAdapter<TaskEntity.TaskItemEntity> {

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

        if (taskNameView != null) {
            taskNameView.setText(taskItemEntity.name);
        }
        if (projectNameView != null) {
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
        }
        if (timeView != null) {
            if (taskItemEntity.dueTime > 0) {
                timeView.setVisibility(View.VISIBLE);
                timeView.setText(DateUtils.getTimeDateFormatMm(taskItemEntity.dueTime));
                if (taskItemEntity.dueTime < DateUtils.millis()) {
                    timeView.setTextColor(Color.parseColor("#FF0000"));
                    timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_fail, 0, 0, 0);
                } else {
                    timeView.setTextColor(Color.parseColor("#FF8c8f92"));
                    timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.task_time_icon, 0, 0, 0);
                }
            } else {
                timeView.setVisibility(View.GONE);
            }
        }
        if (checkListView != null) {
            if (taskItemEntity.itemCount > 0) {
                checkListView.setVisibility(View.VISIBLE);
                checkListView.setText(taskItemEntity.doneItemCount + "/" + taskItemEntity.itemCount);
            } else {
                checkListView.setVisibility(View.GONE);
            }
        }
        if (documentNumView != null) {
            if (taskItemEntity.attachmentCount > 0) {
                documentNumView.setVisibility(View.VISIBLE);
                documentNumView.setText(String.valueOf(taskItemEntity.attachmentCount));
            } else {
                documentNumView.setVisibility(View.GONE);
            }
        }
        if (commentNumView != null) {
            if (taskItemEntity.commentCount > 0) {
                commentNumView.setVisibility(View.VISIBLE);
                commentNumView.setText(String.valueOf(taskItemEntity.commentCount));
            } else {
                commentNumView.setVisibility(View.GONE);
            }
        }
        if (recyclerView != null) {
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
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
        if (startTimmingView != null) {
            if (taskItemEntity.isTiming) {
                startTimmingView.setImageResource(R.drawable.orange_side_dot_bg);
                startTimmingView.setTag(R.drawable.orange_side_dot_bg);
            } else {
                startTimmingView.setImageResource(R.mipmap.icon_start_20);
                startTimmingView.setTag(R.mipmap.icon_start_20);
            }
        }
        if (checkBox != null) {
            checkBox.setChecked(taskItemEntity.state);
        }
        holder.bindChildClick(checkBox);
        holder.bindChildClick(startTimmingView);
    }
}
