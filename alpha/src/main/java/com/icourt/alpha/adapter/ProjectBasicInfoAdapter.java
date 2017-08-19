package com.icourt.alpha.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectBasicTextInfoActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/17
 * version 2.0.0
 */

public class ProjectBasicInfoAdapter extends BaseArrayRecyclerAdapter<ProjectBasicItemEntity> implements BaseRecyclerAdapter.OnItemClickListener {

    private static final int CLIENT_TYPE = 0;//客户type
    private static final int OTHER_TYPE = 1;//其他type
    private List<ProjectDetailEntity.ClientsBean> clientsBeens = new ArrayList<>();

    public void setClientsBeens(List<ProjectDetailEntity.ClientsBean> clientsBeens) {
        this.clientsBeens = clientsBeens;
    }

    @Override
    public int getItemViewType(int position) {
        ProjectBasicItemEntity entity = getItem(position);
        if (entity.type == Const.PROJECT_CLIENT_TYPE) {
            return CLIENT_TYPE;
        }
        return OTHER_TYPE;
    }

    public ProjectBasicInfoAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        switch (viewtype) {
            case CLIENT_TYPE:
                return R.layout.project_detail_item_client_layout;
            case OTHER_TYPE:
                return R.layout.adapter_item_project_basic_info_layout;
        }
        return R.layout.adapter_item_project_basic_info_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectBasicItemEntity projectBasicItemEntity, int position) {
        ImageView iconView = holder.obtainView(R.id.key_icon);
        TextView keyView = holder.obtainView(R.id.key_name_tv);
        int resourcId = getImageByType(projectBasicItemEntity.type);
        if (resourcId > 0)
            iconView.setImageResource(getImageByType(projectBasicItemEntity.type));
        iconView.setVisibility(resourcId > 0 ? View.VISIBLE : View.GONE);
        keyView.setText(projectBasicItemEntity.key);
        if (getItemViewType(position) == CLIENT_TYPE) {
            RecyclerView recyclerView = holder.obtainView(R.id.client_recyclerview);
            ProjectClientAdapter projectClientAdapter = null;
            if (recyclerView.getLayoutManager() == null) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(projectClientAdapter = new ProjectClientAdapter());
            }
            projectClientAdapter = (ProjectClientAdapter) recyclerView.getAdapter();
            projectClientAdapter.bindData(true, clientsBeens);
        } else {
            TextView valueView = holder.obtainView(R.id.value_name_tv);
            valueView.setText(projectBasicItemEntity.value);
        }
    }

    private int getImageByType(int type) {
        switch (type) {
            case Const.PROJECT_NAME_TYPE://项目名称
                return R.mipmap.project_name_icon;
            case Const.PROJECT_TYPE_TYPE://项目类型
                return R.mipmap.project_type_icon;
            case Const.PROJECT_DEPARTMENT_TYPE://负责部门
                return R.mipmap.project_department_icon;
            case Const.PROJECT_CLIENT_TYPE://客户
                return R.mipmap.project_customer_icon;
            case Const.PROJECT_TIME_TYPE://项目时间
                return R.mipmap.project_time_icon;
            case Const.PROJECT_ANYUAN_LAWYER_TYPE://案源律师
                return R.mipmap.lawyer;
            case Const.PROJECT_NUMBER_TYPE://项目编号
                return R.mipmap.number;
        }
        return -1;
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        ProjectBasicItemEntity entity = getItem(position);
        switch (entity.type) {
            case Const.PROJECT_NAME_TYPE:
            case Const.PROJECT_TYPE_TYPE:
            case Const.PROJECT_NUMBER_TYPE:
            case Const.PROJECT_CASE_TYPE:
            case Const.PROJECT_CASENUMBER_TYPE:
            case Const.PROJECT_COMPETENT_TYPE:
                ProjectBasicTextInfoActivity.launch(view.getContext(), entity.value, entity.type);
                break;
        }
    }
}
