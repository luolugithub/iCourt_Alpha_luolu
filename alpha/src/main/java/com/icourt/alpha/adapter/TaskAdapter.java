package com.icourt.alpha.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.utils.LoginInfoUtils.getLoginUserId;
import static com.icourt.alpha.utils.LoginInfoUtils.getLoginUserInfo;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class TaskAdapter extends BaseArrayRecyclerAdapter<TaskEntity>
        implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemLongClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener {
    TimeEntity.ItemEntity itemEntity;

    public TaskAdapter() {
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
        this.setOnItemChildClickListener(this);
    }

    public void setItemEntity(TimeEntity.ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_parent_task;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity taskEntity, int position) {
        if (taskEntity == null) return;
        TextView task_title_time_tv = holder.obtainView(R.id.task_time_group_name);
        TextView task_title_count_tv = holder.obtainView(R.id.task_time_group_count);
        task_title_time_tv.setText(taskEntity.groupName);
        task_title_count_tv.setText(String.valueOf(taskEntity.groupTaskCount));
        RecyclerView recyclerView = holder.obtainView(R.id.parent_item_task_recyclerview);
        TaskItemAdapter taskItemAdapter;
        if (recyclerView.getLayoutManager() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            taskItemAdapter = new TaskItemAdapter();
            recyclerView.setAdapter(taskItemAdapter);
            taskItemAdapter.setItemEntity(itemEntity);
            taskItemAdapter.setOnItemClickListener(super.onItemClickListener);
            taskItemAdapter.setOnItemChildClickListener(super.onItemChildClickListener);
        }
        taskItemAdapter = (TaskItemAdapter) recyclerView.getAdapter();


        taskItemAdapter.bindData(true, taskEntity.items);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
//        new CenterMenuDialog(view.getContext(), null, Arrays.asList(
//                new ItemsEntity("分配给", R.mipmap.tab_message),
//                new ItemsEntity("到期日", R.mipmap.tab_message),
//                new ItemsEntity("提醒", R.mipmap.tab_message),
//                new ItemsEntity("项目/任务组", R.mipmap.tab_message),
//                new ItemsEntity("开始计时", R.mipmap.tab_message),
//                new ItemsEntity("查看详情", R.mipmap.tab_message)))
//                .show();
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        new CenterMenuDialog(view.getContext(), null, Arrays.asList(
                new ItemsEntity("分配给", R.mipmap.tab_message),
                new ItemsEntity("到期日", R.mipmap.tab_message),
                new ItemsEntity("提醒", R.mipmap.tab_message),
                new ItemsEntity("项目/任务组", R.mipmap.tab_message),
                new ItemsEntity("开始计时", R.mipmap.tab_message),
                new ItemsEntity("查看详情", R.mipmap.tab_message)))
                .show();
        return true;
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        TimerManager.getInstance().stopTimer();
                        ((ImageView)view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        TimerManager.getInstance().addTimer(getTimer(itemEntity));
                        ((ImageView)view).setImageResource(R.drawable.orange_side_dot_bg);
                    }
                    break;
                case R.id.task_item_checkbox:
                    CheckBox checkbox = (CheckBox) view;
                    if (checkbox.isChecked()) {//完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(view.getContext(), "该任务由多人负责,确定完成?", itemEntity, checkbox);
                            } else {
                                updateTask(itemEntity, true, checkbox);
                            }
                        } else {
                            updateTask(itemEntity, true, checkbox);
                        }
                    } else {
                        updateTask(itemEntity, false, checkbox);
                    }
                    break;

            }
        }
    }

    /**
     * 获取添加计时实体
     *
     * @return
     */
    private TimeEntity.ItemEntity getTimer(TaskEntity.TaskItemEntity taskItemEntity) {
        TimeEntity.ItemEntity itemEntity = new TimeEntity.ItemEntity();
        if (taskItemEntity != null) {
            itemEntity.taskPkId = taskItemEntity.id;
            itemEntity.name = taskItemEntity.name;
            itemEntity.workDate = DateUtils.millis();
            itemEntity.createUserId = getLoginUserId();
            itemEntity.username = getLoginUserInfo().getName();
            itemEntity.startTime = DateUtils.millis();
            if (taskItemEntity.matter != null) {
                itemEntity.matterPkId = taskItemEntity.matter.id;
            }
        }
        return itemEntity;
    }

    /**
     * 显示多人任务提醒
     *
     * @param context
     * @param message
     * @param itemEntity
     * @param checkbox
     */
    private void showFinishDialog(final Context context, String message, final TaskEntity.TaskItemEntity itemEntity, final CheckBox checkbox) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        updateTask(itemEntity, true, checkbox);
                        break;
                    case Dialog.BUTTON_NEGATIVE://取消
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     * @param state
     * @param checkbox
     */
    private void updateTask(TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckBox checkbox) {
        if (state) {
            showLoadingDialog(checkbox.getContext(), "完成任务...");
        } else {
            showLoadingDialog(checkbox.getContext(), "取消完成任务...");
        }
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                checkbox.setChecked(state);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                checkbox.setChecked(!state);
            }
        });
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @param state
     * @return
     */
    private String getTaskJson(TaskEntity.TaskItemEntity itemEntity, boolean state) {
        try {
            itemEntity.state = state;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
