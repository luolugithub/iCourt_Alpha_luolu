package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;

/**
 * Description  项目概览客户item的子适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/18
 * version 2.0.0
 */

public class ProjectClientAdapter extends BaseArrayRecyclerAdapter<ProjectDetailEntity.ClientsBean> implements BaseRecyclerAdapter.OnItemClickListener{

    public ProjectClientAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_project_client_item_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectDetailEntity.ClientsBean clientsBean, int position) {
        TextView name = holder.obtainView(R.id.client_name_tv);
        name.setText(clientsBean.contactName);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        ProjectDetailEntity.ClientsBean clientsBean = getItem(position);
    }
}
