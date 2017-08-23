package com.icourt.alpha.fragment;

import android.support.annotation.NonNull;

import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public abstract class BaseRecentContactFragment extends BaseFragment {


    private Observer<List<RecentContact>> recentContactMessageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            if (recentContacts == null || recentContacts.isEmpty()) return;

            List<RecentContact> recentContacts1 = new ArrayList<>();
            //过滤其它消息
            for (RecentContact recentContact : recentContacts) {
                if (recentContact != null && recentContact.getMsgType() == MsgTypeEnum.text ||
                        recentContact.getMsgType() == MsgTypeEnum.custom) {
                    recentContacts1.add(recentContact);
                }
                //查询一条
                NIMClient.getService(MsgService.class)
                        .queryMessageListByType(
                                MsgTypeEnum.text,
                                MessageBuilder.createEmptyMessage(
                                        recentContact.getContactId(),
                                        recentContact.getSessionType(),
                                        recentContact.getTime()),
                                1)
                        .setCallback(new RequestCallback<List<IMMessage>>() {
                            @Override
                            public void onSuccess(List<IMMessage> imMessages) {
                                if (imMessages != null) {

                                }
                            }

                            @Override
                            public void onFailed(int i) {

                            }

                            @Override
                            public void onException(Throwable throwable) {

                            }
                        });

            }


            recentContactReceive(recentContacts1);
        }
    };
    private Observer<RecentContact> deleteRecentContactObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact == null) return;
            recentContactDeleted(recentContact);
        }
    };

    private Observer<List<Team>> teamUpdateObserver = new Observer<List<Team>>() {
        @Override
        public void onEvent(List<Team> teams) {
            if (teams == null) return;
            if (teams.isEmpty()) return;
            teamUpdates(teams);
        }
    };
    Observer<List<OnlineClient>> clientsObserver = new Observer<List<OnlineClient>>() {
        @Override
        public void onEvent(List<OnlineClient> onlineClients) {
            onlineClientEvent(onlineClients);
        }
    };

    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            onUserStatusChanged(code);
        }
    };


    @Override
    public void onResume() {
        // 进入最近联系人列表界面，建议放在onResume中
        NIMClient.getService(MsgService.class)
                .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        super.onResume();
    }

    @Override
    public void onPause() {
        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
        NIMClient.getService(MsgService.class)
                .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        registerNimObserver(false);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        registerNimObserver(true);
    }


    private void registerNimObserver(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(recentContactMessageObserver, register);
        service.observeRecentContactDeleted(deleteRecentContactObserver, register);
        NIMClient.getService(TeamServiceObserver.class)
                .observeTeamUpdate(teamUpdateObserver, register);
        NIMClient.getService(AuthServiceObserver.class)
                .observeOtherClients(clientsObserver, register);
        NIMClient.getService(AuthServiceObserver.class)
                .observeOnlineStatus(userStatusObserver, register);
    }

    /**
     * 获取群组
     *
     * @param requestCallback
     */
    protected final void getTeams(RequestCallback<List<Team>> requestCallback) {
        NIMClient.getService(TeamService.class)
                .queryTeamList()
                .setCallback(requestCallback);
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
                        threadContactDbService = new ContactDbService(getLoginUserId());
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
        }).compose(this.<List<GroupContactBean>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }


    /**
     * 收到会话 或者更新会话
     *
     * @param recentContacts
     */
    protected abstract void recentContactReceive(@NonNull List<RecentContact> recentContacts);


    /**
     * 收到会话 或者更新会话
     *
     * @param recentContacts
     */
    protected abstract void recentContactReceive(@NonNull RecentContact recentContacts);

    /**
     * 会话删除
     *
     * @param recentContact
     */
    protected abstract void recentContactDeleted(@NonNull RecentContact recentContact);


    /**
     * 登陆状态
     *
     * @param onlineClients
     */
    protected abstract void onlineClientEvent(List<OnlineClient> onlineClients);


    /**
     * 用户登陆状态发生变化
     *
     * @param code
     */
    protected abstract void onUserStatusChanged(StatusCode code);

    /**
     * team更新
     *
     * @param teams
     */
    protected abstract void teamUpdates(@NonNull List<Team> teams);


}
