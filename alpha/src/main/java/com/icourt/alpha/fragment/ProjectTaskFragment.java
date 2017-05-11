package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

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
    String projectId;
    List<TaskEntity> allTaskEntities;
    List<TaskEntity.TaskItemEntity> todayTaskEntities;//今天到期
    List<TaskEntity.TaskItemEntity> beAboutToTaskEntities;//即将到期
    List<TaskEntity.TaskItemEntity> futureTaskEntities;//未来
    List<TaskEntity.TaskItemEntity> noDueTaskEntities;//为指定到期

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
        projectId = getArguments().getString(KEY_PROJECT_ID);
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.task_list_null_text);
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

        allTaskEntities = new ArrayList<>();
        todayTaskEntities = new ArrayList<>();
        beAboutToTaskEntities = new ArrayList<>();
        futureTaskEntities = new ArrayList<>();
        noDueTaskEntities = new ArrayList<>();
    }

    @Override
    protected void getData(boolean isRefresh) {
        clearLists();
        getApi().projectQueryTaskList(projectId, 0, 0, 1, -1).enqueue(new SimpleCallBack<TaskEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                stopRefresh();
                getTaskGroupData(response.body().result);
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
    private void getTaskGroupData(TaskEntity taskEntity) {
        if (taskEntity != null) {
            if (taskEntity.items != null) {
                for (TaskEntity.TaskItemEntity taskItemEntity : taskEntity.items) {
                    if (taskItemEntity.dueTime > 0) {
                        if (TextUtils.equals(DateUtils.getTimeDateFormatYear(taskItemEntity.dueTime), DateUtils.getTimeDateFormatYear(DateUtils.millis()))) {
                            todayTaskEntities.add(taskItemEntity);
                        } else if (DateUtils.getDayDiff(new Date(taskItemEntity.dueTime), new Date(DateUtils.millis())) <= 3 && DateUtils.getDayDiff(new Date(taskItemEntity.dueTime), new Date(DateUtils.millis())) > 0) {
                            beAboutToTaskEntities.add(taskItemEntity);
                        } else if (DateUtils.getDayDiff(new Date(taskItemEntity.dueTime), new Date(DateUtils.millis())) > 3) {
                            futureTaskEntities.add(taskItemEntity);
                        }
                    } else {
                        noDueTaskEntities.add(taskItemEntity);
                    }
                }

                if (todayTaskEntities.size() > 0) {
                    TaskEntity todayTask = new TaskEntity();
                    todayTask.items = todayTaskEntities;
                    todayTask.groupName = "今天";
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

                taskAdapter.bindData(true, allTaskEntities);
            }
        }
    }

    private void clearLists() {
        if (todayTaskEntities != null)
            todayTaskEntities.clear();
        if (beAboutToTaskEntities != null)
            beAboutToTaskEntities.clear();
        if (futureTaskEntities != null)
            futureTaskEntities.clear();
        if (noDueTaskEntities != null)
            noDueTaskEntities.clear();
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
