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
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseTaskRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.manager.TimerManager;

/**
 * Description
 * Company Beijing icourt
 * author  danyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：17/8/31
 * version 2.0.0
 */

public class TaskItemAdapter3 extends BaseTaskRecyclerAdapter<TaskEntity.TaskItemEntity> {
    private boolean isAddTime = true;//添加计时权限
    private static final int BLACK_COLOR = 0xFFa6a6a6;
    private static final int RED_COLOR = 0xFFec1d37;

    private static final int TYPE_TASK = 0;//item是任务
    private static final int TYPE_TASK_HEAD = 1;//item是任务组

    public void setAddTime(boolean addTime) {
        isAddTime = addTime;
    }

    @Override
    public int bindView(int viewtype) {
        if (viewtype == TYPE_TASK_HEAD) {
            return R.layout.adapter_item_task_title;
        } else {
            return R.layout.adapter_item_task;
        }
    }

    @Override
    public int getItemViewType(int position) {
        TaskEntity.TaskItemEntity item = getItem(position);
        if (item != null) {
            if (item.type == 0) {//说明是任务
                return TYPE_TASK;
            } else if (item.type == 1) {//说明是任务组
                return TYPE_TASK_HEAD;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, int position) {
        if (taskItemEntity.getItemType() == TYPE_TASK) {//说明是任务
            initTask(holder, taskItemEntity, position);
        } else if (taskItemEntity.getItemType() == TYPE_TASK_HEAD) {//说明是任务组
            initTaskGroup(holder, taskItemEntity, position);
        }
    }

    public boolean updateItem(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity == null) return false;
        int index = getData().indexOf(taskItemEntity);
        if (index >= 0 && index < getData().size()) {
            getData().set(index, taskItemEntity);
            if (getParentHeaderFooterAdapter() != null) {
                index = index + getParentHeaderFooterAdapter().getHeaderCount();
            }
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    /**
     * 初始化任务
     *
     * @param holder
     * @param taskItemEntity
     * @param position
     */
    private void initTaskGroup(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, int position) {
        holder.setText(R.id.task_time_group_name, taskItemEntity.groupName);
        holder.setText(R.id.task_time_group_count, String.valueOf(taskItemEntity.groupItemCount));
    }

    /**
     * 初始化任务
     *
     * @param holder
     * @param taskItemEntity
     * @param position
     */
    private void initTask(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, int position) {
        ImageView checkBox = holder.obtainView(R.id.task_item_checkbox);
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

        projectNameView.setText(getProjectName(taskItemEntity));
        if (taskItemEntity.state || !taskItemEntity.valid) {
            timeView.setVisibility(taskItemEntity.updateTime > 0 ? View.VISIBLE : View.GONE);
            timeView.setText(DateUtils.get23Hour59MinFormat(taskItemEntity.updateTime));
            timeView.setTextColor(BLACK_COLOR);
            timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.task_time_icon, 0, 0, 0);
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
        String timerTaskid = TimerManager.getInstance().getTimerTaskId();
        taskItemEntity.isTiming = !TextUtils.isEmpty(timerTaskid) && TextUtils.equals(timerTaskid, taskItemEntity.id);
        startTimmingViewSelect(startTimmingView, taskItemEntity.isTiming);
        checkBox.setSelected(taskItemEntity.state);
        if (!taskItemEntity.valid) {
            startTimmingView.setVisibility(View.GONE);
            checkBox.setImageResource(R.mipmap.restore);
//            checkBox.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.restore, 0, 0, 0);
        }
        holder.bindChildClick(checkBox);
        holder.bindChildClick(startTimmingView);
    }

    /**
     * 获取项目名称
     *
     * @param taskItemEntity
     * @return
     */
    private String getProjectName(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity == null) return "";
        StringBuilder stringBuilder = new StringBuilder();
        if (taskItemEntity.matter != null) {
            stringBuilder.append(taskItemEntity.matter.name);
            if (taskItemEntity.parentFlow != null) {
                if (!TextUtils.isEmpty(taskItemEntity.parentFlow.name))
                    stringBuilder.append(" － ").append(taskItemEntity.parentFlow.name);
            } else {
                if (!TextUtils.isEmpty(taskItemEntity.parentName))
                    stringBuilder.append(" － ").append(taskItemEntity.parentName);
            }
        } else {
            stringBuilder.append("未指定所属项目");
        }
        return stringBuilder.toString();
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
