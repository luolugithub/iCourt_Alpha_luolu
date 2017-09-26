package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;

import java.util.ArrayList;
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

        yearList.add("2015");
        yearList.add("2016");
        yearList.add("2017");

        for (int i = 1; i <= 12; i++) {
            monthList.add(String.valueOf(i));
        }

        wheelviewYear.setAdapter(new TimeWheelAdapter(yearList));
        wheelviewMonth.setAdapter(new TimeWheelAdapter(monthList));

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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
