package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskSearchActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.manager.TimerManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.umeng.analytics.MobclickAgent;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
 * Description 我分配的、选择查看对象（未完成｜已完成）
 * Company Beijing icourt
 *
 * @author lu.zhao  E-mail:zhaolu@icourt.cc
 *         date createTime：17/5/19
 *         version 2.0.0
 */

public class TaskOtherListFragment extends BaseTaskFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    /**
     * 实例化当前Fragment所要传递的参数标识
     * 我分配的／查看他人，所对应的参数应该是START_TYPE枚举里的两种类型。
     */
    public static final String TAG_START_TYPE = "startType";
    /**
     * 未完成／已完成，所对应的应该是IS_FINISH_TYPE枚举里的两种类型。
     */
    public static final String TAG_FINISH_TYPE = "finishType";
    public static final String TAG_IDS = "ids";

    /**
     * 我分配的
     */
    public static final int MY_ALLOT_TYPE = 1;
    /**
     * 查看其他人
     */
    public static final int SELECT_OTHER_TYPE = 2;

    @IntDef({MY_ALLOT_TYPE,
            SELECT_OTHER_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface START_TYPE {

    }


    /**
     * 未完成
     */
    public static final int UNFINISH_TYPE = 1;
    /**
     * 已完成
     */
    public static final int FINISH_TYPE = 2;

    @IntDef({UNFINISH_TYPE,
            FINISH_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IS_FINISH_TYPE {

    }

    //TODO 一大堆没用的常量与变量
    /**
     * 今天任务（暂时保留字段）
     */
    public static final int TASK_TYPE_TODAY = 1;
    /**
     * 即将到期任务（暂时保留字段）
     */
    public static final int TASK_TYPE_BEABOUT = 2;
    /**
     * 未来任务（暂时保留字段）
     */
    public static final int TASK_TYPE_FUTURE = 3;
    /**
     * 未指定到期任务（暂时保留字段）
     */
    public static final int TASK_TYPE_NODUE = 4;
    /**
     * 已过期任务（暂时保留字段）
     */
    public static final int TASK_TYPE_DATED = 5;

    /**
     * 用来判断是不是第一次进入该界面，如果是，滚动到一条任务，隐藏搜索栏。
     */
    private boolean isFirstTimeIntoPage = true;

    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private LinearLayoutManager linearLayoutManager;
    /**
     * 我分配的任务列表／他人的任务列表（我分配的暂时已经废弃了）
     */
    int startType;
    /**
     * 未完成／已完成
     */
    int finishType;

    ArrayList<String> ids;

    /**
     * 分页页码（暂时保留字段）
     */
    private int pageIndex = 1;

    TaskAdapter taskAdapter;

    TaskEntity.TaskItemEntity lastEntity;

    //TODO 没用的变量
    /**
     * 用来存储每个group有多少个数量（暂时保留，待分页再优化）。
     */
    private ArrayMap<String, Integer> mArrayMap = new ArrayMap<>();


    public static TaskOtherListFragment newInstance(@START_TYPE int startType, @IS_FINISH_TYPE int finishType, ArrayList<String> ids) {
        TaskOtherListFragment taskOtherListFragment = new TaskOtherListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAG_START_TYPE, startType);
        bundle.putInt(TAG_FINISH_TYPE, finishType);
        bundle.putStringArrayList(TAG_IDS, ids);
        taskOtherListFragment.setArguments(bundle);
        return taskOtherListFragment;
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
        startType = getArguments().getInt(TAG_START_TYPE);
        finishType = getArguments().getInt(TAG_FINISH_TYPE);
        ids = getArguments().getStringArrayList(TAG_IDS);
        if (startType == TaskOtherListFragment.SELECT_OTHER_TYPE) {
            recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, R.string.empty_list_task_other_people_task);
        } else {
            recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, R.string.empty_list_task);
        }
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout.setEnableLoadmore(false);
        taskAdapter = new TaskAdapter();
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.header_search_comm, (ViewGroup) recyclerView.getParent(), false);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        taskAdapter.addHeaderView(headerView);
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(taskAdapter);

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
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
                TaskSearchActivity.launchTask(getContext(), getAssignTos(), 0);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        switch (startType) {
            case MY_ALLOT_TYPE:
                getMyAllotTask(isRefresh);
                break;
            case SELECT_OTHER_TYPE:
                getMyAllotTask(isRefresh);
                break;
            default:
                break;
        }
    }

    /**
     * 获取查看对象集合的ids
     *
     * @return
     */
    private String getAssignTos() {
        if (ids != null) {
            StringBuffer buffer = new StringBuffer();
            for (String s : ids) {
                buffer.append(s).append(",");
            }
            return buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return null;
    }

    /**
     * 获取我分配的任务
     * <p>
     * assignedByMe：接口有可能会传该参数，该接口已修改无数遍 fuck
     */
    private void getMyAllotTask(final boolean isRefresh) {
        if (isRefresh) {
            pageIndex = 1;
        }
        int assignedByMe = 0, stateType = 0, pageSize = 0;
        if (startType == MY_ALLOT_TYPE) {
            assignedByMe = 0;
        } else if (startType == SELECT_OTHER_TYPE) {
            assignedByMe = 1;
        }
        String orderBy = null;
        if (finishType == FINISH_TYPE) {
            stateType = 1;
            orderBy = "updateTime";
        } else if (finishType == UNFINISH_TYPE) {
            stateType = 0;
            orderBy = "dueTime";
        }
        pageIndex = -1;
        pageSize = 10000;
        callEnqueue(
                getApi().taskListItemQuery(getAssignTos(), stateType, 0, orderBy, pageIndex, pageSize, 0),
                new SimpleCallBack<TaskEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                        if (response.body().result != null) {
                            getTaskGroupData(response.body().result);
                            if (isRefresh)
                                recyclerView.enableEmptyView(response.body().result.items);
                            stopRefresh();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                        recyclerView.enableEmptyView(null);
                    }
                }
        );
    }

    /**
     * 对接口返回数据进行分组(今天、即将到期、未来、未指定日期)
     *
     * @param taskEntity
     */
    private void getTaskGroupData(final TaskEntity taskEntity) {
        if (taskEntity == null) {
            return;
        }
        if (taskEntity.items == null) {
            return;
        }
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
                        taskAdapter.setNewData(searchPolymerizationEntities);
                        recyclerView.enableEmptyView(searchPolymerizationEntities);
                        goFirstTask();
                    }
                });
    }

    /**
     * 分组排序，并添加到显示的集合
     *
     * @param taskItemEntities
     */
    private List<TaskEntity.TaskItemEntity> groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        //展示所要用到的列表集合
        List<TaskEntity.TaskItemEntity> allTaskEntities = new ArrayList<>();
        //今天到期
        List<TaskEntity.TaskItemEntity> todayTaskEntities = new ArrayList<>();
        //即将到期
        List<TaskEntity.TaskItemEntity> beAboutToTaskEntities = new ArrayList<>();
        //未来
        List<TaskEntity.TaskItemEntity> futureTaskEntities = new ArrayList<>();
        //为指定到期
        List<TaskEntity.TaskItemEntity> noDueTaskEntities = new ArrayList<>();
        //已过期
        List<TaskEntity.TaskItemEntity> datedTaskEntities = new ArrayList<>();
        for (TaskEntity.TaskItemEntity taskItemEntity : taskItemEntities) {
            if (taskItemEntity.dueTime > 0) {
                if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis())) || DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) < 0) {
                    //今天到期
                    taskItemEntity.groupName = getString(R.string.task_today_due);
                    todayTaskEntities.add(taskItemEntity);
                } else if (DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) <= 3 && DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) > 0) {
                    //即将到期
                    taskItemEntity.groupName = getString(R.string.task_due_soon);
                    beAboutToTaskEntities.add(taskItemEntity);
                } else if (DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) > 3) {
                    //未来
                    taskItemEntity.groupName = getString(R.string.task_future);
                    futureTaskEntities.add(taskItemEntity);
                } else {
                    //已到期
                    taskItemEntity.groupName = getString(R.string.task_was_due);
                    datedTaskEntities.add(taskItemEntity);
                }
            } else {
                //未指定到期日
                taskItemEntity.groupName = getString(R.string.task_no_due_time);
                noDueTaskEntities.add(taskItemEntity);
            }
        }
        //将分组信息添加到allTaskEntities集合中，并且在mArrayMap中记录每组的数量
        //已到期
        addToAllTaskEntities(getString(R.string.task_was_due), datedTaskEntities, allTaskEntities);
        //今天到期
        addToAllTaskEntities(getString(R.string.task_today_due), todayTaskEntities, allTaskEntities);
        //即将到期
        addToAllTaskEntities(getString(R.string.task_due_soon), beAboutToTaskEntities, allTaskEntities);
        //未来
        addToAllTaskEntities(getString(R.string.task_future), futureTaskEntities, allTaskEntities);
        //未指定到期日
        addToAllTaskEntities(getString(R.string.task_no_due_time), noDueTaskEntities, allTaskEntities);
        return allTaskEntities;
    }

    /**
     * 将分组的list添加到总的任务集合中去
     *
     * @param list
     */
    private void addToAllTaskEntities(String groupName, List<TaskEntity.TaskItemEntity> list, List<TaskEntity.TaskItemEntity> allTaskEntities) {
        if (list == null || list.size() == 0) {
            return;
        }
        //创建一个群组标题的item
        TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
        itemEntity.groupName = groupName;
        itemEntity.groupTaskCount = list.size();
        //0：普通；1：任务组。
        itemEntity.type = 1;
        allTaskEntities.add(itemEntity);
        allTaskEntities.addAll(list);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, final View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            case R.id.task_item_start_timming:
                if (itemEntity == null) {
                    return;
                }
                //停止计时
                if (itemEntity.isTiming) {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    stopTiming(itemEntity);
                } else {
                    //开始计时
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    startTiming(itemEntity);
                }
                break;
            case R.id.task_item_checkbox:
                if (itemEntity == null) {
                    return;
                }
                //完成任务
                if (!itemEntity.state) {
                    if (itemEntity.attendeeUsers != null) {
                        if (itemEntity.attendeeUsers.size() > 1) {
                            showFinishDialog(view.getContext(), getString(R.string.task_is_confirm_complete_task), itemEntity, SHOW_FINISH_DIALOG);
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
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        //item为任务的时候才可以点击
        if (taskItemEntity != null && taskItemEntity.type == 0) {
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
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

    /**
     * 停止刷新
     */
    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }


    @Override
    protected void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response) {
        taskAdapter.updateItem(requestEntity);
        //开始计时成功，跳转到计时页。
        if (response.body() != null) {
            TimerTimingActivity.launch(getActivity(), response.body());
        }
    }

    @Override
    protected void stopTimingBack(TaskEntity.TaskItemEntity requestEntity) {
        //停止计时成功，跳转到计时详情页
        taskAdapter.updateItem(requestEntity);
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        TimerDetailActivity.launch(getActivity(), timer);
    }

    @Override
    protected void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {
        //他人任务列表不能删除任务
    }

    @Override
    protected void taskUpdateBack(@ChangeType int type, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        //更新任务成功的回调:修改状态，修改所属项目／任务组，修改负责人，修改到期时间
        //因为他人任务只有开始/结束计时，完成/未完成任务的操作，所以不需要刷新列表。
        taskAdapter.updateItem(itemEntity);
    }

    @Override
    protected void taskTimingUpdateEvent(String taskId) {
        //添加计时
        if (!TextUtils.isEmpty(taskId)) {
            taskAdapter.notifyDataSetChanged();
        } else {
            //结束计时
            if (lastEntity != null) {
                lastEntity.isTiming = false;
            }
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) {
            return;
        }
        switch (event.action) {
            //刷新的广播
            case TaskActionEvent.TASK_REFRESG_ACTION:
                getData(true);
                break;
            //删除的广播
            case TaskActionEvent.TASK_DELETE_ACTION:
                if (event.entity == null) {
                    return;
                }
                //因为考虑到本地分组的原因，所以需要刷新全部（等后端添加分页逻辑，再进行修改）
                getData(true);
                break;
            //添加的广播
            case TaskActionEvent.TASK_ADD_ITEM_ACITON:
                getData(true);
                break;
            //更新计时的广播
            case TaskActionEvent.TASK_UPDATE_ITEM:
                if (event.entity == null) {
                    return;
                }
                if (recyclerView != null) {
                    updateChildItem(event.entity);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 更新子item
     *
     * @param itemEntity
     */
    private void updateChildItem(TaskEntity.TaskItemEntity itemEntity) {
        taskAdapter.updateItem(itemEntity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
