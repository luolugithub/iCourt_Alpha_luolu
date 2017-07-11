package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.ReminderItemEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;

import java.util.ArrayList;
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

    private String taskReminderType;
    int customPosition;//自定义的position

    public void setTaskReminderType(String taskReminderType) {
        this.taskReminderType = taskReminderType;
    }

    public void setCustomPosition(int customPosition) {
        this.customPosition = customPosition;
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
            TextView pointTv = holder.obtainView(R.id.custom_point_text);
            ImageView checkView = holder.obtainView(R.id.custom_check_image);

            final WheelView hourWheelView = holder.obtainView(R.id.hour_wheelView);
            final WheelView minuteWheelView = holder.obtainView(R.id.minute_wheelView);
            LinearLayout deadlineSelectLl = holder.obtainView(R.id.deadline_select_ll);

            if (reminderItemEntity.customTimeItemEntity != null) {
                unitNumberTv.setText(reminderItemEntity.customTimeItemEntity.unitNumber);
                unitTv.setText(reminderItemEntity.customTimeItemEntity.unit);
                pointTv.setText(reminderItemEntity.customTimeItemEntity.point);
                if (TextUtils.equals(reminderItemEntity.customTimeItemEntity.unit, "天前")) {
                    pointTv.setVisibility(View.VISIBLE);
                } else {
                    pointTv.setVisibility(View.INVISIBLE);
                }
            }

            if (TextUtils.equals(TaskReminderEntity.ALL_DAY, taskReminderType)) {
                hourWheelView.setAdapter(new TimeWheelAdapter(getDay365()));
                minuteWheelView.setAdapter(new TimeWheelAdapter(getUnit("天", null, null)));
            } else if (TextUtils.equals(TaskReminderEntity.PRECISE, taskReminderType)) {
                hourWheelView.setAdapter(new TimeWheelAdapter(getDay365()));
                minuteWheelView.setAdapter(new TimeWheelAdapter(getUnit("天", "小时", "分钟")));
            }

            hourWheelView.setCurrentItem(0);
            hourWheelView.setTextSize(16);
            hourWheelView.setCyclic(false);
            hourWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int i) {
                    unitNumberTv.setText((String) hourWheelView.getAdapter().getItem(i));
                }
            });
            minuteWheelView.setCurrentItem(0);
            minuteWheelView.setTextSize(16);
            minuteWheelView.setCyclic(false);
            minuteWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int i) {

                    unitTv.setText(((String) minuteWheelView.getAdapter().getItem(i)) + "前");
                    if (TextUtils.equals((String) minuteWheelView.getAdapter().getItem(i), "天")) {
                        hourWheelView.setAdapter(new TimeWheelAdapter(getDay365()));
                    } else if (TextUtils.equals((String) minuteWheelView.getAdapter().getItem(i), "小时") || TextUtils.equals((String) minuteWheelView.getAdapter().getItem(i), "分钟")) {
                        hourWheelView.setAdapter(new TimeWheelAdapter(getHourOrMin99()));
                    }
                }
            });

            checkView.setImageResource(R.mipmap.checkmark);
            holder.bindChildClick(unitNumberTv);
            holder.bindChildClick(unitTv);
            holder.bindChildClick(pointTv);
            if (customPosition == position) {
                deadlineSelectLl.setVisibility(View.VISIBLE);
            } else {
                deadlineSelectLl.setVisibility(View.GONE);
            }


            deadlineSelectLl.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });

        }
    }

    private class TimeWheelAdapter implements WheelAdapter<String> {
        List<String> timeList = new ArrayList<>();

        public TimeWheelAdapter(List<String> timeList) {
            this.timeList = timeList;
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
}
