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
import com.icourt.alpha.utils.DateUtils;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TaskItemAdapter extends BaseArrayRecyclerAdapter<TaskEntity.TaskItemEntity> {
    private boolean isAddTime = true;//添加计时权限
    private static final int BLACK_COLOR = 0xFFa6a6a6;
    private static final int RED_COLOR = 0xFFec1d37;

    public void setAddTime(boolean addTime) {
        isAddTime = addTime;
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
        startTimmingView.setVisibility(isAddTime ? View.VISIBLE : View.GONE);
        StringBuffer stringBuffer = new StringBuffer();
        if (taskItemEntity.matter != null) {
            stringBuffer.append(taskItemEntity.matter.name);
            if (taskItemEntity.parentFlow != null) {
                if (!TextUtils.isEmpty(taskItemEntity.parentFlow.name))
                    stringBuffer.append(" － ").append(taskItemEntity.parentFlow.name);
            } else {
                if (!TextUtils.isEmpty(taskItemEntity.parentName))
                    stringBuffer.append(" － ").append(taskItemEntity.parentName);
            }
        } else {
            stringBuffer.append("未指定所属项目");
        }
        projectNameView.setText(stringBuffer.toString());
        if (taskItemEntity.state) {
            timeTextSetData(timeView, taskItemEntity.updateTime);
        } else {
            timeTextSetData(timeView, taskItemEntity.dueTime);
        }
        textViewSetData(checkListView, taskItemEntity.doneItemCount + "/" + taskItemEntity.itemCount, taskItemEntity.itemCount);
        textViewSetData(documentNumView, String.valueOf(taskItemEntity.attachmentCount), taskItemEntity.attachmentCount);
        textViewSetData(commentNumView, String.valueOf(taskItemEntity.commentCount), taskItemEntity.commentCount);
        if (taskItemEntity.attendeeUsers != null) {
            TaskUsersAdapter usersAdapter;
            if (recyclerView.getLayoutManager() == null) {
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
                layoutManager.setAutoMeasureEnabled(true);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                layoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(usersAdapter = new TaskUsersAdapter(recyclerView.getContext()));
            }
            usersAdapter = (TaskUsersAdapter) recyclerView.getAdapter();
            usersAdapter.bindData(true, taskItemEntity.attendeeUsers);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
        }
        if (checkListView.getVisibility() == View.VISIBLE &&
                documentNumView.getVisibility() == View.VISIBLE &&
                commentNumView.getVisibility() == View.VISIBLE &&
                timeView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }

        startTimmingViewSelect(startTimmingView, taskItemEntity.isTiming);
        checkBox.setChecked(taskItemEntity.state);
        holder.bindChildClick(checkBox);
        holder.bindChildClick(startTimmingView);
    }

    /**
     * 设置时间
     *
     * @param timeView
     * @param timeMins
     */
    private void timeTextSetData(TextView timeView, long timeMins) {
        timeView.setVisibility(timeMins > 0 ? View.VISIBLE : View.GONE);
        timeView.setText(DateUtils.get23Hour59MinFormat(timeMins));
        timeView.setTextColor(timeMins < DateUtils.millis() ? RED_COLOR : BLACK_COLOR);
        timeView.setCompoundDrawablesWithIntrinsicBounds(timeMins < DateUtils.millis() ? R.mipmap.ic_fail : R.mipmap.task_time_icon, 0, 0, 0);
    }

    /**
     * 设置开始计时图标
     *
     * @param startTimmingView
     * @param isTiming
     */
    private void startTimmingViewSelect(ImageView startTimmingView, boolean isTiming) {
        if (startTimmingView == null) return;
        startTimmingView.setImageResource(isTiming ? R.drawable.orange_side_dot_bg : R.mipmap.icon_start_20);
        startTimmingView.setTag(isTiming ? R.drawable.orange_side_dot_bg : R.mipmap.icon_start_20);
    }

    /**
     * 检查项、附件、评论设置数量
     *
     * @param textView
     * @param content
     */
    private void textViewSetData(TextView textView, String content, long count) {
        if (textView == null) return;
        textView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        textView.setText(content);
    }

}
