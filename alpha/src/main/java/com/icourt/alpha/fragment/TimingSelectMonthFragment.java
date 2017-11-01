package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.StringWheelAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 计时模块时间选择器的月选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectMonthFragment extends BaseFragment {

    private static final int TEXT_SIZE_WHEELVIEW = 20;

    private static final String KEY_SELECTED_DATE = "keySelectedDate";

    Unbinder unbinder;

    @BindView(R.id.year_wheelview)
    WheelView yearWheelview;
    @BindView(R.id.month_wheelview)
    WheelView monthWheelview;

    StringWheelAdapter yearAdapter;
    StringWheelAdapter monthAdapter;

    private int selectedYearPosition;//选中的年所在的position
    private int selectedMonthPosition;//选中的月所在的position

    public static TimingSelectMonthFragment newInstance(long selectedDate) {
        TimingSelectMonthFragment fragment = new TimingSelectMonthFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SELECTED_DATE, selectedDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_month, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {

        final Calendar calendar = Calendar.getInstance();
        if (getArguments() != null) {
            long timeMillis = getArguments().getLong(KEY_SELECTED_DATE, System.currentTimeMillis());
            calendar.setTimeInMillis(timeMillis);
        }

        yearWheelview.setTextSize(TEXT_SIZE_WHEELVIEW);
        yearWheelview.setCyclic(false);
        monthWheelview.setTextSize(TEXT_SIZE_WHEELVIEW);
        monthWheelview.setCyclic(false);

        List<String> yearList = new ArrayList<>();
        List<String> monthList = new ArrayList<>();

        int year = calendar.get(Calendar.YEAR);
        for (int i = TimerDateManager.START_YEAR; i <= year; i++) {//2015年-当前年
            yearList.add(String.valueOf(i));
        }

        for (int i = 1; i <= 12; i++) {//1-12月
            monthList.add(String.valueOf(i));
        }

        yearWheelview.setAdapter(yearAdapter = new StringWheelAdapter(yearList));
        monthWheelview.setAdapter(monthAdapter = new StringWheelAdapter(monthList));

        yearWheelview.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                verifyDate(i, selectedMonthPosition);
            }
        });

        monthWheelview.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                verifyDate(selectedYearPosition, i);
            }
        });

        for (int i = 0; i < yearList.size(); i++) {
            if (TextUtils.equals(yearList.get(i), String.valueOf(calendar.get(Calendar.YEAR)))) {
                selectedYearPosition = i;
            }
        }
        for (int i = 0; i < monthList.size(); i++) {
            if (TextUtils.equals(monthList.get(i), String.valueOf(calendar.get(Calendar.MONTH) + 1))) {//因为Calendar的月份是以0开始的，所以这里要加1。
                selectedMonthPosition = i;
            }
        }
        yearWheelview.setCurrentItem(selectedYearPosition);
        monthWheelview.setCurrentItem(selectedMonthPosition);
    }


    /**
     * 校验选中的年、月是否是符合时间范围内的
     *
     * @param yearPosition
     * @param monthPosition
     */
    private void verifyDate(int yearPosition, int monthPosition) {
        if (monthWheelview == null) return;
        selectedYearPosition = yearPosition;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        String monthItem = monthAdapter.getItem(monthPosition);
        if (Integer.valueOf(yearAdapter.getItem(selectedYearPosition)) == year && Integer.valueOf(monthItem) > (month + 1)) {//如果选中的年份、月份大于当前日期所在的年份、月份。
            List<String> monthList = monthAdapter.getTimeList();
            for (int i = 0; i < monthList.size(); i++) {
                if (Integer.valueOf(monthList.get(i)) == month + 1) {
                    selectedMonthPosition = i;
                    break;
                }
            }
            monthWheelview.setCurrentItem(selectedMonthPosition);
        } else {
            selectedMonthPosition = monthPosition;
        }
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle arguments = new Bundle();
        TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
        int currentYear = Integer.valueOf(yearAdapter.getItem(selectedYearPosition));
        int currentMonth = Integer.valueOf(monthAdapter.getItem(selectedMonthPosition));

        timingSelectEntity.startTimeMillis = DateUtils.getSupportBeginDayofMonth(currentYear, currentMonth).getTime();
        timingSelectEntity.endTimeMillis = DateUtils.getSupportEndDayofMonth(currentYear, currentMonth).getTime();
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
