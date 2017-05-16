package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskOwerEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description  选择任务负责人
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/16
 * version 2.0.0
 */

public class TaskOwerListAdapter extends MultiSelectRecyclerAdapter<TaskOwerEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_ower_layout;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, TaskOwerEntity taskOwerEntity, boolean selected, int position) {
        ImageView userIcon = holder.obtainView(R.id.user_icon);
        TextView nameView = holder.obtainView(R.id.user_name_tv);
        ImageView arrowView = holder.obtainView(R.id.isSelected_view);

        GlideUtils.loadUser(userIcon.getContext(), taskOwerEntity.pic, userIcon);
        nameView.setText(taskOwerEntity.name);
        arrowView.setImageResource(selected ? R.mipmap.checkmark : 0);
    }
}
