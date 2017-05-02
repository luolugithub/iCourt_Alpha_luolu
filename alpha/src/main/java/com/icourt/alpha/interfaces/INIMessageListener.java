package com.icourt.alpha.interfaces;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;

import java.util.List;

public interface INIMessageListener {
    /**
     * 收到消息
     *
     * @param list
     */
    void onMessageReceived(List<IMMessage> list);

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