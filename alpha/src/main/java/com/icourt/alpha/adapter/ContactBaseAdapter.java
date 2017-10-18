package com.icourt.alpha.adapter;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.asange.recyclerviewadapter.SelectableEntity;
import com.icourt.alpha.adapter.baseadapter.BaseSelectableAdapter;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.StringUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

/**
 * Description  加载本地的联系人列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/4
 * version 2.1.0
 */
public abstract class ContactBaseAdapter<T extends SelectableEntity> extends BaseSelectableAdapter<T> {
    ContactDbService contactDbService;
    final List<GroupContactBean> localGroupContactBeans = new ArrayList<>();
    OrderedRealmCollectionChangeListener contactChangeListener = new OrderedRealmCollectionChangeListener<RealmResults<ContactDbModel>>() {
        @Override
        public void onChange(RealmResults<ContactDbModel> contactDbModels, OrderedCollectionChangeSet orderedCollectionChangeSet) {
            if (contactDbModels != null) {
                localGroupContactBeans.clear();
                localGroupContactBeans.addAll(ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels)));
                notifyDataSetChanged();
            }
        }
    };

    final List<Team> localTeams = new ArrayList<>();

    /**
     * 通过userid 查询 uid: eg. 7B113D3F234411E7843370106FAECE2E
     *
     * @param userId
     * @return
     */
    @CheckResult
    @Nullable
    protected final GroupContactBean getContactByUserId(String userId) {
        for (int i = 0; i < localGroupContactBeans.size(); i++) {
            GroupContactBean contactBean = localGroupContactBeans.get(i);
            if (contactBean == null) {
                continue;
            }
            if (TextUtils.equals(userId, contactBean.userId)) {
                return contactBean;
            }
        }
        return getContactByAccid(StringUtils.toLowerCase(userId));
    }

    @Override
    public boolean bindData(boolean isRefresh, @NonNull List<T> datas) {
        if (isRefresh) {
            initContacts();
            initTeams();
        }
        return super.bindData(isRefresh, datas);
    }

    /**
     * 通过accid 查询 accid: eg. 7b113d3f234411e7843370106faece2e
     *
     * @param accId
     * @return
     */
    @CheckResult
    @Nullable
    protected final GroupContactBean getContactByAccid(String accId) {
        GroupContactBean contactBean = new GroupContactBean();
        contactBean.accid = accId;
        int indexOf = localGroupContactBeans.indexOf(contactBean);
        if (indexOf >= 0) {
            return localGroupContactBeans.get(indexOf);
        }
        return IMUtils.convert2GroupContact(getNimUser(accId));
    }

    /**
     * @param accid
     * @return
     */
    @CheckResult
    @Nullable
    protected final NimUserInfo getNimUser(String accid) {
        try {
            return NIMClient.getService(UserService.class)
                    .getUserInfo(accid);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取teams
     */
    private void initTeams() {
        NIMClient.getService(TeamService.class)
                .queryTeamList()
                .setCallback(new RequestCallbackWrapper<List<Team>>() {
                    @Override
                    public void onResult(int code, List<Team> result, Throwable exception) {
                        if (result != null) {
                            localTeams.clear();
                            localTeams.addAll(result);
                            notifyDataSetChanged();
                        }
                    }
                });
    }

    private void initContacts() {
        contactDbService = new ContactDbService(LoginInfoUtils.getLoginUserId());
        RealmResults<ContactDbModel> contactDbModels = contactDbService.queryAllAsync();
        if (contactDbModels != null) {
            contactDbModels.removeChangeListener(contactChangeListener);
            contactDbModels.addChangeListener(contactChangeListener);
        }
    }

    @Nullable
    @CheckResult
    protected final Team getTeamById(String id) {
        if (localTeams == null) {
            return null;
        }
        for (Team team : localTeams) {
            if (StringUtils.equalsIgnoreCase(id, team.getId(), false)) {
                return team;
            }
        }
        NIMClient.getService(TeamService.class).queryTeam(id).setCallback(new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team team) {
                if (team != null) {
                    localTeams.add(team);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        return null;
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
