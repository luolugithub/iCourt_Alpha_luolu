package com.icourt.alpha.adapter;

import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.GroupDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SpannableUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupAdapter extends MultiSelectRecyclerAdapter<GroupEntity> implements BaseRecyclerAdapter.OnItemClickListener {

    int foregroundColor = 0xFFed6c00;

    public GroupAdapter() {
        this.setOnItemClickListener(this);
    }

    private String keyWord;

    public GroupAdapter(String keyWord) {
        this.keyWord = keyWord;
        this.setOnItemClickListener(this);
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_group;
    }

    @Override
    public void onBindSelectableHolder(ViewHolder holder, GroupEntity groupEntity, boolean selected, int position) {
        if (groupEntity == null) return;
        ImageView group_icon_iv = holder.obtainView(R.id.group_icon_iv);
        TextView group_name_tv = holder.obtainView(R.id.group_name_tv);
        CheckedTextView ctv_group = holder.obtainView(R.id.ctv_group);
        if (!TextUtils.isEmpty(keyWord)) {
            String originalText = groupEntity.name;
            SpannableString textForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor);
            group_name_tv.setText(textForegroundColorSpan);
        } else {
            group_name_tv.setText(groupEntity.name);
        }
        if (TextUtils.isEmpty(groupEntity.pic)) {
            IMUtils.setTeamIcon(groupEntity.name, group_icon_iv);
        } else {
            GlideUtils.loadGroup(group_icon_iv.getContext(), groupEntity.pic, group_icon_iv);
        }
        ctv_group.setBackgroundResource(selected ? R.mipmap.checkmark : 0);
        ctv_group.setVisibility(selected ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        if (isSelectable()) {
            toggleSelected(position);
        } else {
            GroupEntity item = getItem(getRealPos(position));
            if (item == null) return;
            GroupDetailActivity.launchTEAM(view.getContext(), item.tid);
        }
    }
}
