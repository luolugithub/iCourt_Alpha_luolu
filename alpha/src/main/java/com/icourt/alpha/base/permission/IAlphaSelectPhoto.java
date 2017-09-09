package com.icourt.alpha.base.permission;

import cn.finalteam.galleryfinal.GalleryFinal;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/9
 * version 2.1.0
 */
public interface IAlphaSelectPhoto {
    int REQ_CODE_CAMERA = 61000;//拍照
    int REQ_CODE_GALLERY_SINGLE = 61001;//单选
    int REQ_CODE_GALLERY_MUTI = 61003;//多选

    /**
     * 检查权限 并多选图片
     *
     * @param onHanlderResultCallback
     */
    void checkAndSelectMutiPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback);

    /**
     * 多选图片
     *
     * @param onHanlderResultCallback
     */
    void selectMutiPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback);

    /**
     * 检查权限 单选
     *
     * @param onHanlderResultCallback
     */
    void checkAndSelectSingleFromPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback);

    /**
     * 检查单权限 单选
     *
     * @param onHanlderResultCallback
     */
    void selectSingleFromPhotos(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback);

    /**
     * 拍照
     *
     * @param onHanlderResultCallback
     */
    void selectFromCamera(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback);

    /**
     * 检查权限 并拍照
     *
     * @param onHanlderResultCallback
     */
    void checkAndSelectFromCamera(GalleryFinal.OnHanlderResultCallback onHanlderResultCallback);
}
