package com.icourt.alpha.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchProjectActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskItemAdapter2;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnTasksChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
 * Description 任务列表
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：17/9/6
 * version 2.0.0
 */

public class TaskListFragment2 extends BaseTaskFragment implements
        BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemLongClickListener,
        BaseQuickAdapter.OnItemChildClickListener {

    public static final String TYPE = "type";//type的传参标识
    public static final String STATE_TYPE = "stateType";//stateType的传参标识，stateType参数含义：-1，全部任务；0，未完成；1，已完成；3，已删除。

    public static final int TYPE_ALL = 0;//全部
    public static final int TYPE_MY_ATTENTION = 2;//我关注的

    Unbinder unbinder;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    //新任务的相关布局
    @BindView(R.id.new_task_cardview)
    CardView newTaskCardview;
    @BindView(R.id.new_task_count_tv)
    TextView newTaskCountTv;
    @BindView(R.id.next_task_close_iv)
    ImageView nextTaskCloseIv;
    @BindView(R.id.next_task_tv)
    TextView nextTaskTv;
    @BindView(R.id.next_task_layout)
    LinearLayout nextTaskLayout;
    @BindView(R.id.next_task_cardview)
    CardView nextTaskCardview;

    LinearLayoutManager linearLayoutManager;
    TaskItemAdapter2 taskAdapter;
    List<TaskEntity.TaskItemEntity> newTaskEntities;//新任务

    int type = 0;//0，全部；1，我关注的。
    int stateType = 0;//全部任务：－1；已完成：1；未完成：0；已删除：3。
    OnTasksChangeListener onTasksChangeListener;
    boolean isFirstTimeIntoPage = true;//是否是第一次进入界面，第一次进入界面，要隐藏搜索栏，滚动到第一个任务。

    TaskEntity.TaskItemEntity lastEntity;//最后一个操作的任务实体
    Handler handler = new Handler();

    TabTaskFragment tabTaskFragment = null;//爷爷Fragment

    boolean isUpdate = true;//新任务的item是否更新的标识
    View childItemView;//新任务的itemView
    //新任务提醒动画加载完成的监听
    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (childItemView != null) {
                if (childItemView instanceof CardView) {
                    CardView cardView = (CardView) childItemView;
                    cardView.setCardBackgroundColor(0xFFFFFFFF);
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


    public static TaskListFragment2 newInstance(int type, int stateType) {
        TaskListFragment2 projectTaskFragment = new TaskListFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        bundle.putInt(STATE_TYPE, stateType);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnTasksChangeListener) {
            onTasksChangeListener = (OnTasksChangeListener) getParentFragment();
        } else {
            try {
                onTasksChangeListener = (OnTasksChangeListener) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initView() {
        isEditTask = true;
        isAddTime = true;
        isDeleteTask = true;
        newTaskEntities = new ArrayList<>();

        tabTaskFragment = getParentTabTaskFragment();
        type = getArguments().getInt(TYPE);
        stateType = getArguments().getInt(STATE_TYPE);
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans5Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        taskAdapter = new TaskItemAdapter2();
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        taskAdapter.addHeaderView(headerView);
        taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemLongClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);

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


        getData(true);
    }

    @OnClick({R.id.new_task_cardview,
            R.id.next_task_close_iv,
            R.id.next_task_cardview})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchProjectActivity.launchTask(getContext(), getLoginUserId(), type, SearchProjectActivity.SEARCH_TASK);
                break;
            case R.id.new_task_cardview:
                if (tabTaskFragment != null) {
                    if (tabTaskFragment.select_position != 0) {
                        tabTaskFragment.isShowCalendar = false;
                        tabTaskFragment.setFirstTabText("未完成", 0);
                        tabTaskFragment.updateListData(0);
                        tabTaskFragment.isAwayScroll = true;
                    } else {
                        if (newTaskEntities != null) {
                            if (newTaskEntities.size() > 1) {
                                nextTaskLayout.setVisibility(View.VISIBLE);
                                updateNextTaskState();
                                v.setClickable(false);
                            } else if (newTaskEntities.size() == 1) {
                                if (newTaskEntities.get(0) != null) {
                                    updateNextTaskState();
                                    scrollToByPosition(newTaskEntities.get(0).id);
                                }
                            }
                        }
                    }
                    newTaskCardview.setVisibility(View.GONE);
                }
                break;
            case R.id.next_task_cardview://下一个
                updateNextTaskState();
                break;
            case R.id.next_task_close_iv://关闭'下一个'弹框,全部修改为已读
                if (newTaskEntities != null) {
                    showLoadingDialog(null);
                    List<String> ids = new ArrayList<>();
                    for (TaskEntity.TaskItemEntity newTaskEntity : newTaskEntities) {
                        ids.add(newTaskEntity.id);
                    }
                    onCheckNewTask(ids);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取爷爷fragment：TabTaskFragment
     *
     * @return
     */
    private TabTaskFragment getParentTabTaskFragment() {
        if (getParentFragment() != null && getParentFragment() instanceof TaskAllFragment) {
            if (getParentFragment().getParentFragment() != null && getParentFragment().getParentFragment() instanceof TabTaskFragment) {
                return (TabTaskFragment) getParentFragment().getParentFragment();
            }
        }
        return null;
    }

    /**
     * 新任务提醒的下一个
     */
    private void updateNextTaskState() {
        if (newTaskEntities != null) {
            if (newTaskEntities.size() > 0) {
                if (newTaskEntities.get(0) != null) {
                    scrollToByPosition(newTaskEntities.get(0).id);
                }
            }
        }
    }

    /**
     * 滚动到指定位置（查看新任务的时候会调用）
     *
     * @param taskId
     */
    private void scrollToByPosition(final String taskId) {
        isUpdate = true;
        final int itemPosition = getItemPosition(taskId);
        final int itemPositionWithHeader = itemPosition + taskAdapter.getHeaderLayoutCount();

        handler.removeCallbacksAndMessages(null);
        //如果新任务在屏幕完全可见，则直接执行动画。
        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() <= itemPositionWithHeader &&
                itemPositionWithHeader <= linearLayoutManager.findLastCompletelyVisibleItemPosition()) {
            postUpdateItem(taskId, itemPositionWithHeader);
        } else {
            //如果新任务在屏幕上没有完全可见，则将新任务滚动到最顶部，然后执行动画。
            if (tabTaskFragment.isAwayScroll) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutManager.scrollToPositionWithOffset(itemPositionWithHeader, 0);
                        postUpdateItem(taskId, itemPositionWithHeader);
                    }
                }, 100);
            } else {
                linearLayoutManager.scrollToPositionWithOffset(itemPositionWithHeader, 0);
                postUpdateItem(taskId, itemPositionWithHeader);
            }
        }

        List<String> ids = new ArrayList<>();
        ids.add(taskId);
        onCheckNewTask(ids);
        tabTaskFragment.isAwayScroll = false;
    }

    /**
     * item延迟高亮
     *
     * @param taskId
     * @param childPosition
     */
    private void postUpdateItem(final String taskId, final int childPosition) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUpdate) {
                    updateItemViewBackgrond(taskId, childPosition);
                }
            }
        }, 50);
    }

    /**
     * 改变itemview背景颜色
     *
     * @param taskId
     * @param childPosition
     */
    private void updateItemViewBackgrond(String taskId, int childPosition) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(childPosition);
        if (viewHolder != null) {
            childItemView = viewHolder.itemView;
            if (childItemView != null) {
                isUpdate = false;
                startViewAnim(childItemView);
            }
        }
    }

    /**
     * itemview渐变动画
     *
     * @param view
     */
    private void startViewAnim(View view) {
        CardView cardView = null;
        if (view instanceof CardView) {
            cardView = (CardView) view;
        }
        if (cardView != null) {
            ValueAnimator colorAnim = ObjectAnimator.ofInt(cardView, "CardBackgroundColor", 0xFFFCCEA7, 0xFFFFF6E9, 0xFFFFFFFF);
            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.addListener(animatorListener);
            colorAnim.start();
        }
    }


    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament != this) return;
        if (bundle != null) {
            stateType = bundle.getInt(STATE_TYPE);
        }
        this.type = type;
        //刷新
        if (targetFrgament == this && (type == 100 || type == TYPE_MY_ATTENTION)
                && recyclerView != null) {
            getData(true);
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        int attentionType = 0;
        String orderBy;
        if (type == TYPE_ALL) {
            attentionType = 0;
        } else if (type == TYPE_MY_ATTENTION) {
            attentionType = 1;
        }
        if (stateType == 0) {
            orderBy = "dueTime";
        } else {
            orderBy = "updateTime";
        }
        getApi().taskListQuery(0,
                getLoginUserId(),
                stateType,
                attentionType,
                orderBy,
                1,
                -1,
                0).enqueue(new SimpleCallBack<TaskEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                stopRefresh();
                getTaskGroupData(response.body().result);
                if (response.body().result != null) {
                    if (type == TYPE_ALL && onTasksChangeListener != null) {
//                        onTasksChangeListener.onTasksChanged(response.body().result.items);
                    }
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
        if (stateType == 0) {//未完成的任务需要分组
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
                            if (tabTaskFragment != null) {
                                if (tabTaskFragment.isAwayScroll && stateType == 0) {
                                    if (newTaskEntities.size() > 1) {
                                        nextTaskLayout.setVisibility(View.VISIBLE);
                                    }
                                    nextTaskTv.setText(String.format("下一个 (%s)", newTaskEntities.size()));
                                    updateNextTaskState();
                                } else {
                                    if (newTaskEntities.size() > 0) {
                                        newTaskCardview.setVisibility(View.VISIBLE);
                                        newTaskCardview.setClickable(true);
                                        newTaskCountTv.setText(String.valueOf(newTaskEntities.size()));
                                        nextTaskLayout.setVisibility(View.GONE);
                                    } else {
                                        newTaskCardview.setVisibility(View.GONE);
                                    }
                                }
                            }
                            //第一次进入 隐藏搜索框
                            if (isFirstTimeIntoPage) {
                                linearLayoutManager.scrollToPositionWithOffset(taskAdapter.getHeaderLayoutCount(), 0);
                                isFirstTimeIntoPage = false;
                            }
                        }
                    });
        } else if (stateType == 1 || stateType == 3) {//已完成/已删除的任务列表
            taskAdapter.setNewData(taskEntity.items);
            getNewTasksCount();
            if (linearLayoutManager.getStackFromEnd())
                linearLayoutManager.setStackFromEnd(false);
        }
    }

    /**
     * 将服务端返回的任务列表进行分组
     *
     * @param taskItemEntities
     */
    private List<TaskEntity.TaskItemEntity> groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        List<TaskEntity.TaskItemEntity> allTaskEntities = new ArrayList<>();//所有任务的分组
        List<TaskEntity.TaskItemEntity> todayTaskEntities = new ArrayList<>();//今天到期
        List<TaskEntity.TaskItemEntity> beAboutToTaskEntities = new ArrayList<>();//即将到期
        List<TaskEntity.TaskItemEntity> futureTaskEntities = new ArrayList<>();//未来
        List<TaskEntity.TaskItemEntity> noDueTaskEntities = new ArrayList<>();//为指定到期
        List<TaskEntity.TaskItemEntity> datedTaskEntities = new ArrayList<>();//已过期
        newTaskEntities.clear();//新任务列表清空

        for (TaskEntity.TaskItemEntity taskItemEntity : taskItemEntities) {
            if (taskItemEntity.dueTime > 0) {//今天到期
                long dueTimeDiff = DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime);
                if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis())) || dueTimeDiff < 0) {
                    todayTaskEntities.add(taskItemEntity);
                } else if (dueTimeDiff <= 3 && dueTimeDiff > 0) {//即将到期
                    beAboutToTaskEntities.add(taskItemEntity);
                } else if (dueTimeDiff > 3) {//未来
                    futureTaskEntities.add(taskItemEntity);
                } else {//已到期
                    datedTaskEntities.add(taskItemEntity);
                }
            } else {//未指定到期日
                noDueTaskEntities.add(taskItemEntity);
            }
            //新任务列表
            if (DateUtils.millis() - taskItemEntity.assignTime <= TimeUnit.DAYS.toMillis(1) && !TextUtils.isEmpty(getLoginUserId())) {
                if (taskItemEntity.createUser != null) {
                    if (!TextUtils.equals(taskItemEntity.createUser.userId, getLoginUserId())) {
                        if (!TextUtils.isEmpty(taskItemEntity.readUserIds)) {
                            if (!taskItemEntity.readUserIds.contains(getLoginUserId())) {
                                newTaskEntities.add(taskItemEntity);
                            }
                        } else {
                            newTaskEntities.add(taskItemEntity);
                        }
                    }
                }
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


    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) return;

        switch (event.action) {
            case TaskActionEvent.TASK_REFRESG_ACTION://刷新的动作
                if (refreshLayout != null)
                    refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_DELETE_ACTION://删除的动作
                if (event.entity == null) return;
                if (type == TYPE_ALL) {//所有任务列表
                    if (stateType == 0) {//未完成
                        //删除动作暂时重新请求接口
                        if (refreshLayout != null)
                            refreshLayout.startRefresh();
                    } else if (stateType == 1) {//已完成
                        if (taskAdapter != null) {
                            taskAdapter.removeItem(event.entity);
                        }
                    } else if (stateType == 3) {//已删除
                        if (taskAdapter != null) {
                            if (event.entity.valid) {
                                taskAdapter.removeItem(event.entity);
                            } else {
                                taskAdapter.addData(event.entity);
                            }
                        }
                    }
                } else if (type == TYPE_MY_ATTENTION) {
                    //我关注的因为有分组，所以暂时重新请求接口
                    if (refreshLayout != null)
                        refreshLayout.startRefresh();
                }
                break;
            case TaskActionEvent.TASK_ADD_ITEM_ACITON://添加的动作
                if (event.entity == null) return;
                if (type == TYPE_ALL) {
                    if (stateType == 1 || stateType == 3) {//如果是已完成／已删除，可以直接添加item
                        if (taskAdapter != null) {
                            taskAdapter.addData(event.entity);
                        }
                    } else {//未完成的，暂时走刷新逻辑
                        if (refreshLayout != null)
                            refreshLayout.startRefresh();
                    }
                } else if (type == TYPE_MY_ATTENTION) {//如果是我关注的，因为有分组，需要重新刷新列表
                    if (refreshLayout != null)
                        refreshLayout.startRefresh();
                }
                break;
        }

    }


    /**
     * 根据taskId来获取所在的位置
     *
     * @param taskId
     * @return
     */
    private int getItemPosition(String taskId) {
        if (taskAdapter == null || TextUtils.isEmpty(taskId)) return -1;
        for (int i = 0; i < taskAdapter.getData().size(); i++) {
            TaskEntity.TaskItemEntity itemEntity = taskAdapter.getData().get(i);
            if (itemEntity != null) {
                if (TextUtils.equals(taskId, itemEntity.id))
                    return i;
            }
        }
        return -1;
    }


    /**
     * 更新item（开始计时／结束计时）
     *
     * @param taskId   任务的id
     * @param isTiming true：开始计时；false：结束计时。
     */
    private void updateUnFinishChildTimeing(String taskId, boolean isTiming) {
        int itemPos = getItemPosition(taskId);
        if (itemPos >= 0) {
            TaskEntity.TaskItemEntity entity = taskAdapter.getItem(itemPos);
            if (entity != null) {
                if (lastEntity != null)
                    if (!TextUtils.equals(entity.id, lastEntity.id)) {//如果当前操作的任务并不是最后一次操作的任务，需要刷新列表了。
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

    /**
     * 删除成功回调
     *
     * @param itemEntity
     */
    @Override
    protected void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {
        if (refreshLayout != null)
            refreshLayout.startRefresh();
    }


    /**
     * 修改成功回调
     *
     * @param itemEntity
     */
    @Override
    protected void taskUpdateBack(@ChangeType int changeType, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        if (changeType == CHANGE_STATUS) {//如果是修改任务状态，并且是修改为完成/未完成状态，更新新任务数量
            updateNewTaskCount(itemEntity);
        }
        if (changeType == CHANGE_DUETIME) {//修改到期时间、提醒
            getData(true);
        } else {
            taskAdapter.updateItem(itemEntity);
        }
    }


    /**
     * 计时开始／结束的回调
     *
     * @param taskId 如果taskId为空，则说明是结束计时；如果不为空，则说明是开始计时。
     */
    @Override
    protected void taskTimerUpdateBack(String taskId) {
        if (!TextUtils.isEmpty(taskId)) {//添加计时
            TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
            if (updateItem != null) {
                updateUnFinishChildTimeing(updateItem.taskPkId, true);
            }
        } else {//结束计时
            if (lastEntity != null) {
                lastEntity.isTiming = false;
            }
            taskAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 获取新任务数量
     */
    private void getNewTasksCount() {
        getApi().newTasksCountQuery().enqueue(new SimpleCallBack<List<String>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                if (response.body().result != null) {
                    int totalCount = response.body().result.size();
                    if (totalCount > 0) {
                        newTaskCardview.setVisibility(View.VISIBLE);
                        newTaskCardview.setClickable(true);
                        nextTaskLayout.setVisibility(View.GONE);
                        newTaskCountTv.setText(String.valueOf(totalCount));
                    } else {
                        newTaskCardview.setVisibility(View.GONE);
                        nextTaskLayout.setVisibility(View.GONE);
                        newTaskEntities.clear();
                    }
                }
            }
        });
    }

    /**
     * 清空所有已删除的任务
     */
    public void clearAllDeletedTask() {
        if (stateType == 3) {
            if (taskAdapter == null) return;
            if (taskAdapter.getData().size() <= 0) return;
            List<String> ids = new ArrayList<>();
            for (TaskEntity.TaskItemEntity taskItemEntity : taskAdapter.getData()) {
                ids.add(taskItemEntity.id);
            }
            if (ids.size() > 0) {
                showLoadingDialog(null);
                getApi().clearDeletedTask(ids).enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        taskAdapter.getData().clear();
                        taskAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
            }
        }
    }

    /**
     * 恢复已删除任务（已删除任务列表会调用此接口）
     *
     * @param itemEntity
     */
    private void recoverTaskById(final TaskEntity.TaskItemEntity itemEntity) {
        if (itemEntity == null) return;
        showLoadingDialog(null);
        getApi().taskRecoverById(itemEntity.id).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                if (taskAdapter != null) {
                    taskAdapter.removeItem(itemEntity);
                }
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response) {
        taskAdapter.updateItem(requestEntity);
        if (response.body() != null) {
            TimerTimingActivity.launch(getActivity(), response.body());
        }
    }

    @Override
    protected void stopTimingBack(TaskEntity.TaskItemEntity requestEntity) {
        taskAdapter.updateItem(requestEntity);
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        TimerDetailActivity.launch(getActivity(), timer);
    }

    /**
     * 将所有新消息全部置为已读
     */
    public void onCheckNewTask(final List<String> ids) {
        if (newTaskEntities == null) return;
        getApi().checkAllNewTask(ids).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                if (ids != null) {
                    if (ids.size() == 1) {
                        if (newTaskEntities.size() > 0)
                            newTaskEntities.remove(0);
                        if (newTaskEntities.size() > 1) {
                            nextTaskLayout.setVisibility(View.VISIBLE);
                        }
                        newTaskCountTv.setText(String.valueOf(newTaskEntities.size()));
                        nextTaskTv.setText(String.format("下一个 (%s)", newTaskEntities.size()));
                    } else {
                        newTaskEntities.clear();
                    }
                    if (newTaskEntities.size() == 0) {
                        newTaskCardview.setVisibility(View.GONE);
                        nextTaskLayout.setVisibility(View.GONE);
                    }
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
     * 更新新任务数（完成任务的情况，要更新新任务数量）
     *
     * @param taskItemEntity
     */
    public void updateNewTaskCount(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
            if (newTaskEntities != null) {
                if (taskItemEntity.state) {
                    if (newTaskEntities.contains(taskItemEntity)) {
                        newTaskEntities.remove(taskItemEntity);
                    }
                } else {
                    if (!newTaskEntities.contains(taskItemEntity)) {
                        newTaskEntities.add(taskItemEntity);
                    }
                }
            }
        }
        getNewTasksCount();
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        if (taskItemEntity != null && taskItemEntity.type == 0)//任务才可以跳转，任务组不可以
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity item = taskAdapter.getItem(i);
        if (item != null && item.type == 0)//说明是任务
            showLongMenu(item);
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            case R.id.task_item_start_timming://计时的按钮
                if (itemEntity == null)
                    return;
                if (itemEntity.isTiming) {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    stopTiming(itemEntity);
                } else {
                    showLoadingDialog(null);
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    startTiming(itemEntity);
                }
                break;
            case R.id.task_item_checkbox://完成的按钮
                if (itemEntity == null)
                    return;
                if (stateType == 0 || stateType == 1) {//已完成／未完成列表
                    if (!itemEntity.state) {//完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(getContext(), "该任务由多人负责,确定完成?", itemEntity, SHOW_FINISH_DIALOG);
                            } else {
                                updateTaskState(itemEntity, true);
                            }
                        } else {
                            updateTaskState(itemEntity, true);
                        }
                    } else {//取消完成任务
                        updateTaskState(itemEntity, false);
                    }
                } else {//已删除列表
                    recoverTaskById(itemEntity);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
