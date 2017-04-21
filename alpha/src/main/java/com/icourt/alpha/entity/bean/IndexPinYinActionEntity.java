package com.icourt.alpha.entity.bean;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public abstract class IndexPinYinActionEntity extends BaseIndexPinyinBean {

    public boolean isNotNeedToPinyin;//不需要拼音排序


    @Override
    public final boolean isNeedToPinyin() {
        return !isNotNeedToPinyin;
    }

    @Override
    public final boolean isShowSuspension() {
        return !isNotNeedToPinyin;
    }
}
