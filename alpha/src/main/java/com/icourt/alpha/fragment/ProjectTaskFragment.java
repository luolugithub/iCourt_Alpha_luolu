package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchTaskActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.manager.TimerManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.umeng.analytics.MobclickAgent;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 项目下任务列表
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：17/9/5
 * version 2.0.0
 */

public class ProjectTaskFragment extends BaseTaskFragment implements BaseQuickAdapter.OnItemLongClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final String PROJECT_EDIT_TASK_PREMISSION = "MAT:matter.task:edit";
    private static final String PROJECT_DELETE_TASK_PREMISSION = "MAT:matter.task:delete";
    private static final String PROJECT_ADD_TASK_PREMISSION = "MAT:matter.timeLog:add";
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    /**
     * 用来判断是不是第一次进入该界面，如果是，滚动到一条任务，隐藏搜索栏。
     */
    private boolean isFirstTimeIntoPage = true;

    TaskAdapter taskAdapter;
    TaskEntity.TaskItemEntity lastEntity;
    String projectId;

    private LinearLayoutManager linearLayoutManager;

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
        projectId = getArguments().getString(KEY_PROJECT_ID);
        recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, R.string.empty_list_task_project_task);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView.getRecyclerView());
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);

        taskAdapter = new TaskAdapter();
        taskAdapter.addHeaderView(headerView);
        taskAdapter.setOnItemLongClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        taskAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(taskAdapter);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                checkAddTaskAndDocumentPms(projectId);
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstTimeIntoPage) {
            refreshLayout.autoRefresh();
        } else {
            getData(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchTaskActivity.launchFinishTask(getContext(), "", 0, 0, projectId);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取权限列表
     */
    private void checkAddTaskAndDocumentPms(String projectId) {
        callEnqueue(
                getApi().permissionQuery(getLoginUserId(), "MAT", projectId),
                new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {

                        if (response.body().result != null) {
                            if (response.body().result.contains(PROJECT_EDIT_TASK_PREMISSION)) {
                                isEditTask = true;
                            }
                            if (response.body().result.contains(PROJECT_DELETE_TASK_PREMISSION)) {
                                isDeleteTask = true;
                            }
                            if (response.body().result.contains(PROJECT_ADD_TASK_PREMISSION)) {
                                isAddTime = true;
                            }
                        }
                    }
                });
    }

    @Override
    protected void getData(boolean isRefresh) {
        callEnqueue(getApi().taskListQueryByMatterId(
                0,
                "dueTime",
                projectId,
                -1,
                1,
                -1),
                new SimpleCallBack<TaskEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                        //请求成功之后，要将数据进行分组。
                        getTaskGroupDatas(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                        recyclerView.enableEmptyView(null);
                    }
                });
    }

    /**
     * 异步分组
     *
     * @param taskEntity
     */
    private void getTaskGroupDatas(final TaskEntity taskEntity) {
        if (taskEntity != null) {
            recyclerView.enableEmptyView(taskEntity.items);
            if (taskEntity.items != null) {
                Observable.create(new ObservableOnSubscribe<List<TaskEntity.TaskItemEntity>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<TaskEntity.TaskItemEntity>> e) throws Exception {
                        if (e.isDisposed()) {
                            return;
                        }
                        e.onNext(groupingByTasks(taskEntity.items));
                        e.onComplete();
                    }
                }).compose(this.<List<TaskEntity.TaskItemEntity>>bindToLifecycle())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<TaskEntity.TaskItemEntity>>() {
                            @Override
                            public void accept(List<TaskEntity.TaskItemEntity> searchPolymerizationEntities) throws Exception {
                                stopRefresh();
                                taskAdapter.setAddTime(isAddTime);
                                taskAdapter.setNewData(searchPolymerizationEntities);
                                goFirstTask();
                                recyclerView.enableEmptyView(searchPolymerizationEntities);
                                TimerManager.getInstance().timerQuerySync();
                            }
                        });
            }
        } else {
            recyclerView.enableEmptyView(null);
        }
    }

    /**
     * 任务分组（项目下的任务分组是按照任务组来分的）
     *
     * @param taskitems
     */
    private List<TaskEntity.TaskItemEntity> groupingByTasks(List<TaskEntity.TaskItemEntity> taskitems) {
        //展示所要用到的列表集合
        List<TaskEntity.TaskItemEntity> allTaskEntities = new ArrayList<>();
        //用来存放任务组的列表
        List<TaskEntity> taskGroup = new ArrayList<>();
        //没有分配任务组的任务列表
        List<TaskEntity.TaskItemEntity> noitems = new ArrayList<>();
        //所有分配了任务组的任务列表
        List<TaskEntity.TaskItemEntity> taskEntities = new ArrayList<>();
        //我关注的的任务列表
        List<TaskEntity.TaskItemEntity> myStarTaskEntities = new ArrayList<>();

        TimeEntity.ItemEntity timerEntity = TimerManager.getInstance().getTimer();
        for (TaskEntity.TaskItemEntity taskItemEntity : taskitems) {
            //如果该任务正在计时，将任务的isTiming置为true。
            if (TimerManager.getInstance().hasTimer()) {
                if (timerEntity != null) {
                    if (!TextUtils.isEmpty(timerEntity.taskPkId)) {
                        if (TextUtils.equals(timerEntity.taskPkId, taskItemEntity.id)) {
                            taskItemEntity.isTiming = true;
                        } else {
                            taskItemEntity.isTiming = false;
                        }
                    }
                }
            }
            //1:任务组，将所有任务组单独拿出来，存放到taskGroup列表中。
            if (taskItemEntity.type == 1) {
                TaskEntity itemEntity = new TaskEntity();
                itemEntity.groupName = taskItemEntity.name;
                itemEntity.groupId = taskItemEntity.id;
                taskGroup.add(itemEntity);
            } //0:任务，对任务进行分组处理。
            else if (taskItemEntity.type == 0) {
                //如果parentId为空，说明该任务没有分配任务组。
                if (TextUtils.isEmpty(taskItemEntity.parentId)) {
                    noitems.add(taskItemEntity);
                } else {
                    taskEntities.add(taskItemEntity);
                }
                //我关注的任务
                if (taskItemEntity.attentioned == 1) {
                    myStarTaskEntities.add(taskItemEntity);
                }
            }
        }
        //遍历所有任务组，将有任务组的item添加到对应任务组的列表里面。
        if (taskGroup.size() > 0) {
            for (TaskEntity taskEntity : taskGroup) {
                List<TaskEntity.TaskItemEntity> items = new ArrayList<>();
                for (TaskEntity.TaskItemEntity entity : taskEntities) {
                    if (TextUtils.equals(taskEntity.groupId, entity.parentId)) {
                        items.add(entity);
                    }
                }
                taskEntity.items = items;
                taskEntity.groupTaskCount = items.size();
            }
        } else {
            //如果任务组列表为空，将所有任务添加到为分组列表里面。
            if (!taskEntities.isEmpty()) {
                noitems.addAll(taskEntities);
            }
        }
        if (noitems.size() > 0) {
            TaskEntity itemEntity = new TaskEntity();
            itemEntity.groupName = getString(R.string.task_none_group);
            itemEntity.items = noitems;
            itemEntity.groupTaskCount = noitems.size();
            taskGroup.add(itemEntity);
        }
        if (myStarTaskEntities.size() > 0) {
            TaskEntity itemEntity = new TaskEntity();
            itemEntity.groupName = getString(R.string.task_my_attention);
            itemEntity.items = myStarTaskEntities;
            itemEntity.groupTaskCount = myStarTaskEntities.size();
            taskGroup.add(0, itemEntity);
        }

        //taskGroup为分组完成的列表，将分组完成的列表转换成我们要显示的数据格式。
        for (TaskEntity taskEntity : taskGroup) {
            TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
            //表示是任务组
            itemEntity.type = 1;
            itemEntity.groupName = taskEntity.groupName;
            itemEntity.groupTaskCount = taskEntity.groupTaskCount;
            allTaskEntities.add(itemEntity);
            if (taskEntity.items != null) {
                allTaskEntities.addAll(taskEntity.items);
            }
        }
        return allTaskEntities;
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    /**
     * 如果是第一次进入该界面，滚动到第一条任务，隐藏搜索框
     */
    private void goFirstTask() {
        if (isFirstTimeIntoPage && taskAdapter.getData().size() > 0) {
            linearLayoutManager.scrollToPositionWithOffset(taskAdapter.getHeaderLayoutCount(), 0);
            isFirstTimeIntoPage = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTaskEvent(TaskActionEvent event) {
        if (event == null) {
            return;
        }
        if (event.action == TaskActionEvent.TASK_REFRESG_ACTION) {
            getData(true);
        }
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
        //因为项目下是以任务组来分组的，所以如果切换任务的项目／任务组，则需要刷新列表
        if (actionType == CHANGE_PROJECT) {
            getData(true);
        } else {
            taskAdapter.updateItem(itemEntity);
        }
    }

    @Override
    protected void taskTimingUpdateEvent(String taskId) {
        //停止计时的广播
        if (TextUtils.isEmpty(taskId)) {
            if (lastEntity != null) {
                lastEntity.isTiming = false;
            }
            taskAdapter.notifyDataSetChanged();
        } else {
            //开始计时的广播
            taskAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity item = taskAdapter.getItem(i);
        //说明是任务
        if (item != null && item.type == 0) {
            showLongMenu(item);
        }
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            case R.id.task_item_start_timming:
                if (itemEntity == null) {
                    return;
                }
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
                    if (itemEntity == null) {
                        return;
                    }
                    //完成任务
                    if (!itemEntity.state) {
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(getActivity(), getString(R.string.task_is_confirm_complete_task), itemEntity, SHOW_FINISH_DIALOG);
                            } else {
                                updateTaskState(itemEntity, true);
                            }
                        } else {
                            updateTaskState(itemEntity, true);
                        }
                    } else {
                        //取消完成任务
                        updateTaskState(itemEntity, false);
                    }
                } else {
                    showTopSnackBar(R.string.task_not_permission_edit_task);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity item = taskAdapter.getItem(i);
        //说明是任务
        if (item != null && item.type == 0) {
            TaskDetailActivity.launch(view.getContext(), item.id);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
