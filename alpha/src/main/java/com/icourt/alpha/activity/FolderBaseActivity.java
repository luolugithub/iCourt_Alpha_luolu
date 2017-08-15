package com.icourt.alpha.activity;

import com.icourt.alpha.base.BaseActivity;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/11
 * version 2.1.0
 */
public class FolderBaseActivity extends BaseActivity {

    protected static final String KEY_SEA_FILE_REPO_ID = "seaFileRepoId";//仓库id
    protected static final String KEY_SEA_FILE_DIR_PATH = "seaFileDirPath";//目录路径

    protected String getSeaFileRepoId() {
        return getIntent().getStringExtra(KEY_SEA_FILE_REPO_ID);
    }

    protected String getSeaFileDirPath() {
        return getIntent().getStringExtra(KEY_SEA_FILE_DIR_PATH);
    }



}
