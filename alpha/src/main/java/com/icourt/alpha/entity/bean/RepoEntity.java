package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.widget.comparators.ILongFieldEntity;
import com.icourt.json.BooleanTypeAdapter;
import com.icourt.alpha.widget.json.SeaFileTimeJsonAdapter;

import java.io.Serializable;

/**
 * Description  文档资料库
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class RepoEntity implements Serializable, ILongFieldEntity {

    @Override
    public Long getCompareLongField() {
        return last_modified;
    }

    @JsonAdapter(BooleanTypeAdapter.class)
    public boolean encrypted;
    @SerializedName(value = "id",alternate = {"repo_id"})
    public String repo_id;
    @SerializedName(value = "repo_name", alternate = {"name"})
    public String repo_name;

    @JsonAdapter(SeaFileTimeJsonAdapter.class)
    public long last_modified;
    public long size;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final RepoEntity other = (RepoEntity) o;
        return TextUtils.equals(this.repo_id, other.repo_id);
    }

}
