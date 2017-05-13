package com.icourt.alpha.utils;

import com.google.gson.JsonParseException;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.bugtags.library.Bugtags.log;
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
    @Override
    public void onEvent(List<IMMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (IMMessage imMessage : messages) {
            if (imMessage != null) {
                IMUtils.logIMMessage("----------->globalMessageObserver message:", imMessage);
                if (imMessage.getMsgType() == notification) {//推送删除
                    NIMClient.getService(MsgService.class)
                            .deleteChattingHistory(imMessage);
                } else if (imMessage.getMsgType() == text
                        && imMessage.getAttachment() != null) {//机器人 alpha小助手
                    IMUtils.logIMMessage("----------->globalMessageObserver message: alpha小助手", imMessage);
                } else {
                    if (imMessage.getMsgType() == text) {
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
                    }
                }
            }
        }
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
        } catch (JsonParseException e) {
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
