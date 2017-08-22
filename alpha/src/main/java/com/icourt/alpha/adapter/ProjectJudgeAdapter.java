package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;

/**
 * Description  项目下法官列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectJudgeAdapter<T> extends BaseArrayRecyclerAdapter<T> {


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_project_judge_item_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, T t, int position) {
        TextView nameview = holder.obtainView(R.id.judge_name_tv);
        TextView phoneview = holder.obtainView(R.id.judge_phone_tv);
        if (t instanceof ProjectDetailEntity.JudgeBean) {
            ProjectDetailEntity.JudgeBean judgeBean = (ProjectDetailEntity.JudgeBean) t;
            nameview.setText(judgeBean.name);
            phoneview.setText(judgeBean.phone);
        } else if (t instanceof ProjectDetailEntity.ClerkBean) {
            ProjectDetailEntity.ClerkBean clerkBean = (ProjectDetailEntity.ClerkBean) t;
            nameview.setText(clerkBean.name);
            phoneview.setText(clerkBean.phone);
        } else if (t instanceof ProjectDetailEntity.ArbitratorBean) {
            ProjectDetailEntity.ArbitratorBean arbitratorBean = (ProjectDetailEntity.ArbitratorBean) t;
            nameview.setText(arbitratorBean.name);
            phoneview.setText(arbitratorBean.phone);
        } else if (t instanceof ProjectDetailEntity.SecretarieBean) {
            ProjectDetailEntity.SecretarieBean secretarieBean = (ProjectDetailEntity.SecretarieBean) t;
            nameview.setText(secretarieBean.name);
            phoneview.setText(secretarieBean.phone);
        } else if (t instanceof ProjectDetailEntity.GroupsBean) {
            ProjectDetailEntity.GroupsBean groupsBean = (ProjectDetailEntity.GroupsBean) t;
            nameview.setText(groupsBean.name);
            phoneview.setVisibility(View.GONE);
        }
    }
}
