package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.icourt.alpha.view.recyclerviewDivider.ISuspensionAction;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
@Deprecated
public class GroupMemberEntity implements ISuspensionInterface, ISuspensionAction, Serializable {
    public String suspensionTag;
    public String memberId;
    public String groupId;
    public String name;
    public String pic;
    public boolean isShowSuspension=true;

    @Override
    public boolean isShowSuspension() {
        return isShowSuspension;
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
}
