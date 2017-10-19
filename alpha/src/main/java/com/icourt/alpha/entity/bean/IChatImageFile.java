package com.icourt.alpha.entity.bean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/20
 * version 2.1.0
 */
public interface IChatImageFile extends ISeaFile {

    /**
     * 获取消息id
     *
     * @return
     */
    long getChatMsgId();

    /**
     * 获取缩略图
     *
     * @return
     */
    String getChatThumbPic();

    /**
     * 获取中等图片 <=800
     *
     * @return
     */
    String getChatMiddlePic();
}
