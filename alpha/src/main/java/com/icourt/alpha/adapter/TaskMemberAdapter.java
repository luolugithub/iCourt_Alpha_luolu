package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.SelectedRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskMemberEntity;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public class TaskMemberAdapter extends SelectedRecyclerAdapter<TaskMemberEntity> {
    public TaskMemberAdapter(boolean selectable) {
        super(selectable);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_im_contact;
    }

    @Override
    public void onBindSelectableHoder(ViewHolder holder, TaskMemberEntity taskMemberEntity, int position, boolean selected) {
        if (taskMemberEntity == null) return;
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        GlideUtils.loadUser(iv_contact_icon.getContext(), taskMemberEntity.userPic, iv_contact_icon);
        tv_contact_name.setText(taskMemberEntity.userName);
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
