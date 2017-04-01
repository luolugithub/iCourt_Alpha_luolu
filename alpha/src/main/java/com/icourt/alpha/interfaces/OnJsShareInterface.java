package com.icourt.alpha.interfaces;

import android.webkit.JavascriptInterface;

/**
 * Description  js分享接口定义
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：17/2/4
 * version
 */

public interface OnJsShareInterface {

    /**
     * 立即展示分享面板
     *
     * @param title     分享的标题
     * @param content   分享的内容
     * @param icon      分享的图标
     * @param targetUrl 分享的目标地址
     * @param action    分享的动作 扩展
     * @param type      分享的类型 扩展
     */
    @JavascriptInterface
    void showShare(String title, String content, String icon, String targetUrl, String action, int type);


    /**
     * 是否展示分享按钮
     *
     * @param isShow    是否展示分享按钮
     * @param title     分享的标题
     * @param content   分享的内容
     * @param icon      分享的图标
     * @param targetUrl 分享的目标地址
     * @param action    分享的动作 扩展
     * @param type      分享的类型 扩展
     */
    @JavascriptInterface
    void setShareData(boolean isShow, String title, String content, String icon, String targetUrl, String action, int type);
}
