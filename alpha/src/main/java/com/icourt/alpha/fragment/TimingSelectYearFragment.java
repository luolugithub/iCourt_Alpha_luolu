package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigkoo.pickerview.lib.WheelView;
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
 * 计时模块时间选择器的年选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectYearFragment extends BaseFragment {

    private static final int TEXT_SIZE_WHEELVIEW = 20;

    private static final String KEY_SELECTED_DATE = "keySelectedDate";

    Unbinder unbinder;

    @BindView(R.id.year_wheelview)
    WheelView yearWheelview;
    StringWheelAdapter yearAdapter;

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
        int selectedYear = TimerDateManager.START_YEAR;
        Calendar calendar = Calendar.getInstance();
        if (getArguments() != null) {
            long timeMillis = getArguments().getLong(KEY_SELECTED_DATE, System.currentTimeMillis());
            calendar.setTimeInMillis(timeMillis);
            selectedYear = calendar.get(Calendar.YEAR);
        }

        yearWheelview.setCyclic(false);
        yearWheelview.setTextSize(TEXT_SIZE_WHEELVIEW);

        calendar.clear();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        List<String> yearList = new ArrayList<>();
        for (int i = TimerDateManager.START_YEAR; i <= year; i++) {
            yearList.add(String.valueOf(i));
        }

        yearWheelview.setAdapter(yearAdapter = new StringWheelAdapter(yearList));

        int currentYearPosition = 0;
        for (int i = 0; i < yearList.size(); i++) {
            if (TextUtils.equals(yearList.get(i), String.valueOf(selectedYear))) {
                currentYearPosition = i;
            }
        }
        yearWheelview.setCurrentItem(currentYearPosition);
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle arguments = new Bundle();

        TimingSelectEntity timingSelectEntity = new TimingSelectEntity();
        int currentYear = Integer.valueOf(yearAdapter.getItem(yearWheelview.getCurrentItem()));

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
