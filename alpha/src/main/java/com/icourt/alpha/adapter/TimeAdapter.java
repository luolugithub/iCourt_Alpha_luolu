package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.view.recyclerviewDivider.ITimeDividerInterface;
import com.icourt.alpha.widget.manager.TimerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Description 计时
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TimeAdapter extends BaseArrayRecyclerAdapter<TimeEntity.ItemEntity> implements ITimeDividerInterface, BaseRecyclerAdapter.OnItemChildClickListener {

    private static final int TIME_TOP_TYPE = 0;
    private static final int TIME_OTHER_TYPE = 1;
    private static final int TIME_SIMPLE_TITLE = 2;
    private HashMap<Integer, Long> timeShowArray = new HashMap<>();//时间分割线
    private long sumTime;


    private boolean useSimpleTitle;

    @Override
    public boolean bindData(boolean isRefresh, List<TimeEntity.ItemEntity> datas) {
        if (isRefresh) {
            timeShowArray.clear();
        }
        return super.bindData(isRefresh, datas);
    }

    public TimeAdapter(boolean useSimpleTitle) {
        this.useSimpleTitle = useSimpleTitle;
        this.setOnItemChildClickListener(this);
    }

    public TimeAdapter() {
        this.setOnItemChildClickListener(this);
    }

    public void setSumTime(long sumTime) {
        this.sumTime = sumTime;
    }

    @Override
    public int getItemViewType(int position) {
        if (useSimpleTitle) {
            return TIME_SIMPLE_TITLE;
        }
        if (position == 0) {
            return TIME_TOP_TYPE;
        }
        return TIME_OTHER_TYPE;
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case TIME_TOP_TYPE:
                return R.layout.adapter_item_time_top;
            case TIME_OTHER_TYPE:
                return R.layout.adapter_item_time;
            case TIME_SIMPLE_TITLE:
                return R.layout.adapter_item_timing_simple;
        }
        return R.layout.adapter_item_time;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TimeEntity.ItemEntity timeEntity, int position) {
        switch (holder.getItemViewType()) {
            case TIME_TOP_TYPE:
                setTypeTopData(holder, timeEntity);
                break;
            case TIME_OTHER_TYPE:
                setTypeOtherData(holder, timeEntity, position);
                break;
            case TIME_SIMPLE_TITLE:
                setTypeSimpleTitle(holder, timeEntity, position);
                break;
        }
    }

    /**
     * 处理简单标题布局
     * R.layout.adapter_item_timing_simple
     *
     * @param holder
     * @param timeEntity
     * @param position
     */
    private void setTypeSimpleTitle(ViewHolder holder, TimeEntity.ItemEntity timeEntity, int position) {
        if (holder == null) return;
        if (timeEntity == null) return;
        ImageView timer_icon = holder.obtainView(R.id.timer_icon);
        TextView timer_count_tv = holder.obtainView(R.id.timer_count_tv);
        TextView timer_title_tv = holder.obtainView(R.id.timer_title_tv);
        View divider_ll = holder.obtainView(R.id.divider_ll);
        TextView divider_time = holder.obtainView(R.id.divider_time);
        TextView divider_time_count = holder.obtainView(R.id.divider_time_count);
        timer_title_tv.setText(TextUtils.isEmpty(timeEntity.name) ? "还未录入工作描述" : timeEntity.name);
        if (timeEntity.state == TimeEntity.ItemEntity.TIMER_STATE_START) {
            timer_count_tv.setText(toTime(timeEntity.useTime));
            timer_icon.setImageResource(R.drawable.orange_side_dot_bg);
        } else {
            timer_icon.setImageResource(R.mipmap.icon_start_20);
            try {
                timer_count_tv.setText(getHm(timeEntity.useTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.bindChildClick(timer_icon);
        addTimeDividerArray(timeEntity, position);
        if (timeShowArray.containsKey(position)) {
            divider_ll.setVisibility(View.VISIBLE);
            if (DateUtils.isToday(timeEntity.workDate)) {
                divider_time.setText("今天");
            } else if (DateUtils.isYesterday(timeEntity.workDate)) {
                divider_time.setText("昨天");
            } else {
                divider_time.setText(DateUtils.getMMMdd(timeEntity.workDate));
            }
            int dayTimingLength = 0;//某天的计时时长
            for (int i = position; i < getData().size(); i++) {
                TimeEntity.ItemEntity item = getItem(i);
                if (item != null && item.workDate == timeEntity.workDate) {
                    dayTimingLength += item.useTime;
                }
            }
            divider_time_count.setText(getHm(dayTimingLength));
        } else {
            divider_ll.setVisibility(View.GONE);
        }
    }

    public String toTime(long times) {
        times /= 1000;
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
    }

    public String getHm(long times) {
        times /= 1000;
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        if (minute <= 0) {
            minute = 1;
        }
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    /**
     * 设置顶部数据
     */
    private void setTypeTopData(ViewHolder holder, TimeEntity.ItemEntity timeEntity) {
        TextView totalView = holder.obtainView(R.id.time_top_total_tv);
        ImageView addView = holder.obtainView(R.id.time_top_add_img);
        if (sumTime > 0) {
            totalView.setText(getHm(sumTime));
        }
    }

    /**
     * 设置列表数据
     */
    public void setTypeOtherData(ViewHolder holder, TimeEntity.ItemEntity timeEntity, int position) {
        addTimeDividerArray(timeEntity, position);
        TextView durationView = holder.obtainView(R.id.time_item_duration_tv);
        TextView quantumView = holder.obtainView(R.id.time_item_quantum_tv);
        ImageView photoView = holder.obtainView(R.id.time_item_user_photo_image);
        TextView descView = holder.obtainView(R.id.time_item_desc_tv);
        TextView userNameView = holder.obtainView(R.id.time_item_user_name_tv);
        TextView typeView = holder.obtainView(R.id.time_item_type_tv);
        durationView.setText(getHm(timeEntity.useTime));
        quantumView.setText(DateUtils.getTimeDurationDate(timeEntity.startTime) + "-" + DateUtils.getTimeDurationDate(timeEntity.endTime));
//        GlideUtils.loadUser(holder.itemView.getContext(), itemEntity.timeUserPic, photoView);
        descView.setText(timeEntity.name);
        userNameView.setText(timeEntity.username);
        typeView.setText(timeEntity.workTypeName);
    }

    /**
     * 处理时间分割线
     *
     * @param timeEntity
     */
    private void addTimeDividerArray(TimeEntity.ItemEntity timeEntity, int position) {
        if (timeEntity == null) return;

        if (!timeShowArray.containsValue(timeEntity.workDate)) {
            timeShowArray.put(position, timeEntity.workDate);
        }
    }

    /**
     * 是否显示时间
     *
     * @param pos
     * @return
     */
    @Override
    public boolean isShowTimeDivider(int pos) {
        if (pos != 0) {
            TimeEntity.ItemEntity item = getItem(pos);
            return item != null && timeShowArray.get(pos) != null;
        }
        return false;
    }

    /**
     * 显示的时间字符串 isShowTimeDivider=true 不可以返回null
     *
     * @param pos
     * @return
     */
    @NonNull
    @Override
    public String getShowTime(int pos) {
        if (pos != 0) {
            TimeEntity.ItemEntity item = getItem(pos);
            return item != null ?
                    DateUtils.getTimeDate(item.workDate) : "null";
        }
        return "";
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        TimeEntity.ItemEntity item = getItem(getRealPos(position));
        if (item == null) return;
        switch (view.getId()) {
            case R.id.timer_icon:
                if (item.state == TimeEntity.TIMER_STATE_END_TYPE) {
                    TimerManager.getInstance().addTimer(item);
                } else {
                    TimerManager.getInstance().stopTimer();
                }
                break;
        }
    }
}
