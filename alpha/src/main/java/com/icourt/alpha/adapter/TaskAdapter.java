package com.icourt.alpha.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.widget.manager.TimerManager;

/**
 * * Description
 * Company Beijing icourt
 * author  danyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/1
 * version 2.0.0
 */

public class TaskAdapter extends BaseAdapter<TaskEntity.TaskItemEntity> {

    /**
     * 添加计时权限
     */
    private boolean isAddTime = true;
    private static final int BLACK_COLOR = 0xFFa6a6a6;
    private static final int RED_COLOR = 0xFFec1d37;

    /**
     * 0,任务
     */
    private static final int TYPE_TASK = 0;
    /**
     * 1，任务组
     */
    private static final int TYPE_TASK_GROUP = 1;

    public void setAddTime(boolean addTime) {
        isAddTime = addTime;
    }

    @Override
    public int getViewType(int index) {
        TaskEntity.TaskItemEntity itemEntity = getItem(index);
        if (itemEntity != null) {
            switch (itemEntity.type) {
                case 0:
                    return TYPE_TASK;
                case 1:
                    return TYPE_TASK_GROUP;
            }
        }
        return super.getViewType(index);
    }

    @Override
    public int bindView(int i) {
        switch (i) {
            //任务
            case TYPE_TASK:
                return R.layout.adapter_item_task;
            //任务组
            case TYPE_TASK_GROUP:
                return R.layout.adapter_item_task_title;
        }
        return 0;
    }

    @Override
    public void onBindHolder(BaseViewHolder baseViewHolder, @Nullable TaskEntity.TaskItemEntity taskItemEntity, int i) {
        int itemViewType = baseViewHolder.getItemViewType();
        if (itemViewType == TYPE_TASK) {//任务
            initTaskItem(baseViewHolder, taskItemEntity);
        } else if (itemViewType == TYPE_TASK_GROUP) {//任务组
            initTaskTitle(baseViewHolder, taskItemEntity);
        }
    }

    /**
     * 构造task的item
     *
     * @param baseViewHolder
     * @param taskItemEntity
     */
    private void initTaskItem(BaseViewHolder baseViewHolder, TaskEntity.TaskItemEntity taskItemEntity) {
        CheckedTextView checkBox = baseViewHolder.obtainView(R.id.task_item_checkbox);
        TextView taskNameView = baseViewHolder.obtainView(R.id.task_title_tv);
        ImageView startTimmingView = baseViewHolder.obtainView(R.id.task_item_start_timming);
        TextView projectNameView = baseViewHolder.obtainView(R.id.task_project_belong_tv);
        TextView timeView = baseViewHolder.obtainView(R.id.task_time_tv);
        TextView checkListView = baseViewHolder.obtainView(R.id.task_check_list_tv);
        TextView documentNumView = baseViewHolder.obtainView(R.id.task_file_num_tv);
        TextView commentNumView = baseViewHolder.obtainView(R.id.task_comment_num_tv);

        RecyclerView recyclerView = baseViewHolder.obtainView(R.id.tasl_member_recyclerview);

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

        textViewSetData(checkListView, String.format("%s/%s", taskItemEntity.doneItemCount, taskItemEntity.itemCount), taskItemEntity.itemCount);
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
                usersAdapter = new TaskUsersAdapter(recyclerView.getContext());
                recyclerView.setAdapter(usersAdapter);
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
        String timerTaskId = TimerManager.getInstance().getTimerTaskId();
        taskItemEntity.isTiming = !TextUtils.isEmpty(timerTaskId) && TextUtils.equals(timerTaskId, taskItemEntity.id);
        startTimmingViewSelect(startTimmingView, taskItemEntity.isTiming);
        if (!taskItemEntity.valid) {
            startTimmingView.setVisibility(View.GONE);
            checkBox.setBackgroundResource(R.mipmap.restore);
        } else {
            startTimmingView.setVisibility(View.VISIBLE);
            checkBox.setBackgroundResource(R.drawable.sl_checkbox);
            checkBox.setChecked(taskItemEntity.state);
        }
        baseViewHolder.bindChildClick(R.id.task_item_checkbox);
        baseViewHolder.bindChildClick(R.id.task_item_start_timming);

    }

    /**
     * 动态计算任务底部宽度，是否显示负责人头像： 暂时不做
     *
     * @param parentLayout
     * @param otherLayout
     * @param recyclerView
     * @return
     */
    private boolean isShowUserRecyclerView(LinearLayout parentLayout, LinearLayout otherLayout, RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }
        if (recyclerView.getAdapter() == null) {
            return false;
        }
        int itemCount = recyclerView.getAdapter().getItemCount();
        int parentWidth = parentLayout.getWidth();
        int otherWidth = otherLayout.getWidth();

        int recyclerViewWidth = itemCount * 26 - (itemCount - 1) * 8;
        return (parentWidth - otherWidth - DensityUtil.dip2px(recyclerView.getContext(), 5) > recyclerViewWidth);
    }

    /**
     * 构造task的title
     *
     * @param baseViewHolder
     * @param taskItemEntity
     */
    private void initTaskTitle(BaseViewHolder baseViewHolder, TaskEntity.TaskItemEntity taskItemEntity) {
        baseViewHolder.setText(R.id.task_time_group_name, taskItemEntity.groupName);
        baseViewHolder.setText(R.id.task_time_group_count, String.valueOf(taskItemEntity.groupTaskCount));
    }

    /**
     * 获取项目名称
     *
     * @param taskItemEntity
     * @return
     */
    private String getProjectName(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (taskItemEntity.matter != null) {
            stringBuilder.append(taskItemEntity.matter.name);
            if (taskItemEntity.parentFlow != null) {
                if (!TextUtils.isEmpty(taskItemEntity.parentFlow.name)) {
                    stringBuilder.append(" － ").append(taskItemEntity.parentFlow.name);
                }
            } else {
                if (!TextUtils.isEmpty(taskItemEntity.parentName)) {
                    stringBuilder.append(" － ").append(taskItemEntity.parentName);
                }
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
        if (startTimmingView == null) {
            return;
        }
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
        if (textView == null) {
            return;
        }
        textView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        textView.setText(content);
    }

    /**
     * 清空所有数据
     */
    public void clearData() {
        getData().clear();
        notifyDataSetChanged();
    }
}
