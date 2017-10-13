package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingSelectEntity;
import com.icourt.alpha.utils.DateUtils;

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

    Unbinder unbinder;

    @BindView(R.id.wheelview_year)
    WheelView wheelviewYear;
    @BindView(R.id.wheelview_month)
    WheelView wheelviewMonth;

    TimeWheelAdapter yearAdapter;
    TimeWheelAdapter monthAdapter;

    private int selectedYearPosition;//选中的年所在的position
    private int selectedMonthPosition;//选中的月所在的position

    public static TimingSelectMonthFragment newInstance() {
        return new TimingSelectMonthFragment();
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

        wheelviewYear.setTextSize(20);
        wheelviewYear.setCyclic(false);
        wheelviewMonth.setTextSize(20);
        wheelviewMonth.setCyclic(false);

        List<String> yearList = new ArrayList<>();
        List<String> monthList = new ArrayList<>();

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        for (int i = 2015; i <= year; i++) {//2015年-当前年
            yearList.add(String.valueOf(i));
        }

        for (int i = 1; i <= 12; i++) {//1-12月
            monthList.add(String.valueOf(i));
        }

        wheelviewYear.setAdapter(yearAdapter = new TimeWheelAdapter(yearList));
        wheelviewMonth.setAdapter(monthAdapter = new TimeWheelAdapter(monthList));

        wheelviewYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                verifyDate(i, selectedMonthPosition);
            }
        });

        wheelviewMonth.setOnItemSelectedListener(new OnItemSelectedListener() {
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
        wheelviewYear.setCurrentItem(selectedYearPosition);
        wheelviewMonth.setCurrentItem(selectedMonthPosition);
    }


    /**
     * 校验选中的年、月是否是符合时间范围内的
     *
     * @param yearPosition
     * @param monthPosition
     */
    private void verifyDate(int yearPosition, int monthPosition) {
        if (wheelviewMonth == null) return;
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
            wheelviewMonth.setCurrentItem(selectedMonthPosition);
        } else {
            selectedMonthPosition = monthPosition;
        }
    }


    private class TimeWheelAdapter implements WheelAdapter<String> {
        List<String> timeList = new ArrayList<>();

        public TimeWheelAdapter(List<String> data) {
            this.timeList = data;
        }

        public List<String> getTimeList() {
            return timeList;
        }

        @Override
        public int getItemsCount() {
            return timeList.size();
        }

        @Override
        public String getItem(int i) {
            return timeList.get(i);
        }

        @Override
        public int indexOf(String o) {
            return timeList.indexOf(o);
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
        timingSelectEntity.startTimeStr = DateUtils.getyyyy_MM_dd(timingSelectEntity.startTimeMillis);
        timingSelectEntity.endTimeStr = DateUtils.getyyyy_MM_dd(timingSelectEntity.endTimeMillis);
        arguments.putSerializable(KEY_FRAGMENT_RESULT, timingSelectEntity);
        return arguments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
