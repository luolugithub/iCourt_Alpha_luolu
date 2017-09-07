package com.icourt.alpha.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchProjectActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.TaskItemAdapter2;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 项目详情：已完成任务页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectEndTaskFragment extends BaseTaskFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemLongClickListener {

    public static final String KEY_PROJECT_ID = "key_project_id";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    Unbinder unbinder;
    TaskItemAdapter2 taskAdapter;
    String projectId;
    private int pageIndex = 1;
    TaskEntity.TaskItemEntity lastEntity;//最后一次操作的任务

    public static ProjectEndTaskFragment newInstance(@NonNull Context context, @NonNull String projectId) {
        ProjectEndTaskFragment projectTaskFragment = new ProjectEndTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        projectTaskFragment.setArguments(bundle);
        return projectTaskFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_project_mine, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString(KEY_PROJECT_ID);
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, "暂无已完成任务");
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        taskAdapter = new TaskItemAdapter2();
        taskAdapter.addHeaderView(headerView);
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        taskAdapter.setOnItemLongClickListener(this);
        taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));
        recyclerView.setAdapter(taskAdapter);


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TaskActionEvent.TASK_REFRESG_ACTION:
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_DELETE_ACTION:
                if (event.entity == null) return;
                if (taskAdapter != null) {
                    taskAdapter.removeItem(event.entity);
                }
                break;
            case TaskActionEvent.TASK_ADD_ITEM_ACITON:
                if (event.entity == null) return;
                if (taskAdapter != null) {
                    taskAdapter.addData(event.entity);
                }
                break;
            case TaskActionEvent.TASK_UPDATE_ITEM:
                if (event.entity == null) return;
                if (taskAdapter != null) {
                    taskAdapter.updateItem(event.entity);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchProjectActivity.launchFinishTask(getContext(), "", 0, 1, SearchProjectActivity.SEARCH_TASK, projectId);
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
                    taskAdapter.setNewData(response.body().result.items);
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
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        if (taskItemEntity != null)
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            case R.id.task_item_start_timming:
                if (itemEntity == null)
                    return;
                if (!itemEntity.isTiming) {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    startTiming(itemEntity);
                } else {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    stopTiming(itemEntity);
                }
                break;
            case R.id.task_item_checkbox:
                if (isEditTask) {
                    if (itemEntity == null)
                        return;
                    if (!itemEntity.state) {//完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(getActivity(), "该任务由多人负责,确定完成?", itemEntity, SHOW_FINISH_DIALOG);
                            } else {
                                updateTaskState(itemEntity, true);
                            }
                        } else {
                            updateTaskState(itemEntity, true);
                        }
                    } else {//取消完成任务
                        updateTaskState(itemEntity, false);
                    }
                } else {
                    showTopSnackBar("您没有编辑任务的权限");
                }
                break;
        }
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity item = taskAdapter.getItem(i);
        if (item != null && item.type == 0)//说明是任务
            showLongMenu(item);
        return false;
    }

    @Override
    protected void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response) {
        taskAdapter.updateItem(requestEntity);
        TimerTimingActivity.launch(getActivity(), response.body());
    }

    @Override
    protected void stopTimingBack(TaskEntity.TaskItemEntity requestEntity) {
        taskAdapter.updateItem(requestEntity);
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        TimerDetailActivity.launch(getActivity(), timer);
    }

    @Override
    protected void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {
        getData(true);
    }

    @Override
    protected void taskUpdateBack(@ChangeType int actionType, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        if (actionType == CHANGE_DUETIME) {
            getData(true);
        } else {
            taskAdapter.updateItem(itemEntity);
        }
    }

    @Override
    protected void taskTimerUpdateBack(String taskId) {
        if (TextUtils.isEmpty(taskId)) {//停止计时的广播
            if (lastEntity != null) {
                lastEntity.isTiming = false;
            }
            taskAdapter.notifyDataSetChanged();
        } else {//开始计时的广播
            taskAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取item所在子容器position
     *
     * @param taskId
     * @return
     */
    private int getItemPosition(String taskId) {
        for (int i = 0; i < taskAdapter.getData().size(); i++) {
            TaskEntity.TaskItemEntity item = taskAdapter.getData().get(i);
            if (item != null) {
                if (TextUtils.equals(item.id, taskId)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 更新item
     *
     * @param taskId
     */
    private void updateChildTimeing(String taskId, boolean isTiming) {
        int pos = getItemPosition(taskId);
        if (pos >= 0) {
            TaskEntity.TaskItemEntity entity = taskAdapter.getItem(pos);
            if (entity != null) {
                if (lastEntity != null)
                    if (!TextUtils.equals(entity.id, lastEntity.id)) {
                        lastEntity.isTiming = false;
                        taskAdapter.notifyDataSetChanged();
                    }
                if (entity.isTiming != isTiming) {
                    entity.isTiming = isTiming;
                    taskAdapter.updateItem(entity);
                    lastEntity = entity;
                }
            }
        }
    }

}
