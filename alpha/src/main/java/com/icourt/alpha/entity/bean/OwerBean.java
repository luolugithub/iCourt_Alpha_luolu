package com.icourt.alpha.entity.bean;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.io.Serializable;

/**
 * @author 创建人:lu.zhao
 *         <p>
 *         负责人
 * @data 创建时间:16/11/29
 */

public class OwerBean extends BaseIndexPinyinBean implements Serializable{

    private boolean isTop;//是否是最上面的 不需要被转化成拼音的
    private String id;
    private String name;
    private String photoUrl;

    public OwerBean() {

    }

    public OwerBean(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public OwerBean setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public boolean isTop() {
        return isTop;
    }

    public OwerBean setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public String getTarget() {
        return name;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }
}
