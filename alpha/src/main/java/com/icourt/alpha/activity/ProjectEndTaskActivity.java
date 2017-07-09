package com.icourt.alpha.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description 项目详情：已完成任务页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectEndTaskActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    private static final String KEY_PROJECT_ID = "key_project_id";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    TaskItemAdapter taskAdapter;
    String projectId;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    private int pageIndex = 1;
    HeaderFooterAdapter<TaskItemAdapter> headerFooterAdapter;

    public static void launch(@NonNull Context context, @NonNull String projectId) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectEndTaskActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_end_task_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();
        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
        setTitle("查看已完成任务");
        EventBus.getDefault().register(this);
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, "暂无已完成任务");
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        headerFooterAdapter = new HeaderFooterAdapter<>(taskAdapter = new TaskItemAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);
        recyclerView.setAdapter(headerFooterAdapter);

        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));


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
                SearchProjectActivity.launchFinishTask(getContext(), "", 0, 1, SearchProjectActivity.SEARCH_TASK, projectId);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 1;
        }
        getApi().taskListQueryByMatterId(1, "updateTime", projectId, 0, pageIndex, ActionConstants.DEFAULT_PAGE_SIZE).enqueue(new SimpleCallBack<TaskEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                stopRefresh();
                if (response.body().result != null) {
                    taskAdapter.bindData(isRefresh, response.body().result.items);
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
//            if (taskItemEntity.parentFlow != null) {
//                itemEntity.workTypeName = taskItemEntity.parentFlow.name;
//                itemEntity.workTypeId = taskItemEntity.parentFlow.id;
//            }
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
    private void updateTask(final TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckBox checkbox) {
        showLoadingDialog(null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                checkbox.setChecked(state);
                View view = (View) checkbox.getParent();
                if (view != null) {
                    TextView timeView = (TextView) view.findViewById(R.id.task_time_tv);
                    if (state) {
                        timeView.setTextColor(Color.parseColor("#FF8c8f92"));
                        timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.task_time_icon, 0, 0, 0);
                        timeView.setVisibility(View.VISIBLE);
                        timeView.setText(DateUtils.get23Hour59MinFormat(DateUtils.millis()));
                    } else {
                        if (itemEntity.dueTime > 0) {
                            timeView.setVisibility(View.VISIBLE);
                            timeView.setText(DateUtils.get23Hour59MinFormat(itemEntity.dueTime));
                            if (itemEntity.dueTime < DateUtils.millis()) {
                                timeView.setTextColor(Color.parseColor("#FF0000"));
                                timeView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_fail, 0, 0, 0);
                            } else {
                                timeView.setTextColor(Color.parseColor("#FF8c8f92"));
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
//                    getChildPositon(updateItem.taskPkId);
                    updateChildTimeing(updateItem.taskPkId, true);
                }
                break;
            case TimingEvent.TIMING_STOP:
                if (lastEntity != null) {
                    lastEntity.isTiming = false;
                    taskAdapter.notifyDataSetChanged();
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
        if (taskAdapter.getData() != null) {
            for (int i = 0; i < taskAdapter.getData().size(); i++) {
                TaskEntity.TaskItemEntity item = taskAdapter.getData().get(i);
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
        int childPos = getChildPositon(taskId);
        if (childPos >= 0) {
            TaskEntity.TaskItemEntity entity = taskAdapter.getItem(childPos);
            if (entity != null) {
                if (lastEntity != null)
                    if (!TextUtils.equals(entity.id, lastEntity.id)) {
                        lastEntity.isTiming = false;
                        taskAdapter.notifyDataSetChanged();
                    }
                if (entity.isTiming != isTiming) {
                    entity.isTiming = isTiming;
//                    taskAdapter.updateItem(entity);
                    taskAdapter.notifyDataSetChanged();
                    lastEntity = entity;
                }
            }

        }
    }
}
