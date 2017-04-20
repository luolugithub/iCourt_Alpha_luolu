package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public interface INotifyFragment {

    void notifyFragmentUpdate(Fragment targetFrgament,int type,Bundle bundle);
}
