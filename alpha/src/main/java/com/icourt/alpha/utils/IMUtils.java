package com.icourt.alpha.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.view.TextDrawable;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.Arrays;
import java.util.List;

import static com.icourt.alpha.constants.Const.MSG_TYPE_AT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_DING;
import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_IMAGE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_LINK;
import static com.icourt.alpha.constants.Const.MSG_TYPE_SYS;
import static com.icourt.alpha.constants.Const.MSG_TYPE_TXT;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/13
 * version 1.0.0
 */
public class IMUtils {

    //team 背景 请勿轻易修改
    public static final int[] TEAM_ICON_BGS = {
            0xFFA3D9E3,
            0xFFF4E166,
            0XFFED7945,
            0XFFA53D4D,
            0XFF67405A,
            0XFF5A4A45,
            0XFF6A85BC
    };

    /**
     * 是否是图片
     *
     * @param url
     * @return
     */
    public static final boolean isPIC(String url) {
        if (!TextUtils.isEmpty(url)) {
            int pointIndex = url.lastIndexOf(".");
            if (pointIndex >= 0 && pointIndex < url.length()) {
                String fileSuffix = url.substring(pointIndex, url.length());
                return getPICSuffixs().contains(fileSuffix);
            }
        }
        return false;
    }

    /**
     * 图片后缀
     *
     * @return
     */
    public static final List<String> getPICSuffixs() {
        return Arrays.asList(".png", ".jpg", ".gif", ".jpeg", ".PNG", ".JPG", ".GIF", ".JPEG");
    }

    /**
     * 设置team头像
     *
     * @param teamName
     * @param ivSessionIcon
     */
    public static final void setTeamIcon(String teamName, ImageView ivSessionIcon) {
        if (TextUtils.isEmpty(teamName)) return;
        if (ivSessionIcon == null) return;
        char firstUpperLetter = PingYinUtil.getFirstUpperLetter(ivSessionIcon.getContext(), teamName);
        if (PingYinUtil.isUpperLetter(firstUpperLetter)) {//是字母
            //第一个字母是i
            if (TextUtils.equals("i", String.valueOf(firstUpperLetter))) {
                TextDrawable icon = TextDrawable.builder()
                        .buildRound(Character.toString(firstUpperLetter),
                                IMUtils.TEAM_ICON_BGS[2]);
                ivSessionIcon.setImageDrawable(icon);
            } else if (teamName.length() == 1) {//长度为1
                int indexOfUpper = PingYinUtil.indexOfUpperUpperLetter(firstUpperLetter);
                int realIndex = indexOfUpper / IMUtils.TEAM_ICON_BGS.length;
                TextDrawable icon = TextDrawable.builder()
                        .buildRound(Character.toString(firstUpperLetter),
                                IMUtils.TEAM_ICON_BGS[realIndex]);
                ivSessionIcon.setImageDrawable(icon);
            } else if (teamName.length() > 1) {//长度为2 两位
                int indexOfUpper = PingYinUtil.indexOfUpperUpperLetter(firstUpperLetter);
                char indexUpperLetter2 = PingYinUtil.getIndexUpperLetter(ivSessionIcon.getContext(), teamName, 1);
                int indexOfUpper2 = PingYinUtil.indexOfUpperUpperLetter(indexUpperLetter2);
                int realIndex = (indexOfUpper + indexOfUpper2) / IMUtils.TEAM_ICON_BGS.length;
                if (realIndex >= IMUtils.TEAM_ICON_BGS.length) {
                    realIndex = realIndex / IMUtils.TEAM_ICON_BGS.length;
                }
                if (realIndex >= 0) {
                    TextDrawable icon = TextDrawable.builder()
                            .buildRound(Character.toString(firstUpperLetter),
                                    IMUtils.TEAM_ICON_BGS[realIndex]);
                    ivSessionIcon.setImageDrawable(icon);
                }
            }
        } else {//非字母
            TextDrawable icon = TextDrawable.builder()
                    .buildRound("#", IMUtils.TEAM_ICON_BGS[0]);
            ivSessionIcon.setImageDrawable(icon);
        }

    }

