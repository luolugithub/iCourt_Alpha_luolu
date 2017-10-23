package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
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
 * 计时模块时间选择器的年选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectYearFragment extends BaseFragment {

    private static final String KEY_SELECTED_DATE = "keySelectedDate";

    Unbinder unbinder;

    @BindView(R.id.wheelview_year)
    WheelView wheelviewYear;
    TimeWheelAdapter yearAdapter;

    public static TimingSelectYearFragment newInstance(long selectedDate) {
        TimingSelectYearFragment fragment = new TimingSelectYearFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_SELECTED_DATE, selectedDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_year, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        int selectedYear = 2015;
        Calendar calendar = Calendar.getInstance();
        if (getArguments() != null) {
            long timeMillis = getArguments().getLong(KEY_SELECTED_DATE, System.currentTimeMillis());
            calendar.setTimeInMillis(timeMillis);
            selectedYear = calendar.get(Calendar.YEAR);
        }

        wheelviewYear.setCyclic(false);
        wheelviewYear.setTextSize(20);

        calendar.clear();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        List<String> yearList = new ArrayList<>();
        for (int i = 2015; i <= year; i++) {
            yearList.add(String.valueOf(i));
        }

        wheelviewYear.setAdapter(yearAdapter = new TimeWheelAdapter(yearList));

        int currentYearPosition = 0;
        for (int i = 0; i < yearList.size(); i++) {
            if (TextUtils.equals(yearList.get(i), String.valueOf(selectedYear))) {
                currentYearPosition = i;
            }
        }
        wheelviewYear.setCurrentItem(currentYearPosition);
    }

    private class TimeWheelAdapter implements WheelAdapter<String> {
        List<String> timeList = new ArrayList<>();

        public TimeWheelAdapter(List<String> data) {
            this.timeList = data;
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
        int currentYear = Integer.valueOf(yearAdapter.getItem(wheelviewYear.getCurrentItem()));
        timingSelectEntity.startTimeMillis = DateUtils.getSupportBeginDayofYear(currentYear).getTime();
        timingSelectEntity.endTimeMillis = DateUtils.getSupportEndDayofYear(currentYear).getTime();
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
