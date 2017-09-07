package com.icourt.alpha.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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
import com.icourt.alpha.activity.SearchTaskActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskItemAdapter555;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
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

public class TaskOtherListFragment555 extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    //实例化当前Fragment所要传递的参数标识
    private static final String TAG_START_TYPE = "startType";
    private static final String TAG_FINISH_TYPE = "finishType";
    private static final String TAG_IDS = "ids";

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

    List<TaskEntity.TaskItemEntity> allTaskEntities = new ArrayList<>();//展示所要用到的列表集合
    int startType, finishType;
    ArrayList<String> ids;
    private int pageIndex = 1;
    TaskItemAdapter555 taskAdapter;
    HeaderFooterAdapter<TaskItemAdapter555> headerFooterAdapter;

    TaskEntity.TaskItemEntity lastEntity;

    private ArrayMap<String, Integer> mArrayMap = new ArrayMap<>();//用来存储每个group有多少个数量。


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

    public static TaskOtherListFragment555 newInstance(@START_TYPE int startType, @IS_FINISH_TYPE int finishType, ArrayList<String> ids) {
        TaskOtherListFragment555 taskOtherListFragment = new TaskOtherListFragment555();
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
        EventBus.getDefault().register(this);
        startType = getArguments().getInt(TAG_START_TYPE);
        finishType = getArguments().getInt(TAG_FINISH_TYPE);
        ids = getArguments().getStringArrayList(TAG_IDS);

        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        headerFooterAdapter = new HeaderFooterAdapter<>(taskAdapter = new TaskItemAdapter555());
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

        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);
        taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(headerFooterAdapter);

