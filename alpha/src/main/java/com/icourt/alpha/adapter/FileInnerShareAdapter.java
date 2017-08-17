package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.SFileShareUserInfo;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/16
 * version 2.1.0
 */
public class FileInnerShareAdapter extends BaseArrayRecyclerAdapter<SFileShareUserInfo> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_inner_share;
    }

    @Override
    public void onBindHoder(ViewHolder holder, SFileShareUserInfo sFileShareUserInfo, int position) {
        if (sFileShareUserInfo == null) return;
        ImageView user_icon_iv = holder.obtainView(R.id.user_icon_iv);
        user_icon_iv.setVisibility(View.GONE);//暂时不支持


        TextView user_name_tv = holder.obtainView(R.id.user_name_tv);
        TextView user_action_tv = holder.obtainView(R.id.user_action_tv);

        user_action_tv.setText(TextUtils.equals(sFileShareUserInfo.permission, "rw") ? "可读写" : "只读");
        holder.bindChildClick(user_action_tv);
        user_name_tv.setText(sFileShareUserInfo.user_info != null ? sFileShareUserInfo.user_info.nickname : "");
    }
}
