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
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.adapter.IMSessionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.IMSessionEntity;
import com.icourt.alpha.entity.event.GroupActionEvent;
import com.icourt.alpha.entity.event.NoDisturbingEvent;
import com.icourt.alpha.entity.event.SetTopEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.comparators.IMSessionEntityComparator;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

/**
 * Description  会话列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class MessageListFragment extends BaseRecentContactFragment
        implements BaseRecyclerAdapter.OnItemClickListener, OnTabDoubleClickListener {

    private final List<Team> localTeams = new ArrayList<>();
    private final List<GroupContactBean> localGroupContactBeans = new ArrayList<>();

    Unbinder unbinder;
    IMSessionAdapter imSessionAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    HeaderFooterAdapter<IMSessionAdapter> headerFooterAdapter;
    AlphaUserInfo loginUserInfo;
    OnFragmentCallBackListener parentFragmentCallBackListener;
    int unReadNewsNum;//未读消息数量
    IMSessionEntityComparator imSessionEntityComparator = new IMSessionEntityComparator();


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
        Observable.create(new ObservableOnSubscribe<List<IMSessionEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<IMSessionEntity>> e) throws Exception {
                if (e.isDisposed()) return;
                filterMessage(recentContacts);
                List<IMSessionEntity> data = new ArrayList<IMSessionEntity>(imSessionAdapter.getData());
                String loginUserId = getLoginUserId();
                unReadNewsNum = 0;
                for (RecentContact recentContact : recentContacts) {
                    IMUtils.logRecentContact("------------>recentContactReceive:", recentContact);
                    if (recentContact == null) continue;
                    unReadNewsNum += recentContact.getUnreadCount();
                    boolean isExist = false;
                    for (IMSessionEntity imSessionEntity : data) {
                        if (imSessionEntity != null
                                && imSessionEntity.recentContact != null) {
                            if (StringUtils.equalsIgnoreCase(recentContact.getContactId(), imSessionEntity.recentContact.getContactId(), false)) {
                                isExist = true;
                                //解析自定义的消息体
                                IMMessageCustomBody customIMBody = null;
                                if (!TextUtils.isEmpty(recentContact.getContent())) {
                                    try {
                                        customIMBody = JsonUtils.Gson2Bean(recentContact.getContent(), IMMessageCustomBody.class);
                                    } catch (JsonParseException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                if (customIMBody != null) {
                                    imSessionEntity.customIMBody = customIMBody;
                                }
                            }
                        }
                    }
                    if (!isExist) {
                        //解析自定义的消息体
                        IMMessageCustomBody customIMBody = null;
                        if (!TextUtils.isEmpty(recentContact.getContent())) {
                            try {
                                customIMBody = JsonUtils.Gson2Bean(recentContact.getContent(), IMMessageCustomBody.class);
                            } catch (JsonParseException ex) {
                                ex.printStackTrace();
                            }
                        }
                        //装饰实体
                        data.add(new IMSessionEntity(recentContact, customIMBody));
                    }
                }
                Collections.sort(data, imSessionEntityComparator);
                e.onNext(data);
                e.onComplete();
            }
        }).compose(this.<List<IMSessionEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<IMSessionEntity>>() {
                    @Override
                    public void accept(List<IMSessionEntity> imSessionEntities) throws Exception {
                        imSessionAdapter.bindData(true, imSessionEntities);
                        callParentUpdateUnReadNum(unReadNewsNum);
                    }
                });
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

    @Override
    protected void teamUpdates(@NonNull List<Team> teams) {
        for (Team t : teams) {
            int indexOf = localTeams.indexOf(t);
            if (indexOf >= 0) {
                localTeams.set(indexOf, t);
                imSessionAdapter.notifyDataSetChanged();
            } else {
                localTeams.add(t);
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            parentFragmentCallBackListener = (OnFragmentCallBackListener) context;
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
    protected void initView() {
        EventBus.getDefault().register(this);
        loginUserInfo = getLoginUserInfo();
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<IMSessionAdapter>(imSessionAdapter = new IMSessionAdapter(localTeams, localGroupContactBeans));
        imSessionAdapter.setOnItemClickListener(this);
        headerFooterAdapter.addHeader(HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView));
        recyclerView.setAdapter(headerFooterAdapter);
        imSessionAdapter.setOnItemClickListener(this);
        getData(true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetTopEvent setTopEvent) {
        if (setTopEvent == null) return;
        //更新消息置顶状态
        List<IMSessionEntity> data = imSessionAdapter.getData();
        for (IMSessionEntity sessionEntity : data) {
            if (sessionEntity == null) continue;
            RecentContact recentContact = sessionEntity.recentContact;
            if (StringUtils.equalsIgnoreCase(sessionEntity.recentContact.getContactId(), setTopEvent.id, false)) {
                if (setTopEvent.isSetTop && sessionEntity.recentContact.getTag()
                        != ActionConstants.MESSAGE_GROUP_TOP) {
                    recentContact.setTag(ActionConstants.MESSAGE_GROUP_TOP);
                    imSessionAdapter.updateItem(sessionEntity);
                    break;
                } else if (!setTopEvent.isSetTop
                        && sessionEntity.recentContact.getTag() == ActionConstants.MESSAGE_GROUP_TOP) {
                    recentContact.setTag(ActionConstants.MESSAGE_GROUP_NO_TOP);
                    imSessionAdapter.updateItem(sessionEntity);
                    break;
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoDisturbingEvent noDisturbingEvent) {
        if (noDisturbingEvent == null) return;
        //更新群聊消息免打扰状态
        List<IMSessionEntity> data = imSessionAdapter.getData();
       /* for (IMSessionEntity sessionEntity : data) {
            if (sessionEntity != null
                    && sessionEntity.team != null) {
                if (StringUtils.equalsIgnoreCase(noDisturbingEvent.id, sessionEntity.team.getId(), false)) {
                    sessionEntity.isNotDisturb = noDisturbingEvent.isNoDisturbing;
                    imSessionAdapter.updateItem(sessionEntity);
                    break;
                }
            }
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GroupActionEvent groupActionEvent) {
        if (groupActionEvent == null) return;
        //删除已经退出的讨论组的会话
        /*for (IMSessionEntity sessionEntity : imSessionAdapter.getData()) {
            if (sessionEntity != null && sessionEntity.team != null) {
                if (StringUtils.equalsIgnoreCase(sessionEntity.team.getId(), groupActionEvent.tid, false)) {
                    imSessionAdapter.removeItem(sessionEntity);
                    break;
                }
            }
        }*/
    }


    /**
     * 更新 UI 置顶会话优先显示
     *
     * @param setTopIds
     */
    private void updateUIwithTopEntities(List<String> setTopIds) {
        if (setTopIds == null) return;
        for (IMSessionEntity item : imSessionAdapter.getData()) {
            if (item != null
                    && item.recentContact != null) {

                item.recentContact.setTag(ActionConstants.MESSAGE_GROUP_NO_TOP);
                NIMClient.getService(MsgService.class).updateRecent(item.recentContact);
                for (String id : setTopIds) {
                    if (TextUtils.equals(id, item.recentContact.getContactId().toUpperCase())) {

                        //设置为置顶并更新本地
                        item.recentContact.setTag(ActionConstants.MESSAGE_GROUP_TOP);
                        NIMClient.getService(MsgService.class).updateRecent(item.recentContact);
                    }
                }
            }
        }

        Collections.sort(imSessionAdapter.getData(), imSessionEntityComparator);
        imSessionAdapter.notifyDataSetChanged();
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

                unReadNewsNum = 0;
                for (int i = 0; i < recentContacts.size(); i++) {
                    RecentContact recentContact = recentContacts.get(i);
                    if (recentContact == null) continue;
                    unReadNewsNum += recentContact.getUnreadCount();

                    //解析自定义的消息体
                    IMMessageCustomBody customIMBody = null;
                    if (!TextUtils.isEmpty(recentContact.getContent())) {
                        try {
                            customIMBody = JsonUtils.Gson2Bean(recentContact.getContent(), IMMessageCustomBody.class);
                        } catch (JsonParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                    IMSessionEntity imSessionEntity = new IMSessionEntity(recentContact, customIMBody);
                    //装饰实体
                    imSessionEntities.add(imSessionEntity);
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

                        callParentUpdateUnReadNum(unReadNewsNum);

                        getDontDisturbs();
                        getTopSession();
                    }
                });
    }

    /**
     * unReadNum
     * 请求父容器更新未读消息数量
     */
    private void callParentUpdateUnReadNum(int unReadNum) {
        Bundle bundle = new Bundle();
        bundle.putInt("unReadNum", unReadNum);
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            ((OnFragmentCallBackListener) getParentFragment()).OnFragmentCallBack(MessageListFragment.this, bundle);
        } else if (parentFragmentCallBackListener != null) {
            parentFragmentCallBackListener.OnFragmentCallBack(MessageListFragment.this, bundle);
        }
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
                //过滤掉其它消息类型
                if (item.getMsgType() != MsgTypeEnum.custom
                        && item.getMsgType() != MsgTypeEnum.text) {
                    recentContacts.remove(i);
                } else if (item.getMsgType() == MsgTypeEnum.text) {
                    if (TextUtils.isEmpty(item.getContent())) {//去除空的消息
                        recentContacts.remove(i);
                    }
                }
            }
        }
        return recentContacts;
    }


    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        IMSessionEntity data = imSessionAdapter.getData(adapter.getRealPos(position));
        if (data != null && data.customIMBody != null) {
            switch (data.customIMBody.ope) {
                case CHAT_TYPE_P2P:
                    ChatActivity.launchP2P(getActivity(),
                            data.recentContact.getContactId(),
                            data.customIMBody.name);
                    break;
                case CHAT_TYPE_TEAM:
                    TextView tvSessionTitle = holder.obtainView(R.id.tv_session_title);
                    if (data.recentContact != null)
                        ChatActivity.launchTEAM(getActivity(),
                                data.recentContact.getContactId(),
                                String.valueOf(tvSessionTitle.getText()));
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
        getApi().sessionQueryAllNoDisturbingIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        updateLocalDontDisturbs(response.body().result);
                    }
                });
    }


    /**
     * 更新 消息免打扰【群聊】 UI 数据库
     *
     * @param dontDisturbIds 网络同步的免打扰对象
     */
    private void updateLocalDontDisturbs(final List<String> dontDisturbIds) {
        if (dontDisturbIds == null) return;
        Observable.create(new ObservableOnSubscribe<List<IMSessionEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<IMSessionEntity>> e) throws Exception {
                if (e.isDisposed()) return;
                //先将以前免打扰的 关闭
                List<IMSessionEntity> imSessionEntities = imSessionAdapter.getData();
                for (int i = 0; i < imSessionEntities.size(); i++) {
                    IMSessionEntity imSessionEntity = imSessionEntities.get(i);
                    if (imSessionEntity != null
                            && imSessionEntity.recentContact != null
                            && imSessionEntity.recentContact.getSessionType() == SessionTypeEnum.Team) {
                        NIMClient.getService(TeamService.class)
                                .muteTeam(imSessionEntity.recentContact.getContactId(), false);

                        //将新同步的免打扰开启
                        for (int j = 0; j < dontDisturbIds.size(); j++) {
                            if (StringUtils.equalsIgnoreCase(dontDisturbIds.get(j), imSessionEntity.recentContact.getContactId(), false)) {
                                NIMClient.getService(TeamService.class)
                                        .muteTeam(imSessionEntity.recentContact.getContactId(), true);
                            }
                        }
                        NIMClient.getService(MsgService.class)
                                .updateRecent(imSessionEntity.recentContact);
                    }
                }

                e.onNext(imSessionEntities);
                e.onComplete();
            }
        }).compose(this.<java.util.List<com.icourt.alpha.entity.bean.IMSessionEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<IMSessionEntity>>() {
                    @Override
                    public void accept(List<IMSessionEntity> imSessionEntities) throws Exception {
                        imSessionAdapter.notifyDataSetChanged();
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
        getApi().sessionQueryAllsetTopIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        updateUIwithTopEntities(response.body().result);
                    }
                });
    }

}
