package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description 项目成员列表（纵向）
 * Company Beijing icourt
 * author  youxuan  E-mail:zhaolu@icourt.cc
 * date createTime：2017/8/19
 * version 2.0.0
 */
public class ProjectMemberAdapter<T> extends BaseArrayRecyclerAdapter<T> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_project_members_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, T t, int position) {
        if (t == null) return;
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        TextView roleTv = holder.obtainView(R.id.tv_role);
        if (t instanceof ProjectDetailEntity.MembersBean) {
            ProjectDetailEntity.MembersBean membersBean = (ProjectDetailEntity.MembersBean) t;
            GlideUtils.loadUser(iv_contact_icon.getContext(), membersBean.pic, iv_contact_icon);
            tv_contact_name.setText(membersBean.userName);
            roleTv.setText(membersBean.roleName);
        } else if (t instanceof ProjectDetailEntity.AttorneysBean) {
            ProjectDetailEntity.AttorneysBean attorneysBean = (ProjectDetailEntity.AttorneysBean) t;
            GlideUtils.loadUser(iv_contact_icon.getContext(), attorneysBean.attorneyPic, iv_contact_icon);
            tv_contact_name.setText(attorneysBean.attorneyName);
            roleTv.setText(getLawyerType(attorneysBean.attorneyType));
        }
    }

    /**
     * 获取律师类型名称
     *
     * @param type
     * @return
     */
    private String getLawyerType(String type) {
        if (TextUtils.equals("R", type)) {
            return "主办律师";
        } else if (TextUtils.equals("A", type)) {
            return "协办律师";
        } else if (TextUtils.equals("S", type)) {
            return "案源律师";
        } else if (TextUtils.equals("O", type)) {
            return "参与人";
        } else if (TextUtils.equals("C", type)) {
            return "负责人";
        }
        return "";
    }
}
