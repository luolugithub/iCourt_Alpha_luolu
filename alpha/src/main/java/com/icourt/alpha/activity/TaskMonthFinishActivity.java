package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

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

public class TaskMonthFinishActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {


    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    TaskAdapter taskAdapter;

    private int pageIndex = 1;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
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
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_null_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(taskAdapter = new TaskAdapter());
//        taskAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, taskAdapter));
        taskAdapter.setOnItemClickListener(this);
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
                getData(false);
            }
        });
        refreshLayout.setAutoRefresh(true);
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
                        if (isRefresh) {
                            taskAdapter.setNewData(response.body().result.items);
                            enableEmptyView(response.body().result.items);
                        } else {
                            taskAdapter.addData(response.body().result.items);
                        }
//                        pageIndex += 1;
//                        enableLoadMore(response.body().result.items);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                        enableEmptyView(null);
                    }
                });
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 根据数据是否为空，判断是否显示空页面。
     *
     * @param result 用来判断是否要显示空页面的列表
     */
    private void enableEmptyView(List result) {
        if (refreshLayout != null) {
            if (result != null && result.size() > 0) {
                refreshLayout.enableEmptyView(false);
            } else {
                refreshLayout.enableEmptyView(true);
            }
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        if (taskItemEntity != null && taskItemEntity.type == 0) {//item为任务的时候才可以点击
            TaskDetailActivity.launch(view.getContext(), taskItemEntity.id);
        }
    }
}
