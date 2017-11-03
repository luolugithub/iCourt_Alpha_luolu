package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.netease.nimlib.sdk.msg.model.QueryDirectionEnum.QUERY_OLD;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyAtedAdapter extends ContactBaseAdapter<IMMessageCustomBody> implements OnItemClickListener {
    ArrayMap<String, RecentContact> localRecentContacts = new ArrayMap<>();//会话列表 key contactid[云信]
    ArrayMap<Long, Boolean> atReadedMessageMap = new ArrayMap<>();//消息读取状态, key msgid[alpha]

    private int READED_TITLE_COLOR = 0xFFa6a6a6;
    private int READED_CONTENT_COLOR = READED_TITLE_COLOR;
    private int READED_TIME_COLOR = 0xFFd3d3d3;
    private int READED_GROUP_COLOR = READED_TIME_COLOR;

    public MyAtedAdapter() {
        this.setOnItemClickListener(this);
    }



    @Override
    public boolean bindData(boolean isRefresh, @NonNull List<IMMessageCustomBody> datas) {
        if (isRefresh) {
            atReadedMessageMap.clear();
            getRecentContacts();
        }
        return super.bindData(isRefresh, datas);
    }

    private void getRecentContacts() {
        //获取会话列表
        NIMClient.getService(MsgService.class)
                .queryRecentContacts()
                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> recentContacts, Throwable throwable) {
                        if (code == ResponseCode.RES_SUCCESS
                                && recentContacts != null
                                && !recentContacts.isEmpty()) {

                            for (RecentContact recentContact : recentContacts) {
                                if (recentContact == null) {
                                    continue;
                                }
                                localRecentContacts.put(recentContact.getContactId(), recentContact);
                            }
                        }
                    }
                });
    }


    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_my_ated;
    }

    @Override
    public void onBindHolder(final BaseViewHolder holder, @Nullable final IMMessageCustomBody imAtEntity, int i) {
        if (imAtEntity == null) {
            return;
        }
        final ImageView at_user_iv = holder.obtainView(R.id.at_user_iv);
        final TextView at_user_tv = holder.obtainView(R.id.at_user_tv);
        TextView at_time_tv = holder.obtainView(R.id.at_time_tv);
        TextView at_from_group_tv = holder.obtainView(R.id.at_from_group_tv);
        final TextView at_content_tv = holder.obtainView(R.id.at_content_tv);


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
        at_content_tv.setText(imAtEntity.content);
        updateReadViewState(imAtEntity, at_user_iv, at_user_tv, at_content_tv, at_from_group_tv, at_time_tv);

    }

    /**
     * 更新 已读 未读状态
     *
     * @param imAtEntity
     * @param at_user_iv
     * @param at_user_tv
     * @param at_content_tv
     */
    private void updateReadViewState(@Nullable final IMMessageCustomBody imAtEntity,
                                     final ImageView at_user_iv,
                                     final TextView at_user_tv,
                                     final TextView at_content_tv,
                                     final TextView at_from_group_tv,
                                     final TextView at_time_tv) {
        if (at_user_iv == null) {
            return;
        }
        //处理读取状态
        //1. 判断会话
        RecentContact recentContact = localRecentContacts.get(imAtEntity.to);
        int sessionUnReadNum = recentContact == null ? 0 : recentContact.getUnreadCount();
        if (sessionUnReadNum > 0) {

            //2. 判断缓存的消息状态列表
            if (atReadedMessageMap.containsKey(imAtEntity.id)) {
                Boolean aBoolean = atReadedMessageMap.get(imAtEntity.id);
                updateReadedStateView(aBoolean != null && aBoolean.booleanValue(),
                        at_user_iv,
                        at_user_tv,
                        at_content_tv,
                        at_from_group_tv,
                        at_time_tv,
                        imAtEntity);
                return;
            }

            //3. 从数据库拿
            IMMessage emptyMessage = MessageBuilder.createEmptyMessage(
                    imAtEntity.to,
                    imAtEntity.ope == CHAT_TYPE_P2P ? SessionTypeEnum.P2P : SessionTypeEnum.Team,
                    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));//避免测试人员手机改时间
            NIMClient.getService(MsgService.class)
                    .queryMessageListEx(emptyMessage, QUERY_OLD, recentContact.getUnreadCount(), true)
                    .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                        @Override
                        public void onResult(int code, List<IMMessage> imMessages, Throwable throwable) {
                            boolean isReaded = true;
                            if (imMessages != null && !imMessages.isEmpty()) {
                                for (IMMessage imMessage : imMessages) {
                                    if (imMessage == null) {
                                        continue;
                                    }
                                    //缓存
                                    atReadedMessageMap.put(imAtEntity.id, imMessage.getStatus() == MsgStatusEnum.read);

                                    try {
                                        JSONObject jsonObject = new JSONObject(imMessage.getContent());
                                        long id = jsonObject.getLong("id");
                                        if (id == imAtEntity.id) {
                                            isReaded = imMessage.getStatus() == MsgStatusEnum.read;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateReadedStateView(isReaded,
                                        at_user_iv,
                                        at_user_tv,
                                        at_content_tv,
                                        at_from_group_tv,
                                        at_time_tv,
                                        imAtEntity);
                            }
                        }
                    });
        } else {
            updateReadedStateView(true,
                    at_user_iv,
                    at_user_tv,
                    at_content_tv,
                    at_from_group_tv,
                    at_time_tv,
                    imAtEntity);
        }
    }

    private void updateReadedStateView(boolean isReaded,
                                       ImageView at_user_iv,
                                       TextView at_user_tv,
                                       TextView at_content_tv,
                                       TextView at_from_group_tv,
                                       TextView at_time_tv,
                                       @Nullable IMMessageCustomBody imAtEntity) {
        if (at_user_iv == null) {
            return;
        }
        int alphaFontColorGray = getContextColor(R.color.alpha_font_color_gray);
        //更新item
        at_user_iv.setAlpha(isReaded ? 0.5f : 1.0f);
        at_user_tv.setTextColor(isReaded ? READED_TITLE_COLOR : getContextColor(R.color.alpha_font_color_black));
        at_content_tv.setTextColor(isReaded ? READED_CONTENT_COLOR : alphaFontColorGray);
        at_time_tv.setTextColor(isReaded ? READED_TIME_COLOR : alphaFontColorGray);
        at_from_group_tv.setTextColor(isReaded ? READED_GROUP_COLOR : alphaFontColorGray);

        if (!isReaded && !TextUtils.isEmpty(imAtEntity.content)) {
            String originalText = imAtEntity.content;
            String targetText = "@[\\w\\u4E00-\\u9FA5\\uF900-\\uFA2D]*";
            SpannableUtils.setTextForegroundColorSpan(at_content_tv,
                    originalText,
                    targetText,
                    SystemUtils.getColor(at_content_tv.getContext(), R.color.alpha_font_color_orange));
        } else {
            at_content_tv.setText(imAtEntity.content);
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
