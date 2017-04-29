package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description  文档地址 https://www.showdoc.cc/1620156?page_id=14893614
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/12
 * version 1.0.0
 */
@Deprecated
public class IMBodyEntity extends FileInfoEntity {

    public String id;//消息id
    public int show_type;// 消息类型 https://www.showdoc.cc/1620156?page_id=14893614
    public NailEntity pinMsg;//被钉的消息
    public String groupId;//组id
    public String name;// 发消息人name

    public String pic;//发消息人头像
    public String tid;//云信组/对方 id
    public String content;//文本内容
    public String route;//存放连接的url(通知需要)
    public int atAll; // atAll为“1”时为at所有人，atAll为“0”时按照atList来@人员
    public List<AtEntity> atBeanList;//被at的人集合
    public long time;
    public String action_tid;
    public String action_groupId;
    public String path;
    public String file;

    @Override
    public String toString() {
        return "IMBodyEntity{" +
                "show_type=" + show_type +
                ", pinMsg=" + pinMsg +
                ", groupId='" + groupId + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", pic='" + pic + '\'' +
                ", tid='" + tid + '\'' +
                ", content='" + content + '\'' +
                ", route='" + route + '\'' +
                ", atAll=" + atAll +
                ", atBeanList=" + atBeanList +
                ", time=" + time +
                ", action_tid='" + action_tid + '\'' +
                ", action_groupId='" + action_groupId + '\'' +
                '}';
    }
}
