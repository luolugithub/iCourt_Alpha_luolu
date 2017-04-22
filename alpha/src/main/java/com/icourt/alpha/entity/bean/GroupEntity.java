package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupEntity extends IndexPinYinActionEntity {

    public String id;
    public String name;

    @Override
    public String getTarget() {
        if (TextUtils.isEmpty(name)) {
            return "#";
        }
        return name;
    }
}
