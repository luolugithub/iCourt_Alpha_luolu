package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-09-19 09:33
 */

public interface OnFragmentCallBackListener {

    void onFragmentCallBack(Fragment fragment, int type, Bundle params);
}
