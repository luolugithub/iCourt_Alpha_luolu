package com.icourt.alpha.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.IMCustomerMessageEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INIMessageListener;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.dialog.AlertListDialog;
import com.icourt.api.RequestUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.MSG_TYPE_ALPHA;
import static com.icourt.alpha.constants.Const.MSG_TYPE_AT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_DING;
import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_LINK;
import static com.icourt.alpha.constants.Const.MSG_TYPE_SYS;
import static com.icourt.alpha.constants.Const.MSG_TYPE_TXT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_VOICE;

/**
 * Description 聊天基类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public abstract class ChatBaseActivity extends BaseActivity implements INIMessageListener, BaseRecyclerAdapter.OnItemLongClickListener {

    /**
     * 收到消息
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            onMessageReceived(messages);
        }
    };
    /**
     * 收到已读回执
     */
    Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            onMessageReadAckReceived(messageReceipts);
        }
    };

    /**
     * 消息状态发生改变
     */
    Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            onMessageChanged(message);
        }
    };


    /**
     * 消息撤回
     */
    Observer<IMMessage> revokeMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            onMessageRevoke(message);
        }
    };

    private ContactDbService contactDbService;
    private AlphaUserInfo loadedLoginUserInfo;

    /**
     * 获取登陆人信息
     *
     * @return
     */
    @CheckResult
    protected final AlphaUserInfo getLoadedLoginUserInfo() {
        return loadedLoginUserInfo;
    }

    /**
     * 获取登陆uid
     *
     * @return
     */
    protected String getLoadedLoginUserId() {
        AlphaUserInfo loadedLoginUserInfo = getLoadedLoginUserInfo();
        return loadedLoginUserInfo != null ? loadedLoginUserInfo.getUserId() : "";
    }

    /**
     * 获取登陆名称
     *
     * @return
     */
    protected String getLoadedLoginName() {
        AlphaUserInfo loadedLoginUserInfo = getLoadedLoginUserInfo();
        return loadedLoginUserInfo != null ? loadedLoginUserInfo.getName() : "";
    }

    /**
     * 获取登陆token
     *
     * @return
     */
    protected String getLoadedLoginToken() {
        AlphaUserInfo loadedLoginUserInfo = getLoadedLoginUserInfo();
        return loadedLoginUserInfo != null ? loadedLoginUserInfo.getToken() : "";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadedLoginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(getLoginUserId());
        registerObservers(true);
    }

    @Override
    protected void onDestroy() {
        registerObservers(false);
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
        super.onDestroy();
    }

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        service.observeMessageReceipt(messageReceiptObserver, register);
        service.observeMsgStatus(messageStatusObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
    }

    /**
     * 从本地删除消息
     *
     * @param message
     */
    protected void deleteFromDb(IMMessage message) {
        if (message == null) return;
        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
    }


    /**
     * @return 聊天id p2p id或者team id
     */
    protected abstract String getIMChatId();

    /**
     * @return 聊天类型 p2p或者team
     */
    @Const.CHAT_TYPE
    protected abstract int getIMChatType();


    /**
     * 是否是当前聊天组对话
     *
     * @param sessionId
     * @return
     */
    protected final boolean isCurrentRoomSession(String sessionId) {
        return TextUtils.equals(sessionId, getIMChatId());
    }

    /**
     * 发送文本吧消息
     *
     * @param text
     */
    protected final void sendIMTextMsg(String text) {
        IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createTextMsg(getIMChatType(),
                getLoadedLoginName(),
                getIMChatId(),
                text);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        getApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 发送@消息
     *
     * @param text
     * @param isAtAll 是否是at所有人;@所有人 accid可空; 否则不可空
     * @param accIds
     */
    protected final void sendIMAtMsg(@NonNull String text, boolean isAtAll, @Nullable List<String> accIds) {
        IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createAtMsg(getIMChatType(),
                getLoadedLoginName(),
                getIMChatId(),
                text,
                isAtAll,
                accIds);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        getApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
    }

    /**
     * 发送文件消息
     *
     * @param url
     */
    protected final void sendIMFileMsg(String url) {
        IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createFileMsg(getIMChatType(),
                getLoadedLoginName(),
                getIMChatId(),
                url);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        getApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
    }


    /**
     * 转化成自定义的消息体
     *
     * @param param
     * @return
     */
    protected final List<IMCustomerMessageEntity> convert2CustomerMessages(List<IMMessage> param) {
        List<IMCustomerMessageEntity> customerMessageEntities = new ArrayList<>();
        if (param != null) {
            for (IMMessage message : param) {
                if (message != null) {
                    IMCustomerMessageEntity customerMessageEntity = new IMCustomerMessageEntity();
                    customerMessageEntity.imMessage = message;
                    customerMessageEntity.customIMBody = getIMBody(message);
                    customerMessageEntities.add(customerMessageEntity);
                }
            }
        }
        return customerMessageEntities;
    }

    /**
     * 获取消息体
     *
     * @param message
     * @return
     */
    protected final IMMessageCustomBody getIMBody(IMMessage message) {
        IMMessageCustomBody imBodyEntity = null;
        try {
            log("--------------->customBody:" + message.getContent());
            imBodyEntity = JsonUtils.Gson2Bean(message.getContent(), IMMessageCustomBody.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return imBodyEntity;
    }

    @Override
    public void onItemLongClick(BaseRecyclerAdapter adapter, final BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof ChatAdapter) {
            ChatAdapter chatAdapter = (ChatAdapter) adapter;
            final IMCustomerMessageEntity imCustomerMessageEntity = chatAdapter.getItem(position);
            if (imCustomerMessageEntity == null) return;
            if (imCustomerMessageEntity.customIMBody == null) return;
            switch (imCustomerMessageEntity.customIMBody.show_type) {
                case MSG_TYPE_AT:
                case MSG_TYPE_TXT:
                    new AlertListDialog.ListBuilder(getContext())
                            .setDividerColorRes(R.color.alpha_divider_color)
                            .setDividerHeightRes(R.dimen.alpha_height_divider)
                            .setItems(new CharSequence[]{"复制", "钉", "收藏", "转任务"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            TextView chat_txt_tv = holder.obtainView(R.id.chat_txt_tv);
                                            msgActionCopy(chat_txt_tv);
                                            break;
                                        case 1:
                                            msgActionDing(true, imCustomerMessageEntity.customIMBody.id);
                                            break;
                                        case 2:
                                            msgActionCollect(imCustomerMessageEntity.customIMBody.id);
                                            //msgActionCollectCancel(imCustomerMessageEntity.customIMBody.id);
                                            break;
                                        case 3:
                                            msgActionConver2Task(imCustomerMessageEntity.customIMBody);
                                            break;
                                    }
                                }
                            }).show();
                    break;
                case MSG_TYPE_FILE:
                    new AlertListDialog.ListBuilder(getContext())
                            .setDividerColorRes(R.color.alpha_divider_color)
                            .setDividerHeightRes(R.dimen.alpha_height_divider)
                            .setItems(new CharSequence[]{"钉", "收藏", "转任务"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            msgActionDing(true, imCustomerMessageEntity.customIMBody.id);
                                            break;
                                        case 1:
                                            msgActionCollect(imCustomerMessageEntity.customIMBody.id);
                                            //msgActionCollectCancel(imCustomerMessageEntity.customIMBody.id);
                                            break;
                                        case 2:
                                            msgActionConver2Task(imCustomerMessageEntity.customIMBody);
                                            break;
                                    }
                                }
                            })
                            .show();
                    break;
                case MSG_TYPE_DING:
                    break;
                case MSG_TYPE_SYS:
                    break;
                case MSG_TYPE_LINK:
                    break;
                case MSG_TYPE_ALPHA://暂时不用处理
                    break;
                case MSG_TYPE_VOICE://暂时不用处理
                    break;
            }
        }

    }

    /**
     * 消息转任务
     *
     * @param customIMBody
     */
    protected final void msgActionConver2Task(IMMessageCustomBody customIMBody) {
        //TODO 转任务
    }


    /**
     * 消息复制
     *
     * @param textView
     */
    protected final void msgActionCopy(TextView textView) {
        if (textView == null) return;
        if (TextUtils.isEmpty(textView.getText())) return;
        SystemUtils.copyToClipboard(getContext(), "msg", textView.getText());
        showTopSnackBar("复制成功");
    }

    /**
     * 钉消息
     *
     * @param isDing    钉 true 取消钉 false
     * @param dingMsgId
     */
    protected final void msgActionDing(boolean isDing, String dingMsgId) {
        IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createDingMsg(getIMChatType(),
                getLoadedLoginName(),
                getIMChatId(),
                isDing,
                dingMsgId);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        getApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                    }
                });
    }

    /**
     * 收藏消息
     *
     * @param msgId
     */
    protected final void msgActionCollect(String msgId) {
        getApi().msgCollect(msgId)
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            showTopSnackBar("收藏成功");
                        } else {
                            showTopSnackBar("收藏失败");
                        }
                    }
                });
    }

    /**
     * 收藏消息 取消
     *
     * @param msgId
     */
    protected final void msgActionCollectCancel(String msgId) {
        getApi().msgCollectCancel(msgId)
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            showTopSnackBar("取消收藏成功");
                        } else {
                            showTopSnackBar("取消收藏失败");
                        }
                    }
                });
    }
}
