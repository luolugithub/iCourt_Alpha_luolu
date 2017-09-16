package com.icourt.alpha.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.http.RetrofitServiceFactory;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.BugUtils;
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
    private static final String ACTION_SYNC_CONTACT = " action_sync_contact";//同步通讯录
    private static final String ACTION_SYNC_CLIENT = " action_sync_client";//同步客户

    public SyncDataService() {
        super(SyncDataService.class.getName());
    }

    /**
     * 联系人同步服务
     *
     * @param context
     */
    public static void startSyncContact(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, SyncDataService.class);
        intent.setAction(ACTION_SYNC_CONTACT);
        context.startService(intent);
    }

    /**
     * 客户同步服务
     *
     * @param context
     */
    public static void startSysnClient(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, SyncDataService.class);
        intent.setAction(ACTION_SYNC_CLIENT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (TextUtils.equals(intent.getAction(), ACTION_SYNC_CONTACT)) {
                if (LoginInfoUtils.isUserLogin()) {
                    syncContacts();
                }
            } else if (TextUtils.equals(intent.getAction(), ACTION_SYNC_CLIENT)) {
                syncClients();
            }
        }
    }

    /**
     * 同步通讯录
     */
    private void syncContacts() {
        try {
            Response<ResEntity<List<GroupContactBean>>> execute = RetrofitServiceFactory
                    .getChatApiService()
                    .usersQuery().execute();
            if (execute != null && execute.body() != null
                    && execute.body().result != null) {
                if (execute.body().result.isEmpty()) return;
                ContactDbService contactDbService = new ContactDbService(LoginInfoUtils.getLoginUserId());
                contactDbService.deleteAll();
                contactDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<ContactDbModel>>(execute.body().result));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            BugUtils.bugSync("同步联系人异常", e);
            LogUtils.d("----------->SyncDataService syncContacts 失败:" + e);
        }
    }

    /**
     * 同步客户
     */
    private void syncClients() {
        try {
            Response<ResEntity<List<CustomerEntity>>> execute = RetrofitServiceFactory
                    .getAlphaApiService()
                    .getCustomers(100000).execute();
            if (execute != null && execute.body() != null && execute.body().result != null) {
                CustomerDbService customerDbService = new CustomerDbService(LoginInfoUtils.getLoginUserId());
                customerDbService.deleteAll();
                customerDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<CustomerDbModel>>(execute.body().result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            BugUtils.bugSync("同步客户异常", e);
            LogUtils.d("----------->SyncDataService syncClients 失败:" + e);
        }
    }
}
