package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/2
 * version 2.0.0
 */

public class ProjectListAdapter extends BaseArrayRecyclerAdapter<ProjectEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {


    public ProjectListAdapter() {
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_project;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectEntity projectEntity, int position) {
        if (projectEntity == null) return;
        ImageView headerIcon = holder.obtainView(R.id.header_imageview);
        TextView nameView = holder.obtainView(R.id.project_name);
        TextView typeView = holder.obtainView(R.id.project_type);
        TextView taskView = holder.obtainView(R.id.project_task);

        nameView.setText(projectEntity.name);
        taskView.setText(projectEntity.unfinishTask + "/" + projectEntity.allTask);
        switch (Integer.valueOf(projectEntity.matterType)) {
            case 0:
                typeView.setText("争议解决");
                headerIcon.setImageResource(R.mipmap.project_type_dis);
                break;
            case 1:
                typeView.setText("非诉专项");
                headerIcon.setImageResource(R.mipmap.project_type_noju);
                break;
            case 2:
                typeView.setText("常年顾问");
                headerIcon.setImageResource(R.mipmap.project_type_coun);
                break;
            case 3:
                typeView.setText("内部事务");
                headerIcon.setImageResource(R.mipmap.project_type_aff);
                break;
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        ProjectEntity projectEntity = (ProjectEntity) adapter.getItem(position);
        if (projectEntity != null) {
            ProjectDetailActivity.launch(view.getContext(), projectEntity.pkId, projectEntity.name);
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

        return true;
    }
}
