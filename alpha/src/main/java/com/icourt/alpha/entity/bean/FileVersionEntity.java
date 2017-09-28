package com.icourt.alpha.entity.bean;

import android.support.annotation.Nullable;

import com.icourt.alpha.widget.comparators.ILongFieldEntity;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class FileVersionEntity implements ILongFieldEntity, ISeaFile {
    /**
     * "rev_file_size": 61003,
     * "rev_file_id": "aaf6ab1ea3de4c6e225acb632a4648394583cf7d",
     * "ctime": 1502768610,
     * "creator_name": "ba5cbf1a21b111e7843370106faece2e@ifile.com",
     * "creator": "0000000000000000000000000000000000000000",
     * "user_info": {
     * "contact_email": "ba5cbf1a21b111e7843370106faece2e@ifile.com",
     * "email": "ba5cbf1a21b111e7843370106faece2e@ifile.com",
     * "name": "\u5434\u4f51\u70ab"
     * },
     * "root_id": "adc60fcbd634a1aa71de67c9844ad935b32d4e2e",
     * "rev_renamed_old_path": null,
     * "device_name": null,
     * "parent_id": "49f6ba14527e05a7caf6994d8379f4ba509e211d",
     * "new_merge": false,
     * "repo_id": "d4f82446-a37f-478c-b6b5-ed0e779e1768",
     * "version": 1,
     * "client_version": null,
     * "desc": "Reverted file \"12-03-20-u=861344690,2872501191&fm=175&s=C78BF2056A1254D4080D098003003096&w=640&h=640&img.JPEG\" to status at 2017-08-12 20:27:54",
     * "id": "b906e563832c8bdf322c68f40e8ce418d4a84789",
     * "conflict": false,
     * "second_parent_id": null
     */

    public String id;
    public int version;
    public long ctime;
    public String repo_id;
    public long rev_file_size;
    public String rev_file_id;
    public SFileUserInfo user_info;
    public String seaFileFullPath;
    public String seaFilePermission;

    @Override
    public Long getCompareLongField() {
        return ctime;
    }

    @Override
    public String getSeaFileId() {
        return id;
    }

    @Override
    public String getSeaFileRepoId() {
        return repo_id;
    }

    @Override
    public String getSeaFileFullPath() {
        return seaFileFullPath;
    }

    @Nullable
    @Override
    public String getSeaFileVersionId() {
        return id;
    }

    @Override
    public long getSeaFileSize() {
        return rev_file_size;
    }

    @Override
    public String getSeaFilePermission() {
        return seaFilePermission;
    }

    public static class SFileUserInfo implements Serializable {
        public String name;
    }

    @Override
    public String toString() {
        return "FileVersionEntity{" +
                "id='" + id + '\'' +
                ", version=" + version +
                ", ctime=" + ctime +
                ", repo_id='" + repo_id + '\'' +
                ", rev_file_size=" + rev_file_size +
                ", rev_file_id='" + rev_file_id + '\'' +
                ", user_info=" + user_info +
                ", seaFileFullPath='" + seaFileFullPath + '\'' +
                '}';
    }
}
