package com.jeek.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.jeek.calendar.library.R;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;

import java.util.Calendar;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    private void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array, this);
        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, false);
    }

    @Override
    public void onClickThisMonth(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    @Override
    public void onClickLastMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() - 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
        }
        mOnPageChangeListener.setResetDate(false);
        setCurrentItem(getCurrentItem() - 1, true);
        mOnPageChangeListener.setResetDate(true);
    }

    /**
     * 本月选中..
     *
     * @param day
     */
    public void onClickThisMonth(int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem());
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(monthDateView.getSelectYear(), monthDateView.getSelectMonth(), day);
            monthDateView.invalidate();
        }
        onClickThisMonth(monthDateView.getSelectYear(), monthDateView.getSelectMonth(), day);
    }

    @Override
    public void onClickNextMonth(int year, int month, int day) {
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() + 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
            monthDateView.invalidate();
        }
        onClickThisMonth(year, month, day);
        mOnPageChangeListener.setResetDate(false);
        setCurrentItem(getCurrentItem() + 1, true);
        mOnPageChangeListener.setResetDate(true);
    }

    abstract class MyOnPageChangeListener implements OnPageChangeListener {
        private boolean isResetDate = true;

        public void setResetDate(boolean resetDate) {
            isResetDate = resetDate;
        }

        public boolean isResetDate() {
            return isResetDate;
        }

    }

    private MyOnPageChangeListener mOnPageChangeListener = new MyOnPageChangeListener() {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
            if (monthView != null) {
                //重置
                if (isResetDate()) {
                    //非今日所在月份选中第一天
                    //今日所在月份选中今日
                    //monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
                    Calendar calendar = Calendar.getInstance();
                    int currYear = calendar.get(Calendar.YEAR);
                    int currMonth = calendar.get(Calendar.MONTH);

                    //非选中日期所在月份，默认选中第一天

                    if (currYear == monthView.getSelectYear() &&
                            currMonth == monthView.getSelectMonth()) {
                        monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), calendar.get(Calendar.DAY_OF_MONTH));
                    } else {
                        monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), 1);
                    }

                } else {
                    monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
                }

                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(monthView.getSelectYear(), monthView.getSelectMonth(), monthView.getSelectDay());
                }
            } else {
                MonthCalendarView.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPageSelected(position);
                    }
                }, 50);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 跳转到今天
     */
    public void setTodayToView() {
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, true);
        MonthView monthView = mMonthAdapter.getViews().get(mMonthAdapter.getMonthCount() / 2);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    /**
     * 跳转到指定的日期
     *
     * @param timeMillis
     */
    public void setDateToView(long timeMillis) {
        final Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(timeMillis);
        Calendar currentCalendar = Calendar.getInstance();
        int diffMonth = selectedCalendar.get(Calendar.MONTH) - currentCalendar.get(Calendar.MONTH);//两个日期的月份相差
        int diffYearMonth = (selectedCalendar.get(Calendar.YEAR) - currentCalendar.get(Calendar.YEAR)) * 12;//两个日期的年份相差多少个月
        int result = diffMonth + diffYearMonth;
        final int position = mMonthAdapter.getMonthCount() / 2 + result;
        removeOnPageChangeListener(mOnPageChangeListener);//先移除监听，否则会导致选中的日期被监听覆盖。
        setCurrentItem(position, true);
        post(new Runnable() {
            @Override
            public void run() {
                removeOnPageChangeListener(mOnPageChangeListener);
                MonthView monthView = mMonthAdapter.getViews().get(position);
                if (monthView != null) {
                    monthView.clickThisMonth(selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH));
                    removeOnPageChangeListener(mOnPageChangeListener);
                    addOnPageChangeListener(mOnPageChangeListener);
                }
            }
        });
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<MonthView> getMonthViews() {
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

}
