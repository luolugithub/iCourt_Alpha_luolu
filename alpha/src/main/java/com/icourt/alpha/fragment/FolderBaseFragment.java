package com.icourt.alpha.fragment;

import android.support.annotation.NonNull;

import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;

import java.util.List;

import retrofit2.Call;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public abstract class FolderBaseFragment extends BaseFragment {
    protected static final String KEY_SEA_FILE_REPO_ID = "seaFileRepoId";//仓库id
    protected static final String KEY_SEA_FILE_PARENT_DIR_PATH = "seaFileParentDirPath";//父目录路径

    protected String getSeaFileRepoId() {
        return getArguments().getString(KEY_SEA_FILE_REPO_ID, "");
    }

    protected String getSeaFileParentDirPath() {
        return getArguments().getString(KEY_SEA_FILE_PARENT_DIR_PATH, "");
    }

    /**
     * 获取sfile token
     *
     * @param callBack2
     */
    protected Call<SFileTokenEntity<String>> getSFileToken(
            @NonNull SimpleCallBack2<SFileTokenEntity<String>> callBack2) {
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
                getSeaFileParentDirPath()), callBack2);
    }
}
