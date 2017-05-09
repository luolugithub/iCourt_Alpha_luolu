package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.RangeItemEntity;

/**
 * Description  项目程序信息适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/8
 * version 2.0.0
 */

public class ProjectRangeListAdapter extends BaseArrayRecyclerAdapter<RangeItemEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_project_range_item_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, RangeItemEntity rangeItemEntity, int position) {
        TextView keyView = (TextView) holder.obtainView(R.id.range_key_tv);
        TextView valueView = (TextView) holder.obtainView(R.id.range_value_tv);
        keyView.setText(rangeItemEntity.itemName);
        valueView.setText(rangeItemEntity.itemValue);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        return false;
    }
}
