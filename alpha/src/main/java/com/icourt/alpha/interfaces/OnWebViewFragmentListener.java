package com.icourt.alpha.interfaces;

import android.os.Bundle;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/22
 * version 1.0.0
 */
public interface OnWebViewFragmentListener {

    /**
     * WebView加载开始
     *
     * @param fragment
     * @param type
     * @param bundle
     */
    void onWebViewStarted(IWebViewPage fragment, int type, Bundle bundle);

    /**
     * 加载完成
     *
     * @param fragment
     * @param type
     * @param bundle
     */
    void onWebViewFinished(IWebViewPage fragment, int type, Bundle bundle);

    /**
     * 回退
     *
     * @param fragment
     * @param type
     * @param bundle
     */
    void onWebViewGoBack(IWebViewPage fragment, int type, Bundle bundle);

    /**
     * 前进
     *
     * @param fragment
     * @param type
     * @param bundle
     */
    void onWebViewGoForward(IWebViewPage fragment, int type, Bundle bundle);
}
