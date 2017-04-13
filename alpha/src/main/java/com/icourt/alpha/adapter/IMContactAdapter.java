package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Descriptionn  联系人适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class IMContactAdapter extends BaseArrayRecyclerAdapter<GroupContactBean> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_im_contact;
    }

    @Override
    public void onBindHoder(ViewHolder holder, GroupContactBean groupContactBean, int position) {
        if (groupContactBean == null) return;
        ImageView iv_contact_icon = holder.obtainView(R.id.iv_contact_icon);
        TextView tv_contact_name = holder.obtainView(R.id.tv_contact_name);
        GlideUtils.loadUser(iv_contact_icon.getContext(), groupContactBean.pic, iv_contact_icon);
        tv_contact_name.setText(TextUtils.isEmpty(groupContactBean.name)
                ? groupContactBean.userName : groupContactBean.name);
    }

}
