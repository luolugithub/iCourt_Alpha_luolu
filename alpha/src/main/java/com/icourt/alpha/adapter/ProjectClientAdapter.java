package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.SpUtils;

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;

/**
 * Description  项目概览客户item的子适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/18
 * version 2.0.0
 */

public class ProjectClientAdapter extends BaseArrayRecyclerAdapter<ProjectDetailEntity.ClientsBean> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_project_client_item_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectDetailEntity.ClientsBean clientsBean, int position) {
        TextView name = holder.obtainView(R.id.client_name_tv);
        ImageView rightView = holder.obtainView(R.id.right_arrow_iv);
        name.setText(clientsBean.contactName);
        rightView.setVisibility(hasCustomerPermission() ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }
}
