package com.icourt.alpha.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMCustomerMessageEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INIMessageListener;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.widget.dialog.AlertListDialog;
import com.icourt.api.RequestUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import okhttp3.Request;
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

    //收藏的消息列表
    protected final List<String> msgCollectedIdsList = new ArrayList<>();
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
    protected void onResume() {
        super.onResume();
        getMsgCollectedIds();
    }

    /**
     * 获取已经收藏的id列表
     */
    private void getMsgCollectedIds() {
        getApi().msgQueryAllCollectedIds(getIMChatType(), getIMChatId())
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            msgCollectedIdsList.clear();
                            msgCollectedIdsList.addAll(response.body().result);
                        }
                    }
                });
    }


    /**
     * 查询本地联系人
     *
     * @return
     */
    @UiThread
    @Nullable
    @CheckResult
    protected final List<GroupContactBean> queryAllContactFromDb() {
        if (contactDbService != null && contactDbService.isServiceAvailable()) {
            RealmResults<ContactDbModel> contactDbModels = contactDbService.queryAll();
            if (contactDbModels != null) {
                List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels));
                return contactBeen;
            }
        }
        return null;
    }

    /**
     * 异步查询本地联系人
     */
    protected final void queryAllContactFromDbAsync(@NonNull Consumer<List<GroupContactBean>> consumer) {
        if (consumer == null) return;
        Observable.create(new ObservableOnSubscribe<List<GroupContactBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<GroupContactBean>> e) throws Exception {
                ContactDbService threadContactDbService = null;
                try {
                    if (!e.isDisposed()) {
                        threadContactDbService = new ContactDbService(getLoadedLoginUserId());
                        RealmResults<ContactDbModel> contactDbModels = threadContactDbService.queryAll();
                        if (contactDbModels != null) {
                            List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels));
                            e.onNext(contactBeen);
                        }
                        e.onComplete();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (threadContactDbService != null) {
                        threadContactDbService.releaseService();
                    }
                }
            }
        }).compose(this.<List<GroupContactBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
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
    protected void deleteMsgFromDb(IMMessage message) {
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
     * 是否是http链接
     *
     * @param text
     * @return
     */
    protected final boolean isIMLinkText(String text) {
        return UrlUtils.isHttpLink(text);
    }

    /**
     * 发送纯链接的消息
     *
     * @param url
     */
    protected final void sendIMLinkMsg(final String url) {
        if (TextUtils.isEmpty(url)) return;
        try {
            Request request = new Request.Builder().url(url).build();
            RetrofitServiceFactory.provideOkHttpClient()
                    .newCall(request)
                    .enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            sendIMLinkMsgInner(url, null, null, null);
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            if (response != null && response.body() != null) {
                                String value = response.body().string();
                                String htmlTitle = UrlUtils.getHtmlLabel(value, "title");
                                if (!TextUtils.isEmpty(htmlTitle)) {
                                    htmlTitle = htmlTitle.replaceAll("<title>", "");
                                    if (!TextUtils.isEmpty(htmlTitle)) {
                                        htmlTitle = htmlTitle.replaceAll("</title>", "");
                                    }
                                }
                                String htmlDescription = getHtmlDescription(value);
                                String htmlImage = UrlUtils.getHtmlFirstImage(value);

                                sendIMLinkMsgInner(url, htmlTitle, htmlDescription, htmlImage);
                            }
                        }
                    });
        } catch (Exception e) {
            //view-source:错误
            e.printStackTrace();
            sendIMLinkMsgInner(url, null, null, null);
        }
    }


    /**
     * 发送链接 消息
     *
     * @param url
     * @param htmlTitle
     * @param htmlDescription
     * @param htmlImage
     */
    private void sendIMLinkMsgInner(String url, String htmlTitle, String htmlDescription, String htmlImage) {
        if (TextUtils.isEmpty(url)) return;
        IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createLinkMsg(getIMChatType(),
                getLoadedLoginName(),
                getIMChatId(),
                url,
                htmlTitle,
                htmlDescription,
                htmlImage);
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
     * <meta name="keywords" content="正则表达式,html"/>
     * <meta name="description" content="正则表达式,html"/>
     * 优先keywords
     *
     * @param htmlString
     * @return
     */
    private String getHtmlDescription(String htmlString) {
        String htmlKeywordslabel = UrlUtils.getHtmlKeywordslabel(htmlString);
        if (TextUtils.isEmpty(htmlKeywordslabel)) {
            return UrlUtils.getHtmlDescriptionlabel(htmlString);
        }
        return htmlKeywordslabel;
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
            if (imCustomerMessageEntity.imMessage == null) return;
            if (imCustomerMessageEntity.customIMBody == null) return;
            final List<String> menuItems = new ArrayList<>();
            switch (imCustomerMessageEntity.customIMBody.show_type) {
                case MSG_TYPE_AT:
                case MSG_TYPE_TXT:
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList("复制",
                            "钉",
                            msgCollectedIdsList.contains(imCustomerMessageEntity.customIMBody.id) ? "取消收藏" : "收藏",
                            "转任务"));
                    if (imCustomerMessageEntity.imMessage.getDirect() == MsgDirectionEnum.Out
                            && canRevokeMsg(imCustomerMessageEntity.imMessage.getTime())) {
                        menuItems.add("撤回");
                    }
                    break;
                case MSG_TYPE_FILE:
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList("钉",
                            msgCollectedIdsList.contains(imCustomerMessageEntity.customIMBody.id) ? "取消收藏" : "收藏",
                            "转任务"));
                    if (imCustomerMessageEntity.imMessage.getDirect() == MsgDirectionEnum.Out
                            && canRevokeMsg(imCustomerMessageEntity.imMessage.getTime())) {
                        menuItems.add("撤回");
                    }
                    break;
                case MSG_TYPE_DING://不能撤回 收藏的是钉的消息体,钉的消息[文本]可以转任务
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList(msgCollectedIdsList.contains(imCustomerMessageEntity.customIMBody.id) ? "取消收藏" : "收藏"
                            , "转任务"));
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
            showMsgActionDialog(imCustomerMessageEntity.customIMBody, menuItems);
        }

    }

    /**
     * 2分钟以内 可以撤回消息
     *
     * @param msgTime
     * @return
     */
    protected final boolean canRevokeMsg(long msgTime) {
        return System.currentTimeMillis() < msgTime + 2 * 60 * 1_000;
    }

    /**
     * 消息操作菜单
     * "复制", "钉", "收藏","取消收藏", "转任务","撤回"
     *
     * @param customIMBody
     * @param menuItems
     */
    private void showMsgActionDialog(final IMMessageCustomBody customIMBody, final List<String> menuItems) {
        if (customIMBody == null) return;
        if (menuItems == null) return;
        if (menuItems.isEmpty()) return;
        new AlertListDialog.ListBuilder(getContext())
                .setDividerColorRes(R.color.alpha_divider_color)
                .setDividerHeightRes(R.dimen.alpha_height_divider)
                .setItems((String[]) menuItems.toArray(new String[menuItems.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String actionName = menuItems.get(which);
                        if (TextUtils.equals(actionName, "复制")) {
                            msgActionCopy(customIMBody.content);
                        } else if (TextUtils.equals(actionName, "钉")) {
                            msgActionDing(true, customIMBody.id);
                        } else if (TextUtils.equals(actionName, "收藏")) {
                            msgActionCollect(customIMBody.id);
                        } else if (TextUtils.equals(actionName, "取消收藏")) {
                            msgActionCollectCancel(customIMBody.id);
                        } else if (TextUtils.equals(actionName, "转任务")) {
                            msgActionConver2Task(customIMBody);
                        } else if (TextUtils.equals(actionName, "撤回")) {
                            msgActionRevoke(customIMBody.id);
                        }
                    }
                }).show();
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
     * @param charSequence
     */
    protected final void msgActionCopy(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) return;
        SystemUtils.copyToClipboard(getContext(), "msg", charSequence);
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
    protected final void msgActionCollect(final String msgId) {
        getApi().msgCollect(msgId,getIMChatType(),getIMChatId())
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            msgCollectedIdsList.add(msgId);
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
    protected final void msgActionCollectCancel(final String msgId) {
        getApi().msgCollectCancel(msgId,getIMChatType(),getIMChatId())
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            showTopSnackBar("取消收藏成功");
                            msgCollectedIdsList.remove(msgId);
                        } else {
                            showTopSnackBar("取消收藏失败");
                        }
                    }
                });
    }

    /**
     * 消息撤回
     *
     * @param msgId
     */
    protected final void msgActionRevoke(String msgId) {
        if (TextUtils.isEmpty(msgId)) return;
        getApi().msgRevoke(msgId)
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {

                    }
                });
    }
}
