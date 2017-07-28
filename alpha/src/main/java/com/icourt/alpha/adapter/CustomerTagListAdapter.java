package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.SelectedRecyclerAdapter;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/18
 * version 2.0.0
 */

public class CustomerTagListAdapter extends SelectedRecyclerAdapter<String> {

    public CustomerTagListAdapter(boolean selectable) {
        super(selectable);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_select_group_layout;
    }

    @Override
    public void onBindSelectableHoder(ViewHolder holder, String entity, int position, boolean selected) {
        TextView nameView = holder.obtainView(R.id.group_name_tv);
        ImageView arrowView = holder.obtainView(R.id.group_isselect_view);

        nameView.setText(entity);
        arrowView.setImageResource(selected ? R.mipmap.checkmark : 0);
    }
}
