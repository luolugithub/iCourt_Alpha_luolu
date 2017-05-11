package com.icourt.alpha.adapter;

import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.view.TextDrawable;

/**
 * Description  任务相关人 列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/11
 * version 2.0.0
 */

public class TaskUsersAdapter extends BaseArrayRecyclerAdapter<TaskEntity.TaskItemEntity.AttendeeUserEntity> {
    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_user_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUserEntity, int position) {
        ImageView imageView = holder.obtainView(R.id.user_image);
        GlideUtils.loadUser(imageView.getContext(), attendeeUserEntity.pic, imageView);
        if (getItemCount() > 2) {
            if (getData().lastIndexOf(attendeeUserEntity) == position) {
                TextDrawable textDrawable = TextDrawable.builder().buildRound(String.valueOf(getData().size()), 0xFF8c8f92);
                imageView.setImageDrawable(textDrawable);
            }
        } else if (getItemCount() == 2) {
            TextDrawable textDrawable = TextDrawable.builder().buildRound(attendeeUserEntity.userName, 0xFF4A4A4A);
            imageView.setImageDrawable(textDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return getData().size() >= 5 ? 4 : getData().size() == 1 ? 2 : getData().size();
    }
}
