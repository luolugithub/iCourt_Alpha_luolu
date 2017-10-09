package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimingWeekEntity;
import com.icourt.alpha.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 计时模块时间选择器的周选择器
 * Created by zhaodanyang on 2017/9/21.
 */

public class TimingSelectWeekFragment extends BaseFragment {

    public static final long ONE_WEEK_MILLIOS = 7 * 24 * 60 * 60 * 1000 - 1;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Unbinder unbinder;

    @BindView(R.id.wheelview_week)
    WheelView wheelView;
    TimeWheelAdapter adapter;
    int currentCount = 0;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleForward)
    ImageView titleForward;
    @BindView(R.id.titleAction)
    TextView titleAction;
    int position;//当前时间 为0；
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy年MM", Locale.getDefault());

    public static TimingSelectWeekFragment newInstance() {
        return new TimingSelectWeekFragment();
    }

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
        try {//预加载10条数据
            adapter = new TimeWheelAdapter();
            ArrayList<TimingWeekEntity> tempMenus = new ArrayList<>();
            Calendar beforeCal = Calendar.getInstance();
            Calendar laterCal = Calendar.getInstance();
            int year = beforeCal.get(Calendar.YEAR);
            beforeCal.setTime(simpleDateFormat.parse((year - 20) + "-01-01"));
            laterCal.setTime(simpleDateFormat.parse((year + 20) + "-01-01"));
            //当前周的开始时间
            long weekStartTime = 0;
            //当前周的结束时间
            long weekEndTime;
            for (int i = 0; i < 10; i++) {
                int d = 0;
                if (beforeCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//如果是周日，则在当前日期上减去6天，就是周一了
                    d = -6;
                } else {//如果不是周日，周一的起始值是减去今天所对应周几，得出这周的第一天。
                    d = Calendar.MONDAY - beforeCal.get(Calendar.DAY_OF_WEEK);
                }
                //所在周开始日期
                beforeCal.add(Calendar.DAY_OF_WEEK, d);
                weekStartTime = beforeCal.getTimeInMillis();
                weekEndTime = weekStartTime + ONE_WEEK_MILLIOS;
                TimingWeekEntity timingWeekEntity = new TimingWeekEntity();
                timingWeekEntity.startTimeMillios = weekStartTime;
                timingWeekEntity.endTimeMillios = weekEndTime;
                timingWeekEntity.startTimeStr = DateUtils.getyyyy_MM_dd(weekStartTime);
                timingWeekEntity.endTimeStr = DateUtils.getyyyy_MM_dd(weekEndTime);
                beforeCal.add(Calendar.DAY_OF_YEAR, 1);
                tempMenus.add(timingWeekEntity);
            }
            adapter.setTimeList(tempMenus);
            wheelView.setAdapter(adapter);
        } catch (ParseException e) {

        }
        setWeekData();
        titleContent.setText(dateFormatForMonth.format(System.currentTimeMillis()));
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                TimingWeekEntity item = adapter.getItem(i);
                titleContent.setText(dateFormatForMonth.format(item.endTimeMillios));
            }
        });
    }

    /**
     * 获取周数据
     *
     * @return
     */
    private List<TimingWeekEntity> getWeekData() {
        List<TimingWeekEntity> dayList = new ArrayList<>();//显示日期的list
        try {
            //当前周的开始时间
            long weekStartTime = 0;
            //当前周的结束时间
            long weekEndTime;
            Calendar beforeCal = Calendar.getInstance();
            Calendar laterCal = Calendar.getInstance();
            int year = beforeCal.get(Calendar.YEAR);
            beforeCal.setTime(simpleDateFormat.parse((year - 20) + "-01-01"));
            laterCal.setTime(simpleDateFormat.parse((year + 20) + "-01-01"));

            while (weekStartTime < laterCal.getTimeInMillis()) {
                int d = 0;
                if (beforeCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {//如果是周日，则在当前日期上减去6天，就是周一了
                    d = -6;
                } else {//如果不是周日，周一的起始值是减去今天所对应周几，得出这周的第一天。
                    d = Calendar.MONDAY - beforeCal.get(Calendar.DAY_OF_WEEK);
                }
                //所在周开始日期
                beforeCal.add(Calendar.DAY_OF_WEEK, d);
                weekStartTime = beforeCal.getTimeInMillis();
                weekEndTime = weekStartTime + ONE_WEEK_MILLIOS;

                TimingWeekEntity timingWeekEntity = new TimingWeekEntity();
                timingWeekEntity.startTimeMillios = weekStartTime;
                timingWeekEntity.endTimeMillios = weekEndTime;
                timingWeekEntity.startTimeStr = DateUtils.getyyyy_MM_dd(weekStartTime);
                timingWeekEntity.endTimeStr = DateUtils.getyyyy_MM_dd(weekEndTime);
                dayList.add(timingWeekEntity);
                if (weekStartTime <= System.currentTimeMillis() && weekEndTime >= System.currentTimeMillis()) {
                    currentCount = dayList.indexOf(timingWeekEntity);
                }
                beforeCal.setTime(new Date(weekEndTime + 1));
            }
            return dayList;
        } catch (ParseException e) {

        }
        return dayList;
    }

    /**
     * 设置周数据
     */
    private void setWeekData() {
        Observable.create(new ObservableOnSubscribe<List<TimingWeekEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<TimingWeekEntity>> e) throws Exception {
                e.onNext(getWeekData());
                e.onComplete();
            }
        }).delay(300, TimeUnit.MILLISECONDS)
                .compose(this.<List<TimingWeekEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TimingWeekEntity>>() {
                    @Override
                    public void accept(@NonNull final List<TimingWeekEntity> timingDateEntities) throws Exception {
                        adapter.setTimeList(timingDateEntities);
                        wheelView.invalidate();
                        wheelView.setCyclic(false);
                        wheelView.setCurrentItem(currentCount);
                    }
                });
    }


    private class TimeWheelAdapter implements WheelAdapter<TimingWeekEntity> {
        List<TimingWeekEntity> timeList = new ArrayList<>();

        public TimeWheelAdapter() {
        }

        public List<TimingWeekEntity> getTimeList() {
            return timeList;
        }

        public void setTimeList(List<TimingWeekEntity> timeList) {
            this.timeList = timeList;
        }

        public TimeWheelAdapter(List<TimingWeekEntity> data) {
            this.timeList = data;
        }

        @Override
        public int getItemsCount() {
            return timeList.size();
        }

        @Override
        public TimingWeekEntity getItem(int i) {
            return timeList.get(i);
        }

        @Override
        public int indexOf(TimingWeekEntity o) {
            return timeList.indexOf(o);
        }

    }

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                position -= 1;
                titleContent.setText(getBeforeOrLastMonth(position));
                break;
            case R.id.titleForward:
                position += 1;
                titleContent.setText(getBeforeOrLastMonth(position));
                break;
            case R.id.titleAction:
                scrollToToday();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 滚动到今天所在周
     */
    private void scrollToToday() {
        titleContent.setText(dateFormatForMonth.format(System.currentTimeMillis()));
        wheelView.setCurrentItem(currentCount);
    }

    /**
     * 获取前／后n个月
     *
     * @param position
     * @return
     */
    private String getBeforeOrLastMonth(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, position);//正数：往前推1月、2月、3月；负数：往后推1月／2月
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        selectMonthItem(calendar);
        return dateFormatForMonth.format(calendar.getTime());
    }

    /**
     * 滚动到指定的月份
     * @param calendar
     */
    private void selectMonthItem(Calendar calendar) {
        if (adapter == null || adapter.getTimeList().isEmpty()) return;
        int position = 0;
        for (int i = 0; i < adapter.getTimeList().size(); i++) {
            TimingWeekEntity timingWeekEntity = adapter.getTimeList().get(i);
            if (calendar.getTimeInMillis() <= timingWeekEntity.endTimeMillios && calendar.getTimeInMillis() > timingWeekEntity.startTimeMillios) {
                position = i;
            }
        }
        wheelView.setCurrentItem(position);
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle arguments = new Bundle();
        TimingWeekEntity timingWeekEntity = adapter.getItem(wheelView.getCurrentItem());
        if (timingWeekEntity != null) {
            arguments.putSerializable(KEY_FRAGMENT_RESULT, timingWeekEntity);
        }
        return arguments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
