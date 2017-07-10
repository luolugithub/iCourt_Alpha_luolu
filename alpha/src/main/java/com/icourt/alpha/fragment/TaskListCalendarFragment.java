package com.icourt.alpha.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskSimpleAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.PageEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.widget.manager.TimerManager;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  任务周视图
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/7
 * version 1.0.0
 */

public class TaskListCalendarFragment extends BaseFragment {

    public static Fragment newInstance() {
        return new TaskListCalendarFragment();
    }


    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleForward)
    ImageView titleForward;
    @BindView(R.id.titleAction)
    TextView titleAction;
    @BindView(R.id.mcvCalendar)
    MonthCalendarView mcvCalendar;
    @BindView(R.id.rlMonthCalendar)
    RelativeLayout rlMonthCalendar;
    @BindView(R.id.wcvCalendar)
    WeekCalendarView wcvCalendar;
    @BindView(R.id.rvScheduleList)
    ScheduleRecyclerView rvScheduleList;
    @BindView(R.id.rlNoTask)
    RelativeLayout rlNoTask;
    @BindView(R.id.rlScheduleList)
    RelativeLayout rlScheduleList;
    @BindView(R.id.slSchedule)
    ScheduleLayout slSchedule;
    Unbinder unbinder;
    TaskSimpleAdapter taskSimpleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_list_canlendar, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        initCalendarDateView();
        EventBus.getDefault().register(this);
    }

    private void initCalendarDateView() {
        slSchedule.addTaskHints(Arrays.asList(8, 9, 10));
        slSchedule.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                updateTitle(year, month + 1, day);
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                updateTitle(year, month + 1, day);
            }
        });
        updateTitle(slSchedule.getCurrentSelectYear(),
                slSchedule.getCurrentSelectMonth() + 1,
                slSchedule.getCurrentSelectDay());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvScheduleList.setLayoutManager(manager);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        rvScheduleList.setItemAnimator(itemAnimator);
        rvScheduleList.setAdapter(taskSimpleAdapter = new TaskSimpleAdapter());
        taskSimpleAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (rlNoTask != null) {
                    rlNoTask.setVisibility(taskSimpleAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });

        getData(true);
    }


    private void updateTitle(int year, int month, int day) {
        titleContent.setText(String.format("%s年%s月", year, month));
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

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (mcvCalendar.getCurrentItem() > 0) {
                    mcvCalendar.setCurrentItem(mcvCalendar.getCurrentItem() - 1);
                }
                break;
            case R.id.titleForward:
                if (mcvCalendar.getCurrentItem() < mcvCalendar.getAdapter().getCount() - 1) {
                    mcvCalendar.setCurrentItem(mcvCalendar.getCurrentItem() + 1);
                }
                break;
            case R.id.titleAction:
                mcvCalendar.setTodayToView();
                break;
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
                updateTimingItem(event.timingId, event.timingSecond);
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

    /**
     * 更新计时item
     *
     * @param id
     * @param time
     */
    private void updateTimingItem(String id, long time) {

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
