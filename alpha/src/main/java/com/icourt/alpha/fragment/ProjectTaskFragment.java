package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description 项目下任务列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectTaskFragment extends BaseFragment {

    private static final String KEY_PROJECT_ID = "key_project_id";
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    TaskAdapter taskAdapter;

    public static ProjectTaskFragment newInstance(@NonNull String projectId) {
        ProjectTaskFragment projectTaskFragment = new ProjectTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
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
    protected void initView() {
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.null_project);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans5Divider(getContext(), true));
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
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(boolean isRefresh) {
        List<TaskEntity> taskEntitys = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            TaskEntity taskEntity = new TaskEntity();
            if (i == 0) {
                taskEntity.groupName = "今天";
                taskEntity.groupTaskCount = 3;
                List<TaskEntity.TaskItemEntity> itemEntitys = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
                    itemEntity.name = "任务设置时间点的交互方式修改";
                    itemEntity.taskGroupName = "Alpha任务计时 － 计时";
                    itemEntity.itemTaskCount = 6;
                    itemEntity.doneItemTaskCount = 3;
                    itemEntity.documentCount = 5;
                    itemEntity.commentCount = 11;
                    itemEntitys.add(itemEntity);
                }
                taskEntity.taskItemEntitys = itemEntitys;
            } else if (i == 1) {
                taskEntity.groupName = "即将到期";
                taskEntity.groupTaskCount = 4;
                List<TaskEntity.TaskItemEntity> itemEntitys = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
                    itemEntity.name = "任务设置时间点的交互方式修改";
                    itemEntity.taskGroupName = "Alpha任务计时 － 计时";
                    itemEntity.itemTaskCount = 6;
                    itemEntity.doneItemTaskCount = 3;
                    itemEntity.documentCount = 5;
                    itemEntity.commentCount = 11;
                    itemEntitys.add(itemEntity);
                }
                taskEntity.taskItemEntitys = itemEntitys;
            } else if (i == 2) {
                taskEntity.groupName = "未来";
                taskEntity.groupTaskCount = 5;
                List<TaskEntity.TaskItemEntity> itemEntitys = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
                    itemEntity.name = "任务设置时间点的交互方式修改";
                    itemEntity.taskGroupName = "Alpha任务计时 － 计时";
                    itemEntity.itemTaskCount = 6;
                    itemEntity.doneItemTaskCount = 3;
                    itemEntity.documentCount = 5;
                    itemEntity.commentCount = 11;
                    itemEntitys.add(itemEntity);
                }
                taskEntity.taskItemEntitys = itemEntitys;
            } else if (i == 3) {
                taskEntity.groupName = "未指定到期日";
                taskEntity.groupTaskCount = 3;
                List<TaskEntity.TaskItemEntity> itemEntitys = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
                    itemEntity.name = "任务设置时间点的交互方式修改";
                    itemEntity.taskGroupName = "Alpha任务计时 － 计时";
                    itemEntity.itemTaskCount = 6;
                    itemEntity.doneItemTaskCount = 3;
                    itemEntity.documentCount = 5;
                    itemEntity.commentCount = 11;
                    itemEntitys.add(itemEntity);
                }
                taskEntity.taskItemEntitys = itemEntitys;
            }
            taskEntitys.add(taskEntity);
        }
        taskAdapter.bindData(true, taskEntitys);
        stopRefresh();
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
