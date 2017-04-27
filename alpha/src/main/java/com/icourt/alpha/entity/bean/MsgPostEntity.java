package com.icourt.alpha.entity.bean;

import java.util.UUID;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/27
 * version 1.0.0
 */
public class MsgPostEntity {
    private String name;//发送人名字,
    private String to;//接收人id，这个需要根据ope判断
    private int ope;//0点对点，1群聊
    private String magic_id = UUID.randomUUID().toString();
    private String platform = "ANDROID";
    private String content;//消息内容

    private MsgPostEntity() {
    }

    private MsgPostEntity(String name, String to, String content) {
        this.name = name;
        this.to = to;
        this.content = content;
    }

    /**
     * 构建文本消息体
     *
     * @param name    发送方的名字
     * @param to      接收人id，这个需要根据ope判断
     * @param content 文本内容
     * @return
     */
    public static MsgPostEntity createTextMsg(String name, String to, String content) {
        return new MsgPostEntity(name, to, content);
    }
}
