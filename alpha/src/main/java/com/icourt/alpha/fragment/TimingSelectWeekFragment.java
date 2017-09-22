package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.WeekDateEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 计时模块时间选择器的周选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectWeekFragment extends BaseFragment {

    public static final long ONE_WEEK_MILLIOS = 7 * 24 * 60 * 60 * 1000 - 1;

    Unbinder unbinder;

    @BindView(R.id.wheelview_week)
    WheelView wheelView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timing_select_week, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initView() {
        wheelView.setTextSize(20);
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

            }
        });
        final List<WeekDateEntity> list = new ArrayList<>();
        try {
            //当前周的开始时间
            long weekStartTime = 0;
            //当前周的结束时间
            long weekEndTime;
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));
            while (weekStartTime < System.currentTimeMillis()) {
                int d = 0;
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//如果是周日，则在当前日期上减去6天，就是周一了
                    d = -6;
                } else {//如果不是周日，周一的起始值是减去今天所对应周几，得出这周的第一天。
                    d = Calendar.MONDAY - cal.get(Calendar.DAY_OF_WEEK);
                }
                //所在周开始日期
                cal.add(Calendar.DAY_OF_WEEK, d);
                weekStartTime = cal.getTimeInMillis();
                //所在周结束日期
//            cal.add(Calendar.DAY_OF_WEEK, 6);
                weekEndTime = weekStartTime + ONE_WEEK_MILLIOS;
                LogUtils.i("haha开始时间：" + DateUtils.getyyyyMMddHHmm(weekStartTime));
                LogUtils.i("haha结束时间：" + DateUtils.getyyyyMMddHHmm(weekEndTime));

                WeekDateEntity weekDateEntity = new WeekDateEntity();
                weekDateEntity.startTimeMillios = weekStartTime;
                weekDateEntity.endTimeMillios = weekEndTime;
                list.add(weekDateEntity);
                cal.setTime(new Date(weekEndTime + 1));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.i("haha，list的长度" + list.size());

        final TimeWheelAdapter adapter = new TimeWheelAdapter(list);
        wheelView.setAdapter(adapter);
        wheelView.setCyclic(false);
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                WeekDateEntity item = adapter.getItem(i);
                LogUtils.i("haha，时间段所在年份" + item.getYear());
                if (item.endTimeMillios > System.currentTimeMillis()) {
                    wheelView.setCurrentItem(list.size() - 10);
                }
            }
        });
    }


    private class TimeWheelAdapter implements WheelAdapter<WeekDateEntity> {
        List<WeekDateEntity> timeList = new ArrayList<>();

        public TimeWheelAdapter(List<WeekDateEntity> data) {
            this.timeList = data;
        }

        @Override
        public int getItemsCount() {
            return timeList.size();
        }

        @Override
        public WeekDateEntity getItem(int i) {
            return timeList.get(i);
        }

        @Override
        public int indexOf(WeekDateEntity o) {
            return timeList.indexOf(o);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
