package com.icourt.alpha.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseAdapter;
import com.icourt.alpha.entity.bean.SFileShareUserInfo;
import com.icourt.alpha.utils.GlideUtils;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/16
 * version 2.1.0
 */
public class FileInnerShareAdapter extends BaseAdapter<SFileShareUserInfo> {
    boolean canEdit;

    public FileInnerShareAdapter(boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_file_inner_share;
    }

    @Override
    public void onBindHolder(BaseViewHolder holder, @Nullable SFileShareUserInfo sFileShareUserInfo, int i) {
        if (sFileShareUserInfo == null) {
            return;
        }
        ImageView user_icon_iv = holder.obtainView(R.id.user_icon_iv);

        GlideUtils.loadUser(
                user_icon_iv.getContext(),
                sFileShareUserInfo.userInfo != null ? sFileShareUserInfo.userInfo.pic : "",
                user_icon_iv);


        TextView user_name_tv = holder.obtainView(R.id.user_name_tv);
        TextView user_action_tv = holder.obtainView(R.id.user_action_tv);
        user_name_tv.setText(sFileShareUserInfo.userInfo != null ?
                sFileShareUserInfo.userInfo.nickName : "");

        user_action_tv.setText(TextUtils.equals(sFileShareUserInfo.permission, PERMISSION_RW) ? "可读写" : "只读");
        holder.bindChildClick(user_action_tv);
        user_action_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, canEdit ? R.mipmap.arrow_bottom : 0, 0);

    }
}
