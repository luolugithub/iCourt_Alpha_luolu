package com.icourt.alpha.entity.bean;

/**
 * Description  任务模型
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class TaskEntity {

    public String id;
    public String name;
    public MatterEntity matter;
    public int commentCount;

    public static class MatterEntity {
        public String id;
        public String name;
        public int matterType;
    }
}
