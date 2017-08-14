package com.icourt.alpha.activity;

import android.support.annotation.NonNull;

import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;

import java.util.List;

import retrofit2.Call;

import static com.icourt.api.RequestUtils.callEnqueue;

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

    /**
     * 获取sfile token
     *
     * @param callBack2
     */
    protected Call<SFileTokenEntity<String>> getSFileToken(@NonNull SimpleCallBack2<SFileTokenEntity<String>> callBack2) {
        return callEnqueue(getApi().documentTokenQuery(), callBack2);
    }


    /**
     * 获取文件夹下面的文件夹与文件列表
     *
     * @param sfileToken
     * @param callBack2
     */
    protected Call<List<FolderDocumentEntity>> getSFileFolder(
            String sfileToken,
            SimpleCallBack2<List<FolderDocumentEntity>> callBack2) {
        return callEnqueue(getSFileApi().documentDirQuery(
                String.format("Token %s", sfileToken),
                getSeaFileRepoId(),
                getSeaFileDirPath()), callBack2);
    }
}
