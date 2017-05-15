package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.view.recyclerviewDivider.ITimeDividerInterface;

import java.util.HashMap;

/**
 * Description 计时
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TimeAdapter extends BaseArrayRecyclerAdapter<TimeEntity.ItemEntity> implements ITimeDividerInterface {

    private static final int TIME_TOP_TYPE = 0;
    private static final int TIME_OTHER_TYPE = 1;
    private static final int TIME_SIMPLE_TITLE = 2;
    private HashMap<Integer, Long> timeShowArray = new HashMap<>();//时间分割线


    private boolean useSimpleTitle;

    public TimeAdapter(boolean useSimpleTitle) {
        this.useSimpleTitle = useSimpleTitle;
    }

    public TimeAdapter() {
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
        timer_title_tv.setText(timeEntity.name);
    }

    /**
     * 设置顶部数据
     */
    private void setTypeTopData(ViewHolder holder, TimeEntity.ItemEntity timeEntity) {
        TextView totalView = holder.obtainView(R.id.time_top_total_tv);
        ImageView addView = holder.obtainView(R.id.time_top_add_img);
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
        durationView.setText(DateUtils.getTimeDurationDate(timeEntity.useTime));
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
}
