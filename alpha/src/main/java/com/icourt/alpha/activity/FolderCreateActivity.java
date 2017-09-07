package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.filter.SFileNameFilter;
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
public class FolderCreateActivity extends SFileEditBaseActivity {
    private static final String KEY_CACHE_FOLDER = "key_cache_folder" + FolderCreateActivity.class.getSimpleName();//缓存的folder名字
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
        inputNameEt.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getMaxInputLimitNum()),
                new SFileNameFilter()});
        inputNameEt.setText(SpUtils.getInstance().getStringData(KEY_CACHE_FOLDER, ""));
        inputNameEt.setSelection(inputNameEt.getText().length());
    }

    @Override
    protected int getMaxInputLimitNum() {
        return 100;
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            showLoadingDialog("创建中...");
            JsonObject operationJsonObject = new JsonObject();
            operationJsonObject.addProperty("operation", "mkdir");
            callEnqueue(getSFileApi().folderCreate(
                    getSeaFileRepoId(),
                    String.format("%s%s", getSeaFileDirPath(), et.getText().toString().trim()),
                    RequestUtils.createJsonBody(operationJsonObject.toString())),
                    new SFileCallBack<RepoEntity>() {
                        @Override
                        public void onSuccess(Call<RepoEntity> call, Response<RepoEntity> response) {
                            dismissLoadingDialog();
                            showToast("新建文件夹成功");
                            SpUtils.getInstance().remove(KEY_CACHE_FOLDER);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<RepoEntity> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
        }
    }

    @Override
    protected boolean onCancelSubmitInput(EditText et) {
        if (!StringUtils.isEmpty(et.getText())) {
            SpUtils.getInstance().putData(KEY_CACHE_FOLDER, et.getText().toString());
        }
        finish();
        return true;
    }

    protected boolean checkInput(EditText et) {
        if (StringUtils.isEmpty(et.getText())) {
            showTopSnackBar("文件夹名称为空");
            return false;
        }
        return true;
    }
}

