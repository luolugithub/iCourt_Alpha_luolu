package com.icourt.alpha.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseAdapter;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.GlideUtils;

/**
 * @author youxuan  E-mail:xuanyouwu@163.com
 * @version 2.2.1
 * @Description
 * @Company Beijing icourt
 * @date createTime：2017/11/4
 */
public class ContactAdapter extends BaseAdapter<GroupContactBean> {
    @Override
    public int bindView(int i) {
        return R.layout.adapter_item_im_contact;
    }

    @Override
    public void onBindHolder(BaseViewHolder holder, @Nullable GroupContactBean groupContactBean, int i) {
        if (groupContactBean == null) {
            return;
        }
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        TextView ctv_contact = holder.obtainView(R.id.ctv_contact);
        ctv_contact.setVisibility(View.GONE);
        GlideUtils.loadUser(iv_contact_icon.getContext(), groupContactBean.pic, iv_contact_icon);
        tv_contact_name.setText(groupContactBean.name);
    }
}
