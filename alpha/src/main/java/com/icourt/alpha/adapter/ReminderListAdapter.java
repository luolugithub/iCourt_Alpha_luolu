package com.icourt.alpha.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.ReminderItemEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.TaskReminderUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/18
 * version 2.0.0
 */

public class ReminderListAdapter extends MultiSelectRecyclerAdapter<ReminderItemEntity> {


    public static final int UNCUSTOM_TYPE = 1;//非自定义
    public static final int CUSTOM_TYPE = 2;//自定义

    public static final int SELECT_UNIT_TYPE = 1;//选择天、小时、分钟
    public static final int SELECT_TIME_TYPE = 2;//选择时间：  04:22

    private String taskReminderType;
    int customPosition;//自定义的position
    int select_type = SELECT_UNIT_TYPE;//选择器类型
    private RecyclerView recyclerView;
    private Calendar selectedCalendar;//选中的时间

    public void setTaskReminderType(String taskReminderType) {
        this.taskReminderType = taskReminderType;
    }

    public void setCustomPosition(int customPosition) {
        this.customPosition = customPosition;
    }

    public void setSelect_type(int select_type) {
        this.select_type = select_type;
        if (selectedCalendar == null) {
            selectedCalendar = Calendar.getInstance();
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).customTimeItemEntity == null) {
            return UNCUSTOM_TYPE;
        } else {
            return CUSTOM_TYPE;
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case UNCUSTOM_TYPE:
                return R.layout.adapter_item_select_group_layout;
            case CUSTOM_TYPE:
                return R.layout.adapter_item_custom_reminder_layout;
        }
        return R.layout.adapter_item_select_group_layout;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, final ReminderItemEntity reminderItemEntity, boolean selected, int position) {
        if (getItemViewType(position) == UNCUSTOM_TYPE) {
            TextView nameView = holder.obtainView(R.id.group_name_tv);
            ImageView arrowView = holder.obtainView(R.id.group_isselect_view);

            nameView.setText(reminderItemEntity.timeValue);
            arrowView.setImageResource(selected ? R.mipmap.checkmark : 0);
        } else if (getItemViewType(position) == CUSTOM_TYPE) {
            final TextView unitNumberTv = holder.obtainView(R.id.custom_unit_number_text);
            final TextView unitTv = holder.obtainView(R.id.custom_unit_text);
            final TextView pointTv = holder.obtainView(R.id.custom_point_text);
            ImageView checkView = holder.obtainView(R.id.custom_check_image);

            final WheelView hourWheelView = holder.obtainView(R.id.hour_wheelView);
            final WheelView minuteWheelView = holder.obtainView(R.id.minute_wheelView);
            LinearLayout deadlineSelectLl = holder.obtainView(R.id.deadline_select_ll);

            if (reminderItemEntity.customTimeItemEntity != null) {
                unitNumberTv.setText(reminderItemEntity.customTimeItemEntity.unitNumber);
                if (TaskReminderUtils.unitMap.containsKey(reminderItemEntity.customTimeItemEntity.unit)) {
                    unitTv.setText(TaskReminderUtils.unitMap.get(reminderItemEntity.customTimeItemEntity.unit) + "前");
                }
                pointTv.setText(reminderItemEntity.customTimeItemEntity.point);
                if (TextUtils.equals(reminderItemEntity.customTimeItemEntity.unit, "day")) {
                    pointTv.setVisibility(View.VISIBLE);
                } else {
                    pointTv.setVisibility(View.INVISIBLE);
                }
            }
            if (select_type == SELECT_UNIT_TYPE) {
                if (TextUtils.equals(TaskReminderEntity.ALL_DAY, taskReminderType)) {
                    hourWheelView.setAdapter(new TimeWheelAdapter(getDay365()));
                    minuteWheelView.setAdapter(new TimeWheelAdapter(getUnit("天", null, null)));
                } else if (TextUtils.equals(TaskReminderEntity.PRECISE, taskReminderType)) {
                    hourWheelView.setAdapter(new TimeWheelAdapter(getDay365()));
                    minuteWheelView.setAdapter(new TimeWheelAdapter(getUnit("天", "小时", "分钟")));
                }
            } else if (select_type == SELECT_TIME_TYPE) {
                hourWheelView.setAdapter(new TimeWheelAdapter(getTime24or60(24)));
                minuteWheelView.setAdapter(new TimeWheelAdapter(getTime24or60(60)));
            }

            hourWheelView.setCurrentItem(0);
            hourWheelView.setTextSize(16);
            hourWheelView.setCyclic(false);
            hourWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int i) {
                    if (select_type == SELECT_UNIT_TYPE) {
                        reminderItemEntity.customTimeItemEntity.unitNumber = (String) hourWheelView.getAdapter().getItem(i);
                        unitNumberTv.setText((String) hourWheelView.getAdapter().getItem(i));
                    } else if (select_type == SELECT_TIME_TYPE) {
                        if (selectedCalendar != null) {
                            selectedCalendar.set(Calendar.HOUR_OF_DAY, i);
                            selectedCalendar.set(Calendar.MILLISECOND, 0);
                            pointTv.setText(DateUtils.getHHmm(selectedCalendar.getTimeInMillis()));
                            reminderItemEntity.customTimeItemEntity.point = DateUtils.getHHmm(selectedCalendar.getTimeInMillis());
                        }
                    }
                }
            });
            minuteWheelView.setCurrentItem(0);
            minuteWheelView.setTextSize(16);
            minuteWheelView.setCyclic(false);
            minuteWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int i) {
                    String itemStr = ((String) minuteWheelView.getAdapter().getItem(i));
                    if (select_type == SELECT_UNIT_TYPE) {
                        if (TextUtils.equals(itemStr, "天")) {
                            reminderItemEntity.customTimeItemEntity.unit = "day";
                        } else if (TextUtils.equals(itemStr, "小时")) {
                            reminderItemEntity.customTimeItemEntity.unit = "hour";
                        } else if (TextUtils.equals(itemStr, "分钟")) {
                            reminderItemEntity.customTimeItemEntity.unit = "minute";
                        }

                        unitTv.setText(((String) minuteWheelView.getAdapter().getItem(i)) + "前");
                        if (TextUtils.equals((String) minuteWheelView.getAdapter().getItem(i), "天")) {
                            hourWheelView.setAdapter(new TimeWheelAdapter(getDay365()));
                            pointTv.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals((String) minuteWheelView.getAdapter().getItem(i), "小时") || TextUtils.equals((String) minuteWheelView.getAdapter().getItem(i), "分钟")) {
                            hourWheelView.setAdapter(new TimeWheelAdapter(getHourOrMin99()));
                            pointTv.setVisibility(View.GONE);
                        }
                    } else if (select_type == SELECT_TIME_TYPE) {
                        if (selectedCalendar != null) {
                            selectedCalendar.set(Calendar.MINUTE, i);
                            selectedCalendar.set(Calendar.MILLISECOND, 0);
                            pointTv.setText(DateUtils.getHHmm(selectedCalendar.getTimeInMillis()));
                            reminderItemEntity.customTimeItemEntity.point = DateUtils.getHHmm(selectedCalendar.getTimeInMillis());
                            LogUtils.e("i：" + selectedCalendar.get(Calendar.MINUTE));
                            LogUtils.e("Millis：" + selectedCalendar.getTimeInMillis());
                            LogUtils.e("选择时间：" + DateUtils.getHHmm(selectedCalendar.getTimeInMillis()));
                        }
                    }
                }
            });

            checkView.setImageResource(selected ? R.mipmap.checkmark : 0);
            holder.bindChildClick(unitNumberTv);
            holder.bindChildClick(unitTv);
            holder.bindChildClick(pointTv);
            if (customPosition == position) {
                deadlineSelectLl.setVisibility(View.VISIBLE);
            } else {
                deadlineSelectLl.setVisibility(View.GONE);
            }

            hourWheelView.setOnTouchListener(new CustOnTouchListener(recyclerView));
            minuteWheelView.setOnTouchListener(new CustOnTouchListener(recyclerView));

        }
    }

    private class CustOnTouchListener implements View.OnTouchListener {

        RecyclerView recyclerView;

        public CustOnTouchListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (recyclerView == null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    recyclerView.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    recyclerView.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        }
    }

    /**
     * 获取天数list
     *
     * @return
     */
    private List<String> getDay365() {
        List<String> timeList = new ArrayList<>();
        for (int i = 1; i < 366; i++) {
            timeList.add(String.valueOf(i));
        }
        return timeList;
    }

    /**
     * 获取小时／分钟 list
     *
     * @return
     */
    private List<String> getHourOrMin99() {
        List<String> timeList = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            timeList.add(String.valueOf(i));
        }
        return timeList;
    }

    /**
     * 获取单位 list
     *
     * @return
     */
    private List<String> getUnit(String day, String hour, String min) {
        List<String> timeList = new ArrayList<>();
        if (!TextUtils.isEmpty(day)) {
            timeList.add(day);
        }
        if (!TextUtils.isEmpty(hour)) {
            timeList.add(hour);
        }
        if (!TextUtils.isEmpty(min)) {
            timeList.add(min);
        }
        return timeList;
    }

    /**
     * 获取时间：小时list
     *
     * @return
     */
    private List<String> getTime24or60(int num) {
        List<String> timeList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            if (i < 10) {
                timeList.add("0" + String.valueOf(i));
            } else {
                timeList.add(String.valueOf(i));
            }
        }
        return timeList;
    }
}
