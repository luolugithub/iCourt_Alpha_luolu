package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  任务周视图
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/7
 * version 1.0.0
 */

public class TaskListCalendarFragment extends BaseFragment {


    private static final String KEY_TASKS = "key_tasks";
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
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.rlScheduleList)
    RelativeLayout rlScheduleList;
    @BindView(R.id.slSchedule)
    ScheduleLayout slSchedule;
    final ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList = new ArrayList<>();

    public static Fragment newInstance(ArrayList<TaskEntity.TaskItemEntity> data) {
        TaskListCalendarFragment taskListCalendarFragment = new TaskListCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_TASKS, data);
        taskListCalendarFragment.setArguments(args);
        return taskListCalendarFragment;
    }


    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_list_canlendar, inflater, container, savedInstanceState);
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

        initCalendarDateView();
    }

    private void initCalendarDateView() {

        slSchedule.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                updateTitle(year, month + 1, day);
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                updateTitle(year, month + 1, day);
                slSchedule.addTaskHints(
                        getMonthTaskHint(year, month));
            }
        });
        updateTitle(slSchedule.getCurrentSelectYear(),
                slSchedule.getCurrentSelectMonth() + 1,
                slSchedule.getCurrentSelectDay());
        BaseFragmentAdapter fragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(fragmentAdapter);
        fragmentAdapter.bindData(true, Arrays.asList(
                TaskEverydayFragment.newInstance(),
                TaskEverydayFragment.newInstance(),
                TaskEverydayFragment.newInstance(),
                TaskEverydayFragment.newInstance()));

        slSchedule.addTaskHints(
                getMonthTaskHint(slSchedule.getCurrentSelectYear(), slSchedule.getCurrentSelectMonth()));
    }


    private void updateTitle(int year, int month, int day) {
        titleContent.setText(String.format("%s年%s月", year, month));
    }

    /**
     * 获取某一月份的任务小红点
     *
     * @param year
     * @param month
     */
    private List<Integer> getMonthTaskHint(int year, int month) {
        List<Integer> data = new ArrayList<>();
        for (TaskEntity.TaskItemEntity item : taskItemEntityList) {
            if (item == null) continue;
            if (item.dueTime <= 0) continue;
            Calendar clendar = Calendar.getInstance();
            clendar.setTimeInMillis(item.dueTime);
            if (clendar.get(Calendar.YEAR) == year
                    && clendar.get(Calendar.MONTH) == month) {
                int dayOfMonth = clendar.get(Calendar.DAY_OF_MONTH);
                if (!data.contains(dayOfMonth)) {
                    data.add(dayOfMonth);
                }
            }
        }
        return data;
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
