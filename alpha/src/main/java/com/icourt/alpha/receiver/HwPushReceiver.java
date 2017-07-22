package com.icourt.alpha.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huawei.android.pushagent.PushReceiver;
import com.huawei.android.pushagent.api.PushEventReceiver;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.ToastUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/22
 * version 2.0.0
 */
public class HwPushReceiver extends PushEventReceiver {
    // 接收Push消息
    public static final int RECEIVE_PUSH_MSG = 0x100;
    // 接收Push Token消息
    public static final int RECEIVE_TOKEN_MSG = 0x101;
    // 接收Push 自定义通知消息内容
    public static final int RECEIVE_NOTIFY_CLICK_MSG = 0x102;
    // 接收Push LBS 标签上报响应
    public static final int RECEIVE_TAG_LBS_MSG = 0x103;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastUtils.showToast("消息:" + msg.obj);
        }
    };

    /*
     * 显示Push消息
     */
    public void showPushMessage(int type, String msg) {
        if (handler != null) {
            Message message = handler.obtainMessage();
            message.what = type;
            message.obj = msg;
            handler.sendMessageDelayed(message, 1L);
        }
    }

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        LogUtils.d("------------->push1:"+content);
        //showPushMessage(RECEIVE_TOKEN_MSG, content);
    }


    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            LogUtils.d("------------->push2:"+content);
            showPushMessage(RECEIVE_PUSH_MSG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(Context context, PushReceiver.Event event, Bundle extras) {
        if (PushReceiver.Event.NOTIFICATION_OPENED.equals(event) || PushReceiver.Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(PushReceiver.BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "收到通知附加消息： " + extras.getString(PushReceiver.BOUND_KEY.pushMsgKey);
            LogUtils.d("------------->push3:"+content);
            showPushMessage(RECEIVE_NOTIFY_CLICK_MSG, content);
        } else if (PushReceiver.Event.PLUGINRSP.equals(event)) {
            final int TYPE_LBS = 1;
            final int TYPE_TAG = 2;
            int reportType = extras.getInt(PushReceiver.BOUND_KEY.PLUGINREPORTTYPE, -1);
            boolean isSuccess = extras.getBoolean(PushReceiver.BOUND_KEY.PLUGINREPORTRESULT, false);
            String message = "";
            if (TYPE_LBS == reportType) {
                message = "LBS report result :";
            } else if (TYPE_TAG == reportType) {
                message = "TAG report result :";
            }
            LogUtils.d(message + isSuccess);
            showPushMessage(RECEIVE_TAG_LBS_MSG, message + isSuccess);
        }
        super.onEvent(context, event, extras);
    }
}
