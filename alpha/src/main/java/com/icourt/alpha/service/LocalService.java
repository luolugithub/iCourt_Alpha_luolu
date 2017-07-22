package com.icourt.alpha.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.process.aidl.IProcessService;
import com.icourt.alpha.utils.LogUtils;

/**
 * Author: youxuan
 * Date: 2016/6/1 17:36
 * Description: 远程服务
 */
public class LocalService extends Service {
    String TAG = LocalService.class.getSimpleName();

    public static void start(Context context) {
        if (context == null) return;
        try {
            context.startService(new Intent(context, LocalService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalBinder mLocalBinder;

    private LocalServiceConnection mLocalServiceConn;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBinder = new LocalBinder();

        if (mLocalServiceConn == null) {
            mLocalServiceConn = new LocalServiceConnection();
        }

        LogUtils.d("------------->" + TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LogUtils.d("------------->" + TAG + " onStartCommand");

        //  绑定远程服务
//        bindService(new Intent(this, RemoteService.class), mLocalServiceConn, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    /**
     * 通过AIDL实现进程间通信
     */
    class LocalBinder extends IProcessService.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return "LocalService";
        }
    }

    /**
     * 连接远程服务
     */
    class LocalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 与远程服务通信
                IProcessService process = IProcessService.Stub.asInterface(service);
                LogUtils.d("------------->连接" + process.getServiceName() + "服务成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // RemoteException连接过程出现的异常，才会回调,unbind不会回调
            // 监测，远程服务已经死掉，则重启远程服务
            LogUtils.d("------------->远程服务挂掉了,远程服务被杀死");

            // 启动远程服务
            startService(new Intent(LocalService.this, RemoteService.class));

            // 绑定远程服务
            bindService(new Intent(LocalService.this, RemoteService.class), mLocalServiceConn, Context.BIND_IMPORTANT);
        }
    }
}
