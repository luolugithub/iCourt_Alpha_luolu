package com.icourt.alpha.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.LoginInfoUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Description  同步服务
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date ceateTime：17/3/29
 * version
 */

public class SyncDataService extends IntentService {
    private static final String ACTION_SYNC_CONTACT = " action_sync_contact";

    public SyncDataService() {
        super(SyncDataService.class.getName());
    }

    /**
     * 联系人同步服务
     *
     * @param context
     */
    public static void startSysnContact(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, SyncDataService.class);
        intent.setAction(ACTION_SYNC_CONTACT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(), ACTION_SYNC_CONTACT)) {
                syncContacts();
            }
        }
    }

    private void syncContacts() {
        try {
            Response<ResEntity<List<GroupContactBean>>> execute = RetrofitServiceFactory
                    .getChatApiService()
                    .usersQuery().execute();
            ContactDbService contactDbService = new ContactDbService(LoginInfoUtils.getLoginUserId());
            contactDbService.deleteAll();
            contactDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<ContactDbModel>>(execute.body().result));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("----------->SyncDataService syncContacts 失败:" + e);
        }
    }
}
