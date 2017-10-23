package com.icourt.alpha.interfaces;

import com.icourt.alpha.constants.TimingConfig;

/**
 * Description 计时列表界面，计时标题被隐藏的监听
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/13
 * version
 */

public interface OnTimingChangeListener {

    void onHeaderHide(boolean isHide);

    void onTimeChanged(@TimingConfig.TIMINGQUERYTYPE int type, long selectedTimeMillis);

    void onTimeSumChanged(@TimingConfig.TIMINGQUERYTYPE int type, long selectedTimeSum, long todayTimeSum);

}
