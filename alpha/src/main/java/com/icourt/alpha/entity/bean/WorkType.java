package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class WorkType implements Serializable {

    public String pkId;
    public String name;
    public int matterType;
    public String officeId;
    public int state;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final WorkType other = (WorkType) o;
        return TextUtils.equals(this.pkId, other.pkId);
    }
}
