package com.icourt.alpha.adapter;

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
        return R.layout.adapter_item_task;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity taskEntity, int position) {
        if (taskEntity == null) return;
        TextView task_title_tv = holder.obtainView(R.id.task_title_tv);
        TextView task_project_belong_tv = holder.obtainView(R.id.task_project_belong_tv);
        task_title_tv.setText(taskEntity.name);
        task_project_belong_tv.setText(taskEntity.matter == null ? "" : taskEntity.matter.name);
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
    public void onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        new CenterMenuDialog(view.getContext(), null, Arrays.asList(
                new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message),
                new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message)))
                .show();
    }
}
