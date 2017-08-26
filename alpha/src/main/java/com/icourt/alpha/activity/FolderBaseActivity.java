package com.icourt.alpha.activity;

import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.SFileConfig;

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
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限
    protected static final String KEY_SEA_FILE_REPO_TYPE = "seaFileRepoType";//repo类型
    protected static final String KEY_SEA_FILE_REPO_TITLE = "seaFileRepoTitle";//repo标题


    protected String getSeaFileRepoId() {
        return getIntent().getStringExtra(KEY_SEA_FILE_REPO_ID);
    }

    protected String getSeaFileDirPath() {
        return getIntent().getStringExtra(KEY_SEA_FILE_DIR_PATH);
    }


    /**
     * repo 的权限
     *
     * @return
     */
    @SFileConfig.FILE_PERMISSION
    protected String getRepoPermission() {
        String stringPermission = getIntent().getStringExtra(KEY_SEA_FILE_REPO_PERMISSION);
        return SFileConfig.convert2filePermission(stringPermission);
    }

    /**
     * 资料库类型
     *
     * @return
     */
    @SFileConfig.REPO_TYPE
    protected int getRepoType() {
        return SFileConfig.convert2RepoType(getIntent().getIntExtra(KEY_SEA_FILE_REPO_TYPE, 0));
    }

    /**
     * 资料库标题
     *
     * @return
     */
    protected String getRepoTitle() {
        return getIntent().getStringExtra(KEY_SEA_FILE_REPO_TITLE);
    }

}
