package com.icourt.alpha.widget.nim;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.BaseCustomerMsg;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.event.MemberEvent;
import com.icourt.alpha.entity.event.ServerTimingEvent;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.bugtags.library.Bugtags.log;
import static com.netease.nimlib.sdk.msg.constant.MsgTypeEnum.custom;
import static com.netease.nimlib.sdk.msg.constant.MsgTypeEnum.notification;
import static com.netease.nimlib.sdk.msg.constant.MsgTypeEnum.text;

/**
 * Description    全局消息接收观察者;负责删除一些不需要展示的类型消息 解析自定义消息 并通知其它页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/4
 * version 1.0.0
 */
public class GlobalMessageObserver implements Observer<List<IMMessage>> {

    private static long v2time;

    static {
        v2time = Long.parseLong(BuildConfig.APK_RELEASE_TIME);
    }


    @Override
    public void onEvent(List<IMMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (IMMessage imMessage : messages) {
            if (imMessage != null) {
                IMUtils.logIMMessage("----------->globalMessageObserver message:", imMessage);
                if (isFilterMsg(imMessage.getTime())) {
                    continue;
                }
                if (imMessage.getMsgType() == notification) {//推送删除
                    //剔除成员
                    if (imMessage.getAttachment() instanceof MemberChangeAttachment) {
                        MemberChangeAttachment memberChangeAttachment = (MemberChangeAttachment) imMessage.getAttachment();
                        EventBus.getDefault().post(new MemberEvent(imMessage.getSessionId(), memberChangeAttachment.getType(), memberChangeAttachment.getTargets()));
                    }
                    NIMClient.getService(MsgService.class)
                            .deleteChattingHistory(imMessage);
                } else if (imMessage.getMsgType() == text) {
                    //自定义消息体一定是text
                    IMMessageCustomBody imBody = getIMBody(imMessage);
                    //从本地数据库删除
                    if (imBody == null || IMUtils.isFilterChatIMMessage(imBody)) {
                        IMUtils.logIMMessage("----------->globalMessageObserver del message:", imMessage);
                        NIMClient.getService(MsgService.class)
                                .deleteChattingHistory(imMessage);
                    } else {
                        //发送给其它页面
                        EventBus.getDefault().post(imBody);
                    }
                } else if (imMessage.getMsgType() == custom) {//自定义消息 200 alpha小助手； 300 计时同步
                    if (imMessage.getAttachment() != null) {
                        try {
                            String json = imMessage.getAttachment().toJson(false);
                            BaseCustomerMsg baseCustomerMsg = JsonUtils.Gson2Bean(json, BaseCustomerMsg.class);
                            if (baseCustomerMsg != null) {
                                switch (baseCustomerMsg.showType) {
                                    case Const.MSG_TYPE_ALPHA_HELPER:
                                        IMUtils.logIMMessage("----------->globalMessageObserver alpha:", imMessage);
                                        IMMessageCustomBody imBody = new IMMessageCustomBody();
                                        imBody.imMessage = imMessage;
                                        //发送给其它页面
                                        EventBus.getDefault().post(imBody);
                                        break;
                                    case Const.MSG_TYPE_ALPHA_SYNC://同步
                                        ServerTimingEvent serverTimingEvent = JsonUtils.Gson2Bean(json, ServerTimingEvent.class);
                                        if (serverTimingEvent != null) {
                                            EventBus.getDefault().post(serverTimingEvent);
                                        }
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 是否过滤消息 过滤2.0之前的消息
     *
     * @param time
     * @return
     */
    public static boolean isFilterMsg(long time) {
        return time < v2time;
    }

    /**
     * 是否是草稿消息
     *
     * @param imMessage
     * @return
     */
    public static boolean isDraftMsg(IMMessage imMessage) {
        return imMessage != null && imMessage.getStatus() == MsgStatusEnum.draft;
    }

    /**
     * 解析自定义消息
     *
     * @param content
     * @return
     */
    public static final IMMessageCustomBody getIMBody(String content) {
        IMMessageCustomBody imBodyEntity = null;
        try {
            imBodyEntity = JsonUtils.Gson2Bean(content, IMMessageCustomBody.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imBodyEntity;
    }

    /**
     * 解析自定义的消息体
     *
     * @param message
     * @return
     */
    public static final IMMessageCustomBody getIMBody(IMMessage message) {
        IMMessageCustomBody imBodyEntity = null;
        try {
            log("----------->globalMessageObserver customBody:" + message.getContent());
            imBodyEntity = JsonUtils.Gson2Bean(message.getContent(), IMMessageCustomBody.class);
            if (imBodyEntity != null && message != null) {
                imBodyEntity.imMessage = message;
                switch (message.getStatus()) {
                    case draft:
                        imBodyEntity.msg_statu = Const.MSG_STATU_DRAFT;
                        break;
                    case sending:
                        imBodyEntity.msg_statu = Const.MSG_STATU_SENDING;
                        break;
                    case success:
                        imBodyEntity.msg_statu = Const.MSG_STATU_SUCCESS;
                        break;
                    case fail:
                        imBodyEntity.msg_statu = Const.MSG_STATU_FAIL;
                        break;
                    case read:
                        imBodyEntity.msg_statu = Const.MSG_STATU_READ;
                        break;
                    case unread:
                        imBodyEntity.msg_statu = Const.MSG_STATU_UNREAD;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("--------->getIMBody JsonParseException:" + e);
        }
        return imBodyEntity;
    }


    /**
     * 解析自定义的消息体
     * 将本地[发送中的状态]直接置为[发送失败]
     *
     * @param message
     * @return
     */
    public static final IMMessageCustomBody getLocalIMBody(IMMessage message) {
        IMMessageCustomBody imBodyEntity = getIMBody(message);
        if (imBodyEntity != null && message != null) {
            imBodyEntity.imMessage = message;
            switch (message.getStatus()) {
                case sending:
                    imBodyEntity.msg_statu = Const.MSG_STATU_FAIL;
                    break;
            }
        }
        return imBodyEntity;
    }
}
