package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupMemberEntity;
import com.icourt.alpha.utils.GlideUtils;

import static com.icourt.alpha.constants.Const.VIEW_TYPE_GRID;
import static com.icourt.alpha.constants.Const.VIEW_TYPE_ITEM;

/**
 * Description  讨论组成员适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public class GroupMemberAdapter extends MultiSelectRecyclerAdapter<GroupMemberEntity> {


    private int type;

    public GroupMemberAdapter(@Const.AdapterViewType int type) {
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
    public void onBindSelectableHolder(ViewHolder holder, GroupMemberEntity groupMemberEntity, boolean selected, int position) {
        if (groupMemberEntity == null) return;
        ImageView member_icon_iv = holder.obtainView(R.id.member_icon_iv);
        TextView member_name_tv = holder.obtainView(R.id.member_name_tv);

        GlideUtils.loadUser(member_icon_iv.getContext(), groupMemberEntity.pic, member_icon_iv);
        member_name_tv.setText(groupMemberEntity.name);

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            CheckedTextView member_selected_ctv = holder.obtainView(R.id.member_selected_ctv);
            if (member_selected_ctv != null) {
                member_selected_ctv.setVisibility(isSelectable() ? View.VISIBLE : View.GONE);
                member_selected_ctv.setChecked(selected);
            }
        }
    }
}
