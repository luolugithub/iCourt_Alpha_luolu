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
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icourt.alpha.utils.LoginInfoUtils.getLoginUserId;

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

    private OnShowFragmenDialogListener onShowFragmenDialogListener;
    private static final int SHOW_DELETE_DIALOG = 0;//删除提示对话框
    private static final int SHOW_FINISH_DIALOG = 1;//完成任务提示对话框

    private boolean isEditTask = false;//编辑任务权限
    private boolean isDeleteTask = false;//删除任务权限
    private boolean isAddTime = false;//添加计时权限

    public TaskAdapter() {
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
        this.setOnItemChildClickListener(this);
    }

    public void setEditTask(boolean editTask) {
        isEditTask = true;
    }

    public void setDeleteTask(boolean deleteTask) {
        isDeleteTask = true;
    }

    public void setAddTime(boolean addTime) {
        isAddTime = true;
    }

    public void setOnShowFragmenDialogListener(OnShowFragmenDialogListener onShowFragmenDialogListener) {
        this.onShowFragmenDialogListener = onShowFragmenDialogListener;
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
            taskItemAdapter.setAddTime(isAddTime);
            recyclerView.setAdapter(taskItemAdapter);
            taskItemAdapter.setOnItemClickListener(super.onItemClickListener);
            taskItemAdapter.setOnItemChildClickListener(super.onItemChildClickListener);
            taskItemAdapter.setOnItemLongClickListener(super.onItemLongClickListener);
        }
        taskItemAdapter = (TaskItemAdapter) recyclerView.getAdapter();

        taskItemAdapter.bindData(true, taskEntity.items);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
        ItemsEntity timeEntity = new ItemsEntity("开始计时", R.mipmap.time_start_orange_task);
        if (taskItemEntity.isTiming) {
            timeEntity.itemIconRes = R.mipmap.time_stop_orange_task;
            timeEntity.itemTitle = "停止计时";
        } else {
            timeEntity.itemIconRes = R.mipmap.time_start_orange_task;
            timeEntity.itemTitle = "开始计时";
        }
        if (isEditTask && isDeleteTask) {
            showLongMeau(view.getContext(), Arrays.asList(
                    new ItemsEntity("项目/任务组", R.mipmap.project_orange),
                    new ItemsEntity("分配给", R.mipmap.assign_orange),
                    new ItemsEntity("到期日", R.mipmap.date_orange),
                    timeEntity,
                    new ItemsEntity("查看详情", R.mipmap.info_orange),
                    new ItemsEntity("删除", R.mipmap.trash_orange)), taskItemEntity);
        } else if (isDeleteTask && !isEditTask) {
            showLongMeau(view.getContext(), Arrays.asList(
                    new ItemsEntity("查看详情", R.mipmap.info_orange),
                    timeEntity,
                    new ItemsEntity("删除", R.mipmap.trash_orange)), taskItemEntity);
        } else if (!isDeleteTask && isEditTask) {
            showLongMeau(view.getContext(), Arrays.asList(
                    new ItemsEntity("项目/任务组", R.mipmap.project_orange),
                    new ItemsEntity("分配给", R.mipmap.assign_orange),
                    new ItemsEntity("到期日", R.mipmap.date_orange),
                    timeEntity,
                    new ItemsEntity("查看详情", R.mipmap.info_orange)
                    ),
                    taskItemEntity);
        }
        return true;
    }

    private void showLongMeau(Context context, List<ItemsEntity> itemsEntities, TaskEntity.TaskItemEntity taskItemEntity) {
        CenterMenuDialog centerMenuDialog = new CenterMenuDialog(context, null, itemsEntities);
        centerMenuDialog.show();
        centerMenuDialog.setOnItemClickListener(new CustOnItemClickListener(centerMenuDialog, taskItemEntity));
    }

    private class CustOnItemClickListener implements OnItemClickListener {
        CenterMenuDialog centerMenuDialog;
        TaskEntity.TaskItemEntity taskItemEntity;

        CustOnItemClickListener(CenterMenuDialog centerMenuDialog, TaskEntity.TaskItemEntity taskItemEntity) {
            this.centerMenuDialog = centerMenuDialog;
            this.taskItemEntity = taskItemEntity;
        }

        @Override
        public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
            if (centerMenuDialog != null)
                centerMenuDialog.dismiss();
            if (adapter instanceof CenterMenuDialog.MenuAdapter) {
                ItemsEntity entity = (ItemsEntity) adapter.getItem(position);
                if (taskItemEntity != null) {
                    switch (entity.getItemIconRes()) {
                        case R.mipmap.assign_orange://分配给
                            if (onShowFragmenDialogListener != null)
                                if (taskItemEntity.matter != null) {
                                    onShowFragmenDialogListener.showUserSelectDialog(taskItemEntity.matter.id, taskItemEntity);
                                } else {
                                    showToast("请先选择项目");
                                }
                            break;
                        case R.mipmap.date_orange://到期日
                            if (onShowFragmenDialogListener != null)
                                onShowFragmenDialogListener.showDateSelectDialog(taskItemEntity);
                            break;
                        case R.mipmap.info_orange://查看详情
                            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
                            break;
                        case R.mipmap.project_orange://项目/任务组
                            if (onShowFragmenDialogListener != null)
                                onShowFragmenDialogListener.showProjectSelectDialog(taskItemEntity);
                            break;
                        case R.mipmap.time_start_orange_task://开始计时
                            if (!taskItemEntity.isTiming) {
                                TimerManager.getInstance().addTimer(getTimer(taskItemEntity));
                                entity.itemIconRes = R.mipmap.time_stop_orange_task;
                                entity.itemTitle = "停止计时";
                                ((CenterMenuDialog.MenuAdapter) adapter).updateItem(entity);
                            }
                            break;
                        case R.mipmap.time_stop_orange_task://停止计时
                            if (taskItemEntity.isTiming) {
                                TimerManager.getInstance().stopTimer();
                                entity.itemIconRes = R.mipmap.time_start_orange_task;
                                entity.itemTitle = "开始计时";
                                ((CenterMenuDialog.MenuAdapter) adapter).updateItem(entity);
                            }
                            break;
                        case R.mipmap.trash_orange://删除
                            if (taskItemEntity.attendeeUsers != null) {
                                if (taskItemEntity.attendeeUsers.size() > 1) {
                                    showFinishDialog(view.getContext(), "该任务由多人负责,确定删除?", taskItemEntity, SHOW_DELETE_DIALOG, null);
                                } else {
                                    showFinishDialog(view.getContext(), "是非成败转头空，确定要删除吗?", taskItemEntity, SHOW_DELETE_DIALOG, null);
                                }
                            } else {
                                showFinishDialog(view.getContext(), "是非成败转头空，确定要删除吗?", taskItemEntity, SHOW_DELETE_DIALOG, null);
                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, final View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        TimerManager.getInstance().stopTimer();
                        ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        showLoadingDialog(view.getContext(), null);
                        TimerManager.getInstance().addTimer(getTimer(itemEntity), new Callback<TimeEntity.ItemEntity>() {
                            @Override
                            public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                dismissLoadingDialog();
                                ((ImageView) view).setImageResource(R.drawable.orange_side_dot_bg);
                                if (response.body() != null) {
                                    TimerTimingActivity.launch(view.getContext(), response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                                dismissLoadingDialog();
                                ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                            }
                        });
                    }
                    break;
                case R.id.task_item_checkbox:
                    CheckBox checkbox = (CheckBox) view;
                    if (isEditTask) {
                        if (checkbox.isChecked()) {    //完成任务
                            if (itemEntity.attendeeUsers != null) {
                                if (itemEntity.attendeeUsers.size() > 1) {
                                    showFinishDialog(view.getContext(), "该任务由多人负责,确定完成?", itemEntity, SHOW_FINISH_DIALOG, checkbox);
                                } else {
                                    updateTask(itemEntity, true, checkbox);
                                }
                            } else {
                                updateTask(itemEntity, true, checkbox);
                            }
                        } else {
                            updateTask(itemEntity, false, checkbox);
                        }
                    } else {
                        checkbox.setChecked(!checkbox.isChecked());
                        showTopSnackBar(view, "您没有编辑任务的权限");
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
            itemEntity.taskName = taskItemEntity.name;
            itemEntity.name = taskItemEntity.name;
            itemEntity.workDate = DateUtils.millis();
            itemEntity.createUserId = getLoginUserId();
            if (LoginInfoUtils.getLoginUserInfo() != null) {
                itemEntity.username = LoginInfoUtils.getLoginUserInfo().getName();
            }
            itemEntity.startTime = DateUtils.millis();
            if (taskItemEntity.matter != null) {
                itemEntity.matterPkId = taskItemEntity.matter.id;
                itemEntity.matterName = taskItemEntity.matter.name;
            }
            if (taskItemEntity.parentFlow != null) {
                itemEntity.workTypeName = taskItemEntity.parentFlow.name;
                itemEntity.workTypeId = taskItemEntity.parentFlow.id;
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
    private void showFinishDialog(final Context context, String message, final TaskEntity.TaskItemEntity itemEntity, final int type, final CheckBox checkbox) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        if (type == SHOW_DELETE_DIALOG) {
                            deleteTask(context, itemEntity);
                        } else if (type == SHOW_FINISH_DIALOG) {
                            if (itemEntity.state) {
                                updateTask(itemEntity, false, checkbox);
                            } else {
                                updateTask(itemEntity, true, checkbox);
                            }
                        }
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
        showLoadingDialog(checkbox.getContext(), null);
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
     * 删除任务
     */
    private void deleteTask(Context context, TaskEntity.TaskItemEntity itemEntity) {
        showLoadingDialog(context, null);
        getApi().taskDelete(itemEntity.id).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));

            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
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

    public interface OnShowFragmenDialogListener {
        void showUserSelectDialog(String projectId, TaskEntity.TaskItemEntity taskItemEntity);

        void showDateSelectDialog(TaskEntity.TaskItemEntity taskItemEntity);

        void showProjectSelectDialog(TaskEntity.TaskItemEntity taskItemEntity);
    }
}
