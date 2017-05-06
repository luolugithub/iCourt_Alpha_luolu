package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.widget.dialog.CenterMenuDialog;

import java.util.Arrays;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class TaskAdapter extends BaseArrayRecyclerAdapter<TaskEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    public TaskAdapter() {
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_parent_task;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity taskEntity, int position) {
        if (taskEntity == null) return;
        TextView task_title_time_tv = holder.obtainView(R.id.task_time_group_name);
        TextView task_title_count_tv = holder.obtainView(R.id.task_time_group_count);
        task_title_time_tv.setText(taskEntity.groupName);
        task_title_count_tv.setText(String.valueOf(taskEntity.groupTaskCount));
        RecyclerView recyclerView = holder.obtainView(R.id.parent_item_task_recyclerview);

        if (recyclerView.getLayoutManager() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
            recyclerView.setLayoutManager(layoutManager);
        }
        if (recyclerView.getAdapter() == null) {
            TaskItemAdapter taskItemAdapter = new TaskItemAdapter();
            recyclerView.setAdapter(taskItemAdapter);
            taskItemAdapter.setOnItemClickListener(super.onItemClickListener);
            taskItemAdapter.bindData(false, taskEntity.taskItemEntitys);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        new CenterMenuDialog(view.getContext(), null, Arrays.asList(
                new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message),
                new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message)))
                .show();
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        new CenterMenuDialog(view.getContext(), null, Arrays.asList(
                new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message),
                new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message)))
                .show();
        return true;
    }
}
