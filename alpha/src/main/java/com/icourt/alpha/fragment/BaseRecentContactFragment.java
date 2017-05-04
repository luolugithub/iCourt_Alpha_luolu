package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.icourt.alpha.base.BaseFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

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
            recentContactReceive(recentContacts);
        }
    };
    private Observer<RecentContact> deleteRecentContactObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact == null) return;
            recentContactDeleted(recentContact);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(recentContactMessageObserver, true);
        service.observeRecentContactDeleted(deleteRecentContactObserver, true);
    }

    /**
     * 收到会话 或者更新会话
     *
     * @param recentContacts
     */
    protected abstract void recentContactReceive(@NonNull List<RecentContact> recentContacts);

    /**
     * 会话删除
     *
     * @param recentContact
     */
    protected abstract void recentContactDeleted(@NonNull RecentContact recentContact);

    @Override
    public void onDestroy() {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(recentContactMessageObserver, false);
        service.observeRecentContactDeleted(deleteRecentContactObserver, false);
        super.onDestroy();
    }
}
