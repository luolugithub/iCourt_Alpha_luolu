package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TimeEntity;

/**
 * Description 计时
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class TimeAdapter extends BaseArrayRecyclerAdapter<TimeEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    private static final int TIME_TOP_TYPE = 0;
    private static final int TIME_OTHER_TYPE = 1;

    @Override
    public int getItemViewType(int position) {
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
                return R.layout.adapter_item_time_parent;
        }
        return R.layout.adapter_item_time_parent;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TimeEntity timeEntity, int position) {
        switch (holder.getItemViewType()) {
            case TIME_TOP_TYPE:
                setTypeTopData(holder, timeEntity);
                break;
            case TIME_OTHER_TYPE:
                setTypeOtherData(holder, timeEntity);
                break;
        }
    }

    /**
     * 设置顶部数据
     */
    private void setTypeTopData(ViewHolder holder, TimeEntity timeEntity) {
        TextView totalView = holder.obtainView(R.id.time_top_total_tv);
        ImageView addView = holder.obtainView(R.id.time_top_add_img);
    }

    /**
     * 设置列表数据
     */
    public void setTypeOtherData(ViewHolder holder, TimeEntity timeEntity) {
        TextView dateView = holder.obtainView(R.id.time_parent_data_tv);
        RecyclerView recyclerView = holder.obtainView(R.id.time_parent_recyclerview);
        dateView.setText("5月4日");
        if (recyclerView.getLayoutManager() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);
        }
        if (recyclerView.getAdapter() == null) {
            TimeItemAdapter timeItemAdapter = new TimeItemAdapter();
            recyclerView.setAdapter(timeItemAdapter);
            timeItemAdapter.bindData(false, timeEntity.itemEntities);
            timeItemAdapter.setOnItemClickListener(super.onItemClickListener);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        showTopSnackBar(view, "position : " + position);
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        return false;
    }
}
