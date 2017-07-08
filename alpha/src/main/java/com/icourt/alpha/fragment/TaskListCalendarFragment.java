package com.icourt.alpha.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codbking.calendar.CaledarAdapter;
import com.codbking.calendar.CalendarBean;
import com.codbking.calendar.CalendarDateView;
import com.codbking.calendar.CalendarUtil;
import com.codbking.calendar.CalendarView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskSimpleAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.PageEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SystemUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  任务周视图
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/7
 * version 1.0.0
 */

public class TaskListCalendarFragment extends BaseFragment {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleForward)
    ImageView titleForward;
    @BindView(R.id.titleAction)
    TextView titleAction;
    @BindView(R.id.calendarDateView)
    CalendarDateView calendarDateView;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    TaskSimpleAdapter taskSimpleAdapter;

    public static TaskListCalendarFragment newInstance() {
        return new TaskListCalendarFragment();
    }

    final Calendar todayCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_list_canlendar, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        initCalendarDateView();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskSimpleAdapter = new TaskSimpleAdapter());
        getData(true);
    }

    private void initCalendarDateView() {
        calendarDateView.setAdapter(new CaledarAdapter() {
            @Override
            public View getView(View convertView, ViewGroup parentView, CalendarBean bean) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.adapter_item_calendar, null);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DensityUtil.dip2px(getContext(), 32), DensityUtil.dip2px(getContext(), 32));
                    convertView.setLayoutParams(params);
                }
                TextView text = (TextView) convertView.findViewById(R.id.text);

                text.setText("" + bean.day);
                if (isToday(bean)) {//今天
                    ColorStateList colorStateList = SystemUtils.getColorStateList(getContext(), R.color.sl_orange_2_white);
                    if (colorStateList != null) {
                        text.setTextColor(colorStateList);
                    }
                } else if (bean.mothFlag == 0) {//当月的
                    ColorStateList colorStateList = SystemUtils.getColorStateList(getContext(), R.color.sl_balck_2_white);
                    if (colorStateList != null) {
                        text.setTextColor(colorStateList);
                    }
                } else {
                    ColorStateList colorStateList = SystemUtils.getColorStateList(getContext(), R.color.sl_gray_2_white);
                    if (colorStateList != null) {
                        text.setTextColor(colorStateList);
                    }
                }
                return convertView;
            }
        });
        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, CalendarBean bean) {
                titleContent.setText(String.format("%s年%s月", bean.year, bean.moth));
            }
        });


        int[] data = CalendarUtil.getYMD(new Date());
        titleContent.setText(String.format("%s年%s月", data[0], data[1]));
    }

    /**
     * 是否是今天
     *
     * @param calendarBean
     * @return
     */
    private boolean isToday(CalendarBean calendarBean) {
        int year = todayCalendar.get(Calendar.YEAR);
        int month = todayCalendar.get(Calendar.MONTH) + 1;
        int day = todayCalendar.get(Calendar.DAY_OF_MONTH);
        if (calendarBean != null) {
            return year == calendarBean.year
                    && month == calendarBean.moth
                    && day == calendarBean.day;
        }
        return false;
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        //2017-04-07 -2017-04-08
        getApi().getAllTask("2017-04-07", "2017-04-08", Arrays.asList(getLoginUserId()), 1, 0)
                .enqueue(new SimpleCallBack<PageEntity<TaskEntity.TaskItemEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<PageEntity<TaskEntity.TaskItemEntity>>> call, Response<ResEntity<PageEntity<TaskEntity.TaskItemEntity>>> response) {
                        if (response.body().result == null) return;
                        taskSimpleAdapter.bindData(isRefresh, response.body().result.items);
                    }
                });
    }

    @OnClick({R.id.titleBack,
            R.id.titleForward,
            R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (calendarDateView.getCurrentItem() > 0) {
                    calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() - 1, true);
                }
                break;
            case R.id.titleForward:
                if (calendarDateView.getCurrentItem() < calendarDateView.getAdapter().getCount() - 1) {
                    calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() + 1, true);
                }
                break;
            case R.id.titleAction:
                //calendarDateView.setCurrentItem(Integer.MAX_VALUE / 2, true);//直接设置崩溃
                int centerPos = Integer.MAX_VALUE / 2;
                if (calendarDateView.getCurrentItem() < centerPos) {
                    while (calendarDateView.getCurrentItem() < centerPos) {
                        calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() + 1, true);
                    }
                } else if (calendarDateView.getCurrentItem() > centerPos) {
                    while (calendarDateView.getCurrentItem() > centerPos) {
                        calendarDateView.setCurrentItem(calendarDateView.getCurrentItem() - 1, true);
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
