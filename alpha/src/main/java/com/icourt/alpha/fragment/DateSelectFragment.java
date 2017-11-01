package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.TaskReminderUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择日期
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/8
 * version 2.0.0
 */

public class DateSelectFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleForward)
    ImageView titleForward;
    @BindView(R.id.titleAction)
    TextView titleAction;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView compactcalendarView;
    @BindView(R.id.deadline_ll)
    LinearLayout deadlineLl;
    @BindView(R.id.hour_wheelView)
    WheelView hourWheelView;
    @BindView(R.id.minute_wheelView)
    WheelView minuteWheelView;
    @BindView(R.id.deadline_select_ll)
    LinearLayout deadlineSelectLl;
    @BindView(R.id.notice_ll)
    LinearLayout noticeLl;
    @BindView(R.id.repeat_notice_ll)
    LinearLayout repeatNoticeLl;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.duetime_tv)
    TextView duetimeTv;
    @BindView(R.id.clear_dutime_iv)
    ImageView clearDutimeIv;
    @BindView(R.id.task_reminder_text)
    TextView taskReminderText;
    @BindView(R.id.add_reminder_layout)
    LinearLayout addReminderLayout;
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy年MMM", Locale.getDefault());
    Date selectedDate;

    Calendar selectedCalendar, reminderCalendar;
    TaskReminderEntity taskReminderEntity;
    String taskId;//任务id
    String taskReminderType;

    public static DateSelectFragment newInstance(@Nullable Calendar calendar, TaskReminderEntity taskReminderEntity, String taskId) {
        DateSelectFragment dateSelectFragment = new DateSelectFragment();
        Bundle args = new Bundle();
        args.putString("taskId", taskId);
        args.putSerializable("calendar", calendar);
        args.putSerializable("taskReminder", taskReminderEntity);
        dateSelectFragment.setArguments(args);
        return dateSelectFragment;
    }


    OnFragmentCallBackListener onFragmentCallBackListener;
    OnPageFragmentCallBack onPageFragmentCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (getParentFragment() instanceof OnPageFragmentCallBack) {
            onPageFragmentCallBack = (OnPageFragmentCallBack) getParentFragment();
        } else {
            try {
                onPageFragmentCallBack = (OnPageFragmentCallBack) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_date_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    private class TimeWheelAdapter implements WheelAdapter<Integer> {
        List<Integer> timeList = new ArrayList<>();

        public TimeWheelAdapter(int count) {
            for (int i = 0; i < count; i++) {
                timeList.add(i);
            }
        }

        @Override
        public int getItemsCount() {
            return timeList.size();
        }

        @Override
        public Integer getItem(int i) {
            return timeList.get(i);
        }

        @Override
        public int indexOf(Integer o) {
            return timeList.indexOf(o);
        }
    }

    @Override
    protected void initView() {

        hourWheelView.setAdapter(new TimeWheelAdapter(24));
        minuteWheelView.setAdapter(new TimeWheelAdapter(60));
        initCompactCalendar();
        selectedCalendar = (Calendar) getArguments().getSerializable("calendar");

        taskReminderEntity = (TaskReminderEntity) getArguments().getSerializable("taskReminder");
        taskId = getArguments().getString("taskId");

//        if (selectedCalendar == null) selectedCalendar = Calendar.getInstance();
        if (isUnSetDate()) {
            setNullDueTime();
        } else {
            clearDutimeIv.setVisibility(View.VISIBLE);
            if (selectedCalendar != null) {
                int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = selectedCalendar.get(Calendar.MINUTE);
                int second = selectedCalendar.get(Calendar.SECOND);
                if ((hour == 23 && minute == 59 && second == 59)) {
                    setNullDueTime();
                } else {
                    duetimeTv.setText(DateUtils.getFormatDate(selectedCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                    duetimeTv.setTextColor(SystemUtils.getColor(getContext(), R.color.alpha_font_color_black));
                }
            } else {
                setNullDueTime();
            }
        }
        if (TextUtils.isEmpty(taskId)) {
            visibiLayout();
        } else {
            getTaskDetail(taskId);
        }
        if (selectedCalendar == null) selectedCalendar = Calendar.getInstance();
        if (isUnSetDate()) {
            hourWheelView.setCurrentItem(10);
            minuteWheelView.setCurrentItem(0);
        } else {
            hourWheelView.setCurrentItem(selectedCalendar.get(Calendar.HOUR_OF_DAY));
            minuteWheelView.setCurrentItem(selectedCalendar.get(Calendar.MINUTE));
        }
        hourWheelView.setTextSize(16);
        hourWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                selectedCalendar.set(Calendar.HOUR_OF_DAY, i);
                selectedCalendar.set(Calendar.MILLISECOND, 0);
                if (duetimeTv != null)
                    duetimeTv.setText(DateUtils.getFormatDate(selectedCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                taskReminderType = TaskReminderEntity.PRECISE;
            }
        });
        minuteWheelView.setTextSize(16);
        minuteWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                selectedCalendar.set(Calendar.MINUTE, i);
                selectedCalendar.set(Calendar.MILLISECOND, 0);
                if (duetimeTv != null)
                    duetimeTv.setText(DateUtils.getFormatDate(selectedCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                taskReminderType = TaskReminderEntity.PRECISE;
            }
        });

        titleContent.setText(dateFormatForMonth.format(selectedCalendar.getTimeInMillis()));
        compactcalendarView.setCurrentDate(selectedCalendar.getTime());
        compactcalendarView.invalidate();

        deadlineSelectLl.setVisibility(View.GONE);

    }

    /**
     * 具体时间text为'未设置'
     */
    private void setNullDueTime() {
        duetimeTv.setText("");
        duetimeTv.setTextColor(SystemUtils.getColor(getContext(), R.color.alpha_font_color_gray));
        clearDutimeIv.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示隐藏提醒layout
     */
    private void visibiLayout() {
        if (taskReminderEntity != null) {
            if (taskReminderEntity.ruleTime != null || taskReminderEntity.customTime != null) {
                noticeLl.setVisibility(View.VISIBLE);
                addReminderLayout.setVisibility(View.GONE);
                setReminder(taskReminderEntity);
            } else {
                if (TextUtils.isEmpty(taskReminderEntity.taskReminderType)) {
                    taskReminderType = TaskReminderEntity.ALL_DAY;
                } else {
                    taskReminderType = taskReminderEntity.taskReminderType;
                }
                noticeLl.setVisibility(View.GONE);
                addReminderLayout.setVisibility(View.VISIBLE);
            }
        } else {
            noticeLl.setVisibility(View.GONE);
            addReminderLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否未设置时间
     *
     * @return
     */
    private boolean isUnSetDate() {
        if (selectedCalendar != null) {
            int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = selectedCalendar.get(Calendar.MINUTE);
            int second = selectedCalendar.get(Calendar.SECOND);
            return (hour == 23 && minute == 59 && second == 59);
        }
        return true;
    }

    /**
     * 归位未设置
     *
     * @return
     */
    private void setUnSetDate(int hour, int minute, int second) {
        try {
            if (selectedCalendar == null) {
                selectedCalendar = Calendar.getInstance();
            } else {
                reminderCalendar = (Calendar) selectedCalendar.clone();
            }
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
            selectedCalendar.set(Calendar.MINUTE, minute);
            selectedCalendar.set(Calendar.SECOND, second);

            minuteWheelView.setCurrentItem(selectedCalendar.get(Calendar.MINUTE));
            hourWheelView.setCurrentItem(selectedCalendar.get(Calendar.HOUR_OF_DAY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCompactCalendar() {
        compactcalendarView.setUseThreeLetterAbbreviation(false);
        compactcalendarView.setLocale(TimeZone.getDefault(), Locale.CHINESE);
        compactcalendarView.setUseThreeLetterAbbreviation(true);
        compactcalendarView.setDayColumnNames(new String[]{"一", "二", "三", "四", "五", "六", "日"});
        compactcalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
                selectedDate = date;
            }

            @Override
            public void onMonthScroll(Date date) {
                titleContent.setText(dateFormatForMonth.format(date));
            }
        });
        titleContent.setText(dateFormatForMonth.format(System.currentTimeMillis()));

        compactcalendarView.removeAllEvents();

    }


    /**
     * type =100 更新任务提醒数据
     *
     * @param targetFrgament
     * @param type
     * @param bundle
     * @see
     */
    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament != this) return;
        if (bundle != null && type == 100) {
            this.taskReminderEntity = ((TaskReminderEntity) bundle.getSerializable(KEY_FRAGMENT_RESULT));
            getArguments().putSerializable("taskReminder", taskReminderEntity);
            setReminder(taskReminderEntity);

        }
    }

    private void scrollToToday() {
        titleContent.setText(dateFormatForMonth.format(System.currentTimeMillis()));
        compactcalendarView.setCurrentDate(new Date());
        compactcalendarView.invalidate();
        selectedDate = new Date();
    }

    /**
     * 设置提醒数据
     *
     * @param taskReminderEntity
     */
    public void setReminder(TaskReminderEntity taskReminderEntity) {
        StringBuffer buffer = new StringBuffer();
        if (taskReminderEntity != null) {
            if (TextUtils.isEmpty(taskReminderEntity.taskReminderType)) {
                taskReminderType = TaskReminderEntity.ALL_DAY;
            } else {
                taskReminderType = taskReminderEntity.taskReminderType;
            }
            if (taskReminderEntity.ruleTime != null) {
                for (String s : taskReminderEntity.ruleTime) {
                    if (TextUtils.equals(TaskReminderEntity.ALL_DAY, taskReminderEntity.taskReminderType)) {
                        if (TaskReminderUtils.alldayMap.containsKey(s)) {
                            buffer.append(TaskReminderUtils.alldayMap.get(s) + ",");
                        }
                    } else if (TextUtils.equals(TaskReminderEntity.PRECISE, taskReminderEntity.taskReminderType)) {
                        if (TaskReminderUtils.preciseMap.containsKey(s)) {
                            buffer.append(TaskReminderUtils.preciseMap.get(s) + ",");
                        }
                    }
                }
            }
            if (taskReminderEntity.customTime != null) {
                for (TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity : taskReminderEntity.customTime) {
                    buffer.append(getCustReminderData(customTimeItemEntity) + ",");
                }
            }
        }
        if (taskReminderText == null) {
            taskReminderText = (TextView) findViewById(R.id.task_reminder_text);
        }
        if (buffer.length() > 0) {
            taskReminderText.setText(buffer.toString().substring(0, buffer.toString().length() - 1));
        } else {
            taskReminderText.setText("");
        }
    }

    /**
     * 获取自定义提醒str
     *
     * @param custReminderData
     * @return
     */
    private String getCustReminderData(TaskReminderEntity.CustomTimeItemEntity custReminderData) {
        if (custReminderData != null) {
            if (TaskReminderUtils.unitMap.containsKey(custReminderData.unit)) {
                if (TextUtils.equals(custReminderData.unit, "day")) {
                    if (TextUtils.equals(custReminderData.unitNumber, "当") || TextUtils.equals(custReminderData.unitNumber, "0")) {
                        return "当" + TaskReminderUtils.unitMap.get(custReminderData.unit) + custReminderData.point;
                    }
                }
                return custReminderData.unitNumber + TaskReminderUtils.unitMap.get(custReminderData.unit) + "前" + custReminderData.point;
            }
        }
        return "";
    }

    /**
     * 查询任务提醒
     *
     * @param itemEntity
     */
    private void getTaskReminder(final TaskEntity.TaskItemEntity itemEntity) {
        if (itemEntity == null) return;
        callEnqueue(
                getApi().taskReminderQuery(itemEntity.id),
                new SimpleCallBack<TaskReminderEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskReminderEntity>> call, Response<ResEntity<TaskReminderEntity>> response) {
                        dismissLoadingDialog();
                        taskReminderEntity = response.body().result;

                        if (addReminderLayout == null) return;
                        if (itemEntity.state) {
                            addReminderLayout.setVisibility(View.GONE);
                            noticeLl.setVisibility(View.GONE);
                        } else {
                            visibiLayout();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskReminderEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 获取任务详情
     *
     * @param taskId
     */
    private void getTaskDetail(final String taskId) {
        if (TextUtils.isEmpty(taskId)) return;
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskQueryDetailWithRight(taskId),
                new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        TaskEntity.TaskItemEntity itemEntity = response.body().result;
                        if (itemEntity != null) {

                            getTaskReminder(itemEntity);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction,
            R.id.deadline_ll,
            R.id.clear_dutime_iv,
            R.id.add_reminder_layout,
            R.id.notice_ll,
            R.id.repeat_notice_ll,
            R.id.bt_cancel,
            R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                compactcalendarView.showPreviousMonth();
                break;
            case R.id.titleForward:
                compactcalendarView.showNextMonth();
                break;
            case R.id.titleAction:
                scrollToToday();
                break;
            case R.id.deadline_ll:
                if (deadlineSelectLl.getVisibility() == View.VISIBLE) {
                    deadlineSelectLl.setVisibility(View.GONE);
                } else {
                    taskReminderType = TaskReminderEntity.PRECISE;
                    deadlineSelectLl.setVisibility(View.VISIBLE);
                    clearDutimeIv.setVisibility(View.VISIBLE);
                    if (isUnSetDate()) {
                        setUnSetDate(10, 0, 0);
                    }
                    duetimeTv.setText(DateUtils.getFormatDate(selectedCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                    duetimeTv.setTextColor(SystemUtils.getColor(getContext(), R.color.alpha_font_color_black));
                    convertCoustomReminder();
                }
                break;
            case R.id.clear_dutime_iv://1 到期日 转换全天任务
                setNullDueTime();
                setUnSetDate(23, 59, 59);
                deadlineSelectLl.setVisibility(View.GONE);
                taskReminderType = TaskReminderEntity.ALL_DAY;
                convertCoustomReminder();
                break;
            case R.id.add_reminder_layout://添加提醒
                addReminderLayout.setVisibility(View.GONE);
                noticeLl.setVisibility(View.VISIBLE);
                break;
            case R.id.notice_ll://点击提醒 切换
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    if (taskReminderEntity == null) {
                        taskReminderEntity = new TaskReminderEntity();
                        if (TextUtils.isEmpty(duetimeTv.getText())) {
                            taskReminderEntity.taskReminderType = TaskReminderEntity.ALL_DAY;
                        } else {
                            taskReminderEntity.taskReminderType = TaskReminderEntity.PRECISE;
                        }
                    } else {
                        if (TextUtils.isEmpty(taskReminderEntity.taskReminderType)) {
                            if (TextUtils.isEmpty(duetimeTv.getText())) {
                                taskReminderEntity.taskReminderType = TaskReminderEntity.ALL_DAY;
                            } else {
                                taskReminderEntity.taskReminderType = TaskReminderEntity.PRECISE;
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(duetimeTv.getText())) {
                        bundle.putLong(KEY_FRAGMENT_RESULT, getSelectedMillis());
                    }

                    if (TextUtils.isEmpty(taskReminderType)) {
                        taskReminderType = taskReminderEntity.taskReminderType;
                    }

                    if (reminderCalendar == null && selectedCalendar != null) {
                        reminderCalendar = (Calendar) selectedCalendar.clone();
                    }
                    if (reminderCalendar != null)
                        bundle.putLong(KEY_FRAGMENT_RESULT, reminderCalendar.getTimeInMillis());
                    bundle.putSerializable("taskReminder", taskReminderEntity);
                    bundle.putSerializable("taskReminderType", taskReminderType);
                    onFragmentCallBackListener.onFragmentCallBack(DateSelectFragment.this, DateSelectDialogFragment.SELECT_REMINDER, bundle);
                }
                break;
            case R.id.repeat_notice_ll:
                break;
            case R.id.bt_cancel:
                if (getParentFragment() instanceof DateSelectDialogFragment) {
                    ((DateSelectDialogFragment) getParentFragment()).dismiss();
                }
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KEY_FRAGMENT_RESULT, getSelectedMillis());
                    convertReminder();
                    bundle.putSerializable("taskReminder", taskReminderEntity);
                    onFragmentCallBackListener.onFragmentCallBack(DateSelectFragment.this, DateSelectDialogFragment.SELECT_DATE_FINISH, bundle);
                }
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    /**
     * 选择'当'天时，转换'当'为0
     */
    private void convertReminder() {
        if (taskReminderEntity == null) return;
        if (taskReminderEntity.customTime == null) return;
        if (taskReminderEntity.customTime.size() <= 0) return;
        for (TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity : taskReminderEntity.customTime) {
            if (TextUtils.equals(customTimeItemEntity.unit, "day")) {
                if (TextUtils.equals(customTimeItemEntity.unitNumber, "当")) {
                    customTimeItemEntity.unitNumber = "0";
                }
            }
        }
    }

    private long getSelectedMillis() {
        Calendar instance = Calendar.getInstance();
        if (selectedDate == null) {
            selectedDate = new Date();
            selectedDate.setTime(selectedCalendar.getTimeInMillis());
        }
        instance.setTime(selectedDate);
        instance.set(Calendar.HOUR_OF_DAY, selectedCalendar.get(Calendar.HOUR_OF_DAY));
        instance.set(Calendar.MINUTE, selectedCalendar.get(Calendar.MINUTE));
        instance.set(Calendar.SECOND, selectedCalendar.get(Calendar.SECOND));
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);
        addEvents(Calendar.AUGUST, -1);
    }


    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.MONTH, Calendar.AUGUST);
        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }
        log("---------->Events for Aug with simple date formatter: " + dates);
        log("---------->Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.getTime()));
    }

    private void addEvents(int month, int year) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();

            List<Event> events = getEvents(timeInMillis, i);

            compactcalendarView.addEvents(events);
        }
    }

    /**
     * 添加记录事件
     *
     * @param timeInMillis
     * @param day
     * @return
     */
    private List<Event> getEvents(long timeInMillis, int day) {
        return Arrays.asList(new Event(0xFFF6D9C0, timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 转换自定义
     */
    private void convertCoustomReminder() {
        if (taskReminderEntity == null)
            taskReminderEntity = new TaskReminderEntity();
        /**
         * ruleTime设置时间集合
         * 根据ruleTime --->
         */
        if (taskReminderEntity == null) return;
        if (!TextUtils.equals(taskReminderType, taskReminderEntity.taskReminderType)) {
            if (taskReminderEntity.ruleTime != null) {
                Iterator it = taskReminderEntity.ruleTime.iterator();
                while (it.hasNext()) {
                    String ruleTimeitem = (String) it.next();
                    if (taskReminderEntity.customTime == null) {
                        taskReminderEntity.customTime = new ArrayList<>();
                    }
                    addCoustomReminder(ruleTimeitem, taskReminderType);
                    it.remove();
                }
            }
            taskReminderEntity.taskReminderType = taskReminderType;
            setReminder(taskReminderEntity);
        }
    }

    /**
     * 添加自定义
     *
     * @param ruleTimeitem
     * @param taskReminderType
     */
    private void addCoustomReminder(String ruleTimeitem, String taskReminderType) {
        TaskReminderEntity.CustomTimeItemEntity entity = getCustomTime(ruleTimeitem, taskReminderType);
        taskReminderEntity.customTime.add(entity);
    }

    /**
     * 默认转自定义
     * <p>
     * put("0MB", "任务到期时");
     * put("5MB", "5分钟前");
     * put("10MB", "10分钟前");
     * put("30MB", "半小时前");
     * put("1HB", "1小时前");
     * put("2HB", "2小时前");
     * put("1DB", "一天前");
     * put("2DB", "两天前");
     * <p>
     * <p>
     * put("ODB", "当天（9:00)");
     * put("1DB", "一天前（9:00)");
     * put("2DB", "两天前（9:00)");
     * put("1WB", "一周前（9:00)");
     *
     * @param timeKey
     * @return
     */
    private TaskReminderEntity.CustomTimeItemEntity getCustomTime(String timeKey, String taskReminderType) {
        TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity = new TaskReminderEntity.CustomTimeItemEntity();
        if (TextUtils.equals(taskReminderType, TaskReminderEntity.ALL_DAY)) {
            if (TaskReminderUtils.preciseMap.containsKey(timeKey) && reminderCalendar != null) {
                if (TextUtils.equals(timeKey, "0MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getFormatDate(reminderCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "5MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getFormatDate(DateUtils.getMillByHourmin(reminderCalendar.get(Calendar.HOUR_OF_DAY), reminderCalendar.get(Calendar.MINUTE)) - (5 * 60 * 1000), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "10MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getFormatDate(DateUtils.getMillByHourmin(reminderCalendar.get(Calendar.HOUR_OF_DAY), reminderCalendar.get(Calendar.MINUTE)) - (10 * 60 * 1000), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "30MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getFormatDate(DateUtils.getMillByHourmin(reminderCalendar.get(Calendar.HOUR_OF_DAY), reminderCalendar.get(Calendar.MINUTE)) - (30 * 60 * 1000), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "1HB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getFormatDate(DateUtils.getMillByHourmin(reminderCalendar.get(Calendar.HOUR_OF_DAY), reminderCalendar.get(Calendar.MINUTE)) - (60 * 60 * 1000), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "2HB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getFormatDate(DateUtils.getMillByHourmin(reminderCalendar.get(Calendar.HOUR_OF_DAY), reminderCalendar.get(Calendar.MINUTE)) - (2 * 60 * 60 * 1000), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "1DB")) {
                    setAllDayReminder(customTimeItemEntity, "1", "day", DateUtils.getFormatDate(reminderCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                } else if (TextUtils.equals(timeKey, "2DB")) {
                    setAllDayReminder(customTimeItemEntity, "2", "day", DateUtils.getFormatDate(reminderCalendar.getTimeInMillis(), DateUtils.DATE_HHMM_STYLE1));
                }
            }
        } else if (TextUtils.equals(taskReminderType, TaskReminderEntity.PRECISE)) {
            if (TaskReminderUtils.alldayMap.containsKey(timeKey)) {
                if (TextUtils.equals(timeKey, "0DB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", "09:00");
                }
                if (TextUtils.equals(timeKey, "1DB")) {
                    setAllDayReminder(customTimeItemEntity, "1", "day", "09:00");
                }
                if (TextUtils.equals(timeKey, "2DB")) {
                    setAllDayReminder(customTimeItemEntity, "2", "day", "09:00");
                }
                if (TextUtils.equals(timeKey, "1WB")) {
                    setAllDayReminder(customTimeItemEntity, "7", "day", "09:00");
                }
            }
        }
        return customTimeItemEntity;
    }

    private void setAllDayReminder(TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity, String unitNumber, String unit, String point) {
        customTimeItemEntity.unitNumber = unitNumber;
        customTimeItemEntity.unit = unit;
        customTimeItemEntity.point = point;
    }

}
