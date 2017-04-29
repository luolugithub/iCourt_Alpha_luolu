package com.icourt.alpha.entity.bean;

import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class GroupDetailEntity extends GroupEntity {
    public int isJoin;
    public int count;
    public boolean is_private;
    public String admin_id;
    public List<String> members;
}
