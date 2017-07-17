package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchProjectActivity;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.GestureDetectorLayout;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleState;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;

import java.util.ArrayList;
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
    @BindView(R.id.rl_comm_search)
    RelativeLayout rlCommSearch;
    @BindView(R.id.header_comm_search_ll)
    LinearLayout headerCommSearchLl;
    @BindView(R.id.calendar_title_ll)
    LinearLayout calendarTitleLl;
    Unbinder unbinder;
    @BindView(R.id.gestureDetectorLayout)
    GestureDetectorLayout gestureDetectorLayout;
    private int MAXDAILYPAGE = 5000;
    private int dailyTaskPagePOS;


    public static Fragment newInstance(ArrayList<TaskEntity.TaskItemEntity> data) {
        TaskListCalendarFragment taskListCalendarFragment = new TaskListCalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_TASKS, data);
        taskListCalendarFragment.setArguments(args);
        return taskListCalendarFragment;
    }


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
        final ArrayList<TaskEntity.TaskItemEntity> taskEntity = (ArrayList<TaskEntity.TaskItemEntity>) getArguments().getSerializable(KEY_TASKS);
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
                int centerPos = MAXDAILYPAGE / 2;
                long key = clendar.getTimeInMillis() - (centerPos - position) * TimeUnit.DAYS.toMillis(1);
                return TaskEverydayFragment.newInstance(dailyTaskMap.get(key));
            }

            @Override
            public int getCount() {
                return MAXDAILYPAGE;
            }
        });


        //今天 定位在中间
        viewPager.setCurrentItem(MAXDAILYPAGE / 2, false);
        dailyTaskPagePOS = viewPager.getCurrentItem();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //移除每一天已经完成的任务
                Calendar clendar = Calendar.getInstance();
                clendar.set(Calendar.HOUR_OF_DAY, 0);
                clendar.set(Calendar.MINUTE, 0);
                clendar.set(Calendar.SECOND, 0);
                clendar.set(Calendar.MILLISECOND, 0);
                int centerPos = MAXDAILYPAGE / 2;
                long key = clendar.getTimeInMillis() - (centerPos - position) * TimeUnit.DAYS.toMillis(1);
                List<TaskEntity.TaskItemEntity> data = dailyTaskMap.get(key);
                if (data != null) {
                    for (int i = data.size() - 1; i >= 0; i--) {
                        TaskEntity.TaskItemEntity taskItemEntity = data.get(i);
                        if (taskItemEntity.state) {
                            data.remove(i);


                        }
                    }
                }

                //移除本天的小红点
                List<TaskEntity.TaskItemEntity> data2 = dailyTaskMap.get(key);
                if (data2 == null || data2.isEmpty()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(key);
                    slSchedule.removeTaskHint(calendar.get(Calendar.DAY_OF_MONTH));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.removeOnPageChangeListener(taskPageChangeListener);
        viewPager.addOnPageChangeListener(taskPageChangeListener);
        initCalendarDateView();
        initGestureDetector();
    }

    /**
     * 手势解析:处理滚动
     */
    private void initGestureDetector() {
        //第一次 默认隐藏
        gestureDetectorLayout.setY(-DensityUtil.dip2px(getContext(), 50));
        gestureDetectorLayout.setGestureDetector(new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (slSchedule.getmState() == ScheduleState.CLOSE
                        && distanceY > 0) {
                    if (Math.abs(gestureDetectorLayout.getY()) < calendarTitleLl.getHeight()) {
                        float distance = Math.max(-calendarTitleLl.getHeight(), gestureDetectorLayout.getY() - Math.abs(distanceY));
                        gestureDetectorLayout.setY(distance);
                    }
                } else if (slSchedule.getmState() == ScheduleState.OPEN
                        && distanceY < 0) {
                    if (gestureDetectorLayout.getY() < 0) {
                        float distance = Math.min(0, gestureDetectorLayout.getY() + Math.abs(distanceY));
                        gestureDetectorLayout.setY(distance);
                    }
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        }));
    }

    private ViewPager.SimpleOnPageChangeListener taskPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

        /**
         * 是否是向右滑动
         * @param pos
         * @return
         */
        private boolean isRight(int pos) {
            return pos > dailyTaskPagePOS;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            int maxDay = CalendarUtils.getMaxDay(slSchedule.getCurrentSelectYear(), slSchedule.getCurrentSelectMonth());
            int selectedDay = slSchedule.getCurrentSelectDay();

            //得到这个页面 对应的日期
            Calendar clendar = Calendar.getInstance();
            clendar.set(Calendar.HOUR_OF_DAY, 0);
            clendar.set(Calendar.MINUTE, 0);
            clendar.set(Calendar.SECOND, 0);
            clendar.set(Calendar.MILLISECOND, 0);
            int centerPos = MAXDAILYPAGE / 2;
            long key = clendar.getTimeInMillis() - (centerPos - position) * TimeUnit.DAYS.toMillis(1);
            clendar.setTimeInMillis(key);

            if (isRight(position)) {
                if (slSchedule.getCurrentSelectDay() < maxDay) {
                    mcvCalendar.onClickThisMonth(selectedDay + 1);
                } else {
                    mcvCalendar.setCurrentItem(mcvCalendar.getCurrentItem() + 1);
                    //自动定位到第一天
                    mcvCalendar.onClickThisMonth(1);
                }
            } else {
                if (slSchedule.getCurrentSelectDay() > 1) {
                    mcvCalendar.onClickThisMonth(selectedDay - 1);
                } else {
                    mcvCalendar.setCurrentItem(mcvCalendar.getCurrentItem() - 1);
                    int maxDay1 = CalendarUtils.getMaxDay(slSchedule.getCurrentSelectYear(), slSchedule.getCurrentSelectMonth());
                    //自动定位到最后一天
                    mcvCalendar.onClickThisMonth(maxDay1);
                }
            }
            dailyTaskPagePOS = position;

            //计算出对应的年月日
            int months = mcvCalendar.getAdapter().getCount();//月的总数

            //mcvCalendar.onClickThisMonth();
        }
    };

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
        int centerPos = MAXDAILYPAGE / 2;
        long currPageTime = clendar.getTimeInMillis() - (centerPos - viewPager.getCurrentItem()) * TimeUnit.DAYS.toMillis(1);
        clendar.setTimeInMillis(currPageTime);


        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.set(Calendar.YEAR, year);
        targetCalendar.set(Calendar.MONTH, month);
        targetCalendar.set(Calendar.DAY_OF_MONTH, day);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        targetCalendar.set(Calendar.SECOND, 0);
        targetCalendar.set(Calendar.MILLISECOND, 0);

        int distanceDay = (int) ((targetCalendar.getTimeInMillis() - clendar.getTimeInMillis()) / TimeUnit.DAYS.toMillis(1));
        if (distanceDay != 0) {
            int targetPos = viewPager.getCurrentItem() + distanceDay;
            if (targetPos >= 0) {
                viewPager.removeOnPageChangeListener(taskPageChangeListener);
                viewPager.setCurrentItem(targetPos);
                viewPager.addOnPageChangeListener(taskPageChangeListener);
                dailyTaskPagePOS = viewPager.getCurrentItem();
            }
        }
    }

    private void initCalendarDateView() {
        slSchedule.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                log("----------->onClickDate year:" + year + "  month:" + month + "   day:" + day);
                updateTitle(year, month + 1, day);
                scrollToTaskPage(year, month, day);
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                log("----------->onPageChange year:" + year + "  month:" + month + "   day:" + day);
                updateTitle(year, month + 1, day);
            }
        });
        updateTitle(slSchedule.getCurrentSelectYear(),
                slSchedule.getCurrentSelectMonth() + 1,
                slSchedule.getCurrentSelectDay());


        CalendarUtils.getInstance(getContext()).removeAllTaskHint();
        HashMap<Long, List<Integer>> allTaskHint = getAllTaskHint();
        for (Map.Entry<Long, List<Integer>> entry : allTaskHint.entrySet()) {
            Long key = entry.getKey();
            if (key == null) continue;
            List<Integer> value = entry.getValue();
            if (value == null) continue;
            Calendar clendar = Calendar.getInstance();
            clendar.setTimeInMillis(key);
            CalendarUtils.getInstance(getContext())
                    .addTaskHints(clendar.get(Calendar.YEAR), clendar.get(Calendar.MONTH), value);
        }
        slSchedule.invalidate();
    }


    private void updateTitle(int year, int month, int day) {
        titleContent.setText(String.format("%s年%s月", year, month));
    }


    /**
     * 获取所有的任务提示[天 0-31]
     *
     * @return
     */
    private HashMap<Long, List<Integer>> getAllTaskHint() {
        HashMap<Long, List<Integer>> allTaskHintMap = new HashMap<>();
        for (TaskEntity.TaskItemEntity item : taskItemEntityList) {
            if (item == null) continue;
            Calendar clendar = Calendar.getInstance();
            clendar.setTimeInMillis(item.dueTime);
            int day = clendar.get(Calendar.DAY_OF_MONTH);

            Calendar keyClendar = Calendar.getInstance();
            keyClendar.clear();
            keyClendar.set(Calendar.YEAR, clendar.get(Calendar.YEAR));
            keyClendar.set(Calendar.MONTH, clendar.get(Calendar.MONTH));

            long key = keyClendar.getTimeInMillis();
            List<Integer> integers = allTaskHintMap.get(keyClendar.getTimeInMillis());
            if (integers == null) {
                integers = new ArrayList<>();
            }
            if (!integers.contains(day)) {
                integers.add(day);
            }
            allTaskHintMap.put(key, integers);
        }
        return allTaskHintMap;
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
            R.id.titleAction,
            R.id.header_comm_search_ll})
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
            case R.id.header_comm_search_ll:
                SearchProjectActivity.launchTask(getContext(),
                        getLoginUserId(),
                        0,
                        SearchProjectActivity.SEARCH_TASK);
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
