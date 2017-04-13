package com.icourt.alpha.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.HelperNotification;
import com.icourt.alpha.entity.bean.IMBodyEntity;
import com.icourt.alpha.entity.bean.IMSessionEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.widget.parser.HelperNotificationParser;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.vanniktech.emoji.EmojiTextView;

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

    public IMSessionAdapter() {
        alphaUserInfo = LoginInfoUtils.getLoginUserInfo();
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_im_session;
    }

    @Override
    public void onBindHoder(ViewHolder holder, IMSessionEntity imSessionEntity, int position) {
        if (imSessionEntity == null) return;
        ImageView ivSessionIcon = holder.obtainView(R.id.iv_session_icon);
        TextView tvSessionTime = holder.obtainView(R.id.tv_session_time);
        View view_session_top = holder.obtainView(R.id.view_session_top);
        TextView tvSessionTitle = holder.obtainView(R.id.tv_session_title);
        EmojiTextView tvSessionContent = holder.obtainView(R.id.tv_session_content);
        tvSessionContent.setText(imSessionEntity.recentContact.getContent());
        if (imSessionEntity.recentContact != null) {
            //1.设置提示数量
            setUnreadCount(tvSessionTime, imSessionEntity.recentContact.getUnreadCount());

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
                //单聊
                if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.P2P) {
                    setItemDataTeam(imSessionEntity,
                            ivSessionIcon,
                            tvSessionTitle,
                            tvSessionContent);
                } else if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.Team) //群聊
                {
                    setItemDataP2P(imSessionEntity.customIMBody,
                            ivSessionIcon,
                            tvSessionTitle,
                            tvSessionContent);
                }
            }
        }
    }

    /**
     * 展示自定义消息 team
     *
     * @param imSessionEntity
     * @param ivSessionIcon
     * @param tvSessionTitle
     * @param tvSessionContent
     */
    private void setItemDataTeam(IMSessionEntity imSessionEntity, ImageView ivSessionIcon, TextView tvSessionTitle, EmojiTextView tvSessionContent) {
        if (imSessionEntity == null) return;
        //标题
        if (tvSessionTitle != null) {
            if (imSessionEntity.customIMBody != null) {
                tvSessionTitle.setText(imSessionEntity.customIMBody.name);
            } else if (imSessionEntity.contactBean != null) {
                tvSessionTitle.setText(imSessionEntity.contactBean.name);
            }
        }
        //头像
        if (ivSessionIcon != null) {
            String iconUrl = null;
            if (imSessionEntity.customIMBody != null) {
                iconUrl = imSessionEntity.customIMBody.pic;
            }
            if (TextUtils.isEmpty(iconUrl) && imSessionEntity.contactBean != null) {
                iconUrl = imSessionEntity.contactBean.pic;
            }
            if (GlideUtils.canLoadImage(ivSessionIcon.getContext())) {
                GlideUtils.loadUser(ivSessionIcon.getContext(), iconUrl, ivSessionIcon);
            }
        }

        //内容
        if (tvSessionContent != null) {
            if (imSessionEntity.customIMBody != null) {
                switch (imSessionEntity.customIMBody.show_type) {
                    case ActionConstants.IM_MESSAGE_TEXT_SHOWTYPE:
                        tvSessionContent.setText(imSessionEntity.customIMBody.content);
                        break;
                    case ActionConstants.IM_MESSAGE_FILE_SHOWTYPE:
                        tvSessionContent.setText(IMUtils.isPIC(imSessionEntity.customIMBody.fileName) ? "[ 图片 ]" : "[ 文件 ]");
                        break;
                    case ActionConstants.IM_MESSAGE_PIN_SHOWTYPE:
                        setDingViewTeam(imSessionEntity.customIMBody, tvSessionContent);
                        break;
                    case ActionConstants.IM_MESSAGE_AT_SHOWTYPE:
                        break;
                    case ActionConstants.IM_MESSAGE_SYSTEM_SHOWTYPE:
                        break;
                    default:
                        tvSessionContent.setText(imSessionEntity.customIMBody.content);
                        break;
                }
            }
        }
    }

    private void setAtViewState() {

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
     * 展示自定义消息 p2p
     *
     * @param customIMBody
     * @param ivSessionIcon
     * @param tvSessionTitle
     * @param tvSessionContent
     */
    private void setItemDataP2P(IMBodyEntity customIMBody, ImageView ivSessionIcon, TextView tvSessionTitle, EmojiTextView tvSessionContent) {
        if (customIMBody == null) return;
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
     * @param textView
     * @param unreadCount
     */
    private void setUnreadCount(TextView textView, int unreadCount) {
        if (textView == null) return;
        if (unreadCount > 0 && unreadCount <= 99) {
            //直接显示
        } else if (unreadCount > 99) {
            // 显示...
        } else if (unreadCount <= 0) {
            //隐藏
        }
    }
}
