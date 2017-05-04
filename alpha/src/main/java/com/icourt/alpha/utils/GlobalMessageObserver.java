package com.icourt.alpha.utils;

import com.google.gson.JsonParseException;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.bugtags.library.Bugtags.log;

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
                if (imMessage.getAttachment() != null)//机器人 alpha小助手
                {

                } else {
                    IMMessageCustomBody imBody = getIMBody(imMessage);
                    //从本地数据库删除
                    if (imBody == null || IMUtils.isFilterChatIMMessage(imBody)) {
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

    /**
     * 解析自定义的消息体
     *
     * @param message
     * @return
     */
    protected final IMMessageCustomBody getIMBody(IMMessage message) {
        IMMessageCustomBody imBodyEntity = null;
        try {
            log("--------------->GlobalMessageObserver customBody:" + message.getContent());
            imBodyEntity = JsonUtils.Gson2Bean(message.getContent(), IMMessageCustomBody.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return imBodyEntity;
    }
}
