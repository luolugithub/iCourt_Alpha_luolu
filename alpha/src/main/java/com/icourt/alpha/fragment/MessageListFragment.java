package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.asange.recyclerviewadapter.OnItemLongClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.AlphaSpecialHelperActivity;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.activity.LoginSelectActivity;
import com.icourt.alpha.activity.MainActivity;
import com.icourt.alpha.activity.SearchPolymerizationActivity;
import com.icourt.alpha.adapter.IMSessionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.IMSessionEntity;
import com.icourt.alpha.entity.event.GroupActionEvent;
import com.icourt.alpha.entity.event.MemberEvent;
import com.icourt.alpha.entity.event.NoDisturbingEvent;
import com.icourt.alpha.entity.event.SetTopEvent;
import com.icourt.alpha.entity.event.UnReadEvent;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.service.SyncDataService;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.nim.GlobalMessageObserver;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description  会话列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class MessageListFragment extends BaseRecentContactFragment
        implements
        OnTabDoubleClickListener,
        OnItemClickListener,
        OnItemLongClickListener {

    public static MessageListFragment newInstance() {
        return new MessageListFragment();
    }

    private final List<String> localSetTops = new ArrayList<>();
    private final List<String> localNoDisturbs = new ArrayList<>();

    @BindView(R.id.login_status_tv)
    TextView loginStatusTv;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    private int totalUnReadCount;
    private DataChangeAdapterObserver dataChangeAdapterObserver = new DataChangeAdapterObserver() {
        @Override
        protected void updateUI() {
            if (imSessionAdapter != null && contentEmptyText != null) {
                List<IMSessionEntity> data = imSessionAdapter.getData();
                contentEmptyText.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                int unReadCount = 0;
                for (IMSessionEntity sessionEntity : data) {
                    if (sessionEntity != null && sessionEntity.recentContact != null) {
                        if (!localNoDisturbs.contains(sessionEntity.recentContact.getContactId())) {
                            unReadCount += sessionEntity.recentContact.getUnreadCount();
                        }
                    }
                }
                if (totalUnReadCount != unReadCount) {
                    totalUnReadCount = unReadCount;
                    EventBus.getDefault().post(new UnReadEvent(unReadCount));
                }
            }
        }
    };

    Unbinder unbinder;
    IMSessionAdapter imSessionAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    AlphaUserInfo loginUserInfo;
    OnPageFragmentCallBack onPageFragmentCallBack;
    Comparator<IMSessionEntity> imSessionEntityComparator = new Comparator<IMSessionEntity>() {

        @Override
        public int compare(IMSessionEntity o1, IMSessionEntity o2) {
            if (o1 != null
                    && o1.recentContact != null
                    && o2 != null
                    && o2.recentContact != null) {
                //先置顶 都置顶 按时间
                if (localSetTops.contains(o1.recentContact.getContactId()) && localSetTops.contains(o2.recentContact.getContactId())) {
                    long time = o1.recentContact.getTime() - o2.recentContact.getTime();
                    return time == 0 ? 0 : (time > 0 ? -1 : 1);
                } else if (localSetTops.contains(o1.recentContact.getContactId())) {
                    return -1;
                } else if (localSetTops.contains(o2.recentContact.getContactId())) {
                    return 1;
                } else {
                    long time = o1.recentContact.getTime() - o2.recentContact.getTime();
                    return time == 0 ? 0 : (time > 0 ? -1 : 1);
                }
            }
            return 0;
        }
    };
    boolean isFirstIntoPage = true;//是否是第一次进入页面


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPageFragmentCallBack = (OnPageFragmentCallBack) context;
        } catch (ClassCastException e) {
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_message_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    /**
     * 收到消息
     *
     * @param recentContacts
     */
    @Override
    protected void recentContactReceive(@NonNull final List<RecentContact> recentContacts) {
        if (imSessionAdapter == null) {
            return;
        }
        for (RecentContact recentContact : recentContacts) {
            if (recentContact == null) {
                continue;
            }
            addOrUpdateItem(parseSession(recentContact));
        }
    }

    /**
     * 更新一条或者添加一条
     *
     * @param imSessionEntity
     */
    private void addOrUpdateItem(IMSessionEntity imSessionEntity) {
        if (imSessionEntity == null) {
            return;
        }
        List<IMSessionEntity> sessionEntities = imSessionAdapter.getData();
        int indexOf = sessionEntities.indexOf(imSessionEntity);
        if (indexOf >= 0) {
            //1.更新--->2.排序--->3.更新适配器
            sessionEntities.set(indexOf, imSessionEntity);
            Collections.sort(sessionEntities, imSessionEntityComparator);
            imSessionAdapter.notifyDataSetChanged();
        } else {
            sessionEntities.add(imSessionEntity);
            Collections.sort(sessionEntities, imSessionEntityComparator);
            imSessionAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 解析session
     *
     * @param recentContact
     * @return
     */
    @CheckResult
    @Nullable
    private IMSessionEntity parseSession(RecentContact recentContact) {
        if (recentContact == null) {
            return null;
        }
        if (recentContact.getMsgType() == MsgTypeEnum.custom) {
            IMMessageCustomBody customIMBody = getAlphaHelper(recentContact);
            if (customIMBody != null) {
                return new IMSessionEntity(recentContact, customIMBody);
            }
        } else {
            IMMessageCustomBody imBody = GlobalMessageObserver.getIMBody(recentContact.getContent());
            if (imBody != null) {
                if (IMUtils.isFilterChatIMMessage(imBody)) {
                    return null;
                }
                IMSessionEntity imSessionEntity = new IMSessionEntity(recentContact, imBody);
                IMUtils.wrapV1Message(imSessionEntity, recentContact, imBody);
                return imSessionEntity;
            }
        }
        return null;
    }

    /**
     * 获取alpha小 助手
     *
     * @param recentContact
     * @return
     */
    private IMMessageCustomBody getAlphaHelper(RecentContact recentContact) {
        return IMUtils.parseAlphaHelperMsg(recentContact);
    }


    /**
     * 消息删除
     *
     * @param recentContact
     */
    @Override
    protected void recentContactDeleted(@NonNull RecentContact recentContact) {
        if (imSessionAdapter == null) {
            return;
        }
        IMUtils.logRecentContact("------------>recentContactDeleted:", recentContact);
        for (int i = 0; i < imSessionAdapter.getData().size(); i++) {
            IMSessionEntity item = imSessionAdapter.getItem(i);
            if (item != null
                    && item.recentContact != null
                    && StringUtils.equalsIgnoreCase(recentContact.getContactId(), item.recentContact.getContactId(), false)) {
                imSessionAdapter.removeItem(i);
                break;
            }
        }
    }


    /**
     * 只要有Android ios设备 就退出
     *
     * @param onlineClients
     * @return
     */
    private OnlineClient getKikoutClient(List<OnlineClient> onlineClients) {
        if (onlineClients != null && !onlineClients.isEmpty()) {
            for (OnlineClient client : onlineClients) {
                if (client.getClientType() == ClientType.Android ||
                        client.getClientType() == ClientType.iOS) {
                    return client;
                }
            }
        }
        return null;
    }

    @Override
    protected void onlineClientEvent(List<OnlineClient> onlineClients) {
        if (isDetached()) {
            return;
        }
        if (onlineClients == null || onlineClients.size() == 0) {
            updateLoginStateView(false, "");
        } else {
            //被踢啦
            OnlineClient kikoutClient = getKikoutClient(onlineClients);
            if (kikoutClient != null) {
                NIMClient.getService(AuthService.class)
                        .kickOtherClient(kikoutClient)
                        .setCallback(new RequestCallback<Void>() {

                            @Override
                            public void onSuccess(Void param) {
                                dispatchEvent();
                            }

                            public void dispatchEvent() {
                                /**
                                 * 如果是从登陆页面而来 第一次踢掉对方 重新登陆
                                 */
                                if (getActivity() != null
                                        && getActivity().getIntent() != null
                                        && getActivity().getIntent().getBooleanExtra(MainActivity.KEY_FROM_LOGIN, false)) {
                                    AlphaUserInfo loginUserInfo = getLoginUserInfo();
                                    if (loginUserInfo != null) {
                                        getActivity().getIntent().putExtra(MainActivity.KEY_FROM_LOGIN, false);
                                        try {
                                            NIMClient.getService(AuthService.class)
                                                    .login(new LoginInfo(loginUserInfo.getThirdpartId(), loginUserInfo.getChatToken()))
                                                    .setCallback(new RequestCallback() {
                                                        @Override
                                                        public void onSuccess(Object o) {

                                                        }

                                                        @Override
                                                        public void onFailed(int i) {

                                                        }

                                                        @Override
                                                        public void onException(Throwable throwable) {

                                                        }
                                                    });
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        loginout();
                                    }
                                } else {
                                    loginout();
                                }
                            }

                            @Override
                            public void onFailed(int code) {
                                dispatchEvent();
                            }

                            @Override
                            public void onException(Throwable exception) {
                                dispatchEvent();
                            }
                        });
                return;
            }

            OnlineClient client = onlineClients.get(0);
            log("------------>onlineClientEvent:first:" + client.getOs() + "  gettype:" + client.getClientType() + "  loginTime:" + client.getLoginTime());
            switch (client.getClientType()) {
                case ClientType.Windows:
                    updateLoginStateView(true, getString(R.string.message_statu_hint_multiport_logging) + getString(R.string.message_statu_hint_computer_version));
                    break;
                case ClientType.Web:
                    updateLoginStateView(true, getString(R.string.message_statu_hint_multiport_logging) + getString(R.string.message_statu_hint_web_version));
                    break;
                case ClientType.iOS:
                case ClientType.Android:
                    NIMClient.getService(AuthService.class)
                            .kickOtherClient(client)
                            .setCallback(new RequestCallback<Void>() {

                                @Override
                                public void onSuccess(Void param) {

                                }

                                @Override
                                public void onFailed(int code) {

                                }

                                @Override
                                public void onException(Throwable exception) {

                                }
                            });
                    updateLoginStateView(true, getString(R.string.message_statu_hint_multiport_logging) + getString(R.string.message_statu_hint_mobile_version));
                    if (NIMClient.getStatus() == StatusCode.LOGINED) {
                        loginout();
                    }
                    break;
                default:
                    updateLoginStateView(false, "");
                    break;
            }
        }
    }


    /**
     * 更新登陆状态提示
     *
     * @param isShow
     * @param notice
     */
    private void updateLoginStateView(boolean isShow, String notice) {
        if (loginStatusTv == null) {
            return;
        }
        loginStatusTv.setVisibility(isShow ? View.VISIBLE : View.GONE);
        loginStatusTv.setText(notice);
    }

    /**
     * 退出登陆
     */
    private void loginout() {
        LoginSelectActivity.launch(getContext());
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    protected void onUserStatusChanged(StatusCode code) {
        if (isDetached()) {
            return;
        }
        log("------------>onUserStatusChanged:" + code);
        if (code.wontAutoLogin()) {
            bugSync("用户登陆状态:", "" + code + "  被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作");
            loginout();
        } else {
            if (code == StatusCode.NET_BROKEN) {
                updateLoginStateView(true, getString(R.string.error_please_check_network));
            } else if (code == StatusCode.UNLOGIN) {
                //bugSync("用户登陆状态:", "" + code);
                updateLoginStateView(true, getString(R.string.message_statu_hint_no_login));
            } else if (code == StatusCode.CONNECTING) {
                updateLoginStateView(true, getString(R.string.message_statu_hint_nim_status_connecting));
            } else if (code == StatusCode.LOGINING) {
                updateLoginStateView(true, getString(R.string.message_statu_hint_nim_status_logining));
            } else {
                updateLoginStateView(false, "");
            }
        }
    }

    @Override
    protected void teamUpdates(@NonNull List<Team> teams) {
    }

    @Override
    protected void msgStatusUpdate(IMMessage imMessage) {
        if (imMessage == null) {
            return;
        }
        IMUtils.logIMMessage("----------->msgMsgStatusUpdate:", imMessage);
    }


    @Override
    protected void initView() {
        super.initView();
        contentEmptyText.setText(R.string.empty_list_im_chat_msg);
        EventBus.getDefault().register(this);
        loginUserInfo = getLoginUserInfo();
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(null);

        imSessionAdapter = new IMSessionAdapter(localSetTops, localNoDisturbs);
        imSessionAdapter.setOnItemClickListener(this);

        View headerView = imSessionAdapter.addHeader(R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        recyclerView.setAdapter(imSessionAdapter);
        imSessionAdapter.registerAdapterDataObserver(dataChangeAdapterObserver);

        imSessionAdapter.setOnItemClickListener(this);
        imSessionAdapter.setOnItemLongClickListener(this);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetTopEvent setTopEvent) {
        if (setTopEvent == null) {
            return;
        }
        if (setTopEvent.isSetTop) {
            if (!localSetTops.contains(setTopEvent.id)) {
                localSetTops.add(setTopEvent.id);
            }
        } else {
            localSetTops.remove(setTopEvent.id);
        }
        Collections.sort(imSessionAdapter.getData(), imSessionEntityComparator);
        imSessionAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMemberEvent(MemberEvent memberEvent) {
        if (memberEvent == null) {
            return;
        }
        switch (memberEvent.notificationType) {
            case KickMember:
                if (StringUtils.containsIgnoreCase(memberEvent.targets, getLoginUserId())) {
                    removeSession(memberEvent.sessionId);
                }
                break;
        }
    }

    /**
     * 移除某个会话
     *
     * @param id
     */
    private void removeSession(String id) {
        List<IMSessionEntity> data = imSessionAdapter.getData();
        if (data.isEmpty()) {
            return;
        }
        for (int i = data.size() - 1; i >= 0; i--) {
            IMSessionEntity imSessionEntity = data.get(i);
            if (imSessionEntity != null && imSessionEntity.recentContact != null
                    && StringUtils.equalsIgnoreCase(id, imSessionEntity.recentContact.getContactId(), false)) {
                data.remove(i);
                //删除聊天会话
                NIMClient.getService(MsgService.class)
                        .clearChattingHistory(imSessionEntity.recentContact.getContactId(), imSessionEntity.recentContact.getSessionType());
            }
        }
        imSessionAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoDisturbingEvent noDisturbingEvent) {
        if (noDisturbingEvent == null) {
            return;
        }
        if (noDisturbingEvent.isNoDisturbing) {
            if (!localNoDisturbs.contains(noDisturbingEvent.id)) {
                localNoDisturbs.add(noDisturbingEvent.id);
            }
        } else {
            localNoDisturbs.remove(noDisturbingEvent.id);
        }
        imSessionAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GroupActionEvent groupActionEvent) {
        if (groupActionEvent == null) {
            return;
        }
        //删除已经退出的讨论组的会话
        try {
            for (IMSessionEntity sessionEntity : imSessionAdapter.getData()) {
                if (sessionEntity != null && sessionEntity.recentContact != null) {
                    if (StringUtils.equalsIgnoreCase(sessionEntity.recentContact.getContactId(), groupActionEvent.tid, false)) {
                        imSessionAdapter.removeItem(sessionEntity);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && imSessionAdapter != null) {
            getData(true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        getData(true);

        SyncDataService.startSyncContact(getActivity());
        SyncDataService.startSysnClient(getActivity());

        //主动登陆一次
        StatusCode status = NIMClient.getStatus();
        if (status == StatusCode.UNLOGIN
                || status == StatusCode.NET_BROKEN) {
            AlphaUserInfo loginUserInfo = getLoginUserInfo();
            if (loginUserInfo != null) {
                try {
                    NIMClient.getService(AuthService.class)
                            .login(new LoginInfo(loginUserInfo.getThirdpartId(), loginUserInfo.getChatToken()))
                            .setCallback(new RequestCallback() {
                                @Override
                                public void onSuccess(Object o) {

                                }

                                @Override
                                public void onFailed(int i) {

                                }

                                @Override
                                public void onException(Throwable throwable) {

                                }
                            });
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取消息通知列表
     *
     * @param isRefresh 是否刷新
     */
    @Override
    protected void getData(boolean isRefresh) {
        // 查询最近联系人列表数据
        NIMClient.getService(MsgService.class)
                .queryRecentContacts()
                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                        log("----------code:" + code + "  recents:" + recents + " exception:" + exception);
                        if (code == ResponseCode.RES_SUCCESS && recents != null) {
                            filterMessage(recents);//过滤消息
                            wrapRecentContact(recents);
                        }
                    }
                });
    }


    /**
     * 1.包装消息通知列表
     * 2.包含team的信息
     * 3.解析自定义消息的消息体
     *
     * @param recentContacts
     */
    private void wrapRecentContact(final List<RecentContact> recentContacts) {
        if (recentContacts == null) {
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<IMSessionEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<IMSessionEntity>> e) throws Exception {
                if (e.isDisposed()) {
                    return;
                }
                ContactDbService contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
                List<IMSessionEntity> imSessionEntities = new ArrayList<>();
                for (int i = 0; i < recentContacts.size(); i++) {
                    RecentContact recentContact = recentContacts.get(i);
                    if (recentContact == null) {
                        continue;
                    }
                    //解析自定义的消息体
                    IMMessageCustomBody customIMBody = null;
                    if (recentContact.getMsgType() == MsgTypeEnum.custom) {
                        customIMBody = getAlphaHelper(recentContact);
                        if (customIMBody == null) {
                            continue;
                        }
                        IMSessionEntity imSessionEntity = new IMSessionEntity(recentContact, customIMBody);
                        //装饰实体
                        imSessionEntities.add(imSessionEntity);
                    } else {
                        try {
                            customIMBody = JsonUtils.Gson2Bean(recentContact.getContent(), IMMessageCustomBody.class);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            bugSync("回话解析异常", StringUtils.throwable2string(ex) + "\n json:" + recentContact.getContent());
                            log("------------->解析异常:" + ex + "\n" + recentContact.getContactId() + " \n" + recentContact.getContent());
                        }
                        if (customIMBody == null) {
                            continue;
                        }
                        if (IMUtils.isFilterChatIMMessage(customIMBody)) {
                            continue;
                        }
                        IMSessionEntity imSessionEntity = new IMSessionEntity(recentContact, customIMBody);

                        IMUtils.wrapV1Message(imSessionEntity, recentContact, customIMBody);
                        //装饰实体
                        imSessionEntities.add(imSessionEntity);
                    }
                }
                contactDbService.releaseService();
                e.onNext(imSessionEntities);
                e.onComplete();
            }
        }).compose(this.<List<IMSessionEntity>>bindToLifecycle()).
                subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<IMSessionEntity>>() {
                    @Override
                    public void accept(List<IMSessionEntity> imSessionEntities) throws Exception {
                        Collections.sort(imSessionEntities, imSessionEntityComparator);
                        imSessionAdapter.bindData(true, imSessionEntities);
                        getDontDisturbsAndTopSession();
                    }
                });
    }


    /**
     * 过滤掉其它消息体
     *
     * @param recentContacts
     */
    private List<RecentContact> filterMessage(final List<RecentContact> recentContacts) {
        if (recentContacts != null) {
            for (int i = recentContacts.size() - 1; i >= 0; i--) {
                RecentContact item = recentContacts.get(i);
                if (item == null) {
                    continue;
                }
                IMUtils.logRecentContact("--------->messageFragment:i:" + i, item);
                //过滤掉其它消息类型
                if (item.getMsgType() != MsgTypeEnum.custom
                        && item.getMsgType() != MsgTypeEnum.text) {
                    recentContacts.remove(i);
                } else if (item.getMsgType() == MsgTypeEnum.text) {
                    if (TextUtils.isEmpty(item.getContent())) {//去除空的消息
                        recentContacts.remove(i);
                    } /*else if (GlobalMessageObserver.isFilterMsg(item.getTime())) {
                        recentContacts.remove(i);
                    }*/
                }
            }
        }
        return recentContacts;
    }


    private void getDontDisturbsAndTopSession() {
        Observable.zip(
                sendObservable(getChatApi().sessionQueryAllNoDisturbingIdsObservable()),
                sendObservable(getChatApi().sessionQueryAllsetTopIdsObservable()),
                new BiFunction<List<String>, List<String>, SparseArray<List<String>>>() {

                    @Override
                    public SparseArray<List<String>> apply(List<String> strings, List<String> strings2) throws Exception {
                        SparseArray<List<String>> sparseArray = new SparseArray<>();
                        sparseArray.put(0, strings);
                        sparseArray.put(1, strings2);
                        return sparseArray;
                    }
                }
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SparseArray<List<String>>>() {
                    @Override
                    public void accept(SparseArray<List<String>> listSparseArray) throws Exception {
                        if (listSparseArray == null) {
                            return;
                        }
                        localNoDisturbs.clear();
                        localNoDisturbs.addAll(listSparseArray.get(0, new ArrayList<String>()));

                        localSetTops.clear();
                        localSetTops.addAll(listSparseArray.get(1, new ArrayList<String>()));

                        Collections.sort(imSessionAdapter.getData(), imSessionEntityComparator);
                        imSessionAdapter.notifyDataSetChanged();

                        if (isFirstIntoPage) {
                            isFirstIntoPage = false;
                            //隐藏搜索栏
                            linearLayoutManager.scrollToPositionWithOffset(imSessionAdapter.getHeaderCount(), 0);
                        }
                    }
                });
    }


    @Override
    public void onTabDoubleClick(Fragment targetFragment, View v, Bundle bundle) {
        if (targetFragment != MessageListFragment.this) {
            return;
        }
        int nextUnReadItem = findNextUnReadItem(linearLayoutManager.findFirstVisibleItemPosition(), -1);
        if (nextUnReadItem != -1 && ViewCompat.canScrollVertically(recyclerView, 1)) {
            linearLayoutManager.scrollToPositionWithOffset(nextUnReadItem + imSessionAdapter.getHeaderCount(), 0);
        } else {
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }


    /**
     * 找到下一个未读消息位置
     *
     * @param start
     * @param defaultUnFind
     * @return
     */
    private int findNextUnReadItem(int start, int defaultUnFind) {
        if (start < 0) {
            return defaultUnFind;
        }
        List<IMSessionEntity> data = imSessionAdapter.getData();
        for (int i = start; i < data.size(); i++) {
            IMSessionEntity imSessionEntity = data.get(i);
            if (imSessionEntity != null
                    && imSessionEntity.recentContact != null
                    && imSessionEntity.recentContact.getUnreadCount() > 0) {
                return i;
            }
        }
        return defaultUnFind;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchPolymerizationActivity.launch(getContext(),
                        SearchPolymerizationActivity.SEARCH_PRIORITY_CHAT_HISTORTY);
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onItemClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        IMSessionEntity data = imSessionAdapter.getItem(i);
        if (data != null && data.customIMBody != null) {
            switch (data.customIMBody.ope) {
                case CHAT_TYPE_P2P:
                    if (data.isRobot()) {
                        AlphaSpecialHelperActivity.launch(getActivity(),
                                data.recentContact.getContactId(), totalUnReadCount);
                    } else {
                        GroupContactBean groupContactBean = imSessionAdapter.getContact(i);
                        if (groupContactBean != null) {
                            ChatActivity.launchP2P(getActivity(),
                                    data.recentContact.getContactId(),
                                    groupContactBean.name,
                                    0,
                                    totalUnReadCount);
                        }
                    }
                    break;
                case CHAT_TYPE_TEAM:
                    TextView tvSessionTitle = baseViewHolder.obtainView(R.id.tv_session_title);
                    if (data.recentContact != null) {
                        ChatActivity.launchTEAM(getActivity(),
                                data.recentContact.getContactId(),
                                String.valueOf(tvSessionTitle.getText()),
                                0,
                                totalUnReadCount);
                    }
                    break;
            }
        }

        log("--------->data:" + data);
        if (data != null) {
            LogUtils.logObject("-------->contact:", data.recentContact);
        }
    }

    @Override
    public boolean onItemLongClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        final IMSessionEntity item = imSessionAdapter.getItem(i);
        if (item != null) {
            new BottomActionDialog(getContext(),
                    null,
                    Arrays.asList("删除会话"),
                    new BottomActionDialog.OnActionItemClickListener() {
                        @Override
                        public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int p) {
                            dialog.dismiss();
                            imSessionAdapter.removeItem(item);
                            if (item.recentContact != null) {
                                NIMClient.getService(MsgService.class)
                                        .deleteRecentContact(item.recentContact);
                            }
                        }
                    }).show();
        }
        return true;
    }
}
