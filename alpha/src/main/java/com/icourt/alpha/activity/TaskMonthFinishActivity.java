package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseRecyclerAdapter;
import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemChildClickListener;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.manager.TimerManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.umeng.analytics.MobclickAgent;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 本月已完成的任务列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class TaskMonthFinishActivity extends BaseTaskActivity implements OnItemClickListener, OnItemChildClickListener {


    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    TaskAdapter taskAdapter;
    TextView footerView;
    private int pageIndex = 1;
    /**
     * 最后一次操作的任务
     */
    TaskEntity.TaskItemEntity lastEntity;

    public static void launch(@NonNull Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, TaskMonthFinishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_finish_task_layout);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.task_month_finish_task);

        recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, R.string.empty_list_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(taskAdapter = new TaskAdapter());
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        addFooterView();

        taskAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (footerView != null) {
                    footerView.setText(getString(R.string.task_month_finish_task_statistics, String.valueOf(taskAdapter.getRealAdapterCount())));
                }
            }
        });

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshLayout) {
                getData(false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getData(true);
            }
        });

        getData(true);
    }

    private void addFooterView() {
        footerView = (TextView) getContext().getLayoutInflater().inflate(R.layout.footer_folder_document_num, (ViewGroup) recyclerView.getParent(), false);
        View view = taskAdapter.addFooter(footerView);
        footerView.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        if (isRefresh) {
            pageIndex = 1;
        }
        callEnqueue(
                getApi().taskListItemByTimeQuery(
                        getLoginUserId(),
                        1,
                        0,
                        "updateTime",
                        pageIndex,
                        ActionConstants.DEFAULT_PAGE_SIZE,
                        0,
                        DateUtils.getCurrentMonthFirstDay(),
                        DateUtils.getCurrentMonthLastDay()
                ),
                new SimpleCallBack2<ResEntity<TaskEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                        stopRefresh();
                        taskAdapter.bindData(isRefresh, response.body().result.items);
                        recyclerView.enableEmptyView(taskAdapter.getData());
                        enableLoadMore(response.body().result.items);
                        pageIndex += 1;
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
            refreshLayout.setEnableLoadmore(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        if (taskItemEntity != null && taskItemEntity.type == 0) {//item为任务的时候才可以点击
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
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
        //因为没有分组，所以可以直接操作item
        taskAdapter.removeItem(itemEntity);
    }

    @Override
    protected void taskUpdateBack(@ChangeType int actionType, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        taskAdapter.updateItem(itemEntity);
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
}
