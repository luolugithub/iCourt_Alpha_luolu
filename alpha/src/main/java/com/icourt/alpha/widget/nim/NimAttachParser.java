package com.icourt.alpha.widget.nim;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/25
 * version 1.0.0
 */
public class NimAttachParser implements MsgAttachmentParser {
    @Override
    public MsgAttachment parse(String attach) {
        return new NimMsgAttachment(attach);
    }
}
