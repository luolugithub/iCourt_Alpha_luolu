package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description 项目成员列表
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：2017/8/19
 * version 2.0.0
 */
public class ProjectMemberAdapter extends BaseArrayRecyclerAdapter<ProjectDetailEntity.MembersBean> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_im_contact;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectDetailEntity.MembersBean membersBean, int position) {
        if (membersBean == null) return;
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        CheckedTextView ctv_contact = holder.obtainView(R.id.ctv_contact);
        GlideUtils.loadUser(iv_contact_icon.getContext(), membersBean.pic, iv_contact_icon);
        tv_contact_name.setText(membersBean.userName);
        ctv_contact.setVisibility(View.GONE);
    }
}
