package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.GroupDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupAdapter extends BaseArrayRecyclerAdapter<GroupEntity> implements BaseRecyclerAdapter.OnItemClickListener {


    public GroupAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_group;
    }

    @Override
    public void onBindHoder(ViewHolder holder, GroupEntity groupEntity, int position) {
        if (groupEntity == null) return;
        ImageView group_icon_iv = holder.obtainView(R.id.group_icon_iv);
        TextView group_name_tv = holder.obtainView(R.id.group_name_tv);
        group_name_tv.setText(groupEntity.name);
        if (TextUtils.isEmpty(groupEntity.pic)) {
            IMUtils.setTeamIcon(groupEntity.name, group_icon_iv);
        } else {
            GlideUtils.loadUser(group_icon_iv.getContext(), groupEntity.pic, group_icon_iv);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        GroupEntity item = getItem(getRealPos(position));
        if (item == null) return;
        GroupDetailActivity.launchTEAM(view.getContext(), item.tid);
    }
}
