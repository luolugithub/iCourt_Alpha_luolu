package com.icourt.alpha.adapter;

import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description  项目下成员列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectMembersAdapter extends BaseArrayRecyclerAdapter<ProjectDetailEntity.MembersBean> {


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_project_member;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectDetailEntity.MembersBean memberBean, int position) {
        ImageView photoView = holder.obtainView(R.id.iv_member_photo);
        if (GlideUtils.canLoadImage(photoView.getContext())) {
            GlideUtils.loadUser(photoView.getContext(), memberBean.pic, photoView);
        }
    }
}
