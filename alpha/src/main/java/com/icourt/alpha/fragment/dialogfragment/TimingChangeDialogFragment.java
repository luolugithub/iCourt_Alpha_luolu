package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
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
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.widget.manager.TimerDateManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
 * Description 修改开始／结束计时时间的弹出窗
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/16
 * version 1.0.0
 */

public class TimingChangeDialogFragment extends BaseDialogFragment {

    private static final String TIME_START_MILLIS = "timeStartMillis";//用来传递开始时间的tag
    private static final String TIME_END_MILLIS = "timeEndMillis";//用来传递结束时间的tag


    public static final String TYPE_CHANGE_TYPE = "typeChangeType";//用来记录修改的类型，是枚举ChangeTimeType所指定的。

    public static final String TIME_RESULT_MILLIS = "timeResultMillis";//用来将当前选中的时间返回给监听。

    public static final int TYPE_CHANGE_START_TIME = 1;//说明是修改计时开始时间
    public static final int TYPE_CHANGE_END_TIME = 2;//说明是修改计时开始时间

    @IntDef({TYPE_CHANGE_START_TIME, TYPE_CHANGE_END_TIME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChangeTimeType {

    }

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

    private int changeType;//修改时间的类型，对应枚举ChangeTimeType所指定的。
    private long startTimeMillis;//记录传递进来的开始时间
    private long endTimeMillis;//记录传递进来的结束时间

    private long currentTimeMillis;//记录当前选中的时间

    private int lastDatePosition;
    private int lastHourPosition;
    private int lastMinutePosition;

    private DateWheelAdapter dateWheelAdapter;
    private TimeWheelAdapter hourWheelAdapter;
    private TimeWheelAdapter minuteWheelAdapter;

    private OnFragmentCallBackListener onFragmentCallBackListener;

    /**
     * @param type            所要修改的类型，开始时间？结束时间
     * @param timeStartMillis 传递进来的开始时间时间戳
     * @param timeEndMillis   传递进来的结束时间时间戳（如果是正在计时界面，这个值传0）
     * @return
     */
    public static TimingChangeDialogFragment newInstance(@ChangeTimeType int type, long timeStartMillis, long timeEndMillis) {
        TimingChangeDialogFragment fragment = new TimingChangeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_CHANGE_TYPE, type);
        bundle.putLong(TIME_START_MILLIS, timeStartMillis);
        bundle.putLong(TIME_END_MILLIS, timeEndMillis);
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
            startTimeMillis = getArguments().getLong(TIME_START_MILLIS);
            endTimeMillis = getArguments().getLong(TIME_END_MILLIS);
            changeType = arguments.getInt(TYPE_CHANGE_TYPE);
            if (changeType == TYPE_CHANGE_START_TIME) {//说明是修改开始时间
                currentTimeMillis = startTimeMillis;
                tvTitle.setText(getString(R.string.timing_please_select_start_time));
            } else {//说明是修改结束时间
                currentTimeMillis = endTimeMillis;
                tvTitle.setText(getString(R.string.timing_please_select_end_time));
            }
        }

        wheelviewDate.setTextSize(20);
        wheelviewDate.setCyclic(false);
        wheelviewHour.setTextSize(20);
        wheelviewHour.setCyclic(false);
        wheelviewMinute.setTextSize(20);
        wheelviewMinute.setCyclic(false);

        dateWheelAdapter = new DateWheelAdapter();
        ArrayList<TimingDateEntity> tempMenus = new ArrayList<>();
        Calendar cal = TimerDateManager.getStartDate();
        for (int i = 0; i < 10; i++) {
            TimingDateEntity timingDateEntity = new TimingDateEntity();
            timingDateEntity.timeMillios = cal.getTimeInMillis();
            tempMenus.add(timingDateEntity);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        dateWheelAdapter.setTimeList(tempMenus);
        wheelviewDate.setAdapter(dateWheelAdapter);

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
                Calendar cal = TimerDateManager.getStartDate();
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

                        setTime(currentTimeMillis);
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
     * 校验日期、时间、分钟的选中状态是可选
     *
     * @param datePosition   日期在wheelview中的位置
     * @param hourPosition   小时在wheelview中的位置
     * @param minutePosition 分钟在wheelview中的位置
     * @return 是否满足
     */
    private void verifyData(int datePosition, int hourPosition, int minutePosition) {
        //将当前选中的时间转换为时间戳
        Calendar instance = Calendar.getInstance();
        TimingDateEntity item = dateWheelAdapter.getItem(datePosition);
        instance.setTimeInMillis(item.timeMillios);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int day = instance.get(Calendar.DAY_OF_MONTH);
        String hour = hourWheelAdapter.getItem(hourPosition);
        String minute = minuteWheelAdapter.getItem(minutePosition);
        instance.set(year, month, day, Integer.valueOf(hour), Integer.valueOf(minute), 0);
        //验证修改的是开始时间还是结束时间
        if (changeType == TYPE_CHANGE_START_TIME) { //1.修改开始时间
            if (instance.getTimeInMillis() > System.currentTimeMillis()) {//若用户选择了晚于当前时间的时间，则自动滚动到当前时间
                setTime(DateUtils.getFormatMillis(System.currentTimeMillis()));
            } else {//说明满足条件，设置为当前选中时间。
                currentTimeMillis = instance.getTimeInMillis();
                lastDatePosition = datePosition;
                lastHourPosition = hourPosition;
                lastMinutePosition = minutePosition;
            }
        } else { //2.修改结束时间
            //已完成的计时或添加计时，结束时间必须晚于开始时间，不能早于当前时间。
            if (instance.getTimeInMillis() <= startTimeMillis) {//2.1若用户选择了等于或早于开始时间的时间，则自动滚动到开始时间的后一分钟。
                setTime(startTimeMillis + TimeUnit.MINUTES.toMillis(1));
            } else if (instance.getTimeInMillis() > System.currentTimeMillis()) { //2.2若用户选择了晚于当前时间的时间，则自动滚动到当前时间。
                setTime(DateUtils.getFormatMillis(System.currentTimeMillis()));
            } else {//说明满足条件，设置为当前选中时间。
                currentTimeMillis = instance.getTimeInMillis();
                lastDatePosition = datePosition;
                lastHourPosition = hourPosition;
                lastMinutePosition = minutePosition;
            }
        }
    }

    @OnClick({R.id.tv_cancel,
            R.id.tv_finish})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_finish:
                //如果传递进来的时间和当前选中的时间一致，则说明没有修改时间，点击完成就消失
                if (changeType == TYPE_CHANGE_START_TIME && startTimeMillis == currentTimeMillis) {
                    dismiss();
                } else if (changeType == TYPE_CHANGE_END_TIME && endTimeMillis == currentTimeMillis) {
                    dismiss();
                } else {
                    if (onFragmentCallBackListener != null) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(TIME_RESULT_MILLIS, currentTimeMillis);
                        onFragmentCallBackListener.onFragmentCallBack(this, changeType, bundle);
                    }
                    dismiss();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置时间选择器要显示的时间
     *
     * @param millios 时间毫秒数
     */

    private void setTime(long millios) {
        currentTimeMillis = millios;
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
                if (wheelviewDate != null) {
                    wheelviewDate.setCurrentItem(i);
                }
                lastDatePosition = i;
                break;
            }
        }
        //遍历小时，选中所记录的小时
        List<String> hourList = hourWheelAdapter.getTimeList();
        for (int i = 0; i < hourList.size(); i++) {
            if (Integer.valueOf(hourList.get(i)) == hour) {
                if (wheelviewHour != null) {
                    wheelviewHour.setCurrentItem(i);
                }
                lastHourPosition = i;
                break;
            }
        }
        //遍历分钟，选中所记录的分钟
        List<String> minuteList = minuteWheelAdapter.getTimeList();
        for (int i = 0; i < minuteList.size(); i++) {
            if (Integer.valueOf(minuteList.get(i)) == minute) {
                if (wheelviewMinute != null) {
                    wheelviewMinute.setCurrentItem(i);
                }
                lastMinutePosition = i;
                break;
            }
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
