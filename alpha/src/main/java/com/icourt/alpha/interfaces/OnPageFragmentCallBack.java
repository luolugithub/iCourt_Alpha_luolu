package com.icourt.alpha.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public interface OnPageFragmentCallBack {

    /**
     * 请求下一页
     *
     * @param fragment
     * @param type
     * @param bundle
     */
    void onRequest2NextPage(Fragment fragment, int type, Bundle bundle);

    /**
     * 请求上一页
     *
     * @param fragment
     * @param type
     * @param bundle
     */
    void onRequest2LastPage(Fragment fragment, int type, Bundle bundle);

    /**
     * 请求跳转到某一页
     *
     * @param fragment
     * @param type
     * @param pagePos
     * @param bundle
     */
    void onRequest2Page(Fragment fragment, int type, int pagePos, Bundle bundle);


    /**
     * 是否可以跳转到下一个fragment
     *
     * @return
     */
    boolean canGoNextFragment(Fragment fragment);

    /**
     * 是否可以跳转到上一个fragment
     *
     * @return
     */
    boolean canGoLastFragment(Fragment fragment);
}
