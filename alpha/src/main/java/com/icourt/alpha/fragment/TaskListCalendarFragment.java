package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    final Map<Long, ArrayList<TaskEntity.TaskItemEntity>> dailyTaskMap = new HashMap();
    FragmentPagerAdapter fragmentPagerAdapter;

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
        calculateDailyTasks();
        viewPager.setAdapter(fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                //Integer.MAX_VALUE/2 为今天

                Calendar clendar = Calendar.getInstance();
                clendar.set(Calendar.HOUR_OF_DAY, 0);
                clendar.set(Calendar.MINUTE, 0);
                clendar.set(Calendar.SECOND, 0);
                clendar.set(Calendar.MILLISECOND, 0);
                int centerPos = Integer.MAX_VALUE / 2;
                long key = clendar.getTimeInMillis() - (centerPos - position) * TimeUnit.DAYS.toMillis(1);
                return TaskEverydayFragment.newInstance(dailyTaskMap.get(key));
            }

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }
        });


        //今天 定位在中间
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2, false);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int centerPos = Integer.MAX_VALUE / 2;
            }
        });
        initCalendarDateView();
    }

    /**
     * 计算每日的任务
     */
    private void calculateDailyTasks() {
        for (TaskEntity.TaskItemEntity item : taskItemEntityList) {
            if (item == null) continue;
            if (item.dueTime <= 0) continue;
            Calendar clendar = Calendar.getInstance();
            clendar.setTimeInMillis(item.dueTime);
            clendar.set(Calendar.HOUR_OF_DAY, 0);
            clendar.set(Calendar.MINUTE, 0);
            clendar.set(Calendar.SECOND, 0);
            clendar.set(Calendar.MILLISECOND, 0);
            ArrayList<TaskEntity.TaskItemEntity> taskItemEntities = dailyTaskMap.get(clendar.getTimeInMillis());
            if (taskItemEntities == null) {
                taskItemEntities = new ArrayList<>();
            }
            if (!taskItemEntities.contains(item)) {
                taskItemEntities.add(item);
            }
            dailyTaskMap.put(clendar.getTimeInMillis(), taskItemEntities);
        }
    }

    /**
     * 滚动到某天的任务列表
     *
     * @param year
     * @param month
     * @param day
     */
    private void scrollToTaskPage(int year, int month, int day) {
        Calendar clendar = Calendar.getInstance();
        clendar.set(Calendar.HOUR_OF_DAY, 0);
        clendar.set(Calendar.MINUTE, 0);
        clendar.set(Calendar.SECOND, 0);
        clendar.set(Calendar.MILLISECOND, 0);


        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, day);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        targetCalendar.set(Calendar.SECOND, 0);
        targetCalendar.set(Calendar.MILLISECOND, 0);

        int distanceDay = (int) ((targetCalendar.getTimeInMillis() - clendar.getTimeInMillis()) / TimeUnit.DAYS.toMillis(1));
        int centerPos = Integer.MAX_VALUE / 2;
        if (distanceDay != 0) {
            int targetPos = centerPos + distanceDay;
            if (targetPos != viewPager.getCurrentItem() && targetPos >= 0) {
                viewPager.setCurrentItem(targetPos);
            }
        }
    }

    private void initCalendarDateView() {
        slSchedule.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                updateTitle(year, month + 1, day);
                showTopSnackBar("y:" + year + " m:" + month + " d:" + day);
                scrollToTaskPage(year, month, day);
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                updateTitle(year, month + 1, day);
                slSchedule.addTaskHints(
                        getMonthTaskHint(year, month));
                scrollToTaskPage(year, month, day);
            }
        });
        updateTitle(slSchedule.getCurrentSelectYear(),
                slSchedule.getCurrentSelectMonth() + 1,
                slSchedule.getCurrentSelectDay());

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
