package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseRecyclerAdapter;
import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemChildClickListener;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
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

public class TaskMonthFinishActivity extends BaseActivity implements OnItemClickListener, OnItemChildClickListener {


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

    private int pageIndex = 1;

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

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshLayout) {
                getData(true);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getData(false);
            }
        });

        refreshLayout.autoRefresh();
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

    }
}
