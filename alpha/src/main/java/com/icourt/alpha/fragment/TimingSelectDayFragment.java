package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 计时模块时间选择器的日选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectDayFragment extends BaseFragment {

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

    Unbinder unbinder;

    Date selectedDate;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy年MMM", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_day, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        initCompactCalendar();
    }

    private void initCompactCalendar() {
        compactcalendarView.setUseThreeLetterAbbreviation(false);
        compactcalendarView.setLocale(TimeZone.getDefault(), Locale.CHINESE);
        compactcalendarView.setUseThreeLetterAbbreviation(true);
        compactcalendarView.setDayColumnNames(new String[]{"一", "二", "三", "四", "五", "六", "日"});
        final Calendar instance = Calendar.getInstance();
        compactcalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
                selectedDate = date;
            }

            @Override
            public void onMonthScroll(Date date) {
                titleContent.setText(dateFormatForMonth.format(date));
//                instance.setTime(date);
//                if (instance.get(Calendar.YEAR) == 2017 && instance.get(Calendar.MONTH) > 8) {
//                    compactcalendarView.showPreviousMonth();
//                }
            }
        });
        compactcalendarView.shouldScrollMonth(false);
        titleContent.setText(dateFormatForMonth.format(System.currentTimeMillis()));

        compactcalendarView.removeAllEvents();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
