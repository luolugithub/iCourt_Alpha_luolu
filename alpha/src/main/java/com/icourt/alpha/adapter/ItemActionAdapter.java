package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.ItemsEntityImp;
import com.icourt.alpha.utils.GlideUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/12
 * version 1.0.0
 */
public class ItemActionAdapter<T extends ItemsEntityImp> extends BaseArrayRecyclerAdapter<T> {

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_action;
    }

    @Override
    public void onBindHoder(ViewHolder holder, T t, int position) {
        if (t == null) return;
        ImageView iv_icon = holder.obtainView(R.id.iv_icon);
        TextView tv_name = holder.obtainView(R.id.tv_name);
        if (!TextUtils.isEmpty(t.getItemIcon())) {
            GlideUtils.loadUser(iv_icon.getContext(), t.getItemIcon(), iv_icon);
        } else {
            iv_icon.setImageResource(t.getItemIconRes());
        }
        tv_name.setText(t.getItemTitle());
    }
}
