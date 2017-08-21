package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.AlphaSpecialHelperActivity;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.activity.LoginSelectActivity;
import com.icourt.alpha.activity.MainActivity;
import com.icourt.alpha.activity.SearchPolymerizationActivity;
import com.icourt.alpha.adapter.IMSessionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
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
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
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
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.icourt.alpha.constants.Const.MSG_TYPE_ALPHA_HELPER;

/**
 * Description  会话列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class MessageListFragment extends BaseRecentContactFragment
        implements BaseRecyclerAdapter.OnItemClickListener, OnTabDoubleClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    private final List<Team> localTeams = new ArrayList<>();
    private final List<GroupContactBean> localGroupContactBeans = new ArrayList<>();
    private final List<String> localSetTops = new ArrayList<>();
    private final List<String> localNoDisturbs = new ArrayList<>();
    @BindView(R.id.login_status_tv)
    TextView loginStatusTv;
    @BindView(R.id.start_chat_tv)
    TextView startChatTv;
    @BindView(R.id.empty_layout_rl)
    RelativeLayout emptyLayoutRl;
    private int totalUnReadCount;
    private DataChangeAdapterObserver dataChangeAdapterObserver = new DataChangeAdapterObserver() {
        @Override
        protected void updateUI() {
            if (imSessionAdapter != null && emptyLayoutRl != null) {
                List<IMSessionEntity> data = imSessionAdapter.getData();
                emptyLayoutRl.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
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
    HeaderFooterAdapter<IMSessionAdapter> headerFooterAdapter;
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

    @Override
    public void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static MessageListFragment newInstance() {
        return new MessageListFragment();
    }


    /**
     * 收到消息
     *
     * @param recentContacts
     */
    @Override
    protected void recentContactReceive(@NonNull final List<RecentContact> recentContacts) {
        if (imSessionAdapter == null) return;
        filterMessage(recentContacts);
        List<IMSessionEntity> data = new ArrayList<IMSessionEntity>(imSessionAdapter.getData());
        for (RecentContact recentContact : recentContacts) {
            IMUtils.logRecentContact("------------>recentContactReceive:", recentContact);
            if (recentContact == null) continue;
            boolean isExist = false;
            for (IMSessionEntity imSessionEntity : data) {
                if (imSessionEntity != null
                        && imSessionEntity.recentContact != null) {
                    if (StringUtils.equalsIgnoreCase(recentContact.getContactId(), imSessionEntity.recentContact.getContactId(), false)) {
                        isExist = true;
                        //解析自定义的消息体
                        IMMessageCustomBody customIMBody = null;
                        if (recentContact.getMsgType() == MsgTypeEnum.custom) {
                            customIMBody = getAlphaHelper(recentContact);
                            if (customIMBody != null) {
                                imSessionEntity.customIMBody = customIMBody;
                                imSessionEntity.recentContact = recentContact;
                            }
                        } else {
                            try {
                                customIMBody = JsonUtils.Gson2Bean(recentContact.getContent(), IMMessageCustomBody.class);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (customIMBody != null) {
                                if (IMUtils.isFilterChatIMMessage(customIMBody)) continue;
                                imSessionEntity.customIMBody = customIMBody;
                                imSessionEntity.recentContact = recentContact;
                                IMUtils.wrapV1Message(imSessionEntity, recentContact, customIMBody);
                            }
                        }
                    }
                }
            }
            if (!isExist) {
                IMMessageCustomBody customIMBody = null;
                if (recentContact.getMsgType() == MsgTypeEnum.custom) {
                    customIMBody = getAlphaHelper(recentContact);
                    if (customIMBody != null) {
                        data.add(new IMSessionEntity(recentContact, customIMBody));
                    }
                } else {
                    String jsonBody = recentContact.getContent();
                    try {
                        customIMBody = JsonUtils.Gson2Bean(jsonBody, IMMessageCustomBody.class);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (customIMBody != null) {
                        if (IMUtils.isFilterChatIMMessage(customIMBody)) continue;
                        IMSessionEntity imSessionEntity = new IMSessionEntity(recentContact, customIMBody);
                        IMUtils.wrapV1Message(imSessionEntity, recentContact, customIMBody);
                        //装饰实体
                        data.add(imSessionEntity);
                    }
                }
            }
        }
        Collections.sort(data, imSessionEntityComparator);
        imSessionAdapter.bindData(true, data);
    }

    /**
     * 获取alpha小 助手
     *
     * @param recentContact
     * @return
     */
    private IMMessageCustomBody getAlphaHelper(RecentContact recentContact) {
        JSONObject alphaJSONObject = null;
        try {
            alphaJSONObject = JsonUtils.getJSONObject(recentContact.getAttachment().toJson(false));
            if (alphaJSONObject.getInt("showType") == MSG_TYPE_ALPHA_HELPER) {
                String contentStr = alphaJSONObject.getString("content");
                String type = alphaJSONObject.getString("type");
                IMMessageCustomBody imMessageCustomBody = new IMMessageCustomBody();
                if (StringUtils.containsIgnoreCase(type, "APPRO_")) {
                    imMessageCustomBody.content = "审批消息不支持,请到web端查看!";
                } else {
                    imMessageCustomBody.content = contentStr;
                }
                imMessageCustomBody.show_type = MSG_TYPE_ALPHA_HELPER;
                imMessageCustomBody.ope = CHAT_TYPE_P2P;
                imMessageCustomBody.to = recentContact.getContactId();
                return imMessageCustomBody;
            } else {
                NIMClient.getService(MsgService.class)
                        .deleteRecentContact(recentContact);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("AlphaHelper 解析异常", StringUtils.throwable2string(e)
                    + "\nalphaJSONObject:" + alphaJSONObject);
            bugSync("AlphaHelper 解析异常json:", "" + alphaJSONObject);
            LogUtils.d("---------->AlphaHelper 解析异常:" + e);
        }
        return null;
    }

    /**
     * 获取alpha小 助手
     *
     * @return
     */
    private IMMessageCustomBody getAlphaHelper(String s) {
        JSONObject alphaJSONObject = null;
        try {
            alphaJSONObject = JsonUtils.getJSONObject(s);
            if (alphaJSONObject.getInt("showType") == MSG_TYPE_ALPHA_HELPER) {
                String contentStr = alphaJSONObject.getString("content");
                String type = alphaJSONObject.getString("type");
                IMMessageCustomBody imMessageCustomBody = new IMMessageCustomBody();
                if (StringUtils.containsIgnoreCase(type, "APPRO_")) {
                    imMessageCustomBody.content = "审批消息不支持,请到web端查看!";
                } else {
                    imMessageCustomBody.content = contentStr;
                }
                imMessageCustomBody.show_type = MSG_TYPE_ALPHA_HELPER;
                imMessageCustomBody.ope = CHAT_TYPE_P2P;
                imMessageCustomBody.to = s;
                return imMessageCustomBody;
            } else {
                LogUtils.d("---------->AlphaHelper 删除:" + s);
         /*       NIMClient.getService(MsgService.class)
                        .deleteRecentContact(recentContact);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("---------->AlphaHelper 解析异常:" + e + " json:" + s);
        }
        return null;
    }

    private void test() {
        try {
            InputStream is = getContext().getAssets().open("test.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
            JSONArray jsonArray = new JSONArray(stringBuffer.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                IMMessageCustomBody alphaHelper = getAlphaHelper(jsonObject.toString());
                log("--------->解析i:" + i + " data:" + alphaHelper);
            }
        } catch (Exception e) {
            log("--------->解析异常xx");
            e.printStackTrace();
        }
    }

    /**
     * 消息删除
     *
     * @param recentContact
     */
    @Override
    protected void recentContactDeleted(@NonNull RecentContact recentContact) {
        if (imSessionAdapter == null) return;
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
        if (loginStatusTv == null) return;
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
        for (Team t : teams) {
            if (t == null) continue;
            IMUtils.logIMTeam("-------->teamUpdate", t);
            boolean isExist = false;
            for (int i = 0; i < localTeams.size(); i++) {
                Team team = localTeams.get(i);
                if (team != null && StringUtils.equalsIgnoreCase(team.getId(), t.getId(), false)) {
                    isExist = true;
                    localTeams.set(i, t);
                    imSessionAdapter.notifyDataSetChanged();
                }
            }
            if (!isExist) {
                localTeams.add(t);
            }
        }

    }


    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        loginUserInfo = getLoginUserInfo();
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<IMSessionAdapter>(imSessionAdapter = new IMSessionAdapter(localTeams,
                localGroupContactBeans, localSetTops, localNoDisturbs));
        imSessionAdapter.registerAdapterDataObserver(dataChangeAdapterObserver);
        imSessionAdapter.setOnItemClickListener(this);
        View view = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(view);
        View rl_comm_search = view.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        recyclerView.setAdapter(headerFooterAdapter);
        imSessionAdapter.setOnItemClickListener(this);
        imSessionAdapter.setOnItemLongClickListener(this);
        getData(true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetTopEvent setTopEvent) {
        if (setTopEvent == null) return;
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
        if (memberEvent == null) return;
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
        if (data.isEmpty()) return;
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
        if (noDisturbingEvent == null) return;
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
        if (groupActionEvent == null) return;
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
    public void onResume() {
        super.onResume();

        //test();

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
        getTeams(new RequestCallbackWrapper<List<Team>>() {
            @Override
            public void onResult(int code, List<Team> result, Throwable exception) {
                if (result != null) {
                    localTeams.clear();
                    localTeams.addAll(result);
                    imSessionAdapter.notifyDataSetChanged();
                }
            }
        });

        queryAllContactFromDbAsync(new Consumer<List<GroupContactBean>>() {
            @Override
            public void accept(List<GroupContactBean> groupContactBeanList) throws Exception {
                if (groupContactBeanList != null) {
                    localGroupContactBeans.clear();
                    localGroupContactBeans.addAll(groupContactBeanList);
                    imSessionAdapter.notifyDataSetChanged();
                }
            }
        });

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
        if (recentContacts == null) return;
        Observable.create(new ObservableOnSubscribe<List<IMSessionEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<IMSessionEntity>> e) throws Exception {
                if (e.isDisposed()) return;
                ContactDbService contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
                List<IMSessionEntity> imSessionEntities = new ArrayList<>();
                for (int i = 0; i < recentContacts.size(); i++) {
                    RecentContact recentContact = recentContacts.get(i);
                    if (recentContact == null) continue;
                    //解析自定义的消息体
                    IMMessageCustomBody customIMBody = null;
                    if (recentContact.getMsgType() == MsgTypeEnum.custom) {
                        customIMBody = getAlphaHelper(recentContact);
                        if (customIMBody == null) continue;
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
                        if (customIMBody == null) continue;
                        if (IMUtils.isFilterChatIMMessage(customIMBody)) continue;
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
                        //隐藏搜索栏
                        linearLayoutManager.scrollToPositionWithOffset(headerFooterAdapter.getHeaderCount(), 0);
                        getDontDisturbs();
                        getTopSession();
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
                if (item == null) continue;
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


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        IMSessionEntity data = imSessionAdapter.getData(adapter.getRealPos(position));
        if (data != null && data.customIMBody != null) {
            switch (data.customIMBody.ope) {
                case CHAT_TYPE_P2P:
                    if (data.isRobot()) {
                        AlphaSpecialHelperActivity.launch(getActivity(),
                                data.recentContact.getContactId(), totalUnReadCount);
                    } else {
                        if (!TextUtils.isEmpty(data.recentContact.getContactId())) {
                            GroupContactBean groupContactBean = new GroupContactBean();
                            groupContactBean.accid = data.recentContact.getContactId().toLowerCase();
                            int indexOf = localGroupContactBeans.indexOf(groupContactBean);
                            if (indexOf >= 0) {
                                groupContactBean = localGroupContactBeans.get(indexOf);
                                ChatActivity.launchP2P(getActivity(),
                                        data.recentContact.getContactId(),
                                        groupContactBean.name,
                                        0,
                                        totalUnReadCount);
                            }
                        }
                    }
                    break;
                case CHAT_TYPE_TEAM:
                    TextView tvSessionTitle = holder.obtainView(R.id.tv_session_title);
                    if (data.recentContact != null)
                        ChatActivity.launchTEAM(getActivity(),
                                data.recentContact.getContactId(),
                                String.valueOf(tvSessionTitle.getText()),
                                0,
                                totalUnReadCount);
                    break;
            }
        }

        log("--------->data:" + data);
        if (data != null) {
            LogUtils.logObject("-------->contact:", data.recentContact);
        }

    }


    /**
     * 获取消息免打扰
     */
    private void getDontDisturbs() {
        getChatApi().sessionQueryAllNoDisturbingIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            localNoDisturbs.clear();
                            localNoDisturbs.addAll(response.body().result);
                        }
                        imSessionAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPositionWithOffset(headerFooterAdapter.getHeaderCount(), 0);
                    }
                });
    }

    @Override
    public void onTabDoubleClick(Fragment targetFragment, View v, Bundle bundle) {
        if (targetFragment != MessageListFragment.this) return;
        int nextUnReadItem = findNextUnReadItem(linearLayoutManager.findFirstVisibleItemPosition(), -1);
        if (nextUnReadItem != -1 && ViewCompat.canScrollVertically(recyclerView, 1)) {
            linearLayoutManager.scrollToPositionWithOffset(nextUnReadItem + headerFooterAdapter.getHeaderCount(), 0);
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
        if (start < 0) return defaultUnFind;
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

    /**
     * 获取置顶的会话
     */
    private void getTopSession() {
        getChatApi().sessionQueryAllsetTopIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            localSetTops.clear();
                            localSetTops.addAll(response.body().result);
                        }
                        Collections.sort(imSessionAdapter.getData(), imSessionEntityComparator);
                        imSessionAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPositionWithOffset(headerFooterAdapter.getHeaderCount(), 0);
                    }
                });
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, final int position) {
        final IMSessionEntity item = imSessionAdapter.getItem(adapter.getRealPos(position));
        if (item != null) {
            new BottomActionDialog(getContext(),
                    null,
                    Arrays.asList("删除会话"),
                    new BottomActionDialog.OnActionItemClickListener() {
                        @Override
                        public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int p) {
                            dialog.dismiss();
                            imSessionAdapter.getData().remove(item);
                            imSessionAdapter.notifyDataSetChanged();
                            if (item.recentContact != null) {
                                NIMClient.getService(MsgService.class)
                                        .deleteRecentContact(item.recentContact);
                            }
                        }
                    }).show();
        }
        return true;
    }


    @OnClick({R.id.start_chat_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchPolymerizationActivity.launch(getContext(),
                        SearchPolymerizationActivity.SEARCH_PRIORITY_CHAT_HISTORTY);
                break;
            case R.id.start_chat_tv:
                if (getParentFragment() instanceof OnPageFragmentCallBack) {
                    onPageFragmentCallBack = (OnPageFragmentCallBack) getParentFragment();
                }
                if (onPageFragmentCallBack != null) {
                    onPageFragmentCallBack.onRequest2NextPage(MessageListFragment.this, 0, null);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
