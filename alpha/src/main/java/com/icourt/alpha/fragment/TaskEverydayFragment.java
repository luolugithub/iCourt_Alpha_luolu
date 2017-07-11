package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskSimpleAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  每天的任务 fragment
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/10
 * version 2.0.0
 */


public class TaskEverydayFragment extends BaseFragment {

    private static final String KEY_TASKS = "key_tasks";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    TaskSimpleAdapter taskSimpleAdapter;
    final ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList = new ArrayList<>();
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;

    public static TaskEverydayFragment newInstance(ArrayList<TaskEntity.TaskItemEntity> data) {
        TaskEverydayFragment fragment = new TaskEverydayFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_TASKS, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_every_day, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        taskItemEntityList.clear();
        ArrayList<TaskEntity.TaskItemEntity> taskEntity = (ArrayList<TaskEntity.TaskItemEntity>) getArguments().getSerializable(KEY_TASKS);
        if (taskEntity != null) {
            taskItemEntityList.addAll(taskEntity);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(taskSimpleAdapter = new TaskSimpleAdapter());
        taskSimpleAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (contentEmptyText != null) {
                    contentEmptyText.setVisibility(taskSimpleAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
        EventBus.getDefault().register(this);
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        taskSimpleAdapter.bindData(isRefresh, taskItemEntityList);
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
                List<TaskEntity.TaskItemEntity> data = taskSimpleAdapter.getData();
                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                if (timer != null) {
                    for (TaskEntity.TaskItemEntity taskItemEntity : data) {
                        taskItemEntity.isTiming = false;
                        if (TextUtils.equals(taskItemEntity.id, timer.taskPkId)) {
                            taskItemEntity.isTiming = true;
                        }
                    }
                    taskSimpleAdapter.notifyDataSetChanged();
                }
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                break;
            case TimingEvent.TIMING_STOP:
                List<TaskEntity.TaskItemEntity> data1 = taskSimpleAdapter.getData();
                for (TaskEntity.TaskItemEntity taskItemEntity : data1) {
                    taskItemEntity.isTiming = false;
                }
                taskSimpleAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
