package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.view.recyclerviewDivider.ISuspensionAction;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupEntity implements ISuspensionInterface, ISuspensionAction, Serializable {
    public String suspensionTag;
    public String id;
    public String tid;
    public String name;
    public String intro;


    //附属信息
    public String admin_id;
    public String create_id;
    public List<String> members;

    @Override
    public boolean isShowSuspension() {
        return true;
    }

    @NonNull
    @Override
    public String getSuspensionTag() {
        return TextUtils.isEmpty(suspensionTag) ? "#" : suspensionTag;
    }

    @Nullable
    @Override
    public String getTargetField() {
        return name;
    }

    @Override
    public void setSuspensionTag(@NonNull String suspensionTag) {
        this.suspensionTag = suspensionTag;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final GroupEntity other = (GroupEntity) o;
        return TextUtils.equals(this.id, other.id);
    }
}
