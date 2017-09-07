package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.widget.manager.TimerManager;

/**
 * * Description
 * Company Beijing icourt
 * author  danyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/1
 * version 2.0.0
 */

public class TaskItemAdapter2 extends BaseMultiItemQuickAdapter<TaskEntity.TaskItemEntity, BaseViewHolder> {

    private boolean isAddTime = true;//添加计时权限
    private static final int BLACK_COLOR = 0xFFa6a6a6;
    private static final int RED_COLOR = 0xFFec1d37;

    private static final int TYPE_TASK = 0;//0,任务
    private static final int TYPE_TASK_GROUP = 1;//1，任务组

    public TaskItemAdapter2() {
        super(null);
        addItemType(TYPE_TASK, R.layout.adapter_item_task2);
        addItemType(TYPE_TASK_GROUP, R.layout.adapter_item_task_title);
    }

    public void setAddTime(boolean addTime) {
        isAddTime = addTime;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity.type == TYPE_TASK_GROUP) {//说明是标题
            initTaskTitle(baseViewHolder, taskItemEntity);
        } else {
            initTaskItem(baseViewHolder, taskItemEntity);
        }
    }

    /**
     * 构造task的item
     *
     * @param baseViewHolder
     * @param taskItemEntity
     */
    private void initTaskItem(BaseViewHolder baseViewHolder, TaskEntity.TaskItemEntity taskItemEntity) {
        CheckedTextView checkBox = baseViewHolder.getView(R.id.task_item_checkbox);
        TextView taskNameView = baseViewHolder.getView(R.id.task_title_tv);
        ImageView startTimmingView = baseViewHolder.getView(R.id.task_item_start_timming);
        TextView projectNameView = baseViewHolder.getView(R.id.task_project_belong_tv);
        TextView timeView = baseViewHolder.getView(R.id.task_time_tv);
        TextView checkListView = baseViewHolder.getView(R.id.task_check_list_tv);
        TextView documentNumView = baseViewHolder.getView(R.id.task_file_num_tv);
        TextView commentNumView = baseViewHolder.getView(R.id.task_comment_num_tv);
        RecyclerView recyclerView = baseViewHolder.getView(R.id.tasl_member_recyclerview);

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
        String timerTaskid = TimerManager.getInstance().getTimerTaskId();
        taskItemEntity.isTiming = !TextUtils.isEmpty(timerTaskid) && TextUtils.equals(timerTaskid, taskItemEntity.id);
        startTimmingViewSelect(startTimmingView, taskItemEntity.isTiming);
        if (!taskItemEntity.valid) {
            startTimmingView.setVisibility(View.GONE);
            checkBox.setBackgroundResource(R.mipmap.restore);
        } else {
            startTimmingView.setVisibility(View.VISIBLE);
            checkBox.setBackgroundResource(R.drawable.sl_checkbox);
            checkBox.setChecked(taskItemEntity.state);
        }

        baseViewHolder.addOnClickListener(R.id.task_item_checkbox);
        baseViewHolder.addOnClickListener(R.id.task_item_start_timming);
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

    /**
     * 删除其中某一个
     *
     * @param t
     * @return
     */
    public boolean removeItem(TaskEntity.TaskItemEntity t) {
        if (t == null) return false;
        int index = getData().indexOf(t);
        if (index >= 0) {
            //remove方法已经对header进行判断了
            remove(index);
            return true;
        }
        return false;
    }

    /**
     * 更新某一条
     *
     * @param t
     * @return
     */
    public boolean updateItem(TaskEntity.TaskItemEntity t) {
        if (t == null) return false;
        int index = getData().indexOf(t);
        if (index >= 0 && index < getData().size()) {
            getData().set(index, t);
            LogUtils.i("修改了=============>" + index);
            notifyItemChanged(index + getHeaderLayoutCount());
            return true;
        }
        return false;
    }
}
