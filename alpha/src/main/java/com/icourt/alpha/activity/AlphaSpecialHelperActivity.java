package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAlphaSpecialHelperAdapter;
import com.icourt.alpha.entity.bean.AlphaSecialHeplerMsgEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.view.recyclerviewDivider.ChatItemDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.team.model.Team;

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
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    ChatAlphaSpecialHelperAdapter chatAlphaSpecialHelperAdapter;

    LinearLayoutManager linearLayoutManager;

    public static void launch(@NonNull Context context, String accid) {
        if (context == null) return;
        Intent intent = new Intent(context, AlphaSpecialHelperActivity.class);
        intent.putExtra(KEY_UID, accid);
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
        setTitle("Alpha助手");
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAlphaSpecialHelperAdapter = new ChatAlphaSpecialHelperAdapter());
        recyclerView.addItemDecoration(new ChatItemDecoration(getContext(), chatAlphaSpecialHelperAdapter));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(false);
            }
        });
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(getLastMessage(), QUERY_OLD, 20, true)
                .setCallback(new RequestCallback<List<IMMessage>>() {
                    @Override
                    public void onSuccess(List<IMMessage> param) {
                        LogUtils.d("----------->query result:" + param);
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


    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
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
                IMUtils.logIMMessage("-------------->chat:", message);
                if (message != null) {
                    AlphaSecialHeplerMsgEntity imBody = getAlphaBody(message);
                    if (imBody != null) {
                        imBody.imMessage = message;
                        customerMessageEntities.add(imBody);
                    }
                }
            }
        }
        return customerMessageEntities;
    }

    private AlphaSecialHeplerMsgEntity getAlphaBody(IMMessage message) {
        try {
            return JsonUtils.Gson2Bean(message.getAttachment().toJson(false), AlphaSecialHeplerMsgEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void onMessageReceived(IMMessageCustomBody customBody) {

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
}
