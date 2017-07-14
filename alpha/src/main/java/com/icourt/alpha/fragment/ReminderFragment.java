package com.icourt.alpha.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.lib.WheelView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ReminderListAdapter;
import com.icourt.alpha.adapter.TimeWheelAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ReminderItemEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.TaskReminderUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  提醒
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/7/8
 * version 2.0.0
 */

public class ReminderFragment extends BaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemChildClickListener {
    Unbinder unbinder;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    ReminderListAdapter reminderListAdapter;
    TaskReminderEntity taskReminderEntity = new TaskReminderEntity();
    @BindView(R.id.bt_clear_reminder)
    TextView btClearReminder;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.add_reminder_text)
    TextView addReminderText;
    int customPosition;//自定义的position
    LinearLayoutManager linearLayoutManager;
    Calendar calendar;
    OnPageFragmentCallBack onPageFragmentCallBack;
    String taskReminderType;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        if (getParentFragment() instanceof OnPageFragmentCallBack) {
            onPageFragmentCallBack = (OnPageFragmentCallBack) getParentFragment();
        } else {
            try {
                onPageFragmentCallBack = (OnPageFragmentCallBack) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_reminder_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static ReminderFragment newInstance(TaskReminderEntity taskReminderEntity, Calendar calendar, String taskReminderType) {
        ReminderFragment reminderFragment = new ReminderFragment();
        Bundle args = new Bundle();
        try {
            if (taskReminderEntity != null)
                args.putSerializable("taskReminder", (TaskReminderEntity) taskReminderEntity.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        args.putSerializable("calendar", calendar);
        args.putString("taskReminderType", taskReminderType);
        reminderFragment.setArguments(args);
        return reminderFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;


    @Override
    protected void initView() {

        titleContent.setText("提醒");
        taskReminderEntity = (TaskReminderEntity) getArguments().getSerializable("taskReminder");
        calendar = (Calendar) getArguments().getSerializable("calendar");
        taskReminderType = getArguments().getString("taskReminderType");

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerview.setAdapter(reminderListAdapter = new ReminderListAdapter());
        if (taskReminderEntity != null)
            reminderListAdapter.setTaskReminderType(taskReminderEntity.taskReminderType);
        reminderListAdapter.setOnItemClickListener(this);
        reminderListAdapter.setOnItemChildClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        List<ReminderItemEntity> reminderItemEntities = new ArrayList<>();
        if (taskReminderEntity != null) {
            if (TextUtils.equals(TaskReminderEntity.ALL_DAY, taskReminderType)) {
                for (Map.Entry<String, String> entry : TaskReminderUtils.alldayMap.entrySet()) {
                    ReminderItemEntity reminderItemEntity = new ReminderItemEntity();
                    reminderItemEntity.timeKey = entry.getKey();
                    reminderItemEntity.timeValue = entry.getValue();
                    reminderItemEntities.add(reminderItemEntity);
                }
            } else if (TextUtils.equals(TaskReminderEntity.PRECISE, taskReminderType)) {
                for (Map.Entry<String, String> entry : TaskReminderUtils.preciseMap.entrySet()) {
                    ReminderItemEntity reminderItemEntity = new ReminderItemEntity();
                    reminderItemEntity.timeKey = entry.getKey();
                    reminderItemEntity.timeValue = entry.getValue();
                    reminderItemEntities.add(reminderItemEntity);
                }
            }
            /**
             * ruleTime设置时间集合
             * 根据ruleTime --->
             */
            if (!TextUtils.equals(taskReminderType, taskReminderEntity.taskReminderType)) {
                if (taskReminderEntity.ruleTime != null) {
                    for (String ruleTimeitem : taskReminderEntity.ruleTime) {
                        if (taskReminderEntity.customTime == null) {
                            taskReminderEntity.customTime = new ArrayList<>();
                        }
                        if (TextUtils.equals(taskReminderType, TaskReminderEntity.ALL_DAY)) {
                            if (!TaskReminderUtils.alldayMap.containsKey(ruleTimeitem)) {
                                addCoustomReminder(ruleTimeitem, taskReminderType);
                            } else {
                                if (calendar != null) {
                                    if (!TextUtils.equals(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE), "09:00")) {
                                        addCoustomReminder(ruleTimeitem, taskReminderType);
                                    }
                                }
                            }
                        } else if (TextUtils.equals(taskReminderType, TaskReminderEntity.PRECISE)) {
                            if (!TaskReminderUtils.preciseMap.containsKey(ruleTimeitem)) {
                                addCoustomReminder(ruleTimeitem, taskReminderType);
                            }
                        }
                    }
                }
            }

            if (taskReminderEntity.customTime != null) {
                for (TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity : taskReminderEntity.customTime) {
                    ReminderItemEntity reminderItemEntity = new ReminderItemEntity();
                    reminderItemEntity.customTimeItemEntity = customTimeItemEntity;
                    reminderItemEntities.add(reminderItemEntity);
                }
            }
            reminderListAdapter.bindData(true, reminderItemEntities);
            if (taskReminderEntity.customTime != null) {
                for (int i = 0; i < reminderListAdapter.getData().size(); i++) {
                    if (reminderListAdapter.getData().get(i) != null) {
                        if (reminderListAdapter.getData().get(i).customTimeItemEntity != null) {
                            reminderListAdapter.setSelected(i, true);
                        }
                    }
                }
            }
            if (taskReminderEntity.ruleTime != null) {
                for (int i = 0; i < reminderItemEntities.size(); i++) {
                    if (taskReminderEntity.ruleTime.contains(reminderItemEntities.get(i).timeKey)) {
                        if (TextUtils.equals(taskReminderType, TaskReminderEntity.ALL_DAY)) {
                            if (!TextUtils.equals(taskReminderType, taskReminderEntity.taskReminderType)) {
                                if (calendar != null) {
                                    if (TextUtils.equals(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE), "09:00")) {
                                        reminderListAdapter.setSelected(i, true);
                                    }
                                } else {
                                    reminderListAdapter.setSelected(i, true);
                                }
                            }else {
                                reminderListAdapter.setSelected(i, true);
                            }
                        } else if (TextUtils.equals(taskReminderType, TaskReminderEntity.PRECISE)) {
                            reminderListAdapter.setSelected(i, true);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加自定义
     *
     * @param ruleTimeitem
     * @param taskReminderType
     */
    private void addCoustomReminder(String ruleTimeitem, String taskReminderType) {
        TaskReminderEntity.CustomTimeItemEntity entity = getCustomTime(ruleTimeitem, taskReminderType);
        if (!taskReminderEntity.customTime.contains(entity)) {
            taskReminderEntity.customTime.add(entity);
        }
    }

    /**
     * 默认转自定义
     * <p>
     * put("0MB", "任务到期时");
     * put("5MB", "5分钟前");
     * put("10MB", "10分钟前");
     * put("30MB", "半小时前");
     * put("1HB", "1小时前");
     * put("2HB", "2小时前");
     * put("1DB", "一天前");
     * put("2DB", "两天前");
     * <p>
     * <p>
     * put("ODB", "当天（9:00)");
     * put("1DB", "一天前（9:00)");
     * put("2DB", "两天前（9:00)");
     * put("1WB", "一周前（9:00)");
     *
     * @param timeKey
     * @return
     */
    private TaskReminderEntity.CustomTimeItemEntity getCustomTime(String timeKey, String taskReminderType) {
        TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity = new TaskReminderEntity.CustomTimeItemEntity();
        if (TextUtils.equals(taskReminderType, TaskReminderEntity.ALL_DAY)) {
            if (TaskReminderUtils.preciseMap.containsKey(timeKey) && calendar != null) {
                if (TextUtils.equals(timeKey, "0MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                } else if (TextUtils.equals(timeKey, "5MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getHHmm(DateUtils.getMillByHourmin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) - (5 * 60 * 1000)));
                } else if (TextUtils.equals(timeKey, "10MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getHHmm(DateUtils.getMillByHourmin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) - (10 * 60 * 1000)));
                } else if (TextUtils.equals(timeKey, "30MB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getHHmm(DateUtils.getMillByHourmin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) - (30 * 60 * 1000)));
                } else if (TextUtils.equals(timeKey, "1HB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getHHmm(DateUtils.getMillByHourmin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) - (60 * 60 * 1000)));
                } else if (TextUtils.equals(timeKey, "2HB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", DateUtils.getHHmm(DateUtils.getMillByHourmin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)) - (2 * 60 * 60 * 1000)));
                } else if (TextUtils.equals(timeKey, "1DB")) {
                    setAllDayReminder(customTimeItemEntity, "1", "day", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                } else if (TextUtils.equals(timeKey, "2DB")) {
                    setAllDayReminder(customTimeItemEntity, "2", "day", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                }
            }
        } else if (TextUtils.equals(taskReminderType, TaskReminderEntity.PRECISE)) {
            if (TaskReminderUtils.alldayMap.containsKey(timeKey)) {
                if (TextUtils.equals(timeKey, "ODB")) {
                    setAllDayReminder(customTimeItemEntity, "0", "day", "09:00");
                }
                if (TextUtils.equals(timeKey, "1DB")) {
                    setAllDayReminder(customTimeItemEntity, "1", "day", "09:00");
                }
                if (TextUtils.equals(timeKey, "2DB")) {
                    setAllDayReminder(customTimeItemEntity, "2", "day", "09:00");
                }
                if (TextUtils.equals(timeKey, "1WB")) {
                    setAllDayReminder(customTimeItemEntity, "7", "day", "09:00");
                }
            }
        }
        return customTimeItemEntity;
    }

    private void setAllDayReminder(TaskReminderEntity.CustomTimeItemEntity customTimeItemEntity, String unitNumber, String unit, String point) {
        customTimeItemEntity.unitNumber = unitNumber;
        customTimeItemEntity.unit = unit;
        customTimeItemEntity.point = point;
    }

    @OnClick({R.id.titleBack,
            R.id.bt_clear_reminder,
            R.id.bt_ok,
            R.id.add_reminder_text})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleBack:
                if (onPageFragmentCallBack != null) {
                    onPageFragmentCallBack.onRequest2Page(this, 0, 0, null);
                }
                break;
            case R.id.bt_clear_reminder://清除提醒
                reminderListAdapter.clearSelected();
                break;
            case R.id.bt_ok:
                if (getParentFragment() == null) return;
                if (getParentFragment().getChildFragmentManager() == null) return;
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    TaskReminderEntity taskReminderEntity = getTrlTaskReminderEntity();
                    bundle.putSerializable("taskReminder", taskReminderEntity);
                    onFragmentCallBackListener.onFragmentCallBack(ReminderFragment.this, DateSelectDialogFragment.SELECT_REMINDER_FINISH, bundle);
                }
                if (onPageFragmentCallBack != null) {
                    onPageFragmentCallBack.onRequest2Page(this, 0, 0, null);
                }
                break;
            case R.id.add_reminder_text://添加自定义
                ReminderItemEntity itemEntity = new ReminderItemEntity();
                TaskReminderEntity.CustomTimeItemEntity ctie = new TaskReminderEntity.CustomTimeItemEntity();
                ctie.point = "09:00";
                ctie.unitNumber = "1";
                ctie.unit = "day";
                itemEntity.customTimeItemEntity = ctie;
                reminderListAdapter.addItem(itemEntity);
                customPosition = reminderListAdapter.getItemCount() - 1;
                reminderListAdapter.setSelected(customPosition, true);
                scrollToPosition(customPosition);
                break;
        }
    }


    /**
     * 获取返回的数据
     *
     * @return
     */
    private TaskReminderEntity getTrlTaskReminderEntity() {
        TaskReminderEntity entity = new TaskReminderEntity();
        entity.ruleTime = new ArrayList<>();
        if (taskReminderEntity != null) {
            entity.taskReminderType = taskReminderType;
        }
        if (reminderListAdapter != null) {
            if (reminderListAdapter.getSelectedData().size() > 0) {
                for (ReminderItemEntity reminderItemEntity : reminderListAdapter.getSelectedData()) {
                    if (!TextUtils.isEmpty(reminderItemEntity.timeKey)) {
                        entity.ruleTime.add(reminderItemEntity.timeKey);
                    }
                    if (reminderItemEntity.customTimeItemEntity != null) {
                        if (entity.customTime == null) {
                            entity.customTime = new ArrayList<>();
                        }
                        entity.customTime.add(reminderItemEntity.customTimeItemEntity);
                    }
                }
                return entity;
            }

        }
        return entity;
    }

    /**
     * 滚动到指定位置
     */
    private void scrollToPosition(int position) {
        if (linearLayoutManager != null && linearLayoutManager.getItemCount() > 0) {
            linearLayoutManager.scrollToPositionWithOffset(position, 50);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {

        reminderListAdapter.toggleSelected(position);
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        customPosition = position;
        ((ReminderListAdapter) adapter).setCustomPosition(position);
        adapter.notifyDataSetChanged();
        scrollToPosition(position);
        TextView pointTv = holder.obtainView(R.id.custom_point_text);
        TextView unitNumberTv = holder.obtainView(R.id.custom_unit_number_text);
        TextView unitTv = holder.obtainView(R.id.custom_unit_text);
        switch (view.getId()) {

            case R.id.custom_point_text:
                ((ReminderListAdapter) adapter).setSelect_type(2);
                ((WheelView) holder.obtainView(R.id.hour_wheelView)).setAdapter(new TimeWheelAdapter(getTime24or60(24)));
                ((WheelView) holder.obtainView(R.id.minute_wheelView)).setAdapter(new TimeWheelAdapter(getTime24or60(60)));

                unitNumberTv.setTextColor(Color.parseColor("#d9d9d9"));
                unitTv.setTextColor(Color.parseColor("#d9d9d9"));
                pointTv.setTextColor(Color.parseColor("#4a4a4a"));

                break;
            case R.id.custom_unit_number_text:
            case R.id.custom_unit_text:

                unitNumberTv.setTextColor(Color.parseColor("#4a4a4a"));
                unitTv.setTextColor(Color.parseColor("#4a4a4a"));
                pointTv.setTextColor(Color.parseColor("#d9d9d9"));
                ((ReminderListAdapter) adapter).setSelect_type(1);
                break;
        }
    }


    /**
     * 获取时间：小时list
     *
     * @return
     */
    private List<String> getTime24or60(int num) {
        List<String> timeList = new ArrayList<>();
        for (int i = 1; i < num; i++) {
            timeList.add(String.valueOf(i));
        }
        return timeList;
    }
}
