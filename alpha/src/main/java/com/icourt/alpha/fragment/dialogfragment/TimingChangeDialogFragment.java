package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimingDateEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/16
 * version 1.0.0
 */

public class TimingChangeDialogFragment extends BaseDialogFragment {

    private static final String TITLE = "title";
    private static final String TIME_MILLIOS = "timeMillios";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wheelview_date)
    WheelView wheelviewDate;
    @BindView(R.id.wheelview_hour)
    WheelView wheelviewHour;
    @BindView(R.id.wheelview_minute)
    WheelView wheelviewMinute;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_finish)
    TextView tvFinish;

    private Unbinder bind;

    private long timeMillis;//记录传递进来的时间

    private List<TimingDateEntity> dayList;//显示日期的list
    private List<String> hourList;//显示小时的list
    private List<String> minuteList;//显示分钟的list

    private DateWheelAdapter dateWheelAdapter;
    private TimeWheelAdapter hourWheelAdapter;
    private TimeWheelAdapter minuteWheelAdapter;

    public static TimingChangeDialogFragment newInstance(@NonNull String title, long timeMillios) {
        TimingChangeDialogFragment fragment = new TimingChangeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putLong(TIME_MILLIOS, timeMillios);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_timer_change, inflater, container, savedInstanceState);
        bind = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.AppThemeSlideAnimation);
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp15 = DensityUtil.dip2px(getContext(), 15);
                    decorView.setPadding(dp15, dp15, dp15, dp15);
                }
            }
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            tvTitle.setText(arguments.getString(TITLE));
            timeMillis = getArguments().getLong(TIME_MILLIOS);
        }

        wheelviewDate.setTextSize(20);
        wheelviewDate.setCyclic(false);
        wheelviewHour.setTextSize(20);
        wheelviewHour.setCyclic(false);
        wheelviewMinute.setTextSize(20);
        wheelviewMinute.setCyclic(false);

        dayList = new ArrayList<>();
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();

        dateWheelAdapter = new DateWheelAdapter();
        wheelviewDate.setAdapter(dateWheelAdapter);

//        for (int i = 0; i < 24; i++) {
//            hourList.add(String.valueOf(i));
//        }
//
//        for (int i = 0; i < 60; i++) {
//            minuteList.add(String.valueOf(i));
//        }


        Observable.create(new ObservableOnSubscribe<List<TimingDateEntity>>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<List<TimingDateEntity>> e) throws Exception {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));
                while (cal.getTimeInMillis() < System.currentTimeMillis()) {
                    TimingDateEntity dateEntity = new TimingDateEntity();
                    dateEntity.timeMillios = cal.getTimeInMillis();
                    dayList.add(dateEntity);
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }
                e.onNext(dayList);
                e.onComplete();
            }
        })
                .compose(this.<List<TimingDateEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TimingDateEntity>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull final List<TimingDateEntity> timingDateEntities) throws Exception {
                        LogUtils.i("---------setAdapter开始时间：" + System.currentTimeMillis() + "\n" + Thread.currentThread().getName());
                        dateWheelAdapter.setTimeList(timingDateEntities);
                        wheelviewDate.invalidate();

//                        wheelviewDate.setAdapter(new DateWheelAdapter(timingDateEntities));
//                        wheelviewHour.setAdapter(new TimeWheelAdapter(hourList));
//                        wheelviewMinute.setAdapter(new TimeWheelAdapter(minuteList));

//                        setTime(timeMillis);
                    }
                });
    }


    @OnClick({R.id.tv_cancel})
    public void onClick(View view) {

    }

    private void setTime(long millios) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millios);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int minute = instance.get(Calendar.MINUTE);

        Calendar calendar = Calendar.getInstance();
        for (int i = dayList.size() - 1; i > 0; i--) {
            TimingDateEntity dateEntity = dayList.get(i);
            calendar.setTimeInMillis(dateEntity.timeMillios);
            if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == day) {
                wheelviewDate.setCurrentItem(i);
                break;
            }
        }

        for (int i = 0; i < hourList.size(); i++) {
            if (Integer.valueOf(hourList.get(i)) == hour) {
                wheelviewHour.setCurrentItem(i);
                break;
            }
        }

        for (int i = 0; i < minuteList.size(); i++) {
            if (Integer.valueOf(minuteList.get(i)) == minute) {
                wheelviewMinute.setCurrentItem(i);
                break;
            }
        }
    }

    private class TimeWheelAdapter implements WheelAdapter<String> {

        List<String> timeList = new ArrayList<>();

        public TimeWheelAdapter() {
        }

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

    private class DateWheelAdapter implements WheelAdapter<TimingDateEntity> {
        List<TimingDateEntity> timeList = new ArrayList<>();

        public DateWheelAdapter() {
        }

        public List<TimingDateEntity> getTimeList() {
            return timeList;
        }

        public void setTimeList(List<TimingDateEntity> timeList) {
            this.timeList = timeList;
        }

        @Override
        public int getItemsCount() {
            return timeList.size();
        }

        @Override
        public TimingDateEntity getItem(int i) {
            return timeList.get(i);
        }

        @Override
        public int indexOf(TimingDateEntity o) {
            return timeList.indexOf(o);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
