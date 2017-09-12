package com.icourt.alpha.entity.bean;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.icourt.alpha.view.recyclerviewDivider.ISuspensionInterface;
import com.icourt.alpha.widget.filter.IFilterEntity;
import com.icourt.alpha.widget.json.SeaFileTimeJsonAdapter;
import com.icourt.json.BooleanTypeAdapter;
import com.icourt.json.LongTypeAdapter;

import java.io.Serializable;

/**
 * Description  文件或者文件夹实体
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderDocumentEntity
        implements Serializable, IFilterEntity, ISuspensionInterface {
    public static final int TYPE_FILE = 1;
    /**
     * "lock_time": 0,
     * "modifier_email": "ba5cbf1a21b111e7843370106faece2e@ifile.com",
     * "name": "Android 2017-8 工作安排.numbers",
     * "permission": "rw",
     * "is_locked": false,
     * "lock_owner": null,
     * "mtime": 1502364814,
     * "modifier_contact_email": "ba5cbf1a21b111e7843370106faece2e@ifile.com",
     * "locked_by_me": false,
     * "type": "file",
     * "id": "5c534e5ae65ff77ee403d7748b7900daf072737b",
     * "modifier_name": "吴佑炫",
     * "size": 131252
     */
    @SerializedName(value = "id", alternate = {"obj_id"})
    public String id;
    public String commit_id;
    public long lock_time;

    @SerializedName(value = "name", alternate = {"obj_name"})
    public String name;


    public String permission;
    public boolean is_locked;

    @JsonAdapter(SeaFileTimeJsonAdapter.class)
    public long mtime;//文档上传时间

    @JsonAdapter(SeaFileTimeJsonAdapter.class)
    public long deleted_time;//文档删除的时间

    @SerializedName(value = "type", alternate = {"is_dir"})
    @JsonAdapter(DirBooleanTypeAdapter.class)
    public boolean isDir;

    @JsonAdapter(LongTypeAdapter.class)
    public long size;

    /**
     * 唯一区分  repoId+parent_dir+fileName(注意fileName可以重命名)
     */
    public String parent_dir;//路径地址
    public String repoId;//repo id

    public String suspensionTag;
    public String modifier_name;


    public boolean isDir() {
        return isDir;
    }

    @Override
    public boolean isFilter(int type) {
        if (type == TYPE_FILE) {//过滤文件
            return !isDir();
        }
        return false;
    }

    @Nullable
    @Override
    public String getTargetField() {
        return name;
    }

    @Override
    public void setSuspensionTag(String suspensionTag) {
        this.suspensionTag = suspensionTag;
    }

    @Override
    public boolean isShowSuspension() {
        return false;
    }

    @NonNull
    @Override
    public String getSuspensionTag() {
        if (TextUtils.isEmpty(suspensionTag)) {
            return name;
        }
        return suspensionTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) return false;
        if (getClass() != o.getClass())
            return false;
        final FolderDocumentEntity other = (FolderDocumentEntity) o;

        return TextUtils.equals(this.repoId, other.repoId)
                && TextUtils.equals(this.parent_dir, other.parent_dir)
                && TextUtils.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + System.identityHashCode(repoId);
        result = result * 31 + System.identityHashCode(parent_dir);
        result = result * 31 + System.identityHashCode(name);
        return result;
    }

    @Override
    public String toString() {
        return "FolderDocumentEntity{" +
                "id='" + id + '\'' +
                ", commit_id='" + commit_id + '\'' +
                ", lock_time=" + lock_time +
                ", name='" + name + '\'' +
                ", permission='" + permission + '\'' +
                ", is_locked=" + is_locked +
                ", mtime=" + mtime +
                ", deleted_time=" + deleted_time +
                ", isDir=" + isDir +
                ", size=" + size +
                ", parent_dir='" + parent_dir + '\'' +
                ", repoId='" + repoId + '\'' +
                ", suspensionTag='" + suspensionTag + '\'' +
                ", modifier_name='" + modifier_name + '\'' +
                '}';
    }

    public class DirBooleanTypeAdapter extends BooleanTypeAdapter {

        @Override
        public Boolean conver2Object(String value) {
            return TextUtils.equals(value, "dir");
        }
    }
}

