package com.icourt.alpha.entity.bean;

import com.icourt.alpha.utils.DateUtils;

import java.util.Calendar;

/**
 * * Description  编辑计时开始/结束时间，弹出窗所要用到的显示日期的参数
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：17/9/26
 * version 2.1.1
 */

public class TimingDateEntity {

    public long timeMillios;

    @Override
    public String toString() {
        if (DateUtils.isToday(timeMillios)) {
            return "今天";
        }
        return DateUtils.getMMddWeek(timeMillios);
    }
}
