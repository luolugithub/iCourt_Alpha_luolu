package com.icourt.alpha.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.netease.nimlib.sdk.team.model.Team;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyAtedAdapter extends ContactBaseAdapter<IMMessageCustomBody> implements OnItemClickListener {


    public MyAtedAdapter() {
        this.setOnItemClickListener(this);
    }

    @Override
    public long getItemId(int position) {
        IMMessageCustomBody item = getItem(position);
        if (item != null) {
            return item.id;
        }
        return super.getItemId(position);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_my_ated;
    }

    @Override
    public void onBindHolder(BaseViewHolder holder, @Nullable IMMessageCustomBody imAtEntity, int i) {
        if (imAtEntity == null) {
            return;
        }
        ImageView at_user_iv = holder.obtainView(R.id.at_user_iv);
        TextView at_user_tv = holder.obtainView(R.id.at_user_tv);
        TextView at_time_tv = holder.obtainView(R.id.at_time_tv);
        TextView at_from_group_tv = holder.obtainView(R.id.at_from_group_tv);
        TextView at_content_tv = holder.obtainView(R.id.at_content_tv);

        Team team = getTeamById(imAtEntity.to);
        if (team != null) {
            at_from_group_tv.setText(team.getName());
            if (!TextUtils.isEmpty(team.getIcon())) {
                GlideUtils.loadGroup(at_user_iv.getContext(),
                        team.getIcon(),
                        at_user_iv);
            } else {
                setTeamIcon(team.getName(), at_user_iv);
            }
        } else {
            at_from_group_tv.setText("未查询到讨论组");
        }

        GroupContactBean user = getContactByAccid(imAtEntity.from);
        if (user != null) {
            GlideUtils.loadUser(at_user_iv.getContext(),
                    user.pic,
                    at_user_iv);
        }

        at_user_tv.setText(imAtEntity.name);
        at_time_tv.setText(DateUtils.getFormatChatTimeSimple(imAtEntity.send_time));
        if (!TextUtils.isEmpty(imAtEntity.content)) {
            String originalText = imAtEntity.content;
            String targetText = "@[\\w\\u4E00-\\u9FA5\\uF900-\\uFA2D]*";
            SpannableUtils.setTextForegroundColorSpan(at_content_tv,
                    originalText,
                    targetText,
                    SystemUtils.getColor(at_content_tv.getContext(), R.color.alpha_font_color_orange));
        }
    }


    /**
     * 设置team头像
     *
     * @param teamName
     * @param ivSessionIcon
     */
    private void setTeamIcon(String teamName, ImageView ivSessionIcon) {
        if (TextUtils.isEmpty(teamName)) {
            return;
        }
        if (ivSessionIcon == null) {
            return;
        }
        IMUtils.setTeamIcon(teamName, ivSessionIcon);
    }

    @Override
    public void onItemClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        IMMessageCustomBody item = getItem(i);
        if (item == null) {
            return;
        }
        switch (item.ope) {
            case CHAT_TYPE_P2P:
                ChatActivity.launchP2P(view.getContext(),
                        item.to,
                        item.name,
                        item.id, 0);
                break;
            case CHAT_TYPE_TEAM:
                Team team = getTeamById(item.to);
                ChatActivity.launchTEAM(view.getContext(),
                        item.to,
                        team != null ? team.getName() : "",
                        item.id, 0);
                break;
        }
    }
}
