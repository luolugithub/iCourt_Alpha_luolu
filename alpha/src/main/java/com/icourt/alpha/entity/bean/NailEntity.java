package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description  钉的实体
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/12
 * version 1.0.0
 */
public class NailEntity extends FileInfoEntity {
    public int show_type;// 0: 文本消息; 1:文件 ; 2: 钉消息;  3:@消息  4:通知消息
    public int isPining; // 0:取消钉 ; 1:加钉
    public String name;
    public String pic;
    public String id;
    public String content;
    public List<AtEntity> atBeanList;//被at的人集合
}
