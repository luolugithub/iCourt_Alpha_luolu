package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.icourt.alpha.R;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description   返回KEY_FRAGMENT_RESULT long时间戳
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/12
 * version 1.0.0
 */
public class DateSelectDialogFragment extends BaseDialogFragment {

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
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy年MMM", Locale.getDefault());
    Date selectedDate;

    public static DateSelectDialogFragment newInstance(@Nullable Calendar calendar) {
        DateSelectDialogFragment dateSelectDialogFragment = new DateSelectDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("calendar", calendar);
        dateSelectDialogFragment.setArguments(args);
        return dateSelectDialogFragment;
    }


    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_date_select, inflater, container, savedInstanceState);
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
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, 0, dp20 / 2, dp20);
                }
            }
        }

        hourWheelView.setAdapter(new TimeWheelAdapter(24));
        minuteWheelView.setAdapter(new TimeWheelAdapter(60));
        hourWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                log("------------i:" + i);
            }
        });
        initCompactCalendar();
        Calendar calendar = (Calendar) getArguments().getSerializable("calendar");
        if (calendar != null) {
            duetimeTv.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
            hourWheelView.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
            minuteWheelView.setCurrentItem(calendar.get(Calendar.MINUTE));

            titleContent.setText(dateFormatForMonth.format(calendar.getTimeInMillis()));
            compactcalendarView.setCurrentDate(calendar.getTime());
            compactcalendarView.invalidate();
        }

        //延迟显示 必须 否则默认值无效
        deadlineSelectLl.postDelayed(new Runnable() {
            @Override
            public void run() {
                deadlineSelectLl.setVisibility(View.GONE);
            }
        }, 200);
    }

    private void initCompactCalendar() {
        compactcalendarView.setUseThreeLetterAbbreviation(false);
        compactcalendarView.setLocale(TimeZone.getDefault(), Locale.CHINESE);
        compactcalendarView.setUseThreeLetterAbbreviation(true);
        compactcalendarView.setDayColumnNames(new String[]{"日", "一", "二", "三", "四", "五", "六"});
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

        /*loadEvents();
        compactcalendarView.invalidate();
        logEventsByMonth(compactcalendarView);*/
    }


    private void scrollToToday() {
        titleContent.setText(dateFormatForMonth.format(System.currentTimeMillis()));
        compactcalendarView.setCurrentDate(new Date());
        compactcalendarView.invalidate();
    }

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction,
            R.id.deadline_ll,
            R.id.clear_dutime_iv,
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
                    deadlineSelectLl.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.clear_dutime_iv:
                duetimeTv.setText("23:59");
                hourWheelView.setCurrentItem(23);
                minuteWheelView.setCurrentItem(59);
                break;
            case R.id.notice_ll:
                break;
            case R.id.repeat_notice_ll:
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KEY_FRAGMENT_RESULT, getSelectedMillis());
                    onFragmentCallBackListener.onFragmentCallBack(DateSelectDialogFragment.this, 0, bundle);
                }
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private long getSelectedMillis() {
        if (selectedDate == null) {
            selectedDate = new Date();
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(selectedDate);
        log("---------->in:" + hourWheelView.getCurrentItem());
        instance.set(Calendar.HOUR_OF_DAY, hourWheelView.getCurrentItem());
        instance.set(Calendar.MINUTE, minuteWheelView.getCurrentItem());
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
}
