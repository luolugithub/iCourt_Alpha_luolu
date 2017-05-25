package com.icourt.alpha.widget.nim;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/25
 * version 1.0.0
 */
public class NimMsgAttachment implements MsgAttachment {

    private String attach;

    public NimMsgAttachment(String attach) {
        this.attach = attach;
    }

    @Override
    public String toJson(boolean send) {
        return attach;
    }

    @Override
    public String toString() {
        return "NimMsgAttachment{" +
                "attach='" + attach + '\'' +
                '}';
    }
}
