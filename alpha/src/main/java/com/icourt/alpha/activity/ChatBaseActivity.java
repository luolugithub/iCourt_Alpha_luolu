package com.icourt.alpha.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.MsgConvert2Task;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INIMessageListener;
import com.icourt.alpha.utils.GlobalMessageObserver;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.widget.dialog.AlertListDialog;
import com.icourt.api.RequestUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.icourt.alpha.constants.Const.MSG_STATU_FAIL;
import static com.icourt.alpha.constants.Const.MSG_STATU_SUCCESS;
import static com.icourt.alpha.constants.Const.MSG_TYPE_ALPHA;
import static com.icourt.alpha.constants.Const.MSG_TYPE_AT;
import static com.icourt.alpha.constants.Const.MSG_TYPE_DING;
import static com.icourt.alpha.constants.Const.MSG_TYPE_FILE;
import static com.icourt.alpha.constants.Const.MSG_TYPE_IMAGE;
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
    protected final Set<String> msgCollectedIdsList = new HashSet<>();
    //钉的消息列表
    protected final Set<String> msgDingedIdsList = new HashSet<>();
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
            onMessageChanged(GlobalMessageObserver.getIMBody(message));
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
        EventBus.getDefault().register(this);
        loadedLoginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(getLoginUserId());
        registerObservers(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public final void onMessageEvent(IMMessageCustomBody customBody) {
        if (customBody == null) return;
        onMessageReceived(customBody);
        handleGlobalDingMsgStatu(customBody);
    }

    /**
     * 处理全局钉的状态
     *
     * @param customBody
     */
    private void handleGlobalDingMsgStatu(IMMessageCustomBody customBody) {
        if (customBody == null) return;
        switch (customBody.show_type) {
            case MSG_TYPE_DING://钉消息是全局状态 别人钉了 我只能取消钉
                if (customBody.ext != null) {
                    if (customBody.ext.pin) {
                        msgDingedIdsList.add(customBody.ext.id);
                    } else {
                        msgDingedIdsList.remove(customBody.ext.id);
                    }
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getMsgCollectedIds();
        getMsgDingedIds();
        switch (getIMChatType()) {
            case CHAT_TYPE_P2P:
                NIMClient.getService(MsgService.class)
                        .setChattingAccount(getIMChatId(), SessionTypeEnum.P2P);
                break;
            case CHAT_TYPE_TEAM:
                NIMClient.getService(MsgService.class)
                        .setChattingAccount(getIMChatId(), SessionTypeEnum.Team);
                break;
        }
    }


    /**
     * 获取被钉的ids列表
     */
    private void getMsgDingedIds() {
        getChatApi().msgQueryAllDingIds(getIMChatType(), getIMChatId())
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            msgDingedIdsList.clear();
                            msgDingedIdsList.addAll(response.body().result);
                        }
                    }
                });
    }

    /**
     * 获取已经收藏的id列表
     */
    private void getMsgCollectedIds() {
        getChatApi().msgQueryAllCollectedIds(getIMChatType(), getIMChatId())
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
        clearUnReadNum();
        registerObservers(false);
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeMessageReceipt(messageReceiptObserver, register);
        service.observeMsgStatus(messageStatusObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
    }


    /**
     * 获取云信群组信息
     *
     * @param requestCallback
     */
    protected final void getTeamINFO(RequestCallback<Team> requestCallback) {
        NIMClient.getService(TeamService.class)
                .queryTeam(getIMChatId())
                .setCallback(requestCallback);
    }

    /**
     * 获取消息未读数量
     *
     * @return
     */
    protected final void getUnreadNum(@NonNull Consumer<Integer> consumer) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (e.isDisposed()) return;
                List<RecentContact> recentContacts = NIMClient.getService(MsgService.class)
                        .queryRecentContactsBlock();
                int unreadNum = 0;
                if (recentContacts != null && !recentContacts.isEmpty()) {
                    for (RecentContact recentContact : recentContacts) {
                        if (recentContact != null
                                && StringUtils.equalsIgnoreCase(recentContact.getContactId(), getIMChatId(), false)) {
                            unreadNum = recentContact.getUnreadCount();
                            break;
                        }
                    }
                }
                e.onNext(unreadNum);
                e.onComplete();
            }
        }).compose(this.<Integer>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * 清除未读数量
     */
    protected final void clearUnReadNum() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (e.isDisposed()) return;
                switch (getIMChatType()) {
                    case CHAT_TYPE_P2P:
                        //会主动通知recentContact
                        NIMClient.getService(MsgService.class)
                                .clearUnreadCount(getIMChatId(), SessionTypeEnum.P2P);
                        break;
                    case CHAT_TYPE_TEAM:
                        NIMClient.getService(MsgService.class)
                                .clearUnreadCount(getIMChatId(), SessionTypeEnum.Team);
                        break;
                }
                e.onNext(0);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .subscribe();
    }


    @Override
    protected void onPause() {
        super.onPause();
        NIMClient.getService(MsgService.class)
                .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
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
     * @param to
     * @return
     */
    protected final boolean isCurrentRoomSession(String to) {
        return StringUtils.equalsIgnoreCase(to, getIMChatId(), false)
                || StringUtils.equalsIgnoreCase(to, getLoadedLoginUserId(), false);
    }


    /**
     * 发送文本吧消息
     *
     * @param text
     */
    protected final void sendIMTextMsg(String text) {
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createTextMsg(getIMChatType(),
                getLoadedLoginName(),
                getLoadedLoginUserId(),
                getIMChatId(),
                text);
        onMessageReceived(msgPostEntity);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        final String finalJsonBody = jsonBody;
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        if (response.body().result != null) {
                            response.body().result.msg_statu = MSG_STATU_SUCCESS;
                            updateCustomBody(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (finalJsonBody != null) {
                            saveSendNimMsg(finalJsonBody, MsgStatusEnum.fail, false);
                            msgPostEntity.msg_statu = MSG_STATU_FAIL;
                            updateCustomBody(msgPostEntity);
                        }
                    }
                });
    }

    /**
     * 重发消息
     *
     * @param msgPostEntity
     */
    protected void retrySendCustomBody(final IMMessageCustomBody msgPostEntity) {
        if (msgPostEntity == null) return;
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        msgPostEntity.msg_statu = Const.MSG_STATU_SENDING;
        updateCustomBody(msgPostEntity);
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        if (response.body().result != null) {
                            response.body().result.msg_statu = MSG_STATU_SUCCESS;
                            updateCustomBody(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        msgPostEntity.msg_statu = Const.MSG_STATU_FAIL;
                        updateCustomBody(msgPostEntity);
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
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createAtMsg(getIMChatType(),
                getLoadedLoginName(),
                getLoadedLoginUserId(),
                getIMChatId(),
                text,
                isAtAll,
                accIds);
        onMessageReceived(msgPostEntity);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        final String finalJsonBody = jsonBody;
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        if (response.body().result != null) {
                            response.body().result.msg_statu = MSG_STATU_SUCCESS;
                            updateCustomBody(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (finalJsonBody != null) {
                            saveSendNimMsg(finalJsonBody, MsgStatusEnum.fail, false);
                            msgPostEntity.msg_statu = MSG_STATU_FAIL;
                            updateCustomBody(msgPostEntity);
                        }
                    }
                });
    }

    /**
     * 发送图片消息
     *
     * @param path
     */
    protected final void sendIMPicMsg(String path) {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (!file.exists()) {
            showTopSnackBar("文件不存在啦");
            return;
        }

        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createPicMsg(getIMChatType(),
                getLoadedLoginName(),
                getLoadedLoginUserId(),
                getIMChatId(),
                file.getAbsolutePath());
        onMessageReceived(msgPostEntity);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        final String finalJsonBody = jsonBody;
        Map<String, RequestBody> params = new HashMap<>();
        params.put("platform", RequestUtils.createTextBody(msgPostEntity.platform));
        params.put("to", RequestUtils.createTextBody(msgPostEntity.to));
        params.put("from", RequestUtils.createTextBody(msgPostEntity.from));
        params.put("ope", RequestUtils.createTextBody(String.valueOf(msgPostEntity.ope)));
        params.put("name", RequestUtils.createTextBody(msgPostEntity.name));
        params.put("magic_id", RequestUtils.createTextBody(msgPostEntity.magic_id));

        params.put("file\"; filename=\"image.jpg\"", RequestUtils.createImgBody(file));
        getChatApi().msgImageAdd(params)
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        if (response.body().result != null) {
                            response.body().result.msg_statu = MSG_STATU_SUCCESS;
                            updateCustomBody(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (finalJsonBody != null) {
                            saveSendNimMsg(finalJsonBody, MsgStatusEnum.fail, false);
                            msgPostEntity.msg_statu = MSG_STATU_FAIL;
                            updateCustomBody(msgPostEntity);
                        }
                    }
                });
    }

    /**
     * 重新发送图片
     *
     * @param msgPostEntity
     */
    protected final void retrySendIMPicMsg(final IMMessageCustomBody msgPostEntity) {
        if (msgPostEntity == null) return;
        if (msgPostEntity.ext != null
                && !TextUtils.isEmpty(msgPostEntity.ext.thumb)) {
            if (!msgPostEntity.ext.thumb.startsWith("http")) {
                File file = new File(msgPostEntity.ext.thumb);
                if (!file.exists()) {
                    showTopSnackBar("文件不存在啦");
                    return;
                }
                String jsonBody = null;
                try {
                    jsonBody = JsonUtils.Gson2String(msgPostEntity);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
                final String finalJsonBody = jsonBody;
                Map<String, RequestBody> params = new HashMap<>();
                params.put("platform", RequestUtils.createTextBody(msgPostEntity.platform));
                params.put("to", RequestUtils.createTextBody(msgPostEntity.to));
                params.put("from", RequestUtils.createTextBody(msgPostEntity.from));
                params.put("ope", RequestUtils.createTextBody(String.valueOf(msgPostEntity.ope)));
                params.put("name", RequestUtils.createTextBody(msgPostEntity.name));
                params.put("magic_id", RequestUtils.createTextBody(msgPostEntity.magic_id));

                params.put("file\"; filename=\"image.jpg\"", RequestUtils.createImgBody(file));

                msgPostEntity.msg_statu = Const.MSG_STATU_SENDING;
                updateCustomBody(msgPostEntity);

                getChatApi().msgImageAdd(params)
                        .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                            @Override
                            public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                                if (response.body().result != null) {
                                    response.body().result.msg_statu = MSG_STATU_SUCCESS;
                                    updateCustomBody(response.body().result);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                                super.onFailure(call, t);
                                if (finalJsonBody != null) {
                                    msgPostEntity.msg_statu = MSG_STATU_FAIL;
                                    updateCustomBody(msgPostEntity);
                                }
                            }
                        });
            }
        }
    }


    /**
     * 本地保存 将会通知回掉
     *
     * @param content
     */
    @Nullable
    private IMMessage saveSendNimMsg(@NonNull String content, MsgStatusEnum msgStatusEnum, boolean notify) {
        IMMessage mMessage = null;
        switch (getIMChatType()) {
            case CHAT_TYPE_P2P:
                mMessage = MessageBuilder.createTextMessage(getIMChatId(), SessionTypeEnum.P2P, "");
                break;
            case CHAT_TYPE_TEAM:
                mMessage = MessageBuilder.createTextMessage(getIMChatId(), SessionTypeEnum.Team, "");
                break;
        }
        if (mMessage != null) {
            mMessage.setStatus(msgStatusEnum);
            mMessage.setContent(content);
            NIMClient.getService(MsgService.class)
                    .saveMessageToLocal(mMessage, notify);
        }
        return mMessage;
    }

    /**
     * 更新消息
     *
     * @param mMessage
     */
    private void updateNimMsg(IMMessage mMessage) {
        if (mMessage == null) return;
        NIMClient.getService(MsgService.class)
                .updateIMMessageStatus(mMessage);
    }

    /**
     * 更新消息状态
     *
     * @param mMessage
     */
    private void updateNimMsgStatus(IMMessage mMessage) {
        if (mMessage == null) return;
        NIMClient.getService(MsgService.class)
                .updateIMMessageStatus(mMessage);
    }

    /**
     * 更新自定义消息
     *
     * @param msgPostEntity
     */
    private void updateCustomBody(IMMessageCustomBody msgPostEntity) {
        if (msgPostEntity == null) return;
        onMessageChanged(msgPostEntity);
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
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createLinkMsg(getIMChatType(),
                getLoadedLoginName(),
                getLoadedLoginUserId(),
                getIMChatId(),
                url,
                htmlTitle,
                htmlDescription,
                htmlImage);
        onMessageReceived(msgPostEntity);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        final String finalJsonBody = jsonBody;
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        if (response.body().result != null) {
                            response.body().result.msg_statu = MSG_STATU_SUCCESS;
                            updateCustomBody(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (finalJsonBody != null) {
                            saveSendNimMsg(finalJsonBody, MsgStatusEnum.fail, false);
                            msgPostEntity.msg_statu = MSG_STATU_FAIL;
                            updateCustomBody(msgPostEntity);
                        }
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
    protected final List<IMMessageCustomBody> convert2CustomerMessages(List<IMMessage> param) {
        List<IMMessageCustomBody> customerMessageEntities = new ArrayList<>();
        if (param != null) {
            for (IMMessage message : param) {
                if (message != null) {
                    IMMessageCustomBody imBody = getIMBody(message);
                    if (imBody != null) {
                        customerMessageEntities.add(imBody);
                    }
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
        return GlobalMessageObserver.getLocalIMBody(message);
    }

    /**
     * 是否是发出的消息
     *
     * @param from
     * @return
     */
    private boolean isSendMsg(String from) {
        return StringUtils.equalsIgnoreCase(from, getLoadedLoginUserId(), false);
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, final BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof ChatAdapter) {
            ChatAdapter chatAdapter = (ChatAdapter) adapter;
            final IMMessageCustomBody iMMessageCustomBody = chatAdapter.getItem(position);
            if (iMMessageCustomBody == null) return false;
            if (TextUtils.isEmpty(iMMessageCustomBody.id)) return false;
            final List<String> menuItems = new ArrayList<>();
            switch (iMMessageCustomBody.show_type) {
                case MSG_TYPE_AT:
                case MSG_TYPE_TXT:
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList("复制",
                            isDinged(iMMessageCustomBody.id) ? "取消钉" : "钉",
                            isCollected(iMMessageCustomBody.id) ? "取消收藏" : "收藏",
                            "转任务"));
                    if (isSendMsg(iMMessageCustomBody.from)
                            && canRevokeMsg(iMMessageCustomBody.send_time)) {
                        menuItems.add("撤回");
                    }
                    break;
                case MSG_TYPE_FILE:
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList(
                            isDinged(iMMessageCustomBody.id) ? "取消钉" : "钉",
                            isCollected(iMMessageCustomBody.id) ? "取消收藏" : "收藏",
                            "转任务"));
                    if (isSendMsg(iMMessageCustomBody.from)
                            && canRevokeMsg(iMMessageCustomBody.send_time)) {
                        menuItems.add("撤回");
                    }
                    break;
                case MSG_TYPE_IMAGE:
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList(
                            isDinged(iMMessageCustomBody.id) ? "取消钉" : "钉",
                            isCollected(iMMessageCustomBody.id) ? "取消收藏" : "收藏",
                            "转任务"));
                    if (isSendMsg(iMMessageCustomBody.from)
                            && canRevokeMsg(iMMessageCustomBody.send_time)) {
                        menuItems.add("撤回");
                    }
                    break;
                case MSG_TYPE_DING://不能撤回 收藏的是钉的消息体,钉的消息[文本]可以转任务
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList(
                            isCollected(iMMessageCustomBody.id) ? "取消收藏" : "收藏"
                            , "转任务"));
                    break;
                case MSG_TYPE_SYS:
                    break;
                case MSG_TYPE_LINK:
                    menuItems.clear();
                    menuItems.addAll(Arrays.asList(
                            isDinged(iMMessageCustomBody.id) ? "取消钉" : "钉",
                            isCollected(iMMessageCustomBody.id) ? "取消收藏" : "收藏"
                            , "转任务"));
                    break;
                case MSG_TYPE_ALPHA://暂时不用处理
                    break;
                case MSG_TYPE_VOICE://暂时不用处理
                    break;
            }
            if (!menuItems.contains("转发")) {
                menuItems.add("转发");
            }
            showMsgActionDialog(iMMessageCustomBody, menuItems);
        }

        return true;
    }

    /**
     * 是否被钉过
     *
     * @param msgId
     * @return
     */
    private boolean isDinged(String msgId) {
        return msgDingedIdsList.contains(msgId);
    }

    /**
     * 是否收藏过
     *
     * @param msgId
     * @return
     */
    private boolean isCollected(String msgId) {
        return msgCollectedIdsList.contains(msgId);
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
                        } else if (TextUtils.equals(actionName, "取消钉")) {
                            msgActionDing(false, customIMBody.id);
                        } else if (TextUtils.equals(actionName, "收藏")) {
                            msgActionCollect(customIMBody.id);
                        } else if (TextUtils.equals(actionName, "取消收藏")) {
                            msgActionCollectCancel(customIMBody.id);
                        } else if (TextUtils.equals(actionName, "转任务")) {
                            msgActionConvert2Task(customIMBody);
                        } else if (TextUtils.equals(actionName, "撤回")) {
                            msgActionRevoke(customIMBody.id);
                        } else if (TextUtils.equals(actionName, "转发")) {
                            showContactShareDialogFragment(customIMBody.id);
                        }
                    }
                }).show();
    }

    /**
     * 消息转任务
     * 文件 变成附件
     *
     * @param customIMBody
     */
    protected final void msgActionConvert2Task(IMMessageCustomBody customIMBody) {
        if (customIMBody == null) return;
        String textContent = "";
        if (customIMBody.show_type == MSG_TYPE_DING) {
            if (customIMBody.ext != null) {
                textContent = customIMBody.ext.content;
            }
        } else {
            textContent = customIMBody.content;
        }
        if (TextUtils.isEmpty(textContent)) return;
        final String textContentFinal = textContent;
        showLoadingDialog(null);
        getApi().msgConvert2Task(textContentFinal)
                .enqueue(new SimpleCallBack<MsgConvert2Task>() {
                    @Override
                    public void onSuccess(Call<ResEntity<MsgConvert2Task>> call, Response<ResEntity<MsgConvert2Task>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            TaskCreateActivity.launch(getContext(),
                                    response.body().result.content,
                                    response.body().result.startTime);
                        } else {
                            TaskCreateActivity.launch(getContext(),
                                    textContentFinal, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<MsgConvert2Task>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        TaskCreateActivity.launch(getContext(),
                                textContentFinal, null);
                    }
                });
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
    protected final void msgActionDing(final boolean isDing, final String dingMsgId) {
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createDingMsg(getIMChatType(),
                getLoadedLoginName(),
                getLoadedLoginUserId(),
                getIMChatId(),
                isDing,
                dingMsgId);
        onMessageReceived(msgPostEntity);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        final String finalJsonBody = jsonBody;
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        if (response.body().result != null) {
                            if (!isDing) {//取消钉
                                msgDingedIdsList.remove(dingMsgId);
                            } else {//钉
                                msgDingedIdsList.add(dingMsgId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        if (finalJsonBody != null) {
                            saveSendNimMsg(finalJsonBody, MsgStatusEnum.fail, false);
                            msgPostEntity.msg_statu = MSG_STATU_FAIL;
                            updateCustomBody(msgPostEntity);
                        }
                    }
                });
    }

    /**
     * 收藏消息
     *
     * @param msgId
     */

    protected final void msgActionCollect(final String msgId) {
        getChatApi().msgCollect(msgId, getIMChatType(), getIMChatId())
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
        getChatApi().msgCollectCancel(msgId, getIMChatType(), getIMChatId())
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
        getChatApi().msgRevoke(msgId)
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {

                    }
                });
    }


    /**
     * 展示联系人转发对话框
     *
     * @param id
     */
    public void showContactShareDialogFragment(String id) {
        String tag = "ContactShareDialogFragment";
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactShareDialogFragment.newInstance(id)
                .show(mFragTransaction, tag);
    }
}
