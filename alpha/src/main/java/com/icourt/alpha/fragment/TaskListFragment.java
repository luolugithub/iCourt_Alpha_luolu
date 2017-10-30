package com.icourt.alpha.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.asange.recyclerviewadapter.BaseRecyclerAdapter;
import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemChildClickListener;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.asange.recyclerviewadapter.OnItemLongClickListener;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.MainActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TaskSearchActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.constants.TaskConfig;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnTasksChangeListener;
import com.icourt.alpha.utils.ActionConstants;
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
 * Description 任务列表（未完成、已完成、已删除、我关注的）
 * Company Beijing icourt
 *
 * @author zhaodanyang  E-mail:zhaodanyang@icourt.cc
 *         date createTime：17/9/6
 *         version 2.0.0
 */

public class TaskListFragment extends BaseTaskFragment implements
        OnItemClickListener, OnItemChildClickListener, OnItemLongClickListener {

    /**
     * type的传参标识，type的参数的含义：0，全部；1，我关注的。
     */
    public static final String TYPE = "type";
    /**
     * stateType的传参标识，stateType参数含义：-1，全部任务；0，未完成；1，已完成；3，已删除。
     */
    public static final String STATE_TYPE = "stateType";

    /**
     * 全部
     */
    public static final int TYPE_ALL = 0;
    /**
     * 我关注的
     */
    public static final int TYPE_MY_ATTENTION = 2;

    //任务类型：全部、我关注的
    @IntDef({TYPE_ALL,
            TYPE_MY_ATTENTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskType {
    }

    @TaskType
    public static int convert2TaskType(int type) {
        switch (type) {
            case TYPE_ALL:
                return TYPE_ALL;
            case TYPE_MY_ATTENTION:
                return TYPE_MY_ATTENTION;
            default:
                return TYPE_ALL;
        }
    }

    Unbinder unbinder;
    @Nullable
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

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
    TaskAdapter taskAdapter;
    /**
     * 用来新任务的列表
     */
    List<TaskEntity.TaskItemEntity> newTaskEntities;
    /**
     * 任务列表类型：0，全部；1，我关注的。
     */
    @TaskType
    int type = TYPE_ALL;
    /**
     * 任务列表状态
     */
    @TaskConfig.TaskStateType
    int stateType = TaskConfig.TASK_STATETYPE_UNFINISH;
    /**
     * 任务列表变化的监听
     */
    OnTasksChangeListener onTasksChangeListener;
    /**
     * 是否是第一次进入界面，第一次进入界面，要隐藏搜索栏，滚动到第一个任务。
     */
    boolean isFirstTimeIntoPage = true;
    /**
     * 最后一个操作的任务实体
     */
    TaskEntity.TaskItemEntity lastEntity;
    Handler handler = new Handler();
    /**
     * 爷爷Fragment
     */
    TabTaskFragment tabTaskFragment = null;
    /**
     * 新任务的item是否更新的标识
     */
    boolean isUpdate = true;
    /**
     * 新任务的itemView
     */
    View childItemView;

    /**
     * 是否可以加载更多的标识（未完成不能加载更多，已完成、已删除可以加载更多）
     */
    boolean isCanLoadMore = false;

    /**
     * 加载到多少页
     */
    int pageIndex = 1;

    /**
     * 新任务提醒动画加载完成的监听
     */
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

    /**
     * 初始化Fragment的方法
     *
     * @param type      0，全部；1，我关注的。
     * @param stateType 全部任务：－1；未完成：0；已完成：1；已删除：3。
     * @return
     */
    public static TaskListFragment newInstance(@TaskType int type, @TaskConfig.TaskStateType int stateType) {
        TaskListFragment projectTaskFragment = new TaskListFragment();
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

    @Override
    protected void initView() {
        isEditTask = true;
        isAddTime = true;
        isDeleteTask = true;

        newTaskEntities = new ArrayList<>();

        tabTaskFragment = getParentTabTaskFragment();
        if (getArguments() != null) {
            type = convert2TaskType(getArguments().getInt(TYPE));
            stateType = TaskConfig.convert2TaskStateType(getArguments().getInt(STATE_TYPE));
            isCanLoadMore = (stateType != TaskConfig.TASK_STATETYPE_UNFINISH);
        }

        recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, getEmptyContentId(stateType));
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.getRecyclerView().setNestedScrollingEnabled(false);
        taskAdapter = new TaskAdapter();
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_search_comm, recyclerView.getRecyclerView(), false);
        View rlCommSearch = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rlCommSearch);
//        taskAdapter.addHeaderView(headerView);
        View view = taskAdapter.addHeader(headerView);
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        taskAdapter.setOnItemLongClickListener(this);

        refreshLayout.setEnableLoadmore(false);
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

    /**
     * 获取空文案
     *
     * @param stateType
     * @return
     */
    private int getEmptyContentId(int stateType) {
        if (type == TYPE_ALL) {
            switch (stateType) {
                case TaskConfig.TASK_STATETYPE_UNFINISH:
                    return R.string.empty_list_task_unfinished_task;
                case TaskConfig.TASK_STATETYPE_FINISHED:
                    return R.string.empty_list_task_finished_task;
                case TaskConfig.TASK_STATETYPE_DELETED:
                    return R.string.empty_list_task_deleted_task;
                default:
                    break;
            }
        } else if (type == TYPE_MY_ATTENTION) {
            return R.string.empty_list_task_follow_task;
        }
        return R.string.empty_list_task;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) {
            return;
        }
        switch (event.action) {
            //刷新的动作
            case TaskActionEvent.TASK_REFRESG_ACTION:
                getData(true);
                break;
            //删除的动作
            case TaskActionEvent.TASK_DELETE_ACTION:
                if (event.entity == null) {
                    return;
                }
                //所有任务列表
                if (type == TYPE_ALL) {
                    switch (stateType) {
                        //未完成
                        case TaskConfig.TASK_STATETYPE_UNFINISH:
                            //删除动作暂时重新请求接口
                            getData(true);
                            break;
                        //已完成
                        case TaskConfig.TASK_STATETYPE_FINISHED:
                            if (taskAdapter != null) {
                                taskAdapter.removeItem(event.entity);
                                recyclerView.enableEmptyView(taskAdapter.getData());
                            }
                            break;
                        //已删除
                        case TaskConfig.TASK_STATETYPE_DELETED:
                            if (taskAdapter != null) {
                                if (event.entity.valid) {
                                    //从已删除列表中彻底删除
                                    taskAdapter.removeItem(event.entity);
                                    recyclerView.enableEmptyView(taskAdapter.getData());
                                } else {//添加到已删除
                                    taskAdapter.addItem(event.entity);
                                }
                            }
                            break;
                        default:
                            break;

                    }
                } else if (type == TYPE_MY_ATTENTION) {
                    //我关注的因为有分组，所以暂时重新请求接口
                    getData(true);
                }
                break;
            //添加的动作
            case TaskActionEvent.TASK_ADD_ITEM_ACITON:
                if (event.entity == null) {
                    return;
                }
                if (type == TYPE_ALL) {
                    //如果是已完成／已删除，可以直接添加item
                    if (stateType == TaskConfig.TASK_STATETYPE_FINISHED || stateType == TaskConfig.TASK_STATETYPE_DELETED) {
                        if (taskAdapter != null) {
                            taskAdapter.addItem(event.entity);
                        }
                    } else {//未完成的，暂时走刷新逻辑
                        getData(true);
                    }
                } else if (type == TYPE_MY_ATTENTION) {
                    //如果是我关注的，因为有分组，需要重新刷新列表
                    getData(true);
                }
                break;
            default:

                break;
        }
    }

    @OnClick({R.id.new_task_cardview,
            R.id.next_task_close_iv,
            R.id.next_task_cardview})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                TaskSearchActivity.launchTask(getContext(), getLoginUserId(), type);
                break;
            case R.id.new_task_cardview:
                if (tabTaskFragment != null) {
                    if (tabTaskFragment.selectPosition != 0) {
                        tabTaskFragment.isShowCalendar = false;
                        tabTaskFragment.setFirstTabText(getString(R.string.task_unfinished), 0);
                        tabTaskFragment.updateListData(TaskConfig.TASK_STATETYPE_UNFINISH);
                        tabTaskFragment.isAwayScroll = true;
                    } else {
                        if (newTaskEntities != null) {
                            if (newTaskEntities.size() > 1) {
                                showNextTaskView(true);
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
            //下一个
            case R.id.next_task_cardview:
                updateNextTaskState();
                break;
            //关闭'下一个'弹框,全部修改为已读
            case R.id.next_task_close_iv:
                if (newTaskEntities != null) {
                    showLoadingDialog(null);
                    List<String> ids = new ArrayList<>();
                    for (TaskEntity.TaskItemEntity newTaskEntity : newTaskEntities) {
                        ids.add(newTaskEntity.id);
                    }
                    checkNewTaskRead(ids);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 更新下一个新任务的提醒
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
     * @param taskId 所要定位到的任务id
     */
    private void scrollToByPosition(final String taskId) {
        isUpdate = true;
        final int itemPosition = getItemPosition(taskId);
        final int itemPositionWithHeader = itemPosition + taskAdapter.getHeaderCount();

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
        checkNewTaskRead(ids);
        tabTaskFragment.isAwayScroll = false;
    }

    /**
     * item延迟高亮（新任务的时候使用）
     *
     * @param taskId        所要高亮显示的任务动画
     * @param childPosition 该任务所在的位置
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
     * 当新任务执行完高亮之后，恢复新任务的itemview背景颜色（新任务的时候使用）
     *
     * @param taskId        要修改背景色的任务id
     * @param childPosition 任务所在的位置
     */
    private void updateItemViewBackgrond(String taskId, int childPosition) {
        RecyclerView.ViewHolder viewHolder = recyclerView.getRecyclerView().findViewHolderForAdapterPosition(childPosition);
        if (viewHolder != null) {
            childItemView = viewHolder.itemView;
            if (childItemView != null) {
                isUpdate = false;
                startViewAnim(childItemView);
            }
        }
    }

    /**
     * itemview渐变动画（新任务的时候使用）
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
        if (targetFrgament != this) {
            return;
        }
        if (bundle != null) {
            stateType = TaskConfig.convert2TaskStateType(bundle.getInt(STATE_TYPE));
        }
        this.type = convert2TaskType(type);
        //刷新
        boolean isRefresh = targetFrgament == this && (type == 100 || type == TYPE_MY_ATTENTION)
                && recyclerView != null;
        if (isRefresh) {
            getData(true);
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        int attentionType = 0;
        String orderBy;
        if (type == TYPE_ALL) {
            attentionType = 0;
        } else if (type == TYPE_MY_ATTENTION) {
            attentionType = 1;
        }
        if (stateType == TaskConfig.TASK_STATETYPE_UNFINISH) {
            orderBy = "dueTime";
        } else {
            orderBy = "updateTime";
        }

        if (isRefresh) {
            pageIndex = 1;
        }

        int pageSize;
        if (isCanLoadMore) {//如果可以加载更多，说明需要分页
            pageSize = ActionConstants.DEFAULT_PAGE_SIZE;
        } else { //pageSize = -1表示加载全部
            pageSize = -1;
            pageIndex = 1;
        }

        callEnqueue(
                getApi().taskListQuery(0, getLoginUserId(), stateType, attentionType, orderBy, pageIndex, pageSize, 0),
                new SimpleCallBack<TaskEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                        stopRefresh();
                        getTaskGroupData(isRefresh, response.body().result);
                        pageIndex += 1;
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
     * @param isRefresh  是否是刷新
     * @param taskEntity
     */
    private void getTaskGroupData(boolean isRefresh, final TaskEntity taskEntity) {
        if (taskEntity == null || taskEntity.items == null) {
            return;
        }
        if (stateType == TaskConfig.TASK_STATETYPE_UNFINISH) { //未完成的任务需要分组
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
                            taskAdapter.bindData(true, searchPolymerizationEntities);
                            goFirstTask();
                            recyclerView.enableEmptyView(taskAdapter.getData());
                            if (tabTaskFragment != null) {
                                if (tabTaskFragment.isAwayScroll && stateType == TaskConfig.TASK_STATETYPE_UNFINISH) {
                                    if (newTaskEntities.size() > 1) {
                                        showNextTaskView(true);
                                    }
                                    nextTaskTv.setText(getString(R.string.task_next, String.valueOf(newTaskEntities.size())));
                                    updateNextTaskState();
                                } else {
                                    if (newTaskEntities.size() > 0) {
                                        newTaskCardview.setVisibility(View.VISIBLE);
                                        newTaskCardview.setClickable(true);
                                        newTaskCountTv.setText(String.valueOf(newTaskEntities.size()));
                                        showNextTaskView(false);
                                    } else {
                                        newTaskCardview.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
        } else if (stateType == TaskConfig.TASK_STATETYPE_FINISHED || stateType == TaskConfig.TASK_STATETYPE_DELETED) { //已完成/已删除的任务列表
            taskAdapter.bindData(isRefresh, taskEntity.items);
            if (isRefresh) {
                goFirstTask();
            }
            refreshLayout.setEnableLoadmore(enableLoadMore(taskEntity.items));
            recyclerView.enableEmptyView(taskAdapter.getData());
            getNewTasksCount();
            if (linearLayoutManager.getStackFromEnd()) {
                linearLayoutManager.setStackFromEnd(false);
            }
        }
    }

    private boolean enableLoadMore(List result) {
        if (isCanLoadMore && refreshLayout != null) {
            if (result != null && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将服务端返回的任务列表进行分组（按照到期时间分组）
     *
     * @param taskItemEntities 服务端返回的任务列表
     */
    private List<TaskEntity.TaskItemEntity> groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        //所有任务的分组
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
        //新任务列表清空
        newTaskEntities.clear();

        for (TaskEntity.TaskItemEntity taskItemEntity : taskItemEntities) {
            //今天到期
            if (taskItemEntity.dueTime > 0) {
                long dueTimeDiff = DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime);
                if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis())) || dueTimeDiff < 0) {
                    todayTaskEntities.add(taskItemEntity);
                } //即将到期
                else if (dueTimeDiff <= 3 && dueTimeDiff > 0) {
                    beAboutToTaskEntities.add(taskItemEntity);
                } //未来
                else if (dueTimeDiff > 3) {
                    futureTaskEntities.add(taskItemEntity);
                } //已到期
                else {
                    datedTaskEntities.add(taskItemEntity);
                }
            } //未指定到期日
            else {
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
        addToAllTaskEntities(getString(R.string.task_was_due), datedTaskEntities, allTaskEntities);
        addToAllTaskEntities(getString(R.string.task_today_due), todayTaskEntities, allTaskEntities);
        addToAllTaskEntities(getString(R.string.task_due_soon), beAboutToTaskEntities, allTaskEntities);
        addToAllTaskEntities(getString(R.string.task_future), futureTaskEntities, allTaskEntities);
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
        //0：任务；1：任务组。
        itemEntity.type = 1;
        allTaskEntities.add(itemEntity);
        allTaskEntities.addAll(list);
    }

    /**
     * 如果是第一次进入该界面，滚动到第一条任务，隐藏搜索框
     */
    private void goFirstTask() {
        if (isFirstTimeIntoPage && taskAdapter.getData().size() > 0) {
            linearLayoutManager.scrollToPositionWithOffset(taskAdapter.getHeaderCount(), 0);
            isFirstTimeIntoPage = false;
        }
    }

    /**
     * 停止下拉刷新／上拉加载
     */
    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    /**
     * 根据taskId来获取所在的位置
     *
     * @param taskId
     * @return
     */
    private int getItemPosition(String taskId) {
        if (taskAdapter == null || TextUtils.isEmpty(taskId)) {
            return -1;
        }
        for (int i = 0; i < taskAdapter.getData().size(); i++) {
            TaskEntity.TaskItemEntity itemEntity = taskAdapter.getData().get(i);
            if (itemEntity != null) {
                if (TextUtils.equals(taskId, itemEntity.id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 获取新任务数量
     */
    private void getNewTasksCount() {
        callEnqueue(
                getApi().newTasksCountQuery(),
                new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            int totalCount = response.body().result.size();
                            if (totalCount > 0) {
                                newTaskCardview.setVisibility(View.VISIBLE);
                                newTaskCardview.setClickable(true);
                                showNextTaskView(false);
                                newTaskCountTv.setText(String.valueOf(totalCount));
                            } else {
                                newTaskCardview.setVisibility(View.GONE);
                                showNextTaskView(false);
                                newTaskEntities.clear();
                            }
                        }
                    }
                }
        );
    }

    /**
     * 彻底清空所有已删除的任务（已删除的列表长按删除或者清空需要调用此方法）
     */
    public void clearAllDeletedTask() {
        //已删除的任务列表
        if (stateType == TaskConfig.TASK_STATETYPE_DELETED) {
            if (taskAdapter == null || taskAdapter.getData().size() <= 0) {
                return;
            }
            List<String> ids = new ArrayList<>();
            for (TaskEntity.TaskItemEntity taskItemEntity : taskAdapter.getData()) {
                ids.add(taskItemEntity.id);
            }
            if (ids.size() > 0) {
                showLoadingDialog(null);
                callEnqueue(
                        getApi().clearDeletedTask(ids),
                        new SimpleCallBack<JsonElement>() {
                            @Override
                            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                                dismissLoadingDialog();
                                taskAdapter.clearData();
                                recyclerView.enableEmptyView(taskAdapter.getData());
                            }

                            @Override
                            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        }
                );
            }
        }
    }

    /**
     * 恢复已删除任务（已删除任务列表会调用此接口）
     *
     * @param itemEntity
     */
    private void recoverTaskById(final TaskEntity.TaskItemEntity itemEntity) {
        if (itemEntity == null) {
            return;
        }
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskRecoverById(itemEntity.id),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {

                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                }
        );
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
     * 删除成功回调
     *
     * @param itemEntity
     */
    @Override
    protected void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {
        getData(true);
    }

    /**
     * 修改成功回调
     *
     * @param itemEntity
     */
    @Override
    protected void taskUpdateBack(@ChangeType int changeType, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        //如果是修改任务状态，并且是修改为完成/未完成状态，更新新任务数量
        if (changeType == CHANGE_STATUS) {
            updateNewTaskCount(itemEntity);
        }
        //修改到期时间、提醒
        if (changeType == CHANGE_DUETIME) {
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
    protected void taskTimingUpdateEvent(String taskId) {
        //添加计时
        if (!TextUtils.isEmpty(taskId)) {
            taskAdapter.notifyDataSetChanged();
        } //结束计时
        else {
            if (lastEntity != null) {
                lastEntity.isTiming = false;
            }
            taskAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 恢复已删除任务（已删除任务列表会调用此接口）
     *
     * @param itemEntity
     */
    @Override
    protected void taskRevertBack(TaskEntity.TaskItemEntity itemEntity) {
        if (taskAdapter != null) {
            taskAdapter.removeItem(itemEntity);
            recyclerView.enableEmptyView(taskAdapter.getData());
        }
    }

    /**
     * 将新任务全部置为已读
     *
     * @param ids 要置为已读的任务id的集合
     */
    public void checkNewTaskRead(final List<String> ids) {
        if (newTaskEntities == null) {
            return;
        }
        callEnqueue(
                getApi().checkAllNewTask(ids),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        if (ids != null) {
                            if (ids.size() == 1) {
                                if (newTaskEntities.size() > 0) {
                                    newTaskEntities.remove(0);
                                }
                                if (newTaskEntities.size() > 1) {
                                    showNextTaskView(true);
                                }
                                newTaskCountTv.setText(String.valueOf(newTaskEntities.size()));
                                nextTaskTv.setText(getString(R.string.task_next, String.valueOf(newTaskEntities.size())));
                            } else {
                                newTaskEntities.clear();
                            }
                            if (newTaskEntities.size() == 0) {
                                newTaskCardview.setVisibility(View.GONE);
                                showNextTaskView(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                }
        );
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

    /**
     * 显示下一个新任务的提醒按钮
     *
     * @param show
     */
    private void showNextTaskView(boolean show) {
        if (show) {
            nextTaskLayout.setVisibility(View.VISIBLE);
            FragmentActivity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).dismissOverTimingRemindDialogFragment(true);
            }
        } else {
            nextTaskLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        //任务才可以跳转，任务组不可以
        if (taskItemEntity != null && taskItemEntity.type == 0) {
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            //计时的按钮
            case R.id.task_item_start_timming:
                if (itemEntity == null) {
                    return;
                }
                if (itemEntity.isTiming) {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    stopTiming(itemEntity);
                } else {
                    showLoadingDialog(null);
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    startTiming(itemEntity);
                }
                break;
            //完成的按钮
            case R.id.task_item_checkbox:
                if (itemEntity == null) {
                    return;
                }
                //已完成／未完成列表
                if (stateType == TaskConfig.TASK_STATETYPE_UNFINISH || stateType == TaskConfig.TASK_STATETYPE_FINISHED) {
                    if (!itemEntity.state) {
                        //完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(getContext(), getString(R.string.task_is_confirm_complete_task), itemEntity, SHOW_FINISH_DIALOG);
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
                    if (itemEntity.attendeeUsers != null) {
                        if (itemEntity.attendeeUsers.size() > 1) {
                            showFinishDialog(getContext(), getString(R.string.task_is_confirm_revert_task), itemEntity, SHOW_RENEW_DIALOG);
                        } else {
                            showTwiceSureDialog(itemEntity, getString(R.string.task_is_revert), SHOW_RENEW_BUTTOM_SHEET);
                        }
                    } else {
                        showTwiceSureDialog(itemEntity, getString(R.string.task_is_revert_task), SHOW_RENEW_BUTTOM_SHEET);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        //已删除的任务列表不能进行长按操作
        if (stateType == TaskConfig.TASK_STATETYPE_DELETED) {
            return false;
        }
        TaskEntity.TaskItemEntity item = taskAdapter.getItem(i);
        //说明是任务
        if (item != null && item.type == 0) {
            showLongMenu(item);
        }
        return true;
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
