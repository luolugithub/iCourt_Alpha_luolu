package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.SelectedRecyclerAdapter;
import com.icourt.alpha.entity.bean.FilterDropEntity;

/**
 * Description
 * Company Beijing icourt
 * author  zhaolu  E-mail:zhaolu@icourt.cc
 * date createTime：2017/8/4
 * version 2.0.0
 */
public class ListDropDownAdapter extends SelectedRecyclerAdapter<FilterDropEntity> {

    public ListDropDownAdapter(boolean selectable) {
        super(selectable);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_dropdown_layout;
    }

    @Override
    public void onBindSelectableHoder(ViewHolder holder, FilterDropEntity filterDropEntity, int position, boolean selected) {
        if (filterDropEntity == null) return;
        TextView drop_title_tv = holder.obtainView(R.id.drop_title_tv);
        TextView drop_count_tv = holder.obtainView(R.id.drop_count_tv);
        drop_title_tv.setText(filterDropEntity.name);
        drop_count_tv.setText(filterDropEntity.count);
        CheckedTextView ctv_contact = holder.obtainView(R.id.drop_ctv);
        if (ctv_contact != null) {
            if (isSelectable() && ctv_contact.getVisibility() != View.VISIBLE) {
                ctv_contact.setVisibility(View.VISIBLE);
            } else if (!isSelectable() && ctv_contact.getVisibility() != View.GONE) {
                ctv_contact.setVisibility(View.GONE);
            }
            ctv_contact.setBackgroundResource(selected ? R.mipmap.checkmark : 0);
        }
    }
}
