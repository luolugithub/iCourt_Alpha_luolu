package com.icourt.alpha.entity.bean;

import com.icourt.alpha.constants.Const;

import java.util.List;
import java.util.UUID;

/**
 * Description 接口文档 https://www.showdoc.cc/1620156?page_id=14893614
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/28
 * version 1.0.0
 */
public class IMMessageCustomBody {
    public static final String PLATFORM_ANDROID = "ANDROID";
    public String id;
    public String name;
    public String from;
    public String to;

    @Const.CHAT_TYPE
    private int ope;            //0点对点，1群聊

    @Const.MSG_TYPE
    public int show_type;
    public long send_time;
    public String magic_id;
    public String platform;
    public IMMessageExtBody ext;
    public String content;

    public IMMessageCustomBody() {
    }

    private IMMessageCustomBody(@Const.CHAT_TYPE int chatType, @Const.MSG_TYPE int msgType, String name, String to, String content, String magic_id, String platform) {
        this.ope = chatType;
        this.show_type = msgType;
        this.name = name;
        this.to = to;
        this.content = content;
        this.magic_id = magic_id;
        this.platform = platform;
    }


    /**
     * 构建文本消息体
     *
     * @param name    发送方的名字
     * @param to      接收人id，这个需要根据ope判断
     * @param content 文本内容
     * @return
     */
    public static IMMessageCustomBody createTextMsg(@Const.CHAT_TYPE int chatType, String name, String to, String content) {
        return new IMMessageCustomBody(chatType,
                Const.MSG_TYPE_TXT,
                name,
                to,
                content,
                UUID.randomUUID().toString(),
                PLATFORM_ANDROID);
    }

    /**
     * 构建文件消息体
     *
     * @param name    发送方的名字
     * @param to      接收人id，这个需要根据ope判断
     * @param content 文本内容
     * @return
     */
    public static IMMessageCustomBody createFileMsg(@Const.CHAT_TYPE int chatType, String name, String to, String content) {
        return new IMMessageCustomBody(chatType,
                Const.MSG_TYPE_FILE,
                name,
                to,
                content,
                UUID.randomUUID().toString(),
                PLATFORM_ANDROID);
    }

    /**
     * 构建AT消息
     *
     * @param chatType
     * @param name
     * @param to
     * @param content
     * @param isAtall
     * @param usersAccids
     * @return
     */
    public static IMMessageCustomBody createAtMsg(@Const.CHAT_TYPE int chatType, String name, String to, String content, boolean isAtall, List<String> usersAccids) {
        IMMessageCustomBody imMessageCustomBody = new IMMessageCustomBody(chatType,
                Const.MSG_TYPE_AT,
                name,
                to,
                content,
                UUID.randomUUID().toString(),
                PLATFORM_ANDROID);
        imMessageCustomBody.ext = IMMessageExtBody.createAtExtBody(usersAccids, isAtall);
        return imMessageCustomBody;
    }

    /**
     * 构建 钉的消息
     *
     * @param chatType
     * @param name
     * @param to
     * @param isDing
     * @param dingMsgId
     * @return
     */
    public static IMMessageCustomBody createDingMsg(@Const.CHAT_TYPE int chatType, String name, String to, boolean isDing, String dingMsgId) {
        IMMessageCustomBody imMessageCustomBody = new IMMessageCustomBody(chatType,
                Const.MSG_TYPE_DING,
                name,
                to,
                null,
                UUID.randomUUID().toString(),
                PLATFORM_ANDROID);
        imMessageCustomBody.ext = IMMessageExtBody.createDingExtBody(isDing, dingMsgId);
        return imMessageCustomBody;
    }
}
