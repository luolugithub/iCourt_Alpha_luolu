package com.icourt.alpha.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.CommentEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SpannableUtils;

/**
 * Description   评论列表适配器
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class CommentListAdapter extends BaseArrayRecyclerAdapter<CommentEntity.CommentItemEntity> {


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_comment_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, CommentEntity.CommentItemEntity commentItemEntity, int position) {
        ImageView photoView = holder.obtainView(R.id.user_photo_image);
        TextView nameView = holder.obtainView(R.id.user_name_tv);
        TextView timeView = holder.obtainView(R.id.create_time_tv);
        TextView contentView = holder.obtainView(R.id.content_tv);
        if (commentItemEntity.createUser != null) {
            GlideUtils.loadUser(photoView.getContext(), commentItemEntity.createUser.pic, photoView);
            nameView.setText(commentItemEntity.createUser.userName);
        }
        timeView.setText(DateUtils.getFormatChatTime(commentItemEntity.createTime));
        holder.bindChildClick(photoView);
        SpannableUtils.setCommentUrlView(contentView, commentItemEntity.content);

    }



}
