package com.jeek.calendar.widget.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.jeek.calendar.library.R;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekAdapter extends PagerAdapter {

    private SparseArray<WeekView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private WeekCalendarView mWeekCalendarView;
    private DateTime mStartDate;
    private int mWeekCount = 220;

    public WeekAdapter(Context context, TypedArray array, WeekCalendarView weekCalendarView) {
        mContext = context;
        mArray = array;
        mWeekCalendarView = weekCalendarView;
        mViews = new SparseArray<>();
        initStartDate();
        mWeekCount = array.getInteger(R.styleable.WeekCalendarView_week_count, 220);
    }

    private void initStartDate() {
        mStartDate = new DateTime();
        //按中国国情 从周一开始
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7 + 1);
    }

    @Override
    public int getCount() {
        return mWeekCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        for (int i = 0; i < 3; i++) {
            if (position - 2 + i >= 0 && position - 2 + i < mWeekCount && mViews.get(position - 2 + i) == null) {
                instanceWeekView(position - 2 + i);
            }
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public SparseArray<WeekView> getViews() {
        return mViews;
    }

    public int getWeekCount() {
        return mWeekCount;
    }

    public WeekView instanceWeekView(int position) {
        DateTime dateTime = mStartDate.plusWeeks(position - mWeekCount / 2);

        WeekView weekView = new WeekView(mContext, mArray, mStartDate.plusWeeks(position - mWeekCount / 2));
        weekView.setId(position);
        weekView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekView.setOnWeekClickListener(mWeekCalendarView);
        weekView.invalidate();
        mViews.put(position, weekView);
        return weekView;
    }

    /**
     * 获取日期 yyyy年MM月dd日
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeDateFormatYear(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        return formatter.format(milliseconds);
    }

}
