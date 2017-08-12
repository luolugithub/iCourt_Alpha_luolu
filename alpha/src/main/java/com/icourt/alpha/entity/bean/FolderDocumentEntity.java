package com.icourt.alpha.entity.bean;

import android.text.TextUtils;

import com.icourt.alpha.widget.filter.IFilterEntity;

import java.io.Serializable;

/**
 * Description  文件或者文件夹实体
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderDocumentEntity implements Serializable, IFilterEntity {
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
    public String id;
    public long lock_time;
    public String name;
    public String permission;
    public boolean is_locked;
    public long mtime;//文档上传时间
    public String type;
    public long size;

    public boolean isDir() {
        return TextUtils.equals(type, "dir");
    }

    @Override
    public boolean isFilter(int type) {
        if (type == TYPE_FILE) {//过滤文件
            return !isDir();
        }
        return false;
    }
}
