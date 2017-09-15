package com.icourt.alpha.entity.bean;

import java.io.Serializable;

/**
 * Description 不同状态下的任务的数量（未完成、已完成、已取消）
 * Company Beijing icourt
 * author zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/8
 * version 2.0.0
 */

public class TaskCountEntity implements Serializable {

    public String deletedCount;//已删除的任务的数量
    public String doingCount;//未完成的任务的数量
    public String doneCount;//已完成的任务的数量
}
