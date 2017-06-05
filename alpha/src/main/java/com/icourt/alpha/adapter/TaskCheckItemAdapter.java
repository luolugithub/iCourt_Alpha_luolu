package com.icourt.alpha.adapter;

import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskCheckItemEntity;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskCheckItemAdapter extends MultiSelectRecyclerAdapter<TaskCheckItemEntity.ItemEntity> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_check_layout;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, TaskCheckItemEntity.ItemEntity itemEntity, boolean selected, int position) {
        CheckedTextView checkedTextView = holder.obtainView(R.id.check_item_checktext_tv);
        TextView nameView = holder.obtainView(R.id.check_item_name_tv);
        ImageView deleteView = holder.obtainView(R.id.check_item_delete_image);
        if (itemEntity.state) {
            checkedTextView.setChecked(true);
            getSelectedArray().put(position, true);
            nameView.setTextColor(0xFF8c8f92);
        } else {
            checkedTextView.setChecked(false);
            nameView.setTextColor(0xFF4A4A4A);
            getSelectedArray().delete(position);
        }

        nameView.setText(itemEntity.name);
        holder.bindChildClick(checkedTextView);
        holder.bindChildClick(deleteView);
    }
}
