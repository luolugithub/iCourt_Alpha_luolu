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
    @SerializedName(value = "mtime", alternate = {"deleted_time"})
    public long mtime;//文档上传时间


    @SerializedName(value = "type", alternate = {"is_dir"})
    @JsonAdapter(DirBooleanTypeAdapter.class)
    public boolean isDir;

    @JsonAdapter(LongTypeAdapter.class)
    public long size;

    public String parent_dir;
    public String suspensionTag;

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
        return suspensionTag;
    }


    public class DirBooleanTypeAdapter extends BooleanTypeAdapter {

        @Override
        public Boolean conver2Object(String value) {
            return TextUtils.equals(value, "dir");
        }
    }
}

