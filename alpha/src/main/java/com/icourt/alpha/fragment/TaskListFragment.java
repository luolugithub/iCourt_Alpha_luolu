package com.icourt.alpha.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchProjectActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
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
import com.icourt.alpha.interfaces.OnTasksChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description 任务列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class TaskListFragment extends BaseTaskFragment implements
        TaskAdapter.OnShowFragmenDialogListener,
        TaskAdapter.OnUpdateNewTaskCountListener,
        OnFragmentCallBackListener,
        ProjectSelectDialogFragment.OnProjectTaskGroupSelectListener,
        BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener {

    public static final int TYPE_ALL = 0;//全部
    public static final int TYPE_MY_ATTENTION = 2;//我关注的

    private static final int SHOW_FINISH_DIALOG = 1;//完成任务提示对话框
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    LinearLayoutManager linearLayoutManager;
    TaskAdapter taskAdapter;
    TaskItemAdapter taskItemAdapter;
    TaskEntity.TaskItemEntity updateTaskItemEntity;
    List<TaskEntity> allTaskEntities;
    List<TaskEntity.TaskItemEntity> todayTaskEntities;//今天到期
    List<TaskEntity.TaskItemEntity> beAboutToTaskEntities;//即将到期
    List<TaskEntity.TaskItemEntity> futureTaskEntities;//未来
    List<TaskEntity.TaskItemEntity> noDueTaskEntities;//为指定到期
    List<TaskEntity.TaskItemEntity> newTaskEntities;//新任务
    List<TaskEntity.TaskItemEntity> datedTaskEntities;//已过期

    int type, stateType = 0;//全部任务：－1；已完成：1；未完成：0；已删除：3；
    HeaderFooterAdapter<TaskAdapter> headerFooterAdapter;
    HeaderFooterAdapter<TaskItemAdapter> headerFooterItemAdapter;
    OnTasksChangeListener onTasksChangeListener;
    boolean isFirstTimeIntoPage = true;
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
    TaskEntity.TaskItemEntity lastEntity;
    Handler handler = new Handler();
    View childItemView;
    boolean isUpdate = true;
    TabTaskFragment tabTaskFragment = null;

    public static TaskListFragment newInstance(int type, int stateType) {
        TaskListFragment projectTaskFragment = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("stateType", stateType);
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
        super.initView();
        tabTaskFragment = getParentTabTaskFragment();
        type = getArguments().getInt("type");
        stateType = getArguments().getInt("stateType");
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans5Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        if (stateType == 0) {//未完成
            headerFooterAdapter = new HeaderFooterAdapter<>(taskAdapter = new TaskAdapter());
            View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
            View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
            registerClick(rl_comm_search);
            headerFooterAdapter.addHeader(headerView);
            taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));
            recyclerView.setAdapter(headerFooterAdapter);
            taskAdapter.setDeleteTask(true);
            taskAdapter.setEditTask(true);
            taskAdapter.setAddTime(true);
            taskAdapter.setOnShowFragmenDialogListener(this);
            taskAdapter.setOnUpdateNewTaskCountListener(this);
        } else if (stateType == 1 || stateType == 3) {//已完成、已删除
            headerFooterItemAdapter = new HeaderFooterAdapter<>(taskItemAdapter = new TaskItemAdapter());

            View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
            View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
            registerClick(rl_comm_search);
            headerFooterItemAdapter.addHeader(headerView);
            taskItemAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskItemAdapter));
            recyclerView.setAdapter(headerFooterItemAdapter);
            taskItemAdapter.setOnItemClickListener(this);
            taskItemAdapter.setOnItemChildClickListener(this);
        }


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

        allTaskEntities = new ArrayList<>();
        todayTaskEntities = new ArrayList<>();
        beAboutToTaskEntities = new ArrayList<>();
        futureTaskEntities = new ArrayList<>();
        noDueTaskEntities = new ArrayList<>();
        newTaskEntities = new ArrayList<>();
        datedTaskEntities = new ArrayList<>();
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
     * 获取父fragment
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
     * 下一个
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
     * 滚动到指定位置
     *
     * @param taskId
     */
    private void scrollToByPosition(final String taskId) {
        isUpdate = true;
        final int parentPosition = getParentPositon(taskId) + headerFooterAdapter.getHeaderCount();
        final int childPosition = getChildPositon(taskId);
        final int offset = childPosition * 130 + 46;

        handler.removeCallbacksAndMessages(null);
        if (tabTaskFragment.isAwayScroll) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    linearLayoutManager.scrollToPositionWithOffset(parentPosition, DensityUtil.dip2px(getContext(), offset) * -1);
                    postUpdateItem(taskId, childPosition);
                }
            }, 100);
        } else {
            linearLayoutManager.scrollToPositionWithOffset(parentPosition, DensityUtil.dip2px(getContext(), offset) * -1);
            postUpdateItem(taskId, childPosition);
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
        RecyclerView childRecyclerview = getChildRecyclerView(taskId);
        if (childRecyclerview != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) childRecyclerview.getLayoutManager();
            if (layoutManager != null) {
                BaseRecyclerAdapter.ViewHolder viewHolder = (BaseRecyclerAdapter.ViewHolder) childRecyclerview.findViewHolderForAdapterPosition(childPosition);
                if (viewHolder != null) {
                    childItemView = viewHolder.itemView;
                    if (childItemView != null) {
                        isUpdate = false;
                        startViewAnim(childItemView);
                    }
                }
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


    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
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

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament != this) return;
        if (bundle != null) {
            stateType = bundle.getInt("stateType");
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
        clearLists();
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
        if (stateType == 0) {
            Observable.create(new ObservableOnSubscribe<List<TaskEntity>>() {
                @Override
                public void subscribe(ObservableEmitter<List<TaskEntity>> e) throws Exception {
                    if (e.isDisposed()) return;
                    groupingByTasks(taskEntity.items);
                    addDataToAllTask();
                    e.onNext(allTaskEntities);
                    e.onComplete();
                }
            }).compose(this.<List<TaskEntity>>bindToLifecycle())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<TaskEntity>>() {
                        @Override
                        public void accept(List<TaskEntity> searchPolymerizationEntities) throws Exception {
                            taskAdapter.bindData(true, allTaskEntities);
                            if (getParentFragment() != null) {
                                if (getParentFragment() instanceof TaskAllFragment) {
                                    if (getParentFragment().getParentFragment() != null) {
                                        if (getParentFragment().getParentFragment() instanceof TabTaskFragment) {
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
                                    }
                                }
                            }

                            //第一次进入 隐藏搜索框
                            if (isFirstTimeIntoPage) {
                                linearLayoutManager.scrollToPositionWithOffset(headerFooterAdapter.getHeaderCount(), 0);
                                isFirstTimeIntoPage = false;
                            }
                        }
                    });
        } else if (stateType == 1 || stateType == 3) {
            recyclerView.setAdapter(headerFooterItemAdapter);
            taskItemAdapter.bindData(true, taskEntity.items);
            getNewTasksCount();
            if (linearLayoutManager.getStackFromEnd())
                linearLayoutManager.setStackFromEnd(false);
        }
    }

    /**
     * 分组
     *
     * @param taskItemEntities
     */
    private void groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {

        for (TaskEntity.TaskItemEntity taskItemEntity : taskItemEntities) {
            if (taskItemEntity.dueTime > 0) {
                long dueTimeDiff = DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime);
                if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis())) || dueTimeDiff < 0) {
                    todayTaskEntities.add(taskItemEntity);
                } else if (dueTimeDiff <= 3 && dueTimeDiff > 0) {
                    beAboutToTaskEntities.add(taskItemEntity);
                } else if (dueTimeDiff > 3) {
                    futureTaskEntities.add(taskItemEntity);
                } else {
                    datedTaskEntities.add(taskItemEntity);
                }
            } else {
                noDueTaskEntities.add(taskItemEntity);
            }
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
    }

    /**
     * 分组内容添加到allTaskEntities
     */
    private void addDataToAllTask() {
        if (datedTaskEntities.size() > 0) {
            TaskEntity todayTask = new TaskEntity();
            todayTask.items = datedTaskEntities;
            todayTask.groupName = "已到期";
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
        if (newTaskEntities != null)
            newTaskEntities.clear();
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
            case TaskActionEvent.TASK_REFRESG_ACTION:
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_DELETE_ACTION:
                if (event.entity == null) return;
                if (type == TYPE_ALL) {
                    if (stateType == 0) {
                        removeChildItem(event.entity);
                    } else if (stateType == 1) {
                        if (taskItemAdapter != null) {
                            taskItemAdapter.removeItem(event.entity);
                            taskItemAdapter.notifyDataSetChanged();
                        }
                    } else if (stateType == 3) {
                        if (taskItemAdapter != null) {
                            if (event.entity.valid) {
                                taskItemAdapter.removeItem(event.entity);
                            } else {
                                taskItemAdapter.addItem(event.entity);
                            }
                            taskItemAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (type == TYPE_MY_ATTENTION) {
                    removeChildItem(event.entity);
                    if (taskAdapter != null)
                        taskAdapter.notifyDataSetChanged();
                }
                break;
            case TaskActionEvent.TASK_ADD_ITEM_ACITON:
                if (event.entity == null) return;
                if (type == TYPE_ALL) {
                    if (stateType == 1 || stateType == 3) {
                        if (taskItemAdapter != null) {
                            taskItemAdapter.addItem(event.entity);
                        }
                    }
                } else if (type == TYPE_MY_ATTENTION) {
                    refreshLayout.startRefresh();
                }
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
        RecyclerView recyclerView = getChildRecyclerView(itemEntity.id);
        if (recyclerView == null) return;
        if (recyclerView.getAdapter() != null) {
            if (recyclerView.getAdapter() instanceof TaskItemAdapter) {
                TaskItemAdapter adapter = (TaskItemAdapter) recyclerView.getAdapter();
                adapter.removeItem(itemEntity);
            }
        }
    }

    /**
     * 更新子item
     *
     * @param itemEntity
     */
    private void updateChildItem(TaskEntity.TaskItemEntity itemEntity) {
        if (itemEntity == null) return;
        RecyclerView recyclerView = getChildRecyclerView(itemEntity.id);
        if (recyclerView == null) return;
        if (recyclerView.getAdapter() != null) {
            if (recyclerView.getAdapter() instanceof TaskItemAdapter) {
                TaskItemAdapter adapter = (TaskItemAdapter) recyclerView.getAdapter();
                adapter.updateItem(itemEntity);
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
                updateTimming();
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:

                break;
            case TimingEvent.TIMING_STOP:
                if (lastEntity != null) {
                    lastEntity.isTiming = false;
                }
                if (taskAdapter != null)
                    taskAdapter.notifyDataSetChanged();
                if (taskItemAdapter != null)
                    taskItemAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void updateTimming() {
        TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
        if (updateItem != null) {
            if (taskAdapter != null) {
                updateChildTimeing(updateItem.taskPkId, true);
            } else {
                updateChildTimeingBy(updateItem.taskPkId, true);
            }
        }
    }

    /**
     * 获取item所在父容器position
     *
     * @param taskId
     * @return
     */
    private int getParentPositon(String taskId) {
        if (taskAdapter == null) return -1;
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

    /**
     * 获取子view中的RecyclerView
     *
     * @param taskId
     * @return
     */
    private RecyclerView getChildRecyclerView(String taskId) {
        if (taskAdapter == null) return null;
        if (headerFooterAdapter == null) return null;
        int parentPos = getParentPositon(taskId) + headerFooterAdapter.getHeaderCount();
        if (parentPos > 0) {
            int childPos = getChildPositon(taskId);
            if (childPos >= 0) {
                BaseArrayRecyclerAdapter.ViewHolder viewHolderForAdapterPosition = (BaseArrayRecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(parentPos);
                if (viewHolderForAdapterPosition != null) {
                    RecyclerView recyclerview = viewHolderForAdapterPosition.obtainView(R.id.parent_item_task_recyclerview);
                    return recyclerview;
                }
            }
        }
        return null;
    }

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
        } else {
            taskAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新item
     *
     * @param taskId
     */
    private void updateChildTimeingBy(String taskId, boolean isTiming) {
        try {
            int childPos = getChildPositonBy(taskId);
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

    /**
     * 获取item所在子容器position
     *
     * @param taskId
     * @return
     */
    private int getChildPositonBy(String taskId) {
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

    /**
     * 判断item是否显示在当前屏幕
     *
     * @param parentPos
     * @param childPos
     * @return
     */
    private boolean isShowInScreen(int parentPos, int childPos) {
        int laseParentVisible = -1;
        if (parentPos >= 0) {
            if (childPos >= 0) {
                if (linearLayoutManager != null) {
                    laseParentVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                }
                BaseArrayRecyclerAdapter.ViewHolder viewHolderForAdapterPosition = (BaseArrayRecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(parentPos);
                if (viewHolderForAdapterPosition != null) {
                    RecyclerView recyclerview = viewHolderForAdapterPosition.obtainView(R.id.parent_item_task_recyclerview);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerview.getLayoutManager();
                    if (layoutManager != null) {
                        int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                        int viewTop = layoutManager.getChildAt(childPos).getTop();
                        int screenheight = (int) DensityUtil.getHeightInPx(getContext());
                        return laseParentVisible <= parentPos && lastVisibleItem <= childPos && viewTop < screenheight;
                    }
                }
            }
        }
        return false;
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
            final TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(adapter.getRealPos(position));
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                        TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
                            @Override
                            public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                                itemEntity.isTiming = false;
                                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                                TimerDetailActivity.launch(view.getContext(), timer);
                            }

                            @Override
                            public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                                super.onFailure(call, t);
                                itemEntity.isTiming = true;
                            }
                        });
                        ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        showLoadingDialog(null);
                        MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                        TimerManager.getInstance().addTimer(getTimer(itemEntity), new Callback<TimeEntity.ItemEntity>() {
                            @Override
                            public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                dismissLoadingDialog();
                                ((ImageView) view).setImageResource(R.drawable.orange_side_dot_bg);
                                if (response.body() != null) {
                                    itemEntity.isTiming = true;
                                    TimerTimingActivity.launch(view.getContext(), response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                                dismissLoadingDialog();
                                itemEntity.isTiming = false;
                                ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                            }
                        });
                    }
                    break;
                case R.id.task_item_checkbox:
                    if (stateType == 0 || stateType == 1) {
                        CheckBox checkbox = (CheckBox) view;
                        itemEntity.state = !itemEntity.state;
                        if (checkbox.isChecked()) {//完成任务
                            if (itemEntity.attendeeUsers != null) {
                                if (itemEntity.attendeeUsers.size() > 1) {
                                    showDeleteDialog("该任务由多人负责,确定完成?", itemEntity, SHOW_FINISH_DIALOG, checkbox);
                                } else {
                                    updateTaskState(itemEntity);
                                }
                            } else {
                                updateTaskState(itemEntity);
                            }
                        } else {
                            updateTaskState(itemEntity);
                        }
                    } else {
                        recoverTaskById(itemEntity);
                    }
                    break;

            }
        }
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
                        if (type == SHOW_FINISH_DIALOG) {
                            updateTaskState(itemEntity);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    /**
     * 删除成功回调
     *
     * @param itemEntity
     */
    @Override
    protected void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {

    }


    /**
     * 修改成功回调
     *
     * @param itemEntity
     */
    @Override
    protected void taskUpdateBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {

    }


    /**
     * 计时回调
     *
     * @param taskId
     */
    @Override
    protected void taskTimerUpdateBack(String taskId) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

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
                    updateTaskProjectOrGroup(updateTaskItemEntity, null, null, null);
                }
            } else if (fragment instanceof DateSelectDialogFragment) {
                long millis = params.getLong(KEY_FRAGMENT_RESULT);
                updateTaskItemEntity.dueTime = millis;
                TaskReminderEntity taskReminderEntity = (TaskReminderEntity) params.getSerializable("taskReminder");
                updateTaskProjectOrGroup(updateTaskItemEntity, null, null, taskReminderEntity);
            }
        }
    }

    //切换项目之后，任务组id和负责人列表都需要清空
    @Override
    public void onProjectTaskGroupSelect(ProjectEntity projectEntity, TaskGroupEntity taskGroupEntity) {
        if (projectEntity != null) {
            if (updateTaskItemEntity != null) {
                if (updateTaskItemEntity.attendeeUsers != null) {
                    updateTaskItemEntity.attendeeUsers.clear();
                }
            }
        }
        if (taskGroupEntity == null) {
            taskGroupEntity = new TaskGroupEntity();
            taskGroupEntity.id = "";
        }
        updateTaskProjectOrGroup(updateTaskItemEntity, projectEntity, taskGroupEntity, null);
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
            if (taskItemAdapter == null) return;
            if (taskItemAdapter.getData() == null) return;
            if (taskItemAdapter.getData().size() <= 0) return;
            List<String> ids = new ArrayList<>();
            for (TaskEntity.TaskItemEntity taskItemEntity : taskItemAdapter.getData()) {
                ids.add(taskItemEntity.id);
            }
            if (ids.size() > 0) {
                showLoadingDialog(null);
                getApi().clearDeletedTask(ids).enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        taskItemAdapter.clearData();
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
     * 恢复已删除任务
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
                if (taskItemAdapter != null) {
                    taskItemAdapter.removeItem(itemEntity);
                    taskItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

//    /**
//     * 获取添加计时实体
//     *
//     * @return
//     */
//    @Override
//    protected TimeEntity.ItemEntity getTimer(TaskEntity.TaskItemEntity taskItemEntity) {
//        TimeEntity.ItemEntity itemEntity = new TimeEntity.ItemEntity();
//        if (taskItemEntity != null) {
//            itemEntity.taskPkId = taskItemEntity.id;
//            itemEntity.taskName = taskItemEntity.name;
//            itemEntity.name = taskItemEntity.name;
//            itemEntity.workDate = DateUtils.millis();
//            itemEntity.createUserId = getLoginUserId();
//            if (LoginInfoUtils.getLoginUserInfo() != null) {
//                itemEntity.username = LoginInfoUtils.getLoginUserInfo().getName();
//            }
//            itemEntity.startTime = DateUtils.millis();
//            if (taskItemEntity.matter != null) {
//                itemEntity.matterPkId = taskItemEntity.matter.id;
//                itemEntity.matterName = taskItemEntity.matter.name;
//            }
//        }
//        return itemEntity;
//    }

    @Override
    protected void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response) {

    }

    @Override
    protected void stopTimingBack(TaskEntity.TaskItemEntity requestEntity) {

    }

    /**
     * 我知道了
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
     * 更新新任务数量
     *
     * @param taskItemEntity
     */
    @Override
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
}
