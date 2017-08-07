package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskSimpleAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskAllotSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  每天的任务 fragment
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/10
 * version 2.0.0
 */


public class TaskEverydayFragment extends BaseFragment
        implements TaskSimpleAdapter.OnShowFragmenDialogListener,
        OnFragmentCallBackListener,
        ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener {

    private static final String KEY_TASKS = "key_tasks";
    private static final String KEY_DAY = "key_day";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    TaskSimpleAdapter taskSimpleAdapter;
    ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    /**
     * 排序规则
     * 1:全天任务的优先级应该大于非全天任务
     * 2:同一时间到期的任务，应该按修改时间刷新排序
     */
    Comparator<TaskEntity.TaskItemEntity> taskItemEntityComparator = new Comparator<TaskEntity.TaskItemEntity>() {
        @Override
        public int compare(TaskEntity.TaskItemEntity o1, TaskEntity.TaskItemEntity o2) {
            if (o1 != null && o2 != null) {
                Calendar calendarTask1 = Calendar.getInstance();
                calendarTask1.setTimeInMillis(o1.dueTime);
                int taskHour1 = calendarTask1.get(Calendar.HOUR_OF_DAY);
                int taskMinute1 = calendarTask1.get(Calendar.MINUTE);
                int taskSecond1 = calendarTask1.get(Calendar.SECOND);
                boolean isAllDayTask1 = (taskHour1 == 23 && taskMinute1 == 59 && taskSecond1 == 59);


                Calendar calendarTask2 = Calendar.getInstance();
                calendarTask2.setTimeInMillis(o2.dueTime);
                int taskHour2 = calendarTask2.get(Calendar.HOUR_OF_DAY);
                int taskMinute2 = calendarTask2.get(Calendar.MINUTE);
                int taskSecond2 = calendarTask2.get(Calendar.SECOND);
                boolean isAllDayTask2 = (taskHour2 == 23 && taskMinute2 == 59 && taskSecond2 == 59);

                if (isAllDayTask1 && isAllDayTask2) {
                    return 0;
                } else if (isAllDayTask1) {
                    return -1;
                } else if (isAllDayTask2) {
                    return 1;
                } else {
                    //同一时间到期的任务，应该按修改时间刷新排序
                    long distanceCreateTime = calendarTask1.getTimeInMillis() - calendarTask1.getTimeInMillis();
                    if (distanceCreateTime == 0) {
                        long distanceUpdateTime = o1.updateTime - o2.updateTime;
                        if (distanceUpdateTime > 0) {
                            return -1;
                        } else if (distanceUpdateTime < 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else {
                        return distanceCreateTime > 0 ? -1 : 1;
                    }
                }
            }
            return 0;
        }
    };

    public static TaskEverydayFragment newInstance(long dayTime, ArrayList<TaskEntity.TaskItemEntity> data) {
        TaskEverydayFragment fragment = new TaskEverydayFragment();
        Bundle args = new Bundle();
        args.putLong(KEY_DAY, dayTime);
        args.putSerializable(KEY_TASKS, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_every_day, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        taskItemEntityList = (ArrayList<TaskEntity.TaskItemEntity>) getArguments().getSerializable(KEY_TASKS);
        if (taskItemEntityList == null) taskItemEntityList = new ArrayList<>();
        Collections.sort(taskItemEntityList, taskItemEntityComparator);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(taskSimpleAdapter = new TaskSimpleAdapter());
        taskSimpleAdapter.setOnShowFragmenDialogListener(this);
        taskSimpleAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (contentEmptyText != null) {
                    contentEmptyText.setVisibility(taskSimpleAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
        EventBus.getDefault().register(this);
        getData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<TaskEntity.TaskItemEntity> datas = new ArrayList<>(taskSimpleAdapter.getData());
        if (datas != null) {
            boolean updated = false;
            for (int i = datas.size() - 1; i >= 0; i--) {
                TaskEntity.TaskItemEntity taskItemEntity = datas.get(i);
                if (taskItemEntity != null && taskItemEntity.state) {
                    datas.remove(i);
                    updated = true;
                }
            }
            if (updated) {
                taskSimpleAdapter.bindData(true, datas);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        if (event.action == TaskActionEvent.TASK_UPDATE_ITEM) {
            if (event.entity == null) return;

            int indexOf = taskItemEntityList.indexOf(event.entity);
            if (indexOf >= 0) {
                if (isSameTodayTask(event.entity.dueTime)) {
                    taskItemEntityList.set(indexOf, event.entity);
                    taskSimpleAdapter.updateItem(event.entity);
                } else {
                    taskItemEntityList.remove(indexOf);
                    taskSimpleAdapter.bindData(true, taskItemEntityList);
                }
            } else {
                if (isSameTodayTask(event.entity.dueTime)) {
                    taskItemEntityList.add(event.entity);
                    Collections.sort(taskItemEntityList, taskItemEntityComparator);
                    taskSimpleAdapter.bindData(true, taskItemEntityList);
                }
            }
        }
    }


    /**
     * 是否是同一天任务
     *
     * @param time
     * @return
     */
    private boolean isSameTodayTask(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getArguments().getLong(KEY_DAY));

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(time);

        return calendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        taskSimpleAdapter.bindData(isRefresh, taskItemEntityList);
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament != this) return;
        if (bundle != null) {
            ArrayList<TaskEntity.TaskItemEntity> taskItemEntities = (ArrayList<TaskEntity.TaskItemEntity>) bundle.getSerializable(KEY_FRAGMENT_RESULT);
            if (taskItemEntities != null
                    && taskItemEntityList != null
                    && taskItemEntities.hashCode() != taskItemEntityList.hashCode()) {
                taskItemEntityList = taskItemEntities;
                Collections.sort(taskItemEntityList, taskItemEntityComparator);
                taskSimpleAdapter.bindData(true, taskItemEntityList);
            }
        }
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
                List<TaskEntity.TaskItemEntity> data = taskSimpleAdapter.getData();
                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                if (timer != null) {
                    for (TaskEntity.TaskItemEntity taskItemEntity : data) {
                        taskItemEntity.isTiming = false;
                        if (TextUtils.equals(taskItemEntity.id, timer.taskPkId)) {
                            taskItemEntity.isTiming = true;
                        }
                    }
                    taskSimpleAdapter.notifyDataSetChanged();
                }
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                break;
            case TimingEvent.TIMING_STOP:
                List<TaskEntity.TaskItemEntity> data1 = taskSimpleAdapter.getData();
                for (TaskEntity.TaskItemEntity taskItemEntity : data1) {
                    taskItemEntity.isTiming = false;
                }
                taskSimpleAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 展示选择到期时间对话框
     */
    private void showDateSelectDialogFragment(long dueTime, String taskId) {
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
        } else {
            calendar.setTimeInMillis(dueTime);
        }
        DateSelectDialogFragment.newInstance(calendar, null, taskId)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择负责人对话框
     */
    public void showTaskAllotSelectDialogFragment(String projectId, List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUsers) {
        String tag = "TaskAllotSelectDialogFragment";
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
    public void showProjectSelectDialogFragment() {
        String tag = "ProjectSelectDialogFragment";
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }

        ProjectSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    TaskEntity.TaskItemEntity updateTaskItemEntity;

    @Override
    public void showUserSelectDialog(String projectId, TaskEntity.TaskItemEntity taskItemEntity) {
        updateTaskItemEntity = taskItemEntity;
        showTaskAllotSelectDialogFragment(projectId, taskItemEntity.attendeeUsers);
    }

    @Override
    public void showDateSelectDialog(TaskEntity.TaskItemEntity taskItemEntity) {
        updateTaskItemEntity = taskItemEntity;
        if (taskItemEntity != null)
            showDateSelectDialogFragment(taskItemEntity.dueTime, taskItemEntity.id);
    }

    @Override
    public void showProjectSelectDialog(TaskEntity.TaskItemEntity taskItemEntity) {
        updateTaskItemEntity = taskItemEntity;
        showProjectSelectDialogFragment();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params != null) {
            if (fragment instanceof TaskAllotSelectDialogFragment) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (updateTaskItemEntity.attendeeUsers != null) {
                    updateTaskItemEntity.attendeeUsers.clear();
                    updateTaskItemEntity.attendeeUsers.addAll(attusers);
                    updateTask(updateTaskItemEntity, null, null);
                }
            } else if (fragment instanceof DateSelectDialogFragment) {
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                updateTaskItemEntity.dueTime = millis;
                updateTask(updateTaskItemEntity, null, null);

                TaskReminderEntity taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");
                addReminders(updateTaskItemEntity, taskReminderEntity);
            }
        }
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
            Gson gson = new Gson();
            return gson.toJson(taskReminderEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
     * 修改任务
     *
     * @param itemEntity
     */
    private void updateTask(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        showLoadingDialog(null);
        getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskJson(itemEntity, projectEntity, taskGroupEntity)))
                .enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null)
                            EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_UPDATE_ITEM, response.body().result));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @return
     */
    private String getTaskJson(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (itemEntity == null) return null;
        try {
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
            }
            jsonObject.add("attendees", jsonarr);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {

        updateTask2(updateTaskItemEntity, projectEntity, taskGroupEntity);
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     */
    private void updateTask2(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        showLoadingDialog(null);
        getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskJson2(itemEntity, projectEntity, taskGroupEntity)))
                .enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null)
                            EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_UPDATE_ITEM, response.body().result));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @return
     */
    private String getTaskJson2(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (itemEntity == null) return null;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("parentId", itemEntity.parentId);
            jsonObject.addProperty("dueTime", itemEntity.dueTime);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            JsonArray jsonarr = new JsonArray();
            if (projectEntity != null) {
                jsonObject.addProperty("matterId", projectEntity.pkId);
                //jsonarr.add(getLoginUserId());
            } else {
                if (itemEntity.attendeeUsers != null) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        jsonarr.add(attendeeUser.userId);
                    }
                }
            }
            jsonObject.add("attendees", jsonarr);
            if (taskGroupEntity != null) {
                jsonObject.addProperty("parentId", taskGroupEntity.id);
            }
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser
                && taskSimpleAdapter != null) {
            //移除已经完成的任务
            boolean isActioned = false;
            for (int i = taskItemEntityList.size() - 1; i >= 0; i--) {
                TaskEntity.TaskItemEntity taskItemEntity = taskItemEntityList.get(i);
                if (taskItemEntity.state) {
                    isActioned = true;
                    taskItemEntityList.remove(i);
                }
            }
            if (isActioned) {
                Collections.sort(taskItemEntityList, taskItemEntityComparator);
                taskSimpleAdapter.bindData(true, taskItemEntityList);
            }
        }
    }

}
