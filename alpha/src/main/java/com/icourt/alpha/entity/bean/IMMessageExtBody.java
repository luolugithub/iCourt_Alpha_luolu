package com.icourt.alpha.entity.bean;

import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.convertor.IConvertModel;

import java.io.Serializable;
import java.util.List;

/**
 * Description 【模型 请勿轻易修改】
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/28
 * version 1.0.0
 */
public final class IMMessageExtBody implements Serializable, IConvertModel<SFileImageInfoEntity> {
    public IMMessageExtBody() {

    }

    //【模型 请勿轻易修改】
    //文件
    public String repo_id;
    public String path;
    public String name;
    public long size;
    public int width;
    public int height;

    //钉消息 pin
    public long id;
    public boolean pin;


    //@消息 AT
    public List<String> users;
    public boolean is_all;

    //系统辅助消息
    public String type;
    public String content;

    //链接图文消息 link
    public String title;
    public String thumb;
    public String desc;
    public String url;


    //包裹的消息
    public String from;         //发送方名称
    public String to;           //对方id uid或者tid
    @Const.CHAT_TYPE
    private int ope;            //0点对点，1群聊
    @Const.MSG_TYPE
    public int show_type;
    public long send_time;
    public String magic_id;
    public String platform;
    public IMMessageExtBody ext;

    private IMMessageExtBody(List<String> users, boolean is_all) {
        this.users = users;
        this.is_all = is_all;
    }

    public IMMessageExtBody(long id, boolean pin) {
        this.id = id;
        this.pin = pin;
    }

    public IMMessageExtBody(String title, String thumb, String desc, String url) {
        this.title = title;
        this.thumb = thumb;
        this.desc = desc;
        this.url = url;
    }

    /**
     * 构建 扩展的at消息
     *
     * @param users
     * @param is_all
     * @return
     */
    public static IMMessageExtBody createAtExtBody(List<String> users, boolean is_all) {
        return new IMMessageExtBody(users, is_all);
    }

    /**
     * 构建 扩展的图片消息
     *
     * @param filePath
     * @return
     */
    public static IMMessageExtBody createPicExtBody(String filePath) {
        IMMessageExtBody imMessageExtBody = new IMMessageExtBody();
        imMessageExtBody.thumb = filePath;
        return imMessageExtBody;
    }

    /**
     * 构建 扩展的钉消息
     *
     * @param isPing
     * @param pingMsgId
     * @return
     */
    public static IMMessageExtBody createDingExtBody(boolean isPing, long pingMsgId) {
        return new IMMessageExtBody(pingMsgId, isPing);
    }


    /**
     * 构建 扩展的链接消息
     *
     * @param title
     * @param thumb
     * @param desc
     * @param url
     * @return
     */
    public static IMMessageExtBody createLinkExtBody(String title, String thumb, String desc, String url) {
        return new IMMessageExtBody(title, thumb, desc, url);
    }


    @Override
    public String toString() {
        return "IMMessageExtBody{" +
                "repo_id='" + repo_id + '\'' +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", id='" + id + '\'' +
                ", pin=" + pin +
                ", users=" + users +
                ", is_all=" + is_all +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", thumb='" + thumb + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", ope=" + ope +
                ", show_type=" + show_type +
                ", send_time=" + send_time +
                ", magic_id='" + magic_id + '\'' +
                ", platform='" + platform + '\'' +
                ", ext=" + ext +
                '}';
    }

    @Override
    public SFileImageInfoEntity convert2Model() {
        return new SFileImageInfoEntity(size, path, name, repo_id, thumb, width, height);
    }
}
