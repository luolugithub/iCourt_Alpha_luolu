package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.IMSessionEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description 消息通知回话列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class IMSessionAdapter extends BaseArrayRecyclerAdapter<IMSessionEntity> {

    AlphaUserInfo alphaUserInfo;

    /**
     * 获取登陆昵称
     *
     * @return
     */
    private String getLoginUserName() {
        if (alphaUserInfo != null) {
            return alphaUserInfo.getName();
        }
        return null;
    }

    private String getLoginUserId() {
        if (alphaUserInfo != null) {
            return alphaUserInfo.getUserId();
        }
        return null;
    }

    private List<Team> teams;
    private List<GroupContactBean> groupContactBeans;

    /**
     * 获取本地头像
     *
     * @param accid
     * @return
     */
    private String getUserIcon(String accid) {
        if (groupContactBeans != null && !TextUtils.isEmpty(accid)) {
            GroupContactBean groupContactBean = new GroupContactBean();
            groupContactBean.accid = accid.toLowerCase();
            int indexOf = groupContactBeans.indexOf(groupContactBean);
            if (indexOf >= 0) {
                groupContactBean = groupContactBeans.get(indexOf);
                return groupContactBean.pic;
            }
        }
        return "";
    }

    @Nullable
    @CheckResult
    private GroupContactBean getUser(String accid) {
        if (groupContactBeans != null && !TextUtils.isEmpty(accid)) {
            GroupContactBean groupContactBean = new GroupContactBean();
            groupContactBean.accid = accid.toLowerCase();
            int indexOf = groupContactBeans.indexOf(groupContactBean);
            return groupContactBeans.get(indexOf);
        }
        return null;
    }

    @Nullable
    @CheckResult
    public Team getTeam(String id) {
        for (Team team : teams) {
            if (StringUtils.equalsIgnoreCase(id, team.getId(), false)) {
                return team;
            }
        }
        return null;
    }

    public IMSessionAdapter(List<Team> teams, List<GroupContactBean> groupContactBeans) {
        this.teams = teams;
        this.groupContactBeans = groupContactBeans;
        alphaUserInfo = LoginInfoUtils.getLoginUserInfo();
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_im_session;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        IMSessionViewHolder viewHolder = new IMSessionViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(bindView(viewtype), viewGroup, false));
        return viewHolder;
    }

    final class IMSessionViewHolder extends ViewHolder {
        public Badge badge;

        public IMSessionViewHolder(View itemView) {
            super(itemView);
            badge = new QBadgeView(itemView.getContext())
                    .bindTarget(itemView.findViewById(R.id.root));
            badge.setBadgeGravity(Gravity.START | Gravity.TOP);
            badge.setGravityOffset(38, 8, true);
            badge.setBadgeTextSize(10, true);
            badge.stroke(Color.WHITE, 1, true);
            badge.setBadgePadding(3, true);
        }
    }

    @Override
    public void onBindHoder(ViewHolder holder, IMSessionEntity imSessionEntity, int position) {
        if (imSessionEntity == null) return;
        ImageView ivSessionIcon = holder.obtainView(R.id.iv_session_icon);
        TextView tvSessionTime = holder.obtainView(R.id.tv_session_time);
        TextView tvSessionTitle = holder.obtainView(R.id.tv_session_title);
        TextView tvSessionContent = holder.obtainView(R.id.tv_session_content);
        ImageView ivSessionNotDisturb = holder.obtainView(R.id.iv_session_not_disturb);
        tvSessionContent.setText(imSessionEntity.recentContact.getContent());
        if (imSessionEntity.recentContact != null) {
            if (holder instanceof IMSessionViewHolder) {
                //1.设置提示数量
                setUnreadCount(((IMSessionViewHolder) holder).badge, imSessionEntity.recentContact.getUnreadCount());

            }


            //3.设置消息展示的时间
            setTimeView(tvSessionTime, imSessionEntity.recentContact.getTime());


            //4.设置消息体展示
            if (imSessionEntity.recentContact.getAttachment() != null) {
                //目前主要机器人item采用
                setItemAlphaData(imSessionEntity,
                        ivSessionIcon,
                        tvSessionTitle,
                        tvSessionContent);
            } else if (imSessionEntity.customIMBody != null) {

                //展示自定义消息
                setSessionTitle(imSessionEntity, tvSessionTitle);

                setSessionIcon(imSessionEntity, ivSessionIcon);

                setItemData(imSessionEntity,
                        tvSessionContent);
            }

            //5.设置消息免打扰
            setItemDontDisturbs(imSessionEntity, ivSessionNotDisturb);
        }
    }

    /**
     * 展示群消息免打扰的icon
     *
     * @param imSessionEntity
     * @param ivSessionNotDisturb
     */
    private void setItemDontDisturbs(IMSessionEntity imSessionEntity, ImageView ivSessionNotDisturb) {
        if (imSessionEntity == null) return;
        if (ivSessionNotDisturb == null) return;
        ivSessionNotDisturb.setVisibility(imSessionEntity.isNotDisturb
                ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置session 头像
     *
     * @param imSessionEntity
     * @param ivSessionIcon
     */
    public void setSessionIcon(IMSessionEntity imSessionEntity, ImageView ivSessionIcon) {
        if (ivSessionIcon == null) return;
        if (imSessionEntity == null) return;
        if (imSessionEntity.customIMBody == null) return;
        if (imSessionEntity.recentContact == null) return;
        switch (imSessionEntity.customIMBody.ope) {
            case CHAT_TYPE_P2P:
                GroupContactBean user = getUser(imSessionEntity.recentContact.getContactId());
                if (user != null) {
                    if (GlideUtils.canLoadImage(ivSessionIcon.getContext())) {
                        GlideUtils.loadUser(ivSessionIcon.getContext(),
                                user.pic,
                                ivSessionIcon);
                    }
                }
                break;
            case CHAT_TYPE_TEAM:
                Team team = getTeam(imSessionEntity.customIMBody.to);
                if (team != null) {
                    setTeamIcon(team.getName(), ivSessionIcon);
                }
                break;
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

    /**
     * 设置标题
     *
     * @param imSessionEntity
     * @param tvSessionTitle
     */
    public void setSessionTitle(IMSessionEntity imSessionEntity, TextView tvSessionTitle) {
        if (tvSessionTitle == null) return;
        if (imSessionEntity == null) return;
        if (imSessionEntity.customIMBody == null) return;
        if (imSessionEntity.recentContact == null) return;
        switch (imSessionEntity.customIMBody.ope) {
            case CHAT_TYPE_P2P:
                GroupContactBean user = getUser(imSessionEntity.recentContact.getContactId());
                if (user != null) {
                    tvSessionTitle.setText(user.name);
                } else {
                    tvSessionTitle.setText("name field null");
                }
                break;
            case CHAT_TYPE_TEAM:
                Team team = getTeam(imSessionEntity.customIMBody.to);
                if (team != null) {
                    tvSessionTitle.setText(team.getName());
                }
                break;
        }
    }


    /**
     * 展示自定义消息
     *
     * @param imSessionEntity
     * @param tvSessionContent
     */
    private void setItemData(IMSessionEntity imSessionEntity, TextView tvSessionContent) {
        if (imSessionEntity == null) return;
        if (imSessionEntity.customIMBody == null) return;
        if (tvSessionContent == null) return;
        IMMessageCustomBody customIMBody = imSessionEntity.customIMBody;
        //内容
        switch (customIMBody.show_type) {
            case Const.MSG_TYPE_TXT:    //文本消息
                tvSessionContent.setText(customIMBody.content);
                break;
            case Const.MSG_TYPE_IMAGE:
                tvSessionContent.setText("[ 图片 ]");
                break;
            case Const.MSG_TYPE_FILE:     //文件消息
                tvSessionContent.setText("[ 文件 ]");
                break;
            case Const.MSG_TYPE_DING:   //钉消息
                if (customIMBody.ext != null) {
                    StringBuilder dingStringBuilder = new StringBuilder();
                    switch (customIMBody.ope) {
                        case CHAT_TYPE_P2P:
                            break;
                        case CHAT_TYPE_TEAM:
                            if (!TextUtils.isEmpty(customIMBody.ext.name)) {
                                dingStringBuilder.append(customIMBody.ext.name + " : ");
                            }
                            break;
                    }
                    dingStringBuilder.append(customIMBody.content);
                    tvSessionContent.setText(dingStringBuilder.toString());
                } else {
                    tvSessionContent.setText("钉消息ext null");
                }
                break;
            case Const.MSG_TYPE_AT://@消息
                if (customIMBody.ext != null) {
                    int color = 0xFFed6c00;
                    if (customIMBody.ext.is_all) {
                        tvSessionContent.setText("有人@了你");
                        SpannableUtils.setTextForegroundColorSpan(tvSessionContent, "有人@了你", "有人@了你", color);
                    } else if (customIMBody.ext.users != null && customIMBody.ext.users.contains(getLoginUserId())) {
                        tvSessionContent.setText("有人@了你");
                        SpannableUtils.setTextForegroundColorSpan(tvSessionContent, "有人@了你", "有人@了你", color);
                    } else {
                        tvSessionContent.setText(String.format("%s : %s", customIMBody.from, customIMBody.content));
                    }
                } else {
                    tvSessionContent.setText("@消息ext null");
                }
                break;
            case Const.MSG_TYPE_SYS:     //系统辅助消息
                if (customIMBody.ext != null) {
                    tvSessionContent.setText(customIMBody.ext.content);
                } else {
                    tvSessionContent.setText("sys消息ext null");
                }
                break;
            case Const.MSG_TYPE_LINK://链接消息
                if (customIMBody.ext != null) {
                    tvSessionContent.setText(customIMBody.ext.url);
                } else {
                    tvSessionContent.setText("link消息ext null");
                }
                break;
            case Const.MSG_TYPE_ALPHA:   //alpha系统内业务消息 2.0.0暂时不处理
                tvSessionContent.setText("alpha系统内业务消息 2.0.0不支持");
                break;
            case Const.MSG_TYPE_VOICE:   //alpha语音消息 2.0.0暂时不处理
                tvSessionContent.setText("alpha语音消息 2.0.0不支持");
                break;
        }
    }


    /**
     * 初始化附件 主要是机器人
     *
     * @param imSessionEntity
     * @param ivSessionIcon
     * @param tvSessionTitle
     * @param tvSessionContent
     */
    private void setItemAlphaData(IMSessionEntity imSessionEntity, ImageView ivSessionIcon, TextView tvSessionTitle, TextView tvSessionContent) {
        if (imSessionEntity == null) return;
        if (imSessionEntity.recentContact == null) return;
        if (imSessionEntity.recentContact.getAttachment() == null) return;

        //TODO 初始化alpha小助手
        // GlideUtils.ladUser(ivSessionIcon.getContext(), getUserIcon(i), ivSessionIcon);
    }

    /**
     * 设置消息展示的时间
     *
     * @param tvSessionTime
     * @param time
     */
    private void setTimeView(TextView tvSessionTime, long time) {
        if (tvSessionTime == null) return;
        tvSessionTime.setText(DateUtils.getTimeShowString(time, true));
    }


    /**
     * 设置提示数量
     *
     * @param badge
     * @param unreadCount
     */
    private void setUnreadCount(Badge badge, int unreadCount) {
        if (badge == null) return;
        if (unreadCount > 0 && unreadCount <= 99) {
            //直接显示
            badge.setBadgeNumber(unreadCount);
        } else if (unreadCount > 99) {
            // 显示...
            badge.setBadgeText("...");
        } else if (unreadCount <= 0) {
            //隐藏
            badge.setBadgeNumber(0);
        }
    }
}
