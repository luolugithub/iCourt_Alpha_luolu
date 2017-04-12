package com.icourt.alpha.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.entity.bean.IMBodyEntity;
import com.icourt.alpha.entity.bean.IMSessionEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
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
                setItemData(imSessionEntity.recentContact.getAttachment(),
                        ivSessionIcon,
                        tvSessionTitle,
                        tvSessionContent);
            } else if (imSessionEntity.customIMBody != null) {

                //展示自定义消息
                if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.P2P) {
                    setItemDataTeam(imSessionEntity.customIMBody,
                            ivSessionIcon,
                            tvSessionTitle,
                            tvSessionContent);
                } else if (imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.Team) {
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
     * @param customIMBody
     * @param ivSessionIcon
     * @param tvSessionTitle
     * @param tvSessionContent
     */
    private void setItemDataTeam(IMBodyEntity customIMBody, ImageView ivSessionIcon, TextView tvSessionTitle, EmojiTextView tvSessionContent) {
        if (customIMBody == null) return;
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
     * @param attachment
     * @param ivSessionIcon
     * @param tvSessionTitle
     * @param tvSessionContent
     */
    private void setItemData(MsgAttachment attachment, ImageView ivSessionIcon, TextView tvSessionTitle, EmojiTextView tvSessionContent) {
        if (attachment == null) return;
         /*   HelperNotification helperNotification = HelperNotificationParser.getHelperNotification(recentContact.getAttachment().toJson(false));
                if (recentContact.getSessionType() == SessionTypeEnum.P2P) {
                    GroupContactBean groupContactBean = getContactByDB(recentContact.getContactId());
                    holder.photoTextView.setVisibility(View.GONE);
                    if (groupContactBean != null) {
                        holder.nameView.setText(groupContactBean.getName());
                        if (!UIUtils.isNull(groupContactBean.getPic()))
                            holder.photoView.setImageURI(Uri.parse(groupContactBean.getPic()));
                    }
                    holder.contentView.setText(helperNotification.getContent());
                }*/
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
