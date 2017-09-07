package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchProjectActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskItemAdapter2;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.umeng.analytics.MobclickAgent;

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
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class TaskOtherListFragment extends BaseTaskFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    //实例化当前Fragment所要传递的参数标识
    private static final String TAG_START_TYPE = "startType";
    private static final String TAG_FINISH_TYPE = "finishType";
    private static final String TAG_IDS = "ids";

    public static final int MY_ALLOT_TYPE = 1;//我分配的
    public static final int SELECT_OTHER_TYPE = 2;//查看其他人

    public static final int UNFINISH_TYPE = 1;//未完成
    public static final int FINISH_TYPE = 2;//已完成

    public static final int TASK_TODAY_TYPE = 1;//今天任务（暂时保留字段）
    public static final int TASK_BEABOUT_TYPE = 2;//即将到期任务（暂时保留字段）
    public static final int TASK_FUTURE_TYPE = 3;//未来任务（暂时保留字段）
    public static final int TASK_NODUE_TYPE = 4;//未指定到期任务（暂时保留字段）
    public static final int TASK_DATED_TYPE = 5;//已过期任务（暂时保留字段）

    private boolean isFirstTimeIntoPage = true;//用来判断是不是第一次进入该界面，如果是，滚动到一条任务，隐藏搜索栏。

    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private LinearLayoutManager mLinearLayoutManager;

    int startType, finishType;
    ArrayList<String> ids;

    private int pageIndex = 1;//分页页码（暂时保留字段）

    TaskItemAdapter2 taskAdapter;

    TaskEntity.TaskItemEntity lastEntity;

    private ArrayMap<String, Integer> mArrayMap = new ArrayMap<>();//用来存储每个group有多少个数量（暂时保留，待分页再优化）。

    @IntDef({UNFINISH_TYPE,
            FINISH_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IS_FINISH_TYPE {

    }

    @IntDef({MY_ALLOT_TYPE,
            SELECT_OTHER_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface START_TYPE {

    }

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
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setHasFixedSize(true);

        taskAdapter = new TaskItemAdapter2();
        taskAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            protected void updateUI() {
                //当RecyclerView进行刷新的时候，会回调该方法，这个方法会比Decoration先执行。
            }
        });
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.header_search_comm, (ViewGroup) recyclerView.getParent(), false);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        taskAdapter.addHeaderView(headerView);
        taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchProjectActivity.launchTask(getContext(), getAssignTos(), 0, SearchProjectActivity.SEARCH_TASK);
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
        getApi().taskListItemQuery(getAssignTos(), stateType, 0, orderBy, pageIndex, pageSize, 0).enqueue(new SimpleCallBack<TaskEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                if (response.body().result != null) {
                    getTaskGroupData(response.body().result);
                    if (isRefresh)
                        enableEmptyView(response.body().result.items);
                    stopRefresh();
                }
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
    private void getTaskGroupData(final TaskEntity taskEntity) {
        if (taskEntity == null) return;
        if (taskEntity.items == null) return;
        Observable.create(new ObservableOnSubscribe<List<TaskEntity.TaskItemEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TaskEntity.TaskItemEntity>> e) throws Exception {
                if (e.isDisposed()) return;
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
                        if (isFirstTimeIntoPage) {
                            mLinearLayoutManager.scrollToPositionWithOffset(taskAdapter.getHeaderLayoutCount(), 0);
                            isFirstTimeIntoPage = false;
                        }
                    }
                });
    }

    /**
     * 分组排序，并添加到显示的集合
     *
     * @param taskItemEntities
     */
    private List<TaskEntity.TaskItemEntity> groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        List<TaskEntity.TaskItemEntity> allTaskEntities = new ArrayList<>();//展示所要用到的列表集合
        List<TaskEntity.TaskItemEntity> todayTaskEntities = new ArrayList<>();//今天到期
        List<TaskEntity.TaskItemEntity> beAboutToTaskEntities = new ArrayList<>();//即将到期
        List<TaskEntity.TaskItemEntity> futureTaskEntities = new ArrayList<>();//未来
        List<TaskEntity.TaskItemEntity> noDueTaskEntities = new ArrayList<>();//为指定到期
        List<TaskEntity.TaskItemEntity> datedTaskEntities = new ArrayList<>();//已过期
        for (TaskEntity.TaskItemEntity taskItemEntity : taskItemEntities) {
            if (taskItemEntity.dueTime > 0) {
                if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis())) || DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) < 0) {
                    //今天到期
                    taskItemEntity.groupName = "今天到期";
                    todayTaskEntities.add(taskItemEntity);
                } else if (DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) <= 3 && DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) > 0) {
                    //即将到期
                    taskItemEntity.groupName = "即将到期";
                    beAboutToTaskEntities.add(taskItemEntity);
                } else if (DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) > 3) {
                    //未来
                    taskItemEntity.groupName = "未来";
                    futureTaskEntities.add(taskItemEntity);
                } else {
                    //已到期
                    taskItemEntity.groupName = "已到期";
                    datedTaskEntities.add(taskItemEntity);
                }
            } else {
                //未指定到期日
                taskItemEntity.groupName = "未指定到期日";
                noDueTaskEntities.add(taskItemEntity);
            }
        }
        //将分组信息添加到allTaskEntities集合中，并且在mArrayMap中记录每组的数量
        addToAllTaskEntities("已到期", datedTaskEntities, allTaskEntities);
        addToAllTaskEntities("今天到期", todayTaskEntities, allTaskEntities);
        addToAllTaskEntities("即将到期", beAboutToTaskEntities, allTaskEntities);
        addToAllTaskEntities("未来", futureTaskEntities, allTaskEntities);
        addToAllTaskEntities("未指定到期日", noDueTaskEntities, allTaskEntities);
        return allTaskEntities;
    }

    /**
     * 将分组的list添加到总的任务集合中去
     *
     * @param list
     */
    private void addToAllTaskEntities(String groupName, List<TaskEntity.TaskItemEntity> list, List<TaskEntity.TaskItemEntity> allTaskEntities) {
        if (list == null || list.size() == 0)
            return;
        //创建一个群组标题的item
        TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
        itemEntity.groupName = groupName;
        itemEntity.groupTaskCount = list.size();
        itemEntity.type = 1;//0：普通；1：任务组。
        allTaskEntities.add(itemEntity);
        allTaskEntities.addAll(list);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, final View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            case R.id.task_item_start_timming:
                if (itemEntity == null)
                    return;
                if (itemEntity.isTiming) {//停止计时
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    stopTiming(itemEntity);
                } else {//开始计时
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    startTiming(itemEntity);
                }
                break;
            case R.id.task_item_checkbox:
                if (itemEntity == null)
                    return;
                if (!itemEntity.state) {//完成任务
                    if (itemEntity.attendeeUsers != null) {
                        if (itemEntity.attendeeUsers.size() > 1) {
                            showFinishDialog(view.getContext(), "该任务由多人负责,确定完成?", itemEntity, SHOW_FINISH_DIALOG);
                        } else {
                            updateTaskState(itemEntity, true);
                        }
                    } else {
                        updateTaskState(itemEntity, true);
                    }
                } else {//取消完成任务
                    updateTaskState(itemEntity, false);
                }
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        if (taskItemEntity != null && taskItemEntity.type == 0) {//item为任务的时候才可以点击
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }


    /**
     * 根据数据是否为空，判断是否显示空页面。
     *
     * @param result 用来判断是否要显示空页面的列表
     */
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

    /**
     * 停止刷新
     */
    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }


    @Override
    protected void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response) {
        taskAdapter.updateItem(requestEntity);
        if (response.body() != null) {//开始计时成功，跳转到计时页。
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

    }

    @Override
    protected void taskUpdateBack(@ChangeType int type, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        //更新任务成功的回调:修改状态，修改所属项目／任务组，修改负责人，修改到期时间
        //因为他人任务只有开始/结束计时，完成/未完成任务的操作，所以不需要刷新列表。
        taskAdapter.updateItem(itemEntity);
    }

    @Override
    protected void taskTimerUpdateBack(String taskId) {
        if (!TextUtils.isEmpty(taskId)) {//添加计时
            taskAdapter.notifyDataSetChanged();
        } else {//结束计时
            if (lastEntity != null) {
                lastEntity.isTiming = false;
            }
            taskAdapter.notifyDataSetChanged();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TaskActionEvent.TASK_REFRESG_ACTION://刷新的广播
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_DELETE_ACTION://删除的广播
                if (event.entity == null) return;
                //因为考虑到本地分组的原因，所以需要刷新全部（等后端添加分页逻辑，再进行修改）
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_ADD_ITEM_ACITON://添加的广播
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_UPDATE_ITEM://更新计时的广播
                if (event.entity == null) return;
                updateChildItem(event.entity);
                break;
        }
    }

    /**
     * 删除子item
     *
     * @param itemEntity
     */
    private void removeChildItem(TaskEntity.TaskItemEntity itemEntity) {
        if (itemEntity == null) return;
        taskAdapter.removeItem(itemEntity);
    }

    /**
     * 更新子item
     *
     * @param itemEntity
     */
    private void updateChildItem(TaskEntity.TaskItemEntity itemEntity) {
        taskAdapter.updateItem(itemEntity);
    }

    /**
     * 根据任务id，获取任务在Adapter中的位置
     *
     * @param taskId
     * @return
     */
    private int getItemPosition(String taskId) {
        for (int i = 0; i < taskAdapter.getData().size(); i++) {
            TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getData().get(i);
            if (taskItemEntity.type == 0 && TextUtils.equals(taskItemEntity.id, taskId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 更新item
     *
     * @param taskId
     */
    private void updateUnFinishChildTimeing(String taskId, boolean isTiming) {
        int itemPos = getItemPosition(taskId);
        if (itemPos >= 0) {
            TaskEntity.TaskItemEntity entity = taskAdapter.getItem(itemPos);
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
        } else {
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
