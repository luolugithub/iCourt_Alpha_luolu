package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.SelectedRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskGroupEntity;

/**
 * Description  任务组适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectTaskGroupAdapter extends SelectedRecyclerAdapter<TaskGroupEntity> {
    boolean isCanEditGroup = false;//是否可以编辑任务组

    public ProjectTaskGroupAdapter(boolean selectable) {
        super(selectable);
    }

    public void setCanEditGroup(boolean canEditGroup) {
        isCanEditGroup = canEditGroup;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_group_layout;
    }

    @Override
    public void onBindSelectableHoder(ViewHolder holder, TaskGroupEntity taskGroupEntity, int position, boolean selected) {
        TextView nameView = (TextView) holder.obtainView(R.id.task_group_name);
        TextView countView = (TextView) holder.obtainView(R.id.task_group_count);
        ImageView task_group_arrow = holder.obtainView(R.id.task_group_arrow);
        nameView.setText(taskGroupEntity.name);

        if (isSelectable()) {
            task_group_arrow.setVisibility(View.VISIBLE);
            task_group_arrow.setImageResource(selected ? R.mipmap.checkmark : 0);
        } else {
            task_group_arrow.setImageResource(R.mipmap.arrow_right);
            task_group_arrow.setVisibility(isCanEditGroup ? View.VISIBLE : View.GONE);
            countView.setText(String.valueOf(taskGroupEntity.taskCount));
        }
    }

}
