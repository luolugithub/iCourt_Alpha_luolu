package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.interfaces.OnDateSelectedListener;
import com.icourt.alpha.utils.DateUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 计时模块时间选择器的日选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectDayFragment extends BaseFragment {

    private static final String KEY_SELECTED_DATE = "keySelectedDate";

    Unbinder unbinder;

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

    OnDateSelectedListener onDateSelectedListener;

    Calendar selectedDate = Calendar.getInstance();

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());

    public static TimingSelectDayFragment newInstance(long selectedDate) {
        TimingSelectDayFragment fragment = new TimingSelectDayFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SELECTED_DATE, selectedDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_day, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnDateSelectedListener) {
            onDateSelectedListener = (OnDateSelectedListener) parentFragment;
        } else if (context instanceof OnDateSelectedListener) {
            onDateSelectedListener = (OnDateSelectedListener) context;
        }
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            long timeMillis = getArguments().getLong(KEY_SELECTED_DATE, System.currentTimeMillis());
            selectedDate.setTimeInMillis(timeMillis);
        }

        scrollToDate(selectedDate.getTimeInMillis());

        mcvCalendar.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                setYearMonthToView(year, month, day);
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                setYearMonthToView(year, month, day);
            }
        });
    }

    private void setYearMonthToView(int year, int month, int day) {
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, day);
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);
        verifyToday(selectedDate.getTimeInMillis());
        setTitleContent(selectedDate.getTimeInMillis());
        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(selectedDate.getTimeInMillis());
        }
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
                scrollToDate(System.currentTimeMillis());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 日历滚动到指定日期
     *
     * @param timeMillis
     */
    private void scrollToDate(long timeMillis) {
        selectedDate.clear();
        selectedDate.setTimeInMillis(DateUtils.getDayStartTime(timeMillis));
        verifyToday(selectedDate.getTimeInMillis());
        setTitleContent(selectedDate.getTimeInMillis());
        mcvCalendar.setDateToView(timeMillis);
        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(selectedDate.getTimeInMillis());
        }
    }

    /**
     * 判断日期是不是今天
     *
     * @param timeMillis
     */
    private void verifyToday(long timeMillis) {
        if (DateUtils.isToday(timeMillis)) {
            titleAction.setVisibility(View.GONE);
        } else {
            titleAction.setVisibility(View.VISIBLE);
        }
    }

    private void setTitleContent(long timeMillis) {
        titleContent.setText(dateFormatForMonth.format(timeMillis));
    }

    public long getSelectedTime() {
        return selectedDate.getTimeInMillis();
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle arguments = new Bundle();
        TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
        timingSelectEntity.startTimeMillis = selectedDate.getTimeInMillis();
        timingSelectEntity.endTimeMillis = DateUtils.getDayEndTime(selectedDate.getTimeInMillis());
        timingSelectEntity.startTimeStr = DateUtils.getFormatDate(timingSelectEntity.startTimeMillis, DateUtils.DATE_YYYYMMDD_STYLE1);
        timingSelectEntity.endTimeStr = DateUtils.getFormatDate(timingSelectEntity.endTimeMillis, DateUtils.DATE_YYYYMMDD_STYLE1);
        arguments.putSerializable(KEY_FRAGMENT_RESULT, timingSelectEntity);
        return arguments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
