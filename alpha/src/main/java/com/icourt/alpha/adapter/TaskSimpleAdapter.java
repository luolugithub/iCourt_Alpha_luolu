package com.icourt.alpha.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/8
 * version 1.0.0
 */

public class TaskSimpleAdapter extends MultiSelectRecyclerAdapter<TaskEntity.TaskItemEntity>
        implements BaseRecyclerAdapter.OnItemChildClickListener, BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {
    private static final int TIME_HOUR_23 = 23;
    private static final int TIME_MIN_59 = 59;

    public TaskSimpleAdapter() {
        this.setOnItemChildClickListener(this);
        this.setOnItemClickListener(this);
    }

    private OnShowFragmenDialogListener onShowFragmenDialogListener;

    public void setOnShowFragmenDialogListener(OnShowFragmenDialogListener onShowFragmenDialogListener) {
        this.onShowFragmenDialogListener = onShowFragmenDialogListener;
    }

    @Override
    public int bindView(int viewType) {
        return R.layout.adapter_item_simple_task;
    }


    @Override
    public void onBindSelectableHolder(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, boolean selected, int position) {
        if (taskItemEntity == null) {
            return;
        }
        ImageView task_item_checkbox = holder.obtainView(R.id.task_item_checkbox);
        TextView task_name_tv = holder.obtainView(R.id.task_name_tv);
        TextView task_desc_tv = holder.obtainView(R.id.task_desc_tv);
        ImageView task_item_timming_iv = holder.obtainView(R.id.task_item_timming_iv);
        task_name_tv.setText(taskItemEntity.name);
        //已完成-->updateTime
        if (taskItemEntity.state) {
            task_item_checkbox.setImageResource(R.mipmap.checkbox_selected);
            String adjustTimeFormat = getAdjustTimeFormat(taskItemEntity.updateTime);
            if (!TextUtils.isEmpty(adjustTimeFormat)) {
                task_desc_tv.setText(String.format("%s  %s",
                        adjustTimeFormat,
                        getProjectTaskGroupInfo(taskItemEntity)));
            } else {
                task_desc_tv.setText(getProjectTaskGroupInfo(taskItemEntity));
            }
        } else {
            task_item_checkbox.setImageResource(R.mipmap.checkbox_unselect);
            String adjustTimeFormat = getAdjustTimeFormat(taskItemEntity.dueTime);
            if (!TextUtils.isEmpty(adjustTimeFormat)) {
                task_desc_tv.setText(String.format("%s  %s",
                        adjustTimeFormat,
                        getProjectTaskGroupInfo(taskItemEntity)));
            } else {
                task_desc_tv.setText(getProjectTaskGroupInfo(taskItemEntity));
            }
        }
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        if (timer != null) {
            if (StringUtils.equalsIgnoreCase(taskItemEntity.id, timer.taskPkId, false)) {
                taskItemEntity.isTiming = true;
            } else {
                taskItemEntity.isTiming = false;
            }
        }
        task_item_timming_iv.setImageResource(taskItemEntity.isTiming ? R.drawable.orange_side_dot_bg : R.mipmap.icon_start_20);

        holder.bindChildClick(task_item_checkbox);
        holder.bindChildClick(task_item_timming_iv);
    }

    /**
     * 获取显示时间
     * 23:59:59 不显示
     *
     * @param millis
     * @return
     */
    private String getAdjustTimeFormat(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (hourOfDay == TIME_HOUR_23 && minute == TIME_MIN_59 && second == TIME_MIN_59) {
            return "";
        } else {
            return DateUtils.getHHmm(millis);
        }
    }

    /**
     * 获取 项目 任务组等信息的组合
     *
     * @param taskItemEntity
     * @return
     */
    private String getProjectTaskGroupInfo(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
            if (taskItemEntity.matter != null) {
                if (taskItemEntity.parentFlow != null) {
                    if (!TextUtils.isEmpty(taskItemEntity.parentFlow.name)) {
                        return String.format("%s - %s", taskItemEntity.matter.name, taskItemEntity.parentFlow.name);
                    } else {
                        return taskItemEntity.matter.name;
                    }
                } else {
                    if (!TextUtils.isEmpty(taskItemEntity.parentName)) {
                        return String.format("%s - %s", taskItemEntity.matter.name, taskItemEntity.parentName);
                    } else {
                        return taskItemEntity.matter.name;
                    }
                }
            }
        }
        return "未指定所属项目";
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, final View view, int position) {
        final TaskEntity.TaskItemEntity itemEntity = getItem(adapter.getRealPos(position));
        if (itemEntity == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.task_item_checkbox:
                if (itemEntity.attendeeUsers != null && itemEntity.attendeeUsers.size() > 1) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("提示")
                            .setMessage(itemEntity.state ? "该任务由多人负责,确定取消完成?" : "该任务由多人负责,确定完成?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateTask(view.getContext(), itemEntity, !itemEntity.state);
                                }
                            }).setNegativeButton("取消", null)
                            .show();
                } else {
                    updateTask(view.getContext(), itemEntity, !itemEntity.state);
                }
                break;
            case R.id.task_item_timming_iv:
                if (itemEntity.isTiming) {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                            itemEntity.isTiming = false;
                            TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                            TimerDetailActivity.launch(view.getContext(), timer);
                        }

                        @Override
                        public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                            super.onFailure(call, t);
                            itemEntity.isTiming = true;
                        }
                    });
                } else {
                    showLoadingDialog(view.getContext(), null);
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    TimerManager.getInstance().addTimer(getTimer(itemEntity), new Callback<TimeEntity.ItemEntity>() {
                        @Override
                        public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                            dismissLoadingDialog();
                            if (response.body() != null) {
                                itemEntity.isTiming = true;
                                TimerTimingActivity.launch(view.getContext(), response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                            dismissLoadingDialog();
                            itemEntity.isTiming = false;
                        }
                    });
                }
                break;
            default:

                break;
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
            itemEntity.createUserId = LoginInfoUtils.getLoginUserId();
            if (LoginInfoUtils.getLoginUserInfo() != null) {
                itemEntity.username = LoginInfoUtils.getLoginUserInfo().getName();
            }
            itemEntity.startTime = DateUtils.millis();
            if (taskItemEntity.matter != null) {
                itemEntity.matterPkId = taskItemEntity.matter.id;
                itemEntity.matterName = taskItemEntity.matter.name;
            }
        }
        return itemEntity;
    }

    public interface OnShowFragmenDialogListener {
        /**
         * 显示选择负责人dialog
         *
         * @param projectId
         * @param taskItemEntity
         */
        void showUserSelectDialog(String projectId, TaskEntity.TaskItemEntity taskItemEntity);

        /**
         * 显示选择到期日dialog
         *
         * @param taskItemEntity
         */
        void showDateSelectDialog(TaskEntity.TaskItemEntity taskItemEntity);

        /**
         * 显示选择所属项目dialog
         *
         * @param taskItemEntity
         */
        void showProjectSelectDialog(TaskEntity.TaskItemEntity taskItemEntity);
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     * @param state
     * @param checkbox
     */
    private void updateTask(final TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckBox checkbox) {
        showLoadingDialog(checkbox.getContext(), null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                checkbox.setChecked(state);
                View view = (View) checkbox.getParent();
                if (view != null) {
                    TextView timeView = (TextView) view.findViewById(R.id.task_time_tv);
                    if (state) {
                        timeView.setTextColor(0xFF8c8f92);
                        timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.task_time_icon, 0, 0, 0);
                        timeView.setVisibility(View.VISIBLE);
                        timeView.setText(DateUtils.get23Hour59MinFormat(DateUtils.millis()));
                    } else {
                        if (itemEntity.dueTime > 0) {
                            timeView.setVisibility(View.VISIBLE);
                            timeView.setText(DateUtils.get23Hour59MinFormat(itemEntity.dueTime));
                            if (itemEntity.dueTime < DateUtils.millis()) {
                                timeView.setTextColor(0xFF000000);
                                timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_fail, 0, 0, 0);
                            } else {
                                timeView.setTextColor(0xFF8c8f92);
                                timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.task_time_icon, 0, 0, 0);
                            }
                        } else {
                            timeView.setVisibility(View.GONE);
                        }
                    }
                }
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
     * 修改任务状态
     *
     * @param itemEntity
     * @param state
     */
    private void updateTask(Context context,
                            final TaskEntity.TaskItemEntity itemEntity,
                            final boolean state) {
        showLoadingDialog(context, null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state)))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        itemEntity.state = state;
                        updateItem(itemEntity);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("state", state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        TaskEntity.TaskItemEntity taskItemEntity = getItem(position);
        if (taskItemEntity == null) {
            return;
        }
        TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
    }

    /**
     * 删除提示对话框
     */
    private static final int SHOW_DELETE_DIALOG = 0;
    /**
     * 完成任务提示对话框
     */
    private static final int SHOW_FINISH_DIALOG = 1;

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

        TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(adapter.getRealPos(position));
        ItemsEntity timeEntity = new ItemsEntity("开始计时", R.mipmap.time_start_orange_task);
        if (taskItemEntity.isTiming) {
            timeEntity.itemIconRes = R.mipmap.time_stop_orange_task;
            timeEntity.itemTitle = "停止计时";
        } else {
            timeEntity.itemIconRes = R.mipmap.time_start_orange_task;
            timeEntity.itemTitle = "开始计时";
        }
        showLongMenu(view.getContext(), Arrays.asList(
                new ItemsEntity("项目/任务组", R.mipmap.project_orange),
                new ItemsEntity("分配给", R.mipmap.assign_orange),
                new ItemsEntity("到期日", R.mipmap.date_orange),
                timeEntity,
                new ItemsEntity("查看详情", R.mipmap.info_orange),
                new ItemsEntity("删除", R.mipmap.trash_orange)), taskItemEntity);
        return true;
    }

    private void showLongMenu(Context context, List<ItemsEntity> itemsEntities, TaskEntity.TaskItemEntity taskItemEntity) {
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
        public void onItemClick(final BaseRecyclerAdapter adapter, ViewHolder holder, final View view, int position) {
            if (centerMenuDialog != null) {
                centerMenuDialog.dismiss();
            }
            if (adapter instanceof CenterMenuDialog.MenuAdapter) {
                final ItemsEntity entity = (ItemsEntity) adapter.getItem(position);
                if (taskItemEntity != null) {
                    switch (entity.getItemIconRes()) {
                        //分配给
                        case R.mipmap.assign_orange:
                            if (onShowFragmenDialogListener != null) {
                                if (taskItemEntity.matter != null) {
                                    onShowFragmenDialogListener.showUserSelectDialog(taskItemEntity.matter.id, taskItemEntity);
                                } else {
                                    showToast("请先选择项目");
                                }
                            }
                            break;
                        //到期日
                        case R.mipmap.date_orange:
                            if (onShowFragmenDialogListener != null) {
                                onShowFragmenDialogListener.showDateSelectDialog(taskItemEntity);
                            }
                            break;
                        //查看详情
                        case R.mipmap.info_orange:
                            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
                            break;
                        //项目/任务组
                        case R.mipmap.project_orange:
                            if (onShowFragmenDialogListener != null) {
                                onShowFragmenDialogListener.showProjectSelectDialog(taskItemEntity);
                            }
                            break;
                        //开始计时
                        case R.mipmap.time_start_orange_task:
                            if (!taskItemEntity.isTiming) {
                                showLoadingDialog(view.getContext(), null);
                                TimerManager.getInstance().addTimer(getTimer(taskItemEntity), new Callback<TimeEntity.ItemEntity>() {
                                    @Override
                                    public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                        dismissLoadingDialog();
                                        entity.itemIconRes = R.mipmap.time_stop_orange_task;
                                        entity.itemTitle = "停止计时";
                                        if (response.body() != null) {
                                            ((CenterMenuDialog.MenuAdapter) adapter).updateItem(entity);
                                            TimerTimingActivity.launch(view.getContext(), response.body());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                                        dismissLoadingDialog();
                                    }
                                });

                            }
                            break;
                        //停止计时
                        case R.mipmap.time_stop_orange_task:
                            if (taskItemEntity.isTiming) {
                                MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                                TimerManager.getInstance().stopTimer();
                                entity.itemIconRes = R.mipmap.time_start_orange_task;
                                entity.itemTitle = "开始计时";
                                ((CenterMenuDialog.MenuAdapter) adapter).updateItem(entity);
                            }
                            break;
                        //删除
                        case R.mipmap.trash_orange:
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
                        default:

                            break;
                    }
                }
            }
        }
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
                    //确定
                    case Dialog.BUTTON_POSITIVE:
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
                    //取消
                    case Dialog.BUTTON_NEGATIVE:
                        if (type == SHOW_FINISH_DIALOG) {
                            if (checkbox != null) {
                                checkbox.setChecked(itemEntity.state);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    /**
     * 删除任务
     */
    private void deleteTask(Context context, TaskEntity.TaskItemEntity itemEntity) {
        showLoadingDialog(context, null);
        MobclickAgent.onEvent(context, UMMobClickAgent.delete_task_click_id);
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
}
