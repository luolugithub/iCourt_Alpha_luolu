package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchProjectActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRefreshFragmentAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.GestureDetectorLayout;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleState;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    ArrayList<TaskEntity.TaskItemEntity> taskItemEntityList;
    BaseFragmentAdapter fragmentPagerAdapter;
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
        taskItemEntityList = (ArrayList<TaskEntity.TaskItemEntity>) getArguments().getSerializable(KEY_TASKS);
        if (taskItemEntityList == null) {
            taskItemEntityList = new ArrayList<>();
        }
        EventBus.getDefault().register(this);
        viewPager.setAdapter(fragmentPagerAdapter = new BaseRefreshFragmentAdapter(getChildFragmentManager()) {
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
                return TaskEverydayFragment.newInstance(key, getDayTask(key));
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
                boolean deleted = false;
                for (int i = taskItemEntityList.size() - 1; i >= 0; i--) {
                    TaskEntity.TaskItemEntity taskItemEntity = taskItemEntityList.get(i);
                    if (taskItemEntity.state) {
                        deleted = true;
                        taskItemEntityList.remove(i);
                    }
                }

                if (deleted) {
                    List<Integer> taskHint = slSchedule.getTaskHint();
                    if (taskHint != null && !taskHint.isEmpty()) {
                        slSchedule.removeTaskHints(taskHint);
                    }

                    //更新本月的小红点
                    slSchedule.addTaskHints(getMonthTaskHint(slSchedule.getCurrentSelectYear(), slSchedule.getCurrentSelectMonth()));
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
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityY) > DensityUtil.dip2px(getContext(), 50)) {
                    if (slSchedule.getmState() == ScheduleState.CLOSE) {
                        if (gestureDetectorLayout.getY() > -calendarTitleLl.getHeight()
                                && velocityY < 0) {
                            gestureDetectorLayout.setY(-calendarTitleLl.getHeight());
                        }
                    } else if (slSchedule.getmState() == ScheduleState.OPEN) {
                        if (gestureDetectorLayout.getY() < 0
                                && velocityY > 0) {
                            gestureDetectorLayout.setY(0);
                        }
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (Math.abs(distanceX) >= Math.abs(distanceY))
                    return super.onScroll(e1, e2, distanceX, distanceY);
                if (slSchedule.getmState() == ScheduleState.CLOSE
                        && distanceY > 0) {
                    //向上
                    if (Math.abs(gestureDetectorLayout.getY()) < calendarTitleLl.getHeight()) {
                        float distance = Math.max(-calendarTitleLl.getHeight(), gestureDetectorLayout.getY() - Math.abs(distanceY));
                        gestureDetectorLayout.setY(distance);
                    }
                } else if (slSchedule.getmState() == ScheduleState.OPEN
                        && distanceY < 0) {
                    //向下
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
        }
    };


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
        addTaskHints();

        slSchedule.invalidate();
    }

    /**
     * 添加任务提示
     */
    private void addTaskHints() {
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
    }


    private void updateTitle(int year, int month, int day) {
        titleContent.setText(String.format("%s年%s月", year, month));
    }


    /**
     * 动态计算
     *
     * @param targetDayTime
     * @return
     */
    private ArrayList<TaskEntity.TaskItemEntity> getDayTask(long targetDayTime) {
        ArrayList<TaskEntity.TaskItemEntity> taskItemEntities = new ArrayList<>();
        Calendar targetDay = Calendar.getInstance();
        targetDay.setTimeInMillis(targetDayTime);
        for (TaskEntity.TaskItemEntity item : taskItemEntityList) {
            if (item == null) continue;
            if (item.dueTime <= 0) continue;
            Calendar clendar = Calendar.getInstance();
            clendar.setTimeInMillis(item.dueTime);
            clendar.set(Calendar.HOUR_OF_DAY, 0);
            clendar.set(Calendar.MINUTE, 0);
            clendar.set(Calendar.SECOND, 0);
            clendar.set(Calendar.MILLISECOND, 0);

            if (targetDay.get(Calendar.YEAR) == clendar.get(Calendar.YEAR)
                    && targetDay.get(Calendar.DAY_OF_YEAR) == clendar.get(Calendar.DAY_OF_YEAR)) {
                taskItemEntities.add(item);
            }
        }
        return taskItemEntities;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        if (event.action == TaskActionEvent.TASK_UPDATE_ITEM) {
            if (event.entity == null) return;
            int indexOf = taskItemEntityList.indexOf(event.entity);
            if (indexOf >= 0) {
                TaskEntity.TaskItemEntity taskItemEntity = taskItemEntityList.get(indexOf);

                Calendar targetCalendar = Calendar.getInstance();
                targetCalendar.setTimeInMillis(taskItemEntity.dueTime);
                int targetYear = targetCalendar.get(Calendar.YEAR);
                int targetMonth = targetCalendar.get(Calendar.MONTH);


                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTimeInMillis(event.entity.dueTime);
                int eventYear = eventCalendar.get(Calendar.YEAR);
                int eventMonth = eventCalendar.get(Calendar.MONTH);

                //刷新本月
                if (slSchedule != null) {
                    if ((targetYear == slSchedule.getCurrentSelectYear() && targetMonth == slSchedule.getCurrentSelectMonth())
                            || (eventYear == slSchedule.getCurrentSelectYear() && eventMonth == slSchedule.getCurrentSelectMonth())) {
                        List<Integer> taskHint = slSchedule.getTaskHint();
                        if (taskHint != null && !taskHint.isEmpty()) {
                            slSchedule.removeTaskHints(taskHint);
                        }

                        //更新本月的小红点
                        slSchedule.addTaskHints(getMonthTaskHint(slSchedule.getCurrentSelectYear(), slSchedule.getCurrentSelectMonth()));
                    }
                }

                taskItemEntityList.set(indexOf, event.entity);

                //重新计算所有小红点
                addTaskHints();
            }

        }
    }

    @Override
    public synchronized void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament != this) return;
        if (bundle != null) {
            ArrayList<TaskEntity.TaskItemEntity> tasks = (ArrayList<TaskEntity.TaskItemEntity>) bundle.getSerializable(KEY_FRAGMENT_RESULT);
            if (tasks != null && taskItemEntityList != null) {
                taskItemEntityList = tasks;


                //1.更新子fragment
                fragmentPagerAdapter.notifyDataSetChanged();

                //2.更新本月份的小红点
                if (slSchedule != null) {
                    List<Integer> taskHint = slSchedule.getTaskHint();
                    if (taskHint != null && !taskHint.isEmpty()) {
                        slSchedule.removeTaskHints(taskHint);
                    }
                    slSchedule.addTaskHints(getMonthTaskHint(slSchedule.getCurrentSelectYear(), slSchedule.getCurrentSelectMonth()));
                }
                //3.将容器中的小红点再更新一次
                addTaskHints();

                if (slSchedule != null) {
                    slSchedule.invalidate();
                }
            }
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
