package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskGroupEntity;

/**
 * Description  任务组适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectTaskGroupAdapter extends BaseArrayRecyclerAdapter<TaskGroupEntity> implements BaseRecyclerAdapter.OnItemClickListener {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_group_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskGroupEntity taskGroupEntity, int position) {
        TextView nameView = (TextView) holder.obtainView(R.id.task_group_name);
        TextView countView = (TextView) holder.obtainView(R.id.task_group_count);
        nameView.setText(taskGroupEntity.name);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

    }
}
