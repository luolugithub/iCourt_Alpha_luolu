package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.comparators.ILongFieldEntity;
import com.icourt.alpha.widget.json.SeaFileTimeJsonAdapter;
import com.icourt.json.BooleanTypeAdapter;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

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
    public boolean encrypted; //是否是加密状态的资料库
    @SerializedName(value = "id", alternate = {"repo_id"})
    public String repo_id;
    @SerializedName(value = "repo_name", alternate = {"name"})
    public String repo_name;

    @JsonAdapter(SeaFileTimeJsonAdapter.class)
    public long last_modified;

    @JsonAdapter(SeaFileTimeJsonAdapter.class)
    public long mtime;
    public long size;
    public String permission;//权限


    @Expose(serialize = false, deserialize = false)
    public transient long decryptMillisecond;//解密时间 本地用

    public long getUpdateTime() {
        return last_modified > 0 ? last_modified : mtime * 1_000;
    }

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

    /**
     * 是否需要解密
     * 解密后 1小时内不需要解密
     *
     * @return
     */
    public boolean isNeedDecrypt() {
        return encrypted
                && decryptMillisecond <= DateUtils.millis() - TimeUnit.HOURS.toMillis(1);
    }

}
