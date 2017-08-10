package com.icourt.alpha.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ItemsEntity;
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
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icourt.alpha.base.BaseDialogFragment.KEY_FRAGMENT_RESULT;

/**
 * Description 已完成的任务
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class MyFinishTaskActivity extends BaseActivity
        implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener,
        BaseRecyclerAdapter.OnItemLongClickListener,
        OnFragmentCallBackListener,
        ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener {
    private static final int SHOW_DELETE_DIALOG = 0;//删除提示对话框
    private static final int SHOW_FINISH_DIALOG = 1;//完成任务提示对话框
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    private int pageIndex = 1;
    TaskItemAdapter taskItemAdapter;
    HeaderFooterAdapter<TaskItemAdapter> headerFooterAdapter;
    TaskEntity.TaskItemEntity updateTaskItemEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ated);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyFinishTaskActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("查看已完成的任务");
        EventBus.getDefault().register(this);
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, "暂无已完成任务");
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setHasFixedSize(true);

        headerFooterAdapter = new HeaderFooterAdapter<>(taskItemAdapter = new TaskItemAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);
        recyclerView.setBackgroundResource(R.color.alpha_background_window);
        recyclerView.setAdapter(headerFooterAdapter);


        taskItemAdapter.setOnItemClickListener(this);
        taskItemAdapter.setOnItemChildClickListener(this);
        taskItemAdapter.setOnItemLongClickListener(this);
        taskItemAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskItemAdapter));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
        refreshLayout.startRefresh();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchProjectActivity.launchFinishTask(getContext(), getLoginUserId(), 0, 1, SearchProjectActivity.SEARCH_TASK, null);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        if (isRefresh) {
            pageIndex = 1;
        }
        getApi().taskListQuery(0, getLoginUserId(), 1, 0, "updateTime", pageIndex, ActionConstants.DEFAULT_PAGE_SIZE, 0).enqueue(new SimpleCallBack<TaskEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                if (response.body().result != null) {
                    taskItemAdapter.bindData(isRefresh, response.body().result.items);
                    if (isRefresh)
                        enableEmptyView(response.body().result.items);
                    stopRefresh();
                    pageIndex += 1;
                    enableLoadMore(response.body().result.items);
                }
            }

            @Override
            public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
            }
        });
    }

    private void enableEmptyView(List result) {
        if (refreshLayout != null) {
            if (result != null) {
                if (result.size() > 0) {
                    refreshLayout.enableEmptyView(false);
                } else {
                    refreshLayout.enableEmptyView(true);
                }
            }
        }
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(adapter.getRealPos(position));
            if (taskItemEntity != null)
                TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, final View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(adapter.getRealPos(position));
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        TimerManager.getInstance().stopTimer();
                        ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        showLoadingDialog(null);

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
                    if (checkbox.isChecked()) {//完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showDeleteDialog("该任务由多人负责,确定完成?", itemEntity, SHOW_FINISH_DIALOG, checkbox);
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

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(adapter.getRealPos(position));
            if (taskItemEntity.state) {
                return false;
            }
            ItemsEntity timeEntity = new ItemsEntity("开始计时", R.mipmap.time_start_orange_task);
            if (taskItemEntity.isTiming) {
                timeEntity.itemIconRes = R.mipmap.time_stop_orange_task;
                timeEntity.itemTitle = "停止计时";
            } else {
                timeEntity.itemIconRes = R.mipmap.time_start_orange_task;
                timeEntity.itemTitle = "开始计时";
            }
            showLongMeau(view.getContext(), Arrays.asList(
                    new ItemsEntity("项目/任务组", R.mipmap.project_orange),
                    new ItemsEntity("分配给", R.mipmap.assign_orange),
                    new ItemsEntity("到期日", R.mipmap.date_orange),
                    timeEntity,
                    new ItemsEntity("查看详情", R.mipmap.info_orange),
                    new ItemsEntity("删除", R.mipmap.trash_orange)), taskItemEntity);
        }
        return true;
    }

    private void showLongMeau(Context context, List<ItemsEntity> itemsEntities, TaskEntity.TaskItemEntity taskItemEntity) {
        CenterMenuDialog centerMenuDialog = new CenterMenuDialog(context, null, itemsEntities);
        centerMenuDialog.show();
        centerMenuDialog.setOnItemClickListener(new CustOnItemClickListener(centerMenuDialog, taskItemEntity));
    }

    private class CustOnItemClickListener implements BaseRecyclerAdapter.OnItemClickListener {
        CenterMenuDialog centerMenuDialog;
        TaskEntity.TaskItemEntity taskItemEntity;

        CustOnItemClickListener(CenterMenuDialog centerMenuDialog, TaskEntity.TaskItemEntity taskItemEntity) {
            this.centerMenuDialog = centerMenuDialog;
            this.taskItemEntity = taskItemEntity;
            updateTaskItemEntity = taskItemEntity;
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
                                showTaskAllotSelectDialogFragment(taskItemEntity.matter.id, taskItemEntity.attendeeUsers);
                            } else {
                                showToast("请先选择项目");
                            }
                            break;
                        case R.mipmap.date_orange://到期日
                            showDateSelectDialogFragment(taskItemEntity.dueTime, taskItemEntity.id);
                            break;
                        case R.mipmap.info_orange://查看详情
                            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
                            break;
                        case R.mipmap.project_orange://项目/任务组
                            showProjectSelectDialogFragment();
                            break;
                        case R.mipmap.time_start_orange_task://开始计时
                            if (!taskItemEntity.isTiming) {
                                TimerManager.getInstance().addTimer(getTimer(taskItemEntity), new Callback<TimeEntity.ItemEntity>() {
                                    @Override
                                    public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                        dismissLoadingDialog();
                                        if (response.body() != null) {
                                            updateMeauItem(entity, true, menuAdapter);
                                            TimerTimingActivity.launch(view.getContext(), response.body());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                                        dismissLoadingDialog();
                                        updateMeauItem(entity, false, menuAdapter);
                                    }
                                });

                            }
                            break;
                        case R.mipmap.time_stop_orange_task://停止计时
                            if (taskItemEntity.isTiming) {
                                TimerManager.getInstance().stopTimer();
                                updateMeauItem(entity, true, menuAdapter);
                            }
                            break;
                        case R.mipmap.trash_orange://删除
                            if (taskItemEntity.attendeeUsers != null) {
                                if (taskItemEntity.attendeeUsers.size() > 1) {
                                    showDeleteDialog("该任务由多人负责,确定删除?", taskItemEntity, SHOW_DELETE_DIALOG, null);
                                } else {
                                    showDeleteDialog("是非成败转头空，确定要删除吗?", taskItemEntity, SHOW_DELETE_DIALOG, null);
                                }
                            } else {
                                showDeleteDialog("是非成败转头空，确定要删除吗?", taskItemEntity, SHOW_DELETE_DIALOG, null);
                            }
                            break;
                    }
                }
            }
        }
    }

    /**
     * 开始／结束计时之后，更新meau
     */
    private void updateMeauItem(ItemsEntity entity, boolean isTimering, CenterMenuDialog.MenuAdapter menuAdapter) {
        entity.itemIconRes = isTimering ? R.mipmap.time_start_orange_task : R.mipmap.time_start_orange;
        entity.itemTitle = isTimering ? "停止计时" : "开始计时";
        menuAdapter.updateItem(entity);
    }

    /**
     * 展示选择负责人对话框
     */
    public void showTaskAllotSelectDialogFragment(String projectId, List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUsers) {
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
    public void showProjectSelectDialogFragment() {
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
    private void showDateSelectDialogFragment(long dueTime, String taskId) {
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

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params != null) {
            if (fragment instanceof TaskAllotSelectDialogFragment) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (updateTaskItemEntity.attendeeUsers != null) {
                    updateTaskItemEntity.attendeeUsers.clear();
                    updateTaskItemEntity.attendeeUsers.addAll(attusers);
                    updateTask(getTaskJson(updateTaskItemEntity, null, null), updateTaskItemEntity, null);
                }
            } else if (fragment instanceof DateSelectDialogFragment) {
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                updateTaskItemEntity.dueTime = millis;
                TaskReminderEntity taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");
                updateTask(getTaskJson(updateTaskItemEntity, null, null), updateTaskItemEntity, taskReminderEntity);
            }
        }
    }


    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {

        updateTask(getTaskJsonByProject(updateTaskItemEntity, projectEntity, taskGroupEntity), updateTaskItemEntity, null);
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
        }
        return itemEntity;
    }

    /**
     * 删除多人任务对话框
     *
     * @param message
     * @param itemEntity
     * @param checkbox
     */
    private void showDeleteDialog(String message, final TaskEntity.TaskItemEntity itemEntity, final int type, final CheckBox checkbox) {
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        if (type == SHOW_DELETE_DIALOG) {
                            deleteTask(itemEntity);
                        } else if (type == SHOW_FINISH_DIALOG) {
                            if (itemEntity.state) {
                                updateTask(itemEntity, false, checkbox);
                            } else {
                                updateTask(itemEntity, true, checkbox);
                            }
                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE://取消
                        if (type == SHOW_FINISH_DIALOG) {
                            if (checkbox != null)
                                checkbox.setChecked(itemEntity.state);
                        }
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    private void updateTask(String requestJson, final TaskEntity.TaskItemEntity itemEntity, final TaskReminderEntity taskReminderEntity) {
        if (TextUtils.isEmpty(requestJson)) return;
        showLoadingDialog(null);
        getApi().taskUpdateNew(RequestUtils.createJsonBody(requestJson)).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                dismissLoadingDialog();
//                refreshLayout.startRefresh();
                if (response.body().result != null) {
                    if (itemEntity != null && taskReminderEntity != null) {
                        addReminders(updateTaskItemEntity, taskReminderEntity);
                    }
                    int index = taskItemAdapter.getData().indexOf(response.body().result);
                    if (index >= 0) {
                        taskItemAdapter.getData().set(index, response.body().result);
                        taskItemAdapter.notifyDataSetChanged();
                    }
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
     * 修改任务
     *
     * @param itemEntity
     * @param state
     * @param checkbox
     */
    private void updateTask(final TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckBox checkbox) {
        showLoadingDialog(null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                checkbox.setChecked(state);
                itemEntity.state = state;
                itemEntity.updateTime = DateUtils.millis();
                taskItemAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
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
    private void deleteTask(final TaskEntity.TaskItemEntity itemEntity) {
        showLoadingDialog(null);
        getApi().taskDelete(itemEntity.id).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                if (taskItemAdapter.getData().contains(itemEntity)) {
                    taskItemAdapter.removeItem(itemEntity);
                }
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
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @return
     */
    private String getTaskJson(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (itemEntity == null) return null;
        JsonObject jsonObject = new JsonObject();
        jsonObjectAddPro(jsonObject, itemEntity);
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
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @return
     */
    private String getTaskJsonByProject(TaskEntity.TaskItemEntity itemEntity, ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (itemEntity == null) return null;
        JsonObject jsonObject = new JsonObject();
        jsonObjectAddPro(jsonObject, itemEntity);
        JsonArray jsonarr = new JsonArray();
        if (projectEntity != null) {
            jsonObject.addProperty("matterId", projectEntity.pkId);
            // jsonarr.add(getLoginUserId());
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
    }

    private void jsonObjectAddPro(JsonObject jsonObject, TaskEntity.TaskItemEntity itemEntity) {
        jsonObject.addProperty("id", itemEntity.id);
        jsonObject.addProperty("state", itemEntity.state);
        jsonObject.addProperty("valid", true);
        jsonObject.addProperty("name", itemEntity.name);
        jsonObject.addProperty("parentId", itemEntity.parentId);
        jsonObject.addProperty("dueTime", itemEntity.dueTime);
        jsonObject.addProperty("updateTime", DateUtils.millis());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        if (event.action == TaskActionEvent.TASK_REFRESG_ACTION) {
            refreshLayout.startRefresh();
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

                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
                if (updateItem != null) {
                    updateChildTimeing(updateItem.taskPkId, true);
                }
                break;
            case TimingEvent.TIMING_STOP:
                if (lastEntity != null) {
                    lastEntity.isTiming = false;
                    taskItemAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 获取item所在子容器position
     *
     * @param taskId
     * @return
     */
    private int getChildPositon(String taskId) {
        if (taskItemAdapter.getData() != null) {
            for (int i = 0; i < taskItemAdapter.getData().size(); i++) {
                TaskEntity.TaskItemEntity item = taskItemAdapter.getData().get(i);
                if (item != null) {
                    if (TextUtils.equals(item.id, taskId)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    TaskEntity.TaskItemEntity lastEntity;

    /**
     * 更新item
     *
     * @param taskId
     */
    private void updateChildTimeing(String taskId, boolean isTiming) {
        try {
            int childPos = getChildPositon(taskId);
            if (childPos >= 0) {
                TaskEntity.TaskItemEntity entity = taskItemAdapter.getItem(childPos);
                if (entity != null) {
                    if (lastEntity != null)
                        if (!TextUtils.equals(entity.id, lastEntity.id)) {
                            lastEntity.isTiming = false;
                            taskItemAdapter.notifyDataSetChanged();
                        }
                    if (entity.isTiming != isTiming) {
                        entity.isTiming = isTiming;
                        taskItemAdapter.notifyDataSetChanged();
                        lastEntity = entity;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
