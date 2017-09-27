package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TimingDateEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
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

    public static final String TAG_START_TIME = "tagStartTime";
    public static final int TYPE_CHANGE_START_TIME = 0;//说明是修改计时开始时间

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

    private long startTimeMillis;//记录传递进来的时间

    private long currentTimeMillis;//记录当前选中的时间

    private int lastDatePosition;
    private int lastHourPosition;
    private int lastMinutePosition;

    private DateWheelAdapter dateWheelAdapter;
    private TimeWheelAdapter hourWheelAdapter;
    private TimeWheelAdapter minuteWheelAdapter;

    private OnFragmentCallBackListener onFragmentCallBackListener;

    public static TimingChangeDialogFragment newInstance(@NonNull String title, long timeMillios) {
        TimingChangeDialogFragment fragment = new TimingChangeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putLong(TIME_MILLIOS, timeMillios);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentCallBackListener) {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        }
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
            startTimeMillis = getArguments().getLong(TIME_MILLIOS);
        }

        wheelviewDate.setTextSize(20);
        wheelviewDate.setCyclic(false);
        wheelviewHour.setTextSize(20);
        wheelviewHour.setCyclic(false);
        wheelviewMinute.setTextSize(20);
        wheelviewMinute.setCyclic(false);

        try {
            dateWheelAdapter = new DateWheelAdapter();
            ArrayList<TimingDateEntity> tempMenus = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01"));
            for (int i = 0; i < 10; i++) {
                TimingDateEntity timingDateEntity = new TimingDateEntity();
                timingDateEntity.timeMillios = cal.getTimeInMillis();
                tempMenus.add(timingDateEntity);
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
            dateWheelAdapter.setTimeList(tempMenus);
            wheelviewDate.setAdapter(dateWheelAdapter);
        } catch (ParseException e) {

        }

        //显示小时的list
        List<String> hourList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hourList.add(String.valueOf(i));
        }
        hourWheelAdapter = new TimeWheelAdapter(hourList);
        wheelviewHour.setAdapter(hourWheelAdapter);

        //显示分钟的list
        List<String> minuteList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minuteList.add(String.valueOf(i));
        }
        minuteWheelAdapter = new TimeWheelAdapter(minuteList);
        wheelviewMinute.setAdapter(minuteWheelAdapter);

        Observable.create(new ObservableOnSubscribe<List<TimingDateEntity>>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<List<TimingDateEntity>> e) throws Exception {
                List<TimingDateEntity> dayList = new ArrayList<>();//显示日期的list
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
        }).delay(300, TimeUnit.MILLISECONDS)
                .compose(this.<List<TimingDateEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TimingDateEntity>>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull final List<TimingDateEntity> timingDateEntities) throws Exception {
                        dateWheelAdapter.setTimeList(timingDateEntities);
                        wheelviewDate.invalidate();

                        setTime(startTimeMillis);
                    }
                });

        wheelviewDate.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                verifyData(i, lastHourPosition, lastMinutePosition);
            }
        });

        wheelviewHour.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                verifyData(lastDatePosition, i, lastMinutePosition);
            }
        });

        wheelviewMinute.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                verifyData(lastDatePosition, lastHourPosition, i);
            }
        });
    }

    /**
     * 校验日期、时间、分钟的选中状态是可选,也就是是否大于当前时间
     *
     * @param datePosition   日期在wheelview中的位置
     * @param hourPosition   小时在wheelview中的位置
     * @param minutePosition 分钟在wheelview中的位置
     * @return 是否满足
     */
    private void verifyData(int datePosition, int hourPosition, int minutePosition) {
        Calendar instance = Calendar.getInstance();
        TimingDateEntity item = dateWheelAdapter.getItem(datePosition);
        instance.setTimeInMillis(item.timeMillios);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);
        String hour = hourWheelAdapter.getItem(hourPosition);
        String minute = minuteWheelAdapter.getItem(minutePosition);
        instance.set(year, month, day, Integer.valueOf(hour), Integer.valueOf(minute), 0);
        if (instance.getTimeInMillis() > System.currentTimeMillis()) {//如果选中的时间大于当前时间，则不满足
            long millis = System.currentTimeMillis();
            instance.setTimeInMillis(millis);
            int currentYear = instance.get(Calendar.YEAR);
            int currentMonth = instance.get(Calendar.MONTH);
            int currentDay = instance.get(Calendar.DAY_OF_MONTH);
            int currentHour = instance.get(Calendar.HOUR_OF_DAY);
            int currentMinute = instance.get(Calendar.MINUTE);
            //记录当前时间，精确到分钟，秒数置为0。
            instance.set(currentYear, currentMonth, currentDay, currentHour, currentMinute, 0);
            setTime(instance.getTimeInMillis());
        } else {//说明满足条件，设置为当前日期。
            currentTimeMillis = instance.getTimeInMillis();
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_finish:
                //如果传递进来的时间和当前选中的时间一致，则说明没有修改时间，点击完成就消失
                if (startTimeMillis != currentTimeMillis) {
                    if (onFragmentCallBackListener != null) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(TAG_START_TIME, currentTimeMillis);
                        onFragmentCallBackListener.onFragmentCallBack(this, TYPE_CHANGE_START_TIME, bundle);
                    }
                }
                dismiss();
                break;
        }
    }

    /**
     * 设置时间选择器要显示的时间
     *
     * @param millios 时间毫秒数
     */

    private void setTime(long millios) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millios);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);
        int hour = instance.get(Calendar.HOUR_OF_DAY);
        int minute = instance.get(Calendar.MINUTE);

        //从后往前遍历日期，选中当前日
        List<TimingDateEntity> dayList = dateWheelAdapter.getTimeList();
        Calendar calendar = Calendar.getInstance();
        for (int i = dayList.size() - 1; i > 0; i--) {
            TimingDateEntity dateEntity = dayList.get(i);
            calendar.setTimeInMillis(dateEntity.timeMillios);
            if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.DAY_OF_MONTH) == day) {
                wheelviewDate.setCurrentItem(i);
                lastDatePosition = i;
                break;
            }
        }
        //遍历小时，选中所记录的小时
        List<String> hourList = hourWheelAdapter.getTimeList();
        for (int i = 0; i < hourList.size(); i++) {
            if (Integer.valueOf(hourList.get(i)) == hour) {
                wheelviewHour.setCurrentItem(i);
                lastHourPosition = i;
                break;
            }
        }
        //遍历分钟，选中所记录的分钟
        List<String> minuteList = minuteWheelAdapter.getTimeList();
        for (int i = 0; i < minuteList.size(); i++) {
            if (Integer.valueOf(minuteList.get(i)) == minute) {
                wheelviewMinute.setCurrentItem(i);
                lastMinutePosition = i;
                break;
            }
        }
        currentTimeMillis = millios;
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
