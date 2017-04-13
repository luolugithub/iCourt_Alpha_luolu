package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/12
 * version 1.0.0
 */
public class IMBodyEntity extends FileInfoEntity {

    public int show_type;// 0: 文本消息; 1:文件 ; 2: 钉消息;  3:@消息  4:通知消息
    public NailEntity pinMsg;//被钉的消息
    public String groupId;//组id
    public String name;// 发消息人name
    public String id;//消息id
    public String pic;//发消息人头像
    public String tid;//云信组/对方 id
    public String content;//文本内容
    public String route;//存放连接的url(通知需要)
    public int atAll; // atAll为“1”时为at所有人，atAll为“0”时按照atList来@人员
    public List<AtEntity> atBeanList;//被at的人集合
    public long time;
    public String action_tid;
    public String action_groupId;

}
