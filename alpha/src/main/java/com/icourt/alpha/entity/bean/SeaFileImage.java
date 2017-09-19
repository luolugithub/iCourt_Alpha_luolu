package com.icourt.alpha.entity.bean;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/12
 * version 2.1.0
 */
public class SeaFileImage implements ISeaFileImage {

    private String seaFileImageFullPath;
    private String seaFileImageRepoId;

    public SeaFileImage(String seaFileImageFullPath, String seaFileImageRepoId) {
        this.seaFileImageFullPath = seaFileImageFullPath;
        this.seaFileImageRepoId = seaFileImageRepoId;
    }

    @Override
    public String getSeaFileImageFullPath() {
        return seaFileImageFullPath;
    }

    @Override
    public String getSeaFileImageRepoId() {
        return seaFileImageRepoId;
    }
}
