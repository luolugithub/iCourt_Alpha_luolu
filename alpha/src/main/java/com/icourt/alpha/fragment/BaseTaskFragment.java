package com.icourt.alpha.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskAllotSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Description  任务列表碎片基类
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/31
 * version 2.0.0
 * 注意：所有继承这个类的子类都不需要再注册/取消注册广播了。
 */

public abstract class BaseTaskFragment extends BaseFragment implements OnFragmentCallBackListener, ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener {

    protected boolean isEditTask = false;//编辑任务权限
    protected boolean isDeleteTask = false;//删除任务权限
    protected boolean isAddTime = false;//添加计时权限

    public static final int CHANGE_STATUS = 1;//修改任务完成状态
    public static final int CHANGE_PROJECT = 2;//修改任务所属项目/任务组
    public static final int CHANGE_ALLOT = 3;//将任务分配给其他负责人
    public static final int CHANGE_DUETIME = 4;//修改任务到期时间和提醒时间

    /**
     * 以下为对任务的操作状态
     */
    @IntDef({CHANGE_STATUS, CHANGE_PROJECT, CHANGE_ALLOT, CHANGE_DUETIME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChangeType {
    }


    protected static final int SHOW_DELETE_DIALOG = 0;//删除提示对话框
    protected static final int SHOW_FINISH_DIALOG = 1;//完成任务提示对话框

    /**
     * 以下为弹出对话框的分类
     */
    @IntDef({SHOW_DELETE_DIALOG, SHOW_FINISH_DIALOG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType {
    }


    private TaskEntity.TaskItemEntity updateTaskItemEntity;//当前修改任务到期事件、负责人、所属项目的任务

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }


    /**
     * 计时事件的广播接收
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_ADD:
                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                if (timer != null && !TextUtils.isEmpty(timer.taskPkId))
                    taskTimingUpdateEvent(timer.taskPkId);
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:

                break;
            case TimingEvent.TIMING_STOP:
                taskTimingUpdateEvent(null);
                break;
        }
    }

    /**
     * 开始计时
     *
     * @param itemEntity
     */
    protected void startTiming(final TaskEntity.TaskItemEntity itemEntity) {
        TimerManager.getInstance().addTimer(getTimer(itemEntity), new Callback<TimeEntity.ItemEntity>() {
            @Override
            public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                dismissLoadingDialog();
                itemEntity.isTiming = true;
                startTimingBack(itemEntity, response);
            }

            @Override
            public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 结束计时
     *
     * @param itemEntity
     */
    protected void stopTiming(final TaskEntity.TaskItemEntity itemEntity) {
        TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                dismissLoadingDialog();
                itemEntity.isTiming = false;
                stopTimingBack(itemEntity);
            }

            @Override
            public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 修改任务状态(完成、未完成两种状态)
     *
     * @param itemEntity
     * @param state      true：完成状态；false：未完成状态。
     */
    protected void updateTaskState(final TaskEntity.TaskItemEntity itemEntity, final boolean state) {
        showLoadingDialog(null);
        itemEntity.state = state;
        getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskStateJson(itemEntity))).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                dismissLoadingDialog();
                if (response.body().result != null)
                    taskUpdateBack(CHANGE_STATUS, response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                //因为是引用，要将数据置回相反的状态。
                itemEntity.state = !state;
            }
        });
    }


    /**
     * 修改任务所属项目／任务组／提醒时间
     *
     * @param itemEntity
     */
    protected void updateTaskProjectOrGroup(@ChangeType final int type, final TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity, final TaskReminderEntity taskReminderEntity) {
        showLoadingDialog(null);
        getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskProjectOrGroupJson(itemEntity, projectEntity, taskGroupEntity))).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                dismissLoadingDialog();
                if (response.body().result != null) {
                    if (taskReminderEntity != null) {
                        addReminders(response.body().result, taskReminderEntity);
                    }
                    taskUpdateBack(type, response.body().result);
                }

            }

            @Override
            public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }


    /**
     * 添加任务提醒
     *
     * @param taskItemEntity
     * @param taskReminderEntity
     */
    private void addReminders(TaskEntity.TaskItemEntity taskItemEntity, final TaskReminderEntity taskReminderEntity) {
        if (taskReminderEntity == null) return;
        if (taskItemEntity == null) return;
        String json = getReminderJson(taskReminderEntity);
        if (TextUtils.isEmpty(json)) return;
        getApi().taskReminderAdd(taskItemEntity.id, RequestUtils.createJsonBody(json)).enqueue(new SimpleCallBack<TaskReminderEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskReminderEntity>> call, Response<ResEntity<TaskReminderEntity>> response) {

            }

            @Override
            public void onFailure(Call<ResEntity<TaskReminderEntity>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }


    /**
     * 删除任务
     *
     * @param itemEntity
     */
    protected void deleteTask(final TaskEntity.TaskItemEntity itemEntity) {
        showLoadingDialog(null);
        MobclickAgent.onEvent(context, UMMobClickAgent.delete_task_click_id);
        getApi().taskDelete(itemEntity.id).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                taskDeleteBack(itemEntity);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 展示选择负责人对话框
     */
    protected void showTaskAllotSelectDialogFragment(String projectId, List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUsers) {
        String tag = TaskAllotSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }

        TaskAllotSelectDialogFragment.newInstance(projectId, attendeeUsers)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择项目对话框
     */
    protected void showProjectSelectDialogFragment() {
        String tag = ProjectSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }

        ProjectSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择到期时间对话框
     */
    protected void showDateSelectDialogFragment(long dueTime, String taskId) {
        String tag = DateSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        Calendar calendar = Calendar.getInstance();
        if (dueTime <= 0) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        } else {
            calendar.setTimeInMillis(dueTime);
        }
        DateSelectDialogFragment.newInstance(calendar, null, taskId)
                .show(mFragTransaction, tag);
    }

    /**
     * 显示长按弹出的菜单
     *
     * @param taskItemEntity
     */
    protected void showLongMenu(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity.state) {//已完成的任务不能进行长按操作
            return;
        }
        ItemsEntity timeEntity = new ItemsEntity(getString(R.string.task_start_timing), R.mipmap.time_start_orange_task);
        if (taskItemEntity.isTiming) {
            timeEntity.itemIconRes = R.mipmap.time_stop_orange_task;
            timeEntity.itemTitle = getString(R.string.task_stop_timing);
        } else {
            timeEntity.itemIconRes = R.mipmap.time_start_orange_task;
            timeEntity.itemTitle = getString(R.string.task_start_timing);
        }
        if (isEditTask && isDeleteTask) {
            showLongMeau(getActivity(), Arrays.asList(
                    new ItemsEntity(getString(R.string.task_project_or_taskset), R.mipmap.project_orange),
                    new ItemsEntity(getString(R.string.task_allot_to), R.mipmap.assign_orange),
                    new ItemsEntity(getString(R.string.task_due_date), R.mipmap.date_orange),
                    timeEntity,
                    new ItemsEntity(getString(R.string.task_check_detail), R.mipmap.info_orange),
                    new ItemsEntity(getString(R.string.task_delete), R.mipmap.trash_orange)), taskItemEntity);
        } else if (isDeleteTask && !isEditTask) {
            showLongMeau(getActivity(), Arrays.asList(
                    new ItemsEntity(getString(R.string.task_check_detail), R.mipmap.info_orange),
                    timeEntity,
                    new ItemsEntity(getString(R.string.task_delete), R.mipmap.trash_orange)), taskItemEntity);
        } else if (!isDeleteTask && isEditTask) {
            showLongMeau(getActivity(), Arrays.asList(
                    new ItemsEntity(getString(R.string.task_project_or_taskset), R.mipmap.project_orange),
                    new ItemsEntity(getString(R.string.task_allot_to), R.mipmap.assign_orange),
                    new ItemsEntity(getString(R.string.task_due_date), R.mipmap.date_orange),
                    timeEntity,
                    new ItemsEntity(getString(R.string.task_check_detail), R.mipmap.info_orange)
                    ),
                    taskItemEntity);
        }
    }

    private void showLongMeau(Context context, List<ItemsEntity> itemsEntities, TaskEntity.TaskItemEntity taskItemEntity) {
        CenterMenuDialog mCenterMenuDialog = new CenterMenuDialog(context, null, itemsEntities);
        mCenterMenuDialog.show();
        mCenterMenuDialog.setOnItemClickListener(new CustOnItemClickListener(mCenterMenuDialog, taskItemEntity));
    }

    private class CustOnItemClickListener implements BaseRecyclerAdapter.OnItemClickListener {
        CenterMenuDialog centerMenuDialog;
        TaskEntity.TaskItemEntity taskItemEntity;

        CustOnItemClickListener(CenterMenuDialog centerMenuDialog, TaskEntity.TaskItemEntity taskItemEntity) {
            this.centerMenuDialog = centerMenuDialog;
            this.taskItemEntity = taskItemEntity;
        }

        @Override
        public void onItemClick(final BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, final View view, int position) {
            if (centerMenuDialog != null)
                centerMenuDialog.dismiss();
            if (adapter instanceof CenterMenuDialog.MenuAdapter) {
                final ItemsEntity entity = (ItemsEntity) adapter.getItem(position);
                final CenterMenuDialog.MenuAdapter menuAdapter = (CenterMenuDialog.MenuAdapter) adapter;
                if (taskItemEntity != null) {
                    switch (entity.getItemIconRes()) {
                        case R.mipmap.assign_orange://分配给
                            if (taskItemEntity.matter != null) {
                                updateTaskItemEntity = taskItemEntity;
                                showTaskAllotSelectDialogFragment(taskItemEntity.matter.id, taskItemEntity.attendeeUsers);
                            } else {
                                showToast(R.string.task_please_check_project);
                            }
                            break;
                        case R.mipmap.date_orange://到期日
                            updateTaskItemEntity = taskItemEntity;
                            showDateSelectDialogFragment(taskItemEntity.dueTime, taskItemEntity.id);
                            break;
                        case R.mipmap.info_orange://查看详情
                            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
                            break;
                        case R.mipmap.project_orange://项目/任务组
                            updateTaskItemEntity = taskItemEntity;
                            showProjectSelectDialogFragment();
                            break;
                        case R.mipmap.time_start_orange_task://开始计时
                            if (!taskItemEntity.isTiming) {
                                MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                                startTiming(taskItemEntity);
                            }
                            break;
                        case R.mipmap.time_stop_orange_task://停止计时
                            if (taskItemEntity.isTiming) {
                                stopTiming(taskItemEntity);
                            }
                            break;
                        case R.mipmap.trash_orange://删除
                            if (taskItemEntity.attendeeUsers != null) {
                                if (taskItemEntity.attendeeUsers.size() > 1) {
                                    showFinishDialog(view.getContext(), getString(R.string.task_is_confirm_delete_task), taskItemEntity, SHOW_DELETE_DIALOG);
                                } else {
                                    showFinishDialog(view.getContext(), getString(R.string.task_is_confirm_delete), taskItemEntity, SHOW_DELETE_DIALOG);
                                }
                            } else {
                                showFinishDialog(view.getContext(), getString(R.string.task_is_confirm_delete), taskItemEntity, SHOW_DELETE_DIALOG);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * 显示多人任务提醒/删除任务提醒
     *
     * @param context
     * @param message
     * @param itemEntity
     */
    protected void showFinishDialog(final Context context, String message, final TaskEntity.TaskItemEntity itemEntity, @DialogType final int type) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        if (type == SHOW_DELETE_DIALOG) {
                            deleteTask(itemEntity);
                        } else if (type == SHOW_FINISH_DIALOG) {
                            if (itemEntity.state) {
                                updateTaskState(itemEntity, false);
                            } else {
                                updateTaskState(itemEntity, true);
                            }
                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE://取消
                        if (type == SHOW_FINISH_DIALOG) {
                        }
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle(R.string.task_remind); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton(R.string.task_confirm, dialogOnclicListener);
        builder.setNegativeButton(R.string.task_cancel, dialogOnclicListener);
        builder.create().show();
    }

    /**
     * 从选择负责人／选择日期的Fragment返回的回调
     *
     * @param fragment
     * @param type
     * @param params
     */
    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params != null) {
            if (fragment instanceof TaskAllotSelectDialogFragment) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (updateTaskItemEntity.attendeeUsers != null) {
                    updateTaskItemEntity.attendeeUsers.clear();
                    updateTaskItemEntity.attendeeUsers.addAll(attusers);
                    updateTaskProjectOrGroup(CHANGE_ALLOT, updateTaskItemEntity, null, null, null);
                }
            } else if (fragment instanceof DateSelectDialogFragment) {
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                updateTaskItemEntity.dueTime = millis;
                TaskReminderEntity taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");
                updateTaskProjectOrGroup(CHANGE_DUETIME, updateTaskItemEntity, null, null, taskReminderEntity);

            }
        }
    }

    /**
     * 从选择项目的Fragment的回调，任务组id和负责人列表都需要清空
     *
     * @param projectEntity
     * @param taskGroupEntity
     */
    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (projectEntity != null) {
            if (updateTaskItemEntity != null) {
                if (updateTaskItemEntity.attendeeUsers != null) {
                    updateTaskItemEntity.attendeeUsers.clear();
                }
            }
        }
        if (taskGroupEntity == null) {
            taskGroupEntity = new TaskGroupEntity();
            taskGroupEntity.id = "";
        }
        updateTaskProjectOrGroup(CHANGE_PROJECT, updateTaskItemEntity, projectEntity, taskGroupEntity, null);
    }


    /**
     * 获取添加计时实体
     *
     * @return
     */
    protected TimeEntity.ItemEntity getTimer(TaskEntity.TaskItemEntity taskItemEntity) {
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
        }
        return itemEntity;
    }


    /**
     * 获取修改任务状态json（修改任务完成／未完成状态调用这个方法）
     *
     * @param itemEntity
     * @return
     */
    private String getTaskStateJson(TaskEntity.TaskItemEntity itemEntity) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", itemEntity.id);
        jsonObject.addProperty("name", itemEntity.name);
        jsonObject.addProperty("state", itemEntity.state);
        jsonObject.addProperty("valid", true);
        jsonObject.addProperty("updateTime", DateUtils.millis());
        return jsonObject.toString();
    }

    /**
     * 获取任务json（修改任务所属项目／任务组／提醒时间调用这个方法）
     *
     * @param itemEntity
     * @return
     */
    private String getTaskProjectOrGroupJson(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (itemEntity == null) return null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", itemEntity.id);
        jsonObject.addProperty("state", itemEntity.state);
        jsonObject.addProperty("valid", true);
        jsonObject.addProperty("name", itemEntity.name);
        jsonObject.addProperty("parentId", itemEntity.parentId);
        jsonObject.addProperty("dueTime", itemEntity.dueTime);
        jsonObject.addProperty("updateTime", DateUtils.millis());
        if (projectEntity != null) {
            jsonObject.addProperty("matterId", projectEntity.pkId);
        }
        if (taskGroupEntity != null) {
            jsonObject.addProperty("parentId", taskGroupEntity.id);
        }
        JsonArray jsonarr = new JsonArray();
        if (itemEntity.attendeeUsers != null) {
            for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                jsonarr.add(attendeeUser.userId);
            }
            jsonObject.add("attendees", jsonarr);
        }
        return jsonObject.toString();
    }

    /**
     * 获取提醒json
     *
     * @param taskReminderEntity
     * @return
     */
    private String getReminderJson(TaskReminderEntity taskReminderEntity) {
        try {
            if (taskReminderEntity == null) return null;
            return JsonUtils.getGson().toJson(taskReminderEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 任务开始计时成功的回调
     *
     * @param requestEntity 调用该方法传递的参数
     */
    protected abstract void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response);

    /**
     * 任务结束计时成功的回调
     *
     * @param requestEntity 调用该方法传递的参数
     */
    protected abstract void stopTimingBack(TaskEntity.TaskItemEntity requestEntity);

    /**
     * 删除任务回调
     *
     * @param itemEntity
     */
    protected abstract void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity);

    /**
     * 更新任务成功的回调:修改状态，修改所属项目／任务组，修改负责人，修改到期时间
     *
     * @param actionType:CHANGE_STATUS,CHANGE_PROJECT,CHANGE_ALLOT,CHANGE_DUETIME
     * @param itemEntity
     */
    protected abstract void taskUpdateBack(@ChangeType int actionType, @NonNull TaskEntity.TaskItemEntity itemEntity);

    /**
     * 更新计时任务状态回调
     *
     * @param taskId 如果为空，则是接收到停止计时的通知；如果不为空，则是接收到开始计时的通知。
     */
    protected abstract void taskTimingUpdateEvent(String taskId);


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

}
