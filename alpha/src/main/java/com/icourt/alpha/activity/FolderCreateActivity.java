package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.api.RequestUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class FolderCreateActivity extends RepoCreateActivity {
    protected static final String KEY_SEA_FILE_REPO_ID = "seaFileRepoId";//仓库id
    protected static final String KEY_SEA_FILE_DIR_PATH = "seaFileDirPath";//目录路径

    protected String getSeaFileRepoId() {
        return getIntent().getStringExtra(KEY_SEA_FILE_REPO_ID);
    }

    protected String getSeaFileDirPath() {
        return getIntent().getStringExtra(KEY_SEA_FILE_DIR_PATH);
    }

    /**
     * 创建文件夹
     *
     * @param context
     * @param seaFileRepoId
     * @param seaFileDirPath
     */
    public static void launch(
            @NonNull Context context,
            String seaFileRepoId,
            String seaFileDirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderCreateActivity.class);
        intent.putExtra(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_DIR_PATH, seaFileDirPath);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("新建文件夹");
        inputNameEt.setHint("文件夹名称");
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            showLoadingDialog("创建中...");
            JsonObject operationJsonObject = new JsonObject();
            operationJsonObject.addProperty("operation", "mkdir");
            getSFileApi().folderCreate(
                    getSeaFileRepoId(),
                    String.format("%s%s", getSeaFileDirPath(), et.getText().toString()),
                    RequestUtils.createJsonBody(operationJsonObject.toString()))
                    .enqueue(new SFileCallBack<DocumentRootEntity>() {
                        @Override
                        public void onSuccess(Call<DocumentRootEntity> call, Response<DocumentRootEntity> response) {
                            dismissLoadingDialog();
                            showToast("创建文件夹成功");
                            finish();
                        }

                        @Override
                        public void onFailure(Call<DocumentRootEntity> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
        }
    }
}

