package com.icourt.alpha.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.constants.RecentContactExtConfig;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.AtEntity;
import com.icourt.alpha.entity.bean.HelperNotification;
import com.icourt.alpha.entity.bean.IMBodyEntity;
import com.icourt.alpha.entity.bean.IMSessionEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.widget.parser.HelperNotificationParser;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.vanniktech.emoji.EmojiTextView;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

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

    public IMSessionAdapter() {
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
        View view_session_top = holder.obtainView(R.id.view_session_top);
        TextView tvSessionTitle = holder.obtainView(R.id.tv_session_title);
        EmojiTextView tvSessionContent = holder.obtainView(R.id.tv_session_content);
        ImageView ivSessionNotDisturb = holder.obtainView(R.id.iv_session_not_disturb);
        tvSessionContent.setText(imSessionEntity.recentContact.getContent());
        if (imSessionEntity.recentContact != null) {
            if (holder instanceof IMSessionViewHolder) {
                //1.设置提示数量
                setUnreadCount(((IMSessionViewHolder) holder).badge, imSessionEntity.recentContact.getUnreadCount());

            }

            //2.设置置顶的tagview
            setSessionViewWithTop(holder.itemView, view_session_top, imSessionEntity.recentContact.getTag());


            //3.设置消息展示的时间
            setTimeView(tvSessionTime, imSessionEntity.recentContact.getTime());


            //4.设置消息体展示
            if (imSessionEntity.recentContact.getAttachment() != null) {
                //目前主要机器人item采用
                setItemData(imSessionEntity,
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

            LogUtils.d("-------------->pos:" + position);
            //5.设置消息免打扰
            setItemDontDisturbs(imSessionEntity.recentContact, ivSessionNotDisturb);
        }
    }

    /**
     * 展示消息免打扰的icon
     *
     * @param recentContact
     * @param ivSessionNotDisturb
     */
    private void setItemDontDisturbs(RecentContact recentContact, ImageView ivSessionNotDisturb) {
        if (ivSessionNotDisturb == null) return;
        boolean isDontDisturb = false;
        if (recentContact.getExtension() != null) {
            Object o = recentContact.getExtension().get(RecentContactExtConfig.EXT_SETTING_DONT_DISTURB);
            if (o instanceof Boolean) {
                isDontDisturb = ((Boolean) o).booleanValue();
            }
        }
        ivSessionNotDisturb.setVisibility(isDontDisturb
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
        if (imSessionEntity.recentContact == null) return;
        //头像
        if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.P2P) {//单聊
            String iconUrl = null;
            if (imSessionEntity.contactBean != null) {
                iconUrl = imSessionEntity.contactBean.pic;
            } else if (TextUtils.isEmpty(iconUrl) && imSessionEntity.customIMBody != null) {
                iconUrl = imSessionEntity.customIMBody.pic;
            }
            if (GlideUtils.canLoadImage(ivSessionIcon.getContext())) {
                GlideUtils.loadUser(ivSessionIcon.getContext(), iconUrl, ivSessionIcon);
            }
        } else if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.Team) //群聊
        {
            if (imSessionEntity.team != null) {
                setTeamIcon(imSessionEntity.team.getName(), ivSessionIcon);
            }
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
        if (imSessionEntity.recentContact == null) return;
        if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.P2P) {
            if (imSessionEntity.contactBean != null) {
                tvSessionTitle.setText(imSessionEntity.contactBean.name);
            } else if (imSessionEntity.customIMBody != null) {
                tvSessionTitle.setText(imSessionEntity.customIMBody.name);
            }
        } else if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.Team) //群聊
        {
            if (imSessionEntity.team != null) {
                tvSessionTitle.setText(imSessionEntity.team.getName());
            }
        }
    }


    /**
     * 展示自定义消息
     *
     * @param imSessionEntity
     * @param tvSessionContent
     */
    private void setItemData(IMSessionEntity imSessionEntity, EmojiTextView tvSessionContent) {
        if (imSessionEntity == null) return;
        //内容
        if (tvSessionContent != null) {
            if (imSessionEntity.customIMBody != null) {
                switch (imSessionEntity.customIMBody.show_type) {
                    case ActionConstants.IM_MESSAGE_FILE_SHOWTYPE:
                        tvSessionContent.setText(IMUtils.isPIC(imSessionEntity.customIMBody.fileName) ? "[ 图片 ]" : "[ 文件 ]");
                        break;
                    case ActionConstants.IM_MESSAGE_PIN_SHOWTYPE:
                        if (imSessionEntity.recentContact != null) {
                            if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.P2P) {
                                setDingViewP2P(imSessionEntity.customIMBody, tvSessionContent);
                            } else if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.Team) {
                                setDingViewTeam(imSessionEntity.customIMBody, tvSessionContent);
                            }
                        }
                        break;
                    case ActionConstants.IM_MESSAGE_AT_SHOWTYPE:
                        setAtViewState(imSessionEntity, tvSessionContent);
                        break;
                    case ActionConstants.IM_MESSAGE_TEXT_SHOWTYPE:
                    case ActionConstants.IM_MESSAGE_SYSTEM_SHOWTYPE:
                    default:
                        tvSessionContent.setText(imSessionEntity.customIMBody.content);
                        break;
                }
            }
        }
    }


    /**
     * 设置@显示
     *
     * @param imSessionEntity
     * @param tvSessionContent
     */
    private void setAtViewState(IMSessionEntity imSessionEntity, EmojiTextView tvSessionContent) {
        if (imSessionEntity == null) return;
        if (tvSessionContent == null) return;
        if (imSessionEntity.customIMBody == null) return;
        if (isShowAtMe(imSessionEntity)) {
            int color = 0xFFed6c00;
            String targetText = tvSessionContent.getResources().getString(R.string.message_have_at_me_text);
            CharSequence originalText = targetText + imSessionEntity.customIMBody.content;
            SpannableUtils.setTextForegroundColorSpan(tvSessionContent, originalText, targetText, color);
        } else {
            tvSessionContent.setText(imSessionEntity.customIMBody.content);
        }
    }

    /**
     * 是否展示 "[有人@了你]"  @所有人 也展示"[有人@了你]"
     */
    private boolean isShowAtMe(IMSessionEntity imSessionEntity) {
        if (imSessionEntity != null
                && imSessionEntity.recentContact != null
                && imSessionEntity.recentContact.getUnreadCount() > 0) {
            if (imSessionEntity.customIMBody != null) {
                if (imSessionEntity.customIMBody.atAll == 1)//@所有人
                {
                    return true;
                } else if (imSessionEntity.customIMBody.atAll == 0) //单个@ ===>检查是否是@me
                {
                    if (imSessionEntity.customIMBody.atBeanList != null) {
                        for (AtEntity atEntity : imSessionEntity.customIMBody.atBeanList) {
                            if (atEntity == null) continue;
                            if (TextUtils.equals(atEntity.userId, getLoginUserId())) {
                                return true;
                            }
                        }
                    }

                }
            }

        }
        return false;
    }

    /**
     * 设置钉的状态 群聊
     *
     * @param imBodyEntity
     * @param tvSessionContent
     */
    private void setDingViewTeam(IMBodyEntity imBodyEntity, EmojiTextView tvSessionContent) {
        if (imBodyEntity == null) return;
        if (imBodyEntity.pinMsg == null) return;
        if (tvSessionContent == null) return;
        switch (imBodyEntity.pinMsg.isPining) {
            case 0:
                if (TextUtils.equals(getLoginUserName(), imBodyEntity.name)) {
                    tvSessionContent.setText(tvSessionContent.getResources().getString(R.string.message_cancle_ding_one_msg_text));
                } else {
                    tvSessionContent.setText(imBodyEntity.name + ": " + tvSessionContent.getResources().getString(R.string.message_cancle_ding_one_msg_text));
                }
                break;
            case 1:
                if (TextUtils.equals(getLoginUserName(), imBodyEntity.name)) {
                    tvSessionContent.setText(tvSessionContent.getResources().getString(R.string.message_ding_one_msg_text));
                } else {
                    tvSessionContent.setText(imBodyEntity.name + ": " + tvSessionContent.getResources().getString(R.string.message_ding_one_msg_text));
                }
                break;
        }
    }


    /**
     * 设置钉的状态 单聊
     *
     * @param imBodyEntity
     * @param tvSessionContent
     */
    private void setDingViewP2P(IMBodyEntity imBodyEntity, EmojiTextView tvSessionContent) {
        if (imBodyEntity == null) return;
        if (imBodyEntity.pinMsg == null) return;
        if (tvSessionContent == null) return;
        switch (imBodyEntity.pinMsg.isPining) {
            case 0:
                tvSessionContent.setText(tvSessionContent.getResources().getString(R.string.message_cancle_ding_one_msg_text));
                break;
            case 1:
                tvSessionContent.setText(tvSessionContent.getResources().getString(R.string.message_ding_one_msg_text));
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
    private void setItemData(IMSessionEntity imSessionEntity, ImageView ivSessionIcon, TextView tvSessionTitle, EmojiTextView tvSessionContent) {
        if (imSessionEntity == null) return;
        if (imSessionEntity.recentContact == null) return;
        if (imSessionEntity.recentContact.getAttachment() == null) return;
        if (imSessionEntity.contactBean == null) return;
        if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.P2P) {
            if (tvSessionTitle != null) {
                tvSessionTitle.setText(imSessionEntity.contactBean.name);
            }
            if (ivSessionIcon != null && GlideUtils.canLoadImage(ivSessionIcon.getContext())) {
                GlideUtils.loadUser(ivSessionIcon.getContext(), imSessionEntity.contactBean.pic, ivSessionIcon);
            }
            HelperNotification helperNotification = HelperNotificationParser.getHelperNotification(imSessionEntity.recentContact.getAttachment().toJson(false));
            if (helperNotification != null) {
                tvSessionContent.setText(helperNotification.getContent());
            }

        }
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
     * 设置是否是置顶view 的状态
     * 右上角 三角形标志
     * 背景变灰
     *
     * @param itemView
     * @param view_session_top
     * @param tag
     */
    private void setSessionViewWithTop(View itemView, View view_session_top, long tag) {
        if (itemView == null) return;
        if (view_session_top == null) return;
        boolean isTopSession = (tag == ActionConstants.MESSAGE_GROUP_TOP);
        view_session_top.setVisibility(isTopSession ? View.VISIBLE : View.GONE);
        itemView.setBackgroundResource(isTopSession ? R.drawable.list_view_item_other_touch_bg : R.drawable.list_view_item_touch_bg);
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
