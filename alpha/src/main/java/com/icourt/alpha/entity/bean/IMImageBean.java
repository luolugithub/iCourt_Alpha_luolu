package com.icourt.alpha.entity.bean;

import java.io.Serializable;

import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         消息图片
 * @data 创建时间:17/1/10
 */

public class IMImageBean extends PhotoInfo implements Serializable {

    private long size;

    private String thumbPath;
    private String name;

    public IMImageBean() {
        super();
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
