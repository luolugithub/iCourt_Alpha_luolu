package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.SelectedRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class TaskSelectAdapter extends SelectedRecyclerAdapter<TaskEntity.TaskItemEntity> {

    public TaskSelectAdapter(boolean selectable) {
        super(selectable);
    }

    @Override
    public int bindView(int viewType) {
        return R.layout.adapter_item_task_simple;
    }

    @Override
    public void onBindSelectableHoder(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, int position, boolean selected) {
        if (taskItemEntity == null) {
            return;
        }
        TextView work_type_title_tv = holder.obtainView(R.id.task_title_tv);
        work_type_title_tv.setText(taskItemEntity.name);
        ImageView work_type_arrow_iv = holder.obtainView(R.id.task_arrow_iv);
        work_type_arrow_iv.setImageResource(selected ? R.mipmap.checkmark : 0);
    }
}
