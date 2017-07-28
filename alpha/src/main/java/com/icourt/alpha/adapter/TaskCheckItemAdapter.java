package com.icourt.alpha.adapter;

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
        ImageView checkedTextView = holder.obtainView(R.id.check_item_checktext_tv);
        TextView nameView = holder.obtainView(R.id.check_item_name_tv);
        ImageView deleteView = holder.obtainView(R.id.check_item_delete_image);
        if (itemEntity.state) {
            checkedTextView.setImageResource(R.mipmap.checkbox_square_checked_highlight);
            nameView.setTextColor(0xFF8c8f92);
        } else {
            checkedTextView.setImageResource(R.mipmap.checkbox_square);
            nameView.setTextColor(0xFF4A4A4A);
        }

        nameView.setText(itemEntity.name);
//        SpannableUtils.setCommentUrlView(nameView,itemEntity.name);  //检查项支持链接
        holder.bindChildClick(checkedTextView);
        holder.bindChildClick(deleteView);
    }
}
