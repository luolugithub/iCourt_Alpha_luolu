package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAlphaSpecialHelperAdapter;
import com.icourt.alpha.entity.bean.AlphaSecialHeplerMsgEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.event.UnReadEvent;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.bgabadgeview.BGABadgeTextView;
import com.icourt.alpha.view.recyclerviewDivider.ChatItemDecoration;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.team.model.Team;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.netease.nimlib.sdk.msg.model.QueryDirectionEnum.QUERY_OLD;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/13
 * version 1.0.0
 */
public class AlphaSpecialHelperActivity extends ChatBaseActivity {
    private static final String KEY_UID = "key_uid";
    private static final String KEY_TOTAL_UNREAD_NUM = "key_total_unread_num";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    ChatAlphaSpecialHelperAdapter chatAlphaSpecialHelperAdapter;

    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.title_Badge_tv)
    BGABadgeTextView titleBadgeTv;

    public static void launch(@NonNull Context context, String accid, int totalUnreadCount) {
        if (context == null) return;
        Intent intent = new Intent(context, AlphaSpecialHelperActivity.class);
        intent.putExtra(KEY_UID, accid);
        intent.putExtra(KEY_TOTAL_UNREAD_NUM, totalUnreadCount);
        context.startActivity(intent);
    }

    @Override
    protected void teamUpdates(@NonNull List<Team> teams) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alpha_special_helper);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected String getIMChatId() {
        return getIntent().getStringExtra(KEY_UID);
    }

    @Override
    protected int getIMChatType() {
        return CHAT_TYPE_P2P;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("Alpha小助手");
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAlphaSpecialHelperAdapter = new ChatAlphaSpecialHelperAdapter());
        recyclerView.addItemDecoration(new ChatItemDecoration(getContext(), chatAlphaSpecialHelperAdapter));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(false);
            }
        });
        getData(true);
        updateTotalUnRead(getIntent().getIntExtra(KEY_TOTAL_UNREAD_NUM, 0));
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(getLastMessage(), QUERY_OLD, 20, true)
                .setCallback(new RequestCallback<List<IMMessage>>() {
                    @Override
                    public void onSuccess(List<IMMessage> param) {
                        if (param != null) {
                            for (IMMessage imMessage : param) {
                                IMUtils.logIMMessage("----------->query result:", imMessage);
                            }
                        }
                        chatAlphaSpecialHelperAdapter.addItems(0, convert2CustomerAlphaMessages(param));
                        stopRefresh();
                        if (isRefresh) {
                            scrollToBottom();
                        }
                        if (chatAlphaSpecialHelperAdapter.getItemCount() < 20) {
                            clearUnReadNum();
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        log("--------->load fail:" + code);
                        stopRefresh();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        log("--------->load  exe:" + exception);
                        stopRefresh();
                    }
                });
    }

    /**
     * 获取最后一条消息
     *
     * @return
     */
    private IMMessage getLastMessage() {
        if (chatAlphaSpecialHelperAdapter.getItemCount() > 0) {
            AlphaSecialHeplerMsgEntity item = chatAlphaSpecialHelperAdapter.getItem(0);
            if (item != null && item.imMessage != null) {
                return item.imMessage;
            }
        }
        return MessageBuilder.createEmptyMessage(getIMChatId(), SessionTypeEnum.P2P, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnReadEvent(UnReadEvent event) {
        if (event == null) return;
        updateTotalUnRead(event.unReadCount);
    }

    /**
     * 更新总数
     *
     * @param num
     */
    private void updateTotalUnRead(int num) {
        int unReadNum = num;
        if (unReadNum > 99) {
            //显示99+
            titleBadgeTv.showTextBadge("99+");
        } else if (unReadNum > 0) {
            titleBadgeTv.showTextBadge(String.valueOf(unReadNum));
        } else {
            titleBadgeTv.hiddenBadge();
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    /* 转化成自定义的消息体
    *
    * @param param
    * @return
    */
    protected final List<AlphaSecialHeplerMsgEntity> convert2CustomerAlphaMessages(List<IMMessage> param) {
        List<AlphaSecialHeplerMsgEntity> customerMessageEntities = new ArrayList<>();
        if (param != null) {
            for (IMMessage message : param) {
                if (message != null) {
                    AlphaSecialHeplerMsgEntity imBody = getAlphaBody(message);
                    if (imBody != null) {
                        if (imBody.showType == 200) {
                            imBody.imMessage = message;
                            customerMessageEntities.add(imBody);
                        }
                    }
                }
            }
        }
        return customerMessageEntities;
    }

    private AlphaSecialHeplerMsgEntity getAlphaBody(IMMessage message) {
        JSONObject alphaJSONObject = null;
        try {
            String s = message.getAttachment().toJson(false);
            alphaJSONObject = JsonUtils.getJSONObject(s);
            if (alphaJSONObject.has("type")) {
                String type = alphaJSONObject.getString("type");
                //屏蔽审批消息
                if (StringUtils.containsIgnoreCase(type, "APPRO_")) {
                    return null;
                }
            }
            return JsonUtils.Gson2Bean(s, AlphaSecialHeplerMsgEntity.class);
        } catch (Exception e) {
            bugSync("AlphaHelper 解析异常2", StringUtils.throwable2string(e)
                    + "\nalphaJSONObject:" + alphaJSONObject);
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 是否
     *
     * @param id
     * @return
     */
    private boolean isMyRoomMsg(String id) {
        return StringUtils.equalsIgnoreCase(getIntent().getStringExtra(KEY_UID), id, false);
    }

    @Override
    public void onMessageReceived(IMMessageCustomBody customBody) {
        if (customBody != null
                && customBody.imMessage != null
                && isMyRoomMsg(customBody.imMessage.getSessionId())) {
            AlphaSecialHeplerMsgEntity imBody = getAlphaBody(customBody.imMessage);
            if (imBody != null) {
                imBody.imMessage = customBody.imMessage;
                chatAlphaSpecialHelperAdapter.addItem(imBody);
                scrollToBottom();
            }
        }
    }

    @Override
    public void onMessageReadAckReceived(List<MessageReceipt> list) {

    }

    @Override
    public void onMessageChanged(IMMessageCustomBody customBody) {

    }

    @Override
    public void onMessageRevoke(IMMessage message) {

    }

    @Override
    public void onMessageRevoke(long msgId) {

    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                AlphaSpeciaSetActivity.launch(this, getIMChatId());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 滚动到底部
     */

    private void scrollToBottom() {
        if (linearLayoutManager != null) {
            linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.getItemCount() - 1, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
