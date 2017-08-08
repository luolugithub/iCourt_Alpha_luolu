package com.icourt.alpha.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.utils.DensityUtil;
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

    int dp8, dp13;

    public TaskUsersAdapter(Context context) {
        this.dp8 = DensityUtil.dip2px(context, -8);
        this.dp13 = DensityUtil.sp2px(context, 13);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_task_user_layout;
    }

    @Override
    public void onBindHoder(ViewHolder holder, TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUserEntity, int position) {
        ImageView imageView = holder.obtainView(R.id.user_image);
        if (attendeeUserEntity == null) return;
        if (getItemCount() > 2) {
            if (position == 0) {
                TextDrawable textDrawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(0xFFcacaca)
                        .fontSize(dp13)
                        .endConfig()
                        .buildRound(String.valueOf(getData().size()), 0xFFf8f8f9);
                imageView.setImageDrawable(textDrawable);
                setChildViewParams(holder, dp8);
            } else {
                GlideUtils.loadUser(imageView.getContext(), attendeeUserEntity.pic, imageView);
            }
        } else {
            GlideUtils.loadUser(imageView.getContext(), attendeeUserEntity.pic, imageView);
        }

        if (position == getItemCount() - 1) {
            setChildViewParams(holder, 0);
        } else {
            setChildViewParams(holder, dp8);
        }
    }

    private void setChildViewParams(ViewHolder holder, int left) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if (params.leftMargin != left) {
            params.setMargins(left, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return getData().size() >= 5 ? 4 : getData().size();
    }
}
