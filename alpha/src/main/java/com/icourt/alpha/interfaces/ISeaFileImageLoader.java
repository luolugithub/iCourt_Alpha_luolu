package com.icourt.alpha.interfaces;

import android.widget.ImageView;

/**
 * Description sfile 图片加载
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public interface ISeaFileImageLoader {

    /**
     * 加载图片
     *
     * @param fileName 文件名
     * @param view
     * @param type     0 缩略图
     */
    void loadSFileImage(String fileName, ImageView view, int type, int size);
}
