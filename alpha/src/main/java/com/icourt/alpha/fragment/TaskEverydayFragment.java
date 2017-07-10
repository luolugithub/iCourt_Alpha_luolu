package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskSimpleAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.PageEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  每天的任务 fragment
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/10
 * version 2.0.0
 */


public class TaskEverydayFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    TaskSimpleAdapter taskSimpleAdapter;

    public static TaskEverydayFragment newInstance() {
        return new TaskEverydayFragment();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskSimpleAdapter = new TaskSimpleAdapter());
        EventBus.getDefault().register(this);
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        //2017-04-07 -2017-04-08
        getApi().getAllTask("2017-07-08", "2017-07-09", Arrays.asList(getLoginUserId()), 0)
                .enqueue(new SimpleCallBack<PageEntity<TaskEntity.TaskItemEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<PageEntity<TaskEntity.TaskItemEntity>>> call, Response<ResEntity<PageEntity<TaskEntity.TaskItemEntity>>> response) {
                        if (response.body().result == null) return;
                        taskSimpleAdapter.bindData(isRefresh, response.body().result.items);
                        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                        if (timer != null) {
                            TaskEntity.TaskItemEntity taskItemEntity = new TaskEntity.TaskItemEntity();
                            taskItemEntity.id = timer.taskPkId;
                            int indexOf = taskSimpleAdapter.getData().indexOf(taskItemEntity);
                            if (indexOf >= 0) {
                                taskItemEntity = taskSimpleAdapter.getItem(indexOf);
                                taskItemEntity.isTiming = true;
                                taskSimpleAdapter.updateItem(taskItemEntity);
                            }
                        }
                    }
                });
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
