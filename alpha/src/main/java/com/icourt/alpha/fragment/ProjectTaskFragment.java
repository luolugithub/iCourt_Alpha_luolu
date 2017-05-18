package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 项目下任务列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectTaskFragment extends BaseFragment {

    private static final String KEY_PROJECT_ID = "key_project_id";
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    TaskAdapter taskAdapter;
    String projectId;
    List<TaskEntity> allTaskEntities;
    List<TaskEntity.TaskItemEntity> todayTaskEntities;//今天到期
    List<TaskEntity.TaskItemEntity> beAboutToTaskEntities;//即将到期
    List<TaskEntity.TaskItemEntity> futureTaskEntities;//未来
    List<TaskEntity.TaskItemEntity> noDueTaskEntities;//未指定到期
    List<TaskEntity.TaskItemEntity> datedTaskEntities;//已过期

    public static ProjectTaskFragment newInstance(@NonNull String projectId) {
        ProjectTaskFragment projectTaskFragment = new ProjectTaskFragment();
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
        EventBus.getDefault().register(this);
        projectId = getArguments().getString(KEY_PROJECT_ID);
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans5Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(taskAdapter = new TaskAdapter());
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
            }
        });
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();

        allTaskEntities = new ArrayList<>();
        todayTaskEntities = new ArrayList<>();
        beAboutToTaskEntities = new ArrayList<>();
        futureTaskEntities = new ArrayList<>();
        noDueTaskEntities = new ArrayList<>();
        datedTaskEntities = new ArrayList<>();
    }

    @Override
    protected void getData(boolean isRefresh) {
        clearLists();
        getApi().projectQueryTaskList(projectId, 0, 0, 1, -1).enqueue(new SimpleCallBack<TaskEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                stopRefresh();
                getTaskGroupData(response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
            }
        });
    }

    /**
     * 对接口返回数据进行分组(今天、即将到期、未来、未指定日期)
     *
     * @param taskEntity
     */
    private void getTaskGroupData(TaskEntity taskEntity) {
        if (taskEntity != null) {
            if (taskEntity.items != null) {
                for (TaskEntity.TaskItemEntity taskItemEntity : taskEntity.items) {
                    if (taskItemEntity.dueTime > 0) {
                        if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis()))) {
                            todayTaskEntities.add(taskItemEntity);
                        } else if (DateUtils.getDayDiff(new Date(DateUtils.millis()), new Date(taskItemEntity.dueTime)) <= 3 && DateUtils.getDayDiff(new Date(DateUtils.millis()), new Date(taskItemEntity.dueTime)) > 0) {
                            beAboutToTaskEntities.add(taskItemEntity);
                        } else if (DateUtils.getDayDiff(new Date(DateUtils.millis()), new Date(taskItemEntity.dueTime)) > 3) {
                            futureTaskEntities.add(taskItemEntity);
                        } else {
                            datedTaskEntities.add(taskItemEntity);
                        }
                    } else {
                        noDueTaskEntities.add(taskItemEntity);
                    }
                }


                if (datedTaskEntities.size() > 0) {
                    TaskEntity todayTask = new TaskEntity();
                    todayTask.items = datedTaskEntities;
                    todayTask.groupName = "已过期";
                    todayTask.groupTaskCount = datedTaskEntities.size();
                    allTaskEntities.add(todayTask);
                }
                if (todayTaskEntities.size() > 0) {
                    TaskEntity todayTask = new TaskEntity();
                    todayTask.items = todayTaskEntities;
                    todayTask.groupName = "今天到期";
                    todayTask.groupTaskCount = todayTaskEntities.size();
                    allTaskEntities.add(todayTask);
                }

                if (beAboutToTaskEntities.size() > 0) {
                    TaskEntity task = new TaskEntity();
                    task.items = beAboutToTaskEntities;
                    task.groupName = "即将到期";
                    task.groupTaskCount = beAboutToTaskEntities.size();
                    allTaskEntities.add(task);
                }

                if (futureTaskEntities.size() > 0) {
                    TaskEntity task = new TaskEntity();
                    task.items = futureTaskEntities;
                    task.groupName = "未来";
                    task.groupTaskCount = futureTaskEntities.size();
                    allTaskEntities.add(task);
                }

                if (noDueTaskEntities.size() > 0) {
                    TaskEntity task = new TaskEntity();
                    task.items = noDueTaskEntities;
                    task.groupName = "未指定到期日";
                    task.groupTaskCount = noDueTaskEntities.size();
                    allTaskEntities.add(task);
                }

                taskAdapter.bindData(true, allTaskEntities);
            }
        }
    }

    private void clearLists() {
        if (allTaskEntities != null)
            allTaskEntities.clear();
        if (datedTaskEntities != null)
            datedTaskEntities.clear();
        if (todayTaskEntities != null)
            todayTaskEntities.clear();
        if (beAboutToTaskEntities != null)
            beAboutToTaskEntities.clear();
        if (futureTaskEntities != null)
            futureTaskEntities.clear();
        if (noDueTaskEntities != null)
            noDueTaskEntities.clear();
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
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
                    getParentPositon(updateItem.taskPkId);
                    getChildPositon(updateItem.taskPkId);
                    updateChildTimeing(updateItem.taskPkId, true);
                }
                break;
            case TimingEvent.TIMING_STOP:
                TimeEntity.ItemEntity stopItem = TimerManager.getInstance().getTimer();
                if (stopItem != null) {
                    getParentPositon(stopItem.taskPkId);
                    getChildPositon(stopItem.taskPkId);
                    updateChildTimeing(stopItem.taskPkId, false);
                }
                break;
        }
    }

    /**
     * 获取item所在父容器position
     *
     * @param taskId
     * @return
     */
    private int getParentPositon(String taskId) {
        if (taskAdapter.getData() != null) {
            for (int i = 0; i < taskAdapter.getData().size(); i++) {
                TaskEntity task = taskAdapter.getData().get(i);
                if (task != null && task.items != null) {
                    for (int j = 0; j < task.items.size(); j++) {
                        TaskEntity.TaskItemEntity item = task.items.get(j);
                        if (item != null) {
                            if (TextUtils.equals(item.id, taskId)) {
                                return i;
                            }
                        }
                    }
                }
            }
        }
        return -1;
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
                TaskEntity task = taskAdapter.getData().get(i);
                if (task != null && task.items != null) {
                    for (int j = 0; j < task.items.size(); j++) {
                        TaskEntity.TaskItemEntity item = task.items.get(j);
                        if (item != null) {
                            if (TextUtils.equals(item.id, taskId)) {
                                return j;
                            }
                        }
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
        int parentPos = getParentPositon(taskId);
        if (parentPos >= 0) {
            int childPos = getChildPositon(taskId);
            if (childPos >= 0) {
                BaseArrayRecyclerAdapter.ViewHolder viewHolderForAdapterPosition = (BaseArrayRecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(parentPos);
                if (viewHolderForAdapterPosition != null) {
                    RecyclerView recyclerview = viewHolderForAdapterPosition.obtainView(R.id.parent_item_task_recyclerview);
                    if (recyclerview != null) {
                        TaskItemAdapter itemAdapter = (TaskItemAdapter) recyclerview.getAdapter();
                        if (itemAdapter != null) {
                            TaskEntity.TaskItemEntity entity = itemAdapter.getItem(childPos);
                            if (entity != null) {
                                if (lastEntity != null)
                                    if (!TextUtils.equals(entity.id, lastEntity.id)) {
                                        lastEntity.isTiming = false;
                                        taskAdapter.notifyDataSetChanged();
                                    }
                                if (entity.isTiming != isTiming) {
                                    entity.isTiming = isTiming;
                                    itemAdapter.updateItem(entity);
                                    lastEntity = entity;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }
}
