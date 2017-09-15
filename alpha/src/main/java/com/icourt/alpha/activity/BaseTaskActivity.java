package com.icourt.alpha.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.base.BaseActivity;
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
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Description  任务基类
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/31
 * version 2.0.0
 */

public abstract class BaseTaskActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
    }

    /**
     * 计时事件
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
                    taskTimerUpdateBack(timer.taskPkId);
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:

                break;
            case TimingEvent.TIMING_STOP:
                taskTimerUpdateBack(null);
                break;
        }
    }

    /**
     * 修改任务状态
     *
     * @param itemEntity
     */
    protected void updateTaskState(final TaskEntity.TaskItemEntity itemEntity) {
        showLoadingDialog(null);
        getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskStateJson(itemEntity))).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                dismissLoadingDialog();
                if (response.body().result != null)
                    taskUpdateBack(response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }


    /**
     * 修改任务所属项目／任务组／提醒
     *
     * @param itemEntity
     */
    protected void updateTaskProjectOrGroup(final TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity, final TaskReminderEntity taskReminderEntity) {
        showLoadingDialog(null);
        getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskProjectOrGroupJson(itemEntity, projectEntity, taskGroupEntity))).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                dismissLoadingDialog();
                if (response.body().result != null) {
                    if (taskReminderEntity != null) {
                        addReminders(response.body().result, taskReminderEntity);
                    }
                    taskUpdateBack(response.body().result);
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
    protected void addReminders(TaskEntity.TaskItemEntity taskItemEntity, final TaskReminderEntity taskReminderEntity) {
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
     * 获取修改任务状态json
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
     * 获取任务json
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
     * 展示选择负责人对话框
     */
    protected void showTaskAllotSelectDialogFragment(String projectId, List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUsers) {
        String tag = TaskAllotSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
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
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
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
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
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
     * 删除任务
     *
     * @param itemEntity
     */
    protected abstract void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity);

    /**
     * 更新任务
     *
     * @param itemEntity
     */
    protected abstract void taskUpdateBack(@NonNull TaskEntity.TaskItemEntity itemEntity);

    /**
     * 更加计时任务状态
     *
     * @param taskId
     */
    protected abstract void taskTimerUpdateBack(String taskId);

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
