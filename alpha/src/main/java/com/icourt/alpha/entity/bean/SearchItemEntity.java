package com.icourt.alpha.entity.bean;

import com.icourt.alpha.constants.Const;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchItemEntity implements ISearchItemEntity {

    public String id;
    public long id2;
    public int type;
    @Const.SEARCH_TYPE
    public int classfyType;
    public CharSequence title;
    public CharSequence content;
    public String icon;
    public CharSequence keyWord;
    public long recordTime;

    public SearchItemEntity(CharSequence title, CharSequence content, String icon, CharSequence keyWord) {
        this.title = title;
        this.content = content;
        this.icon = icon;
        this.keyWord = keyWord;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getId2() {
        return id2;
    }

    @Override
    public int type() {
        return type;
    }

    @Override
    public int classfyType() {
        return classfyType;
    }

    @Override
    public long getRecordTime() {
        return recordTime;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public CharSequence getContent() {
        return content;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public CharSequence getKeyWord() {
        return keyWord;
    }
}
