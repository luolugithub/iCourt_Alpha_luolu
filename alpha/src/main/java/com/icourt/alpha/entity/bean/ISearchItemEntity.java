package com.icourt.alpha.entity.bean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public interface ISearchItemEntity {

    String getId();

    int type();

    int classfyType();

    long getRecordTime();

    CharSequence getTitle();

    CharSequence getContent();

    String getIcon();

    CharSequence getKeyWord();
}
