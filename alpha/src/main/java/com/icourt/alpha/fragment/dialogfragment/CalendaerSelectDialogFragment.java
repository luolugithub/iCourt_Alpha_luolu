package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class CalendaerSelectDialogFragment extends BaseDialogFragment {

    Unbinder unbinder;
    Date selectedDate;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleForward)
    ImageView titleForward;
    @BindView(R.id.titleAction)
    TextView titleAction;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView compactcalendarView;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());

    OnFragmentCallBackListener onFragmentCallBackListener;

    public static CalendaerSelectDialogFragment newInstance() {
        return new CalendaerSelectDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_calender, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
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
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        initCompactCalendar();
    }

    private void initCompactCalendar() {
        compactcalendarView.setUseThreeLetterAbbreviation(false);
        compactcalendarView.setLocale(TimeZone.getDefault(), Locale.CHINESE);
        compactcalendarView.setUseThreeLetterAbbreviation(true);
        compactcalendarView.setDayColumnNames(new String[]{"一", "二", "三", "四", "五", "六", "日"});
        compactcalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
                selectedDate = date;
                btOk.setEnabled(selectedDate != null && !DateUtils.isOverToday(selectedDate.getTime()));
            }

            @Override
            public void onMonthScroll(Date date) {
                titleContent.setText(DateUtils.getFormatDate(date.getTime(), DateUtils.DATE_YYYYMM_STYLE1));
            }
        });
        titleContent.setText(DateUtils.getFormatDate(System.currentTimeMillis(), DateUtils.DATE_YYYYMM_STYLE1));

        compactcalendarView.removeAllEvents();
        //loadEvents();
        compactcalendarView.invalidate();
        // logEventsByMonth(compactcalendarView);
    }


    private void scrollToToday() {
        titleContent.setText(DateUtils.getFormatDate(System.currentTimeMillis(), DateUtils.DATE_YYYYMM_STYLE1));
        compactcalendarView.setCurrentDate(new Date());
        compactcalendarView.invalidate();
        btOk.setEnabled(true);
    }

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction,
            R.id.bt_cancel,
            R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                compactcalendarView.showPreviousMonth();
                break;
            case R.id.titleForward:
                compactcalendarView.showNextMonth();
                break;
            case R.id.titleAction:
                scrollToToday();
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KEY_FRAGMENT_RESULT, getSelectedMillis());
                    ((OnFragmentCallBackListener) getParentFragment()).onFragmentCallBack(this, 0, bundle);
                } else {
                    if (onFragmentCallBackListener != null) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(KEY_FRAGMENT_RESULT, getSelectedMillis());
                        onFragmentCallBackListener.onFragmentCallBack(this, 0, bundle);
                    }
                }
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private long getSelectedMillis() {
        if (selectedDate == null) {
            selectedDate = new Date();
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(selectedDate);
        return instance.getTimeInMillis();
    }

    private void loadEvents() {
        addEvents(-1, -1);
        addEvents(Calendar.DECEMBER, -1);
        addEvents(Calendar.AUGUST, -1);
    }

    private void addEvents(int month, int year) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();

            List<Event> events = getEvents(timeInMillis, i);

            compactcalendarView.addEvents(events);
        }
    }

    /**
     * 添加记录事件
     *
     * @param timeInMillis
     * @param day
     * @return
     */
    private List<Event> getEvents(long timeInMillis, int day) {
        return Arrays.asList(new Event(0xFFF6D9C0, timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