    /**
     *    public static final int MSG_TYPE_TXT = 0;     //文本消息
     public static final int MSG_TYPE_FILE = 1;    //文件消息
     public static final int MSG_TYPE_DING = 2;    //钉消息
     public static final int MSG_TYPE_AT = 3;      //@消息
     public static final int MSG_TYPE_SYS = 4;     //系统辅助消息
     public static final int MSG_TYPE_LINK = 5;    //链接消息
     public static final int MSG_TYPE_ALPHA = 6;   //alpha系统内业务消息
     public static final int MSG_TYPE_VOICE = 7;   //语音消息

     */

    /**
     * 是否过滤聊天消息
     * v.2.0.0
     *
     * @param imMessageCustomBody
     * @return
     */
    public static boolean isFilterChatIMMessage(IMMessageCustomBody imMessageCustomBody) {
        if (imMessageCustomBody != null) {
            switch (imMessageCustomBody.show_type) {
                case MSG_TYPE_TXT:
                    return false;
                case MSG_TYPE_FILE:
                    return false;
                case MSG_TYPE_DING:
                    return false;
                case MSG_TYPE_AT:
                    return false;
                case MSG_TYPE_SYS:
                    return false;
                case MSG_TYPE_LINK:
                    return false;
                case MSG_TYPE_IMAGE:
                    return false;
                default:
                    return true;
            }
        }
        return true;
    }

    /**
     * 输出message
     *
     * @param tag
     * @param team
     */
    public static void logIMTeam(String tag, Team team) {
        try {
            StringBuilder sb = new StringBuilder(tag);
            if (team == null) {
                sb.append("null");
            } else {
                sb.append("\nid:" + team.getId());
                sb.append("\nname:" + team.getName());
                sb.append("\nicon:" + team.getIcon());
            }
            LogUtils.d(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出message
     *
     * @param tag
     * @param message
     */

    public static void logIMMessage(String tag, IMMessage message) {
        try {
            StringBuilder sb = new StringBuilder(tag);
            if (message == null) {
                sb.append("null");
            } else {
                sb.append("\nUuid:" + message.getUuid());
                sb.append("\nsessionId:" + message.getSessionId());
                sb.append("\nsessionType:" + message.getSessionType());
                //sb.append("\nfromNick:" + message.getFromNick());
                sb.append("\nmsgType:" + message.getMsgType());
                sb.append("\nstatus:" + message.getStatus());
                sb.append("\ndirect:" + message.getDirect());
                sb.append("\ncontent:" + message.getContent());

                sb.append("\ntime:" + message.getTime());
                sb.append("\nfromAccount:" + message.getFromAccount());
                sb.append("\nattachment:" + message.getAttachment());
                sb.append("\nattachStatus:" + message.getAttachStatus());
                sb.append("\nremoteExtension:" + message.getRemoteExtension());
                sb.append("\nlocalExtension:" + message.getLocalExtension());
                sb.append("\npushContent:" + message.getPushContent());
                sb.append("\npushPayload:" + message.getPushPayload());
                sb.append("\nisRemoteRead:" + message.isRemoteRead());
                sb.append("\nfromClientType:" + message.getFromClientType());

                if (message.getAttachment() != null) {
                    sb.append("\ngetAttachment:" + message.getAttachment().toJson(false));
                }
            }
            LogUtils.d(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 输出 RecentContact
     *
     * @param tag
     * @param recentContact
     */
    public static void logRecentContact(String tag, RecentContact recentContact) {
        try {
            StringBuilder sb = new StringBuilder(tag);
            if (recentContact == null) {
                sb.append("null");
            } else {
                sb.append("\nContactId:" + recentContact.getContactId());
                sb.append("\nFromAccount:" + recentContact.getFromAccount());
                sb.append("\nFromNick:" + recentContact.getFromNick());
                sb.append("\nSessionType:" + recentContact.getSessionType());
                sb.append("\nRecentMessageId:" + recentContact.getRecentMessageId());
                sb.append("\nMsgType:" + recentContact.getMsgType());
                sb.append("\nMsgStatus:" + recentContact.getMsgStatus());
                sb.append("\nUnreadCount:" + recentContact.getUnreadCount());
                sb.append("\nContent:" + recentContact.getContent());
                sb.append("\ngetTime:" + recentContact.getTime());
                sb.append("\nAttachment:" + recentContact.getAttachment());
                sb.append("\nremoteExtension:" + recentContact.getTag());
                sb.append("\nExtension:" + recentContact.getExtension());
                if (recentContact.getAttachment() != null) {
                    sb.append("\ngetAttachment:" + recentContact.getAttachment().toJson(false));
                }
            }
            LogUtils.d(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