//        recyclerView.addItemDecoration(new TastItemDecoration(getActivity(), new TastItemDecoration.DecorationCallBack() {
//            @Override
//            public String getGroupName(int position) {
//                if (position < 0 || position < headerFooterAdapter.getHeaderCount()) {
//                    //说明是不存在或者是header
//                    return null;
//                } else {
//                    //说明是child
//                    int realPos = taskAdapter555.getRealPos(position);
//                    TaskEntity.TaskItemEntity item = taskAdapter555.getItem(realPos);
//                    return item.groupName;
//                }
//            }
//
//            @Override
//            public int getCountByGroupName(String groupName) {
//                return mArrayMap.get(groupName);
//            }
//        }));


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
                SearchTaskActivity.launchTask(getContext(), getAssignTos(), 0);
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
        Observable.create(new ObservableOnSubscribe<List<TaskEntity.TaskItemEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TaskEntity.TaskItemEntity>> e) throws Exception {
                if (e.isDisposed()) return;
                groupingByTasks(taskEntity.items);
                e.onNext(allTaskEntities);
                e.onComplete();
            }
        }).compose(this.<List<TaskEntity.TaskItemEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TaskEntity.TaskItemEntity>>() {
                    @Override
                    public void accept(List<TaskEntity.TaskItemEntity> searchPolymerizationEntities) throws Exception {
                        taskAdapter.bindData(true, allTaskEntities);
                    }
                });
    }

    /**
     * 分组排序，并添加到显示的集合
     *
     * @param taskItemEntities
     */
    private void groupingByTasks(List<TaskEntity.TaskItemEntity> taskItemEntities) {
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
        addToAllTaskEntities(datedTaskEntities);
        addToAllTaskEntities(todayTaskEntities);
        addToAllTaskEntities(beAboutToTaskEntities);
        addToAllTaskEntities(futureTaskEntities);
        addToAllTaskEntities(noDueTaskEntities);
    }


    /**
     * 将分组的list添加到总的任务集合中去
     *
     * @param list
     */
    private void addToAllTaskEntities(List<TaskEntity.TaskItemEntity> list) {
        if (list == null || list.size() == 0)
            return;
        //创建一个群组标题的item
        TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
        itemEntity.groupName = list.get(0).groupName;
        itemEntity.groupTaskCount = list.size();
        itemEntity.type = 1;//0：普通；1：任务组。
        allTaskEntities.add(itemEntity);
        allTaskEntities.addAll(list);
        mArrayMap.put(list.get(0).groupName, list.size());
    }

    private void clearLists() {
        if (allTaskEntities != null)
            allTaskEntities.clear();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter555) {
            //因为有搜索的header，所以需要获取真实的position
            int realPos = taskAdapter.getRealPos(position);
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(realPos);
            if (taskItemEntity.type == 0) {//只有任务要跳转到任务详情页，任务组不需要跳转
                TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
            }
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, final View view, int position) {
        if (adapter instanceof TaskItemAdapter555) {
            //因为有搜索的header，所以需要获取真实的position
            int realPos = taskAdapter.getRealPos(position);
            final TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(realPos);
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                        TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
                            @Override
                            public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                                itemEntity.isTiming = false;
                                taskAdapter.updateItem(itemEntity);
                                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                                TimerDetailActivity.launch(view.getContext(), timer);
                            }

                            @Override
                            public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                                super.onFailure(call, t);
                                itemEntity.isTiming = true;
                                taskAdapter.updateItem(itemEntity);
                            }
                        });
                        ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                        TimerManager.getInstance().addTimer(getTimer(itemEntity), new Callback<TimeEntity.ItemEntity>() {
                            @Override
                            public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                dismissLoadingDialog();
                                if (response.body() != null) {
                                    itemEntity.isTiming = true;
                                    taskAdapter.updateItem(itemEntity);
                                    TimerTimingActivity.launch(view.getContext(), response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                                dismissLoadingDialog();
                                itemEntity.isTiming = false;
                                taskAdapter.updateItem(itemEntity);
                            }
                        });
                    }
                    break;
                case R.id.task_item_checkbox:
                    CheckBox checkbox = (CheckBox) view;
                    if (checkbox.isChecked()) {//完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(view.getContext(), "该任务由多人负责,确定完成?", itemEntity);
                            } else {
                                updateTask(itemEntity, true);
                            }
                        } else {
                            updateTask(itemEntity, true);
                        }
                    } else {
                        updateTask(itemEntity, false);
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
     */
    private void showFinishDialog(final Context context, String message, final TaskEntity.TaskItemEntity itemEntity) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        updateTask(itemEntity, true);
                        break;
                    case Dialog.BUTTON_NEGATIVE://取消
                        itemEntity.state = false;
                        taskAdapter.updateItem(itemEntity);
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
     * 修改任务完成状态
     *
     * @param itemEntity
     * @param state
     */
    private void updateTask(final TaskEntity.TaskItemEntity itemEntity, final boolean state) {
        showLoadingDialog(null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                itemEntity.state = state;
                taskAdapter.updateItem(itemEntity);
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_UPDATE_DESC_ACTION, itemEntity));
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                itemEntity.state = !state;
                taskAdapter.updateItem(itemEntity);
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
     * 计时事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_ADD:
                TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
                if (updateItem != null) {
                    updateUnFinishChildTimeing(updateItem.taskPkId, true);
                }
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:

                break;
            case TimingEvent.TIMING_STOP:
                if (lastEntity != null) {
                    lastEntity.isTiming = false;
                }
                taskAdapter.notifyDataSetChanged();
                break;
        }
    }

    private int getItemPosition(String taskId) {
        if (taskAdapter.getData() != null) {
            for (int i = 0; i < taskAdapter.getData().size(); i++) {
                TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getData().get(i);
                if (TextUtils.equals(taskItemEntity.id, taskId)) {
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
    private void updateUnFinishChildTimeing(String taskId, boolean isTiming) {
        int itemPos = getItemPosition(taskId);
        if (itemPos >= 0) {
//            int childPos = getChildPositon(taskId);
//            if (childPos >= 0) {
//                BaseArrayRecyclerAdapter.ViewHolder viewHolderForAdapterPosition = (BaseArrayRecyclerAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(parentPos);
//                if (viewHolderForAdapterPosition != null) {
//                    RecyclerView recyclerview = viewHolderForAdapterPosition.obtainView(R.id.parent_item_task_recyclerview);
//                    if (recyclerview != null) {
//                        TaskItemAdapter itemAdapter = (TaskItemAdapter) recyclerview.getAdapter();
//                        if (itemAdapter != null) {
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
//                        }
//                    }
//                }
//            }
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
