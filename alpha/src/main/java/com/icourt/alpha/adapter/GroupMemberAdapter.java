package com.icourt.alpha.adapter;

import android.support.annotation.IntDef;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.GroupMemberEntity;
import com.icourt.alpha.utils.GlideUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Description  讨论组成员适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public class GroupMemberAdapter extends BaseArrayRecyclerAdapter<GroupMemberEntity> {
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_GRID = 1;


    @IntDef({VIEW_TYPE_ITEM,
            VIEW_TYPE_GRID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GroupMemberAdapterViewType {

    }

    private int type;

    public GroupMemberAdapter(@GroupMemberAdapterViewType int type) {
        this.type = type;
    }

    @Override
    public int bindView(int viewtype) {
        switch (type) {
            case VIEW_TYPE_GRID:
                return R.layout.adapter_item_group_member_grid;
            case VIEW_TYPE_ITEM:
                return R.layout.adapter_item_group_member;
            default:
                return R.layout.adapter_item_group_member;
        }
    }

    @Override
    public void onBindHoder(ViewHolder holder, GroupMemberEntity groupMemberEntity, int position) {
        if (groupMemberEntity == null) return;
        ImageView member_icon_iv = holder.obtainView(R.id.member_icon_iv);
        TextView member_name_tv = holder.obtainView(R.id.member_name_tv);

        GlideUtils.loadUser(member_icon_iv.getContext(), groupMemberEntity.pic, member_icon_iv);
        member_name_tv.setText(groupMemberEntity.name);
    }
}
