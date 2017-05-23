package com.icourt.alpha.adapter;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyAtedAdapter extends BaseArrayRecyclerAdapter<IMMessageCustomBody> implements BaseRecyclerAdapter.OnItemClickListener {

    private String loginName = null;
    private List<Team> teams;

    @Nullable
    @CheckResult
    public Team getTeam(String id) {
        if (teams == null) return null;
        for (Team team : teams) {
            if (StringUtils.equalsIgnoreCase(id, team.getId(), false)) {
                return team;
            }
        }
        return null;
    }

    @Nullable
    @CheckResult
    private GroupContactBean getUser(String accid) {
        if (groupContactBeans != null && !TextUtils.isEmpty(accid)) {
            GroupContactBean groupContactBean = new GroupContactBean();
            groupContactBean.accid = accid.toLowerCase();
            int indexOf = groupContactBeans.indexOf(groupContactBean);
            if (indexOf >= 0) {
                return groupContactBeans.get(indexOf);
            }
        }
        return null;
    }

    private List<GroupContactBean> groupContactBeans;

    public MyAtedAdapter(List<Team> teams, List<GroupContactBean> groupContactBeans) {
        AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (loginUserInfo != null) {
            loginName = loginUserInfo.getName();
        }
        this.teams = teams;
        this.groupContactBeans = groupContactBeans;
        this.setOnItemClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_my_ated;
    }


    @Override
    public void onBindHoder(ViewHolder holder, IMMessageCustomBody imAtEntity, int position) {
        if (imAtEntity == null) return;
        ImageView at_user_iv = holder.obtainView(R.id.at_user_iv);
        TextView at_user_tv = holder.obtainView(R.id.at_user_tv);
        TextView at_time_tv = holder.obtainView(R.id.at_time_tv);
        TextView at_from_group_tv = holder.obtainView(R.id.at_from_group_tv);
        TextView at_content_tv = holder.obtainView(R.id.at_content_tv);

        Team team = getTeam(imAtEntity.to);
        if (team != null) {
            at_from_group_tv.setText(team.getName());
            if (!TextUtils.isEmpty(team.getIcon())) {
                GlideUtils.loadGroup(at_user_iv.getContext(),
                        team.getIcon(),
                        at_user_iv);
            } else {
                setTeamIcon(team.getName(), at_user_iv);
            }
        }

        GroupContactBean user = getUser(imAtEntity.from);
        if (user != null) {
            GlideUtils.loadUser(at_user_iv.getContext(),
                    user.pic,
                    at_user_iv);
        }

        at_user_tv.setText(imAtEntity.name);
        at_time_tv.setText(DateUtils.getMMMdd(imAtEntity.send_time));
        if (!TextUtils.isEmpty(imAtEntity.content)) {
            String originalText = imAtEntity.content;
            String targetText = null;
            try {
                targetText = originalText.substring(originalText.trim().indexOf("@"), originalText.trim().indexOf(" "));
            } catch (Exception e) {
            }
            if (TextUtils.isEmpty(targetText)) {
                if (originalText.startsWith("@")) {
                    if (originalText.trim().startsWith("@所有人")) {
                        targetText = "@所有人";
                    } else {
                        targetText = originalText;
                    }
                } else if (originalText.contains("@所有人")) {
                    targetText = "@所有人";
                } else if (originalText.contains("@" + loginName)) {
                    targetText = "@" + loginName;
                }
            }
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
        if (TextUtils.isEmpty(teamName)) return;
        if (ivSessionIcon == null) return;
        IMUtils.setTeamIcon(teamName, ivSessionIcon);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        IMMessageCustomBody item = getItem(getRealPos(position));
        if (item == null) return;
        switch (item.ope) {
            case CHAT_TYPE_P2P:
                ChatActivity.launchP2P(view.getContext(),
                        item.to,
                        item.name,
                        item.send_time,0);
                break;
            case CHAT_TYPE_TEAM:
                Team team = getTeam(item.to);
                ChatActivity.launchTEAM(view.getContext(),
                        item.to,
                        team != null ? team.getName() : "",
                        item.send_time,0);
                break;
        }
    }
}
