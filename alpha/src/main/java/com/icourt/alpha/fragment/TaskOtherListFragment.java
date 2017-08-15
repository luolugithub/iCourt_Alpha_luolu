package com.icourt.alpha.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description 我分配的、选择查看对象（未完成｜已完成）
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class TaskOtherListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    public static final int MY_ALLOT_TYPE = 1;//我分配的
    public static final int SELECT_OTHER_TYPE = 2;//查看其他人

    public static final int UNFINISH_TYPE = 1;//未完成
    public static final int FINISH_TYPE = 2;//已完成

    public static final int TASK_TODAY_TYPE = 1;//今天任务
    public static final int TASK_BEABOUT_TYPE = 2;//即将到期任务
    public static final int TASK_FUTURE_TYPE = 3;//未来任务
    public static final int TASK_NODUE_TYPE = 4;//未指定到期任务
    public static final int TASK_DATED_TYPE = 5;//已过期任务

    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    List<TaskEntity> allTaskEntities = new ArrayList<>();
    List<TaskEntity.TaskItemEntity> todayTaskEntities = new ArrayList<>();//今天到期
    List<TaskEntity.TaskItemEntity> beAboutToTaskEntities = new ArrayList<>();//即将到期
    List<TaskEntity.TaskItemEntity> futureTaskEntities = new ArrayList<>();//未来
    List<TaskEntity.TaskItemEntity> noDueTaskEntities = new ArrayList<>();//为指定到期
    List<TaskEntity.TaskItemEntity> datedTaskEntities = new ArrayList<>();//已过期
    int startType, finishType;
    ArrayList<String> ids;
    private int pageIndex = 1;
    TaskAdapter taskAdapter;

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
        bundle.putInt("startType", startType);
        bundle.putInt("finishType", finishType);
        bundle.putStringArrayList("ids", ids);
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
        EventBus.getDefault().register(this);
        startType = getArguments().getInt("startType");
        finishType = getArguments().getInt("finishType");
        ids = getArguments().getStringArrayList("ids");
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                getData(false);
            }
        });
        refreshLayout.startRefresh();
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
        clearLists();
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
                    }
                });
    }

    /**
     * 分组
     *
     * @param taskItemEntities
     */
    private void groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {
        for (TaskEntity.TaskItemEntity taskItemEntity : taskItemEntities) {
            if (taskItemEntity.dueTime > 0) {
                if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis())) || DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) < 0) {
                    todayTaskEntities.add(taskItemEntity);
                } else if (DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) <= 3 && DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) > 0) {
                    beAboutToTaskEntities.add(taskItemEntity);
                } else if (DateUtils.getDayDiff(DateUtils.millis(), taskItemEntity.dueTime) > 3) {
                    futureTaskEntities.add(taskItemEntity);
                } else {
                    datedTaskEntities.add(taskItemEntity);
                }
            } else {
                noDueTaskEntities.add(taskItemEntity);
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
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, final View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                        TimerManager.getInstance().stopTimer();
                        ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        ((ImageView) view).setImageResource(R.drawable.orange_side_dot_bg);
                        MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                        TimerManager.getInstance().addTimer(getTimer(itemEntity), new Callback<TimeEntity.ItemEntity>() {
                            @Override
                            public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                if (response.body() != null) {
                                    TimerTimingActivity.launch(view.getContext(), response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {

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

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
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
            itemEntity.name = taskItemEntity.name;
            itemEntity.workDate = DateUtils.millis();
            itemEntity.createUserId = getLoginUserId();
            itemEntity.username = getLoginUserInfo().getName();
            itemEntity.startTime = DateUtils.millis();
            if (taskItemEntity.matter != null) {
                itemEntity.matterPkId = taskItemEntity.matter.id;
            }
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
                itemEntity.state = state;
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_UPDATE_DESC_ACTION, itemEntity));
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

        switch (event.action) {
            case TaskActionEvent.TASK_REFRESG_ACTION:
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_DELETE_ACTION:
                if (event.entity == null) return;
                removeChildItem(event.entity);
                if (taskAdapter != null)
                    taskAdapter.notifyDataSetChanged();
                break;
            case TaskActionEvent.TASK_ADD_ITEM_ACITON:
                refreshLayout.startRefresh();
                break;
            case TaskActionEvent.TASK_UPDATE_ITEM:
                if (event.entity == null) return;
                updateChildItem(event.entity);
                break;
        }
    }

    /**
     * 获取子view中的RecyclerView
     *
     * @param taskId
     * @return
     */
    private RecyclerView getChildRecyclerView(String taskId) {
        if (taskAdapter == null) return null;
        int parentPos = getParentPositon(taskId);
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

                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
                if (updateItem != null) {
                    updateUnFinishChildTimeing(updateItem.taskPkId, true);
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
     * 更新item
     *
     * @param taskId
     */
    private void updateUnFinishChildTimeing(String taskId, boolean isTiming) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
