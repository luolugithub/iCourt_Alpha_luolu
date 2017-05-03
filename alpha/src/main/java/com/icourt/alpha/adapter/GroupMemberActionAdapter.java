package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/2
 * version 1.0.0
 */
public class GroupMemberActionAdapter extends MultiSelectRecyclerAdapter<GroupContactBean> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_group_member_action;
    }


    @Override
    public void onBindSelectableHolder(ViewHolder holder, GroupContactBean groupContactBean, boolean selected, int position) {
        if (groupContactBean == null) return;
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        GlideUtils.loadUser(iv_contact_icon.getContext(), groupContactBean.pic, iv_contact_icon);
        tv_contact_name.setText(groupContactBean.name);
        CheckedTextView ctv_contact = holder.obtainView(R.id.ctv_contact);
        if (ctv_contact != null) {
            if (isSelectable() && ctv_contact.getVisibility() != View.VISIBLE) {
                ctv_contact.setVisibility(View.VISIBLE);
            } else if (!isSelectable() && ctv_contact.getVisibility() != View.GONE) {
                ctv_contact.setVisibility(View.GONE);
            }
            ctv_contact.setBackgroundResource(selected ? R.mipmap.checkmark : 0);
        }
    }
}
