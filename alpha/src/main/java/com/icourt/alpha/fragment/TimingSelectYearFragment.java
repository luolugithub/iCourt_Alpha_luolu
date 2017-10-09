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
import com.icourt.alpha.entity.bean.TimingWeekEntity;
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

    Unbinder unbinder;
    @BindView(R.id.wheelview_year)
    WheelView wheelviewYear;
    TimeWheelAdapter yearAdapter;

    public static TimingSelectYearFragment newInstance() {
        return new TimingSelectYearFragment();
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
        wheelviewYear.setCyclic(false);
        wheelviewYear.setTextSize(20);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        List<String> yearList = new ArrayList<>();
        for (int i = year - 20; i < year + 20; i++) {
            yearList.add(String.valueOf(i));
        }

        wheelviewYear.setAdapter(yearAdapter = new TimeWheelAdapter(yearList));

        int currentYearCount = 0;
        for (int i = 0; i < yearList.size(); i++) {
            if (TextUtils.equals(yearList.get(i), String.valueOf(calendar.get(Calendar.YEAR)))) {
                currentYearCount = i;
            }
        }
        wheelviewYear.setCurrentItem(currentYearCount);
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
        TimingWeekEntity timingWeekEntity = new TimingWeekEntity();
        int currentYear = Integer.valueOf(yearAdapter.getItem(wheelviewYear.getCurrentItem()));
        timingWeekEntity.startTimeMillios = DateUtils.getSupportBeginDayofYear(currentYear).getTime();
        timingWeekEntity.endTimeMillios = DateUtils.getSupportEndDayofYear(currentYear).getTime();
        timingWeekEntity.startTimeStr = DateUtils.getyyyy_MM_dd(timingWeekEntity.startTimeMillios);
        timingWeekEntity.endTimeStr = DateUtils.getyyyy_MM_dd(timingWeekEntity.endTimeMillios);
        arguments.putSerializable(KEY_FRAGMENT_RESULT, timingWeekEntity);
        return arguments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
