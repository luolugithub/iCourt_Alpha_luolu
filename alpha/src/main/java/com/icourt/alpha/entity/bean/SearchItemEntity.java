package com.icourt.alpha.entity.bean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchItemEntity implements ISearchItemEntity {

    public CharSequence title;
    public CharSequence content;
    public String icon;
    public CharSequence keyWord;

    public SearchItemEntity(CharSequence title, CharSequence content, String icon, CharSequence keyWord) {
        this.title = title;
        this.content = content;
        this.icon = icon;
        this.keyWord = keyWord;
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
