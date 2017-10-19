package com.icourt.alpha.entity.bean;

import com.icourt.alpha.constants.SFileConfig;

import java.io.Serializable;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/9
 * version 2.1.0
 */
public class RepoTypeEntity implements Serializable{
    @SFileConfig.REPO_TYPE
    public int repoType;
    public String title;

    public RepoTypeEntity(@SFileConfig.REPO_TYPE int repoType, String title) {
        this.repoType = repoType;
        this.title = title;
    }
}
