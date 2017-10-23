package com.icourt.alpha.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/14
 * version
 */

public class TimingStatisticEntity implements Serializable{

    public long allTimingSum;//所选区间计时总数

    public long todayTimingSum;//本日计时总数

    public List<Long> timingList;//计时统计列表
}
