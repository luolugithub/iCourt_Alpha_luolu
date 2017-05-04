package com.icourt.alpha.interfaces;

import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;

import java.util.List;

public interface INIMessageListener {
    /**
     * 自定义消息
     *
     * @param customBody
     */
    void onMessageReceived(IMMessageCustomBody customBody);

    /**
     * 消息回执
     *
     * @param list
     */
    void onMessageReadAckReceived(List<MessageReceipt> list);

    /**
     * 消息状态改变
     *
     * @param message
     */
    void onMessageChanged(IMMessage message);

    /**
     * 消息 撤回
     *
     * @param message
     */
    void onMessageRevoke(IMMessage message);
}