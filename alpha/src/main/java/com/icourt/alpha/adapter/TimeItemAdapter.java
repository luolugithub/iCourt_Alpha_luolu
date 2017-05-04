package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TimeItemAdapter extends BaseArrayRecyclerAdapter<TimeEntity.ItemEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_time;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TimeEntity.ItemEntity itemEntity, int position) {
        TextView durationView = holder.obtainView(R.id.time_item_duration_tv);
        TextView quantumView = holder.obtainView(R.id.time_item_quantum_tv);
        ImageView photoView = holder.obtainView(R.id.time_item_user_photo_image);
        TextView descView = holder.obtainView(R.id.time_item_desc_tv);
        TextView userNameView = holder.obtainView(R.id.time_item_user_name_tv);
        TextView typeView = holder.obtainView(R.id.time_item_type_tv);
        durationView.setText("00:23");
        quantumView.setText("11:00-11:23");
        GlideUtils.loadUser(holder.itemView.getContext(), itemEntity.timeUserPic, photoView);
        descView.setText(itemEntity.timeDes);
        userNameView.setText(itemEntity.timeUserName);
        typeView.setText(itemEntity.timeType);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }

    @Override
    public void onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }
}
