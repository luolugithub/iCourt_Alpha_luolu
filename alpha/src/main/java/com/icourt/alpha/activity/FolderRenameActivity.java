package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.FileUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  文件夹重命名
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class FolderRenameActivity extends FolderCreateActivity {
    /**
     * 更新文件／文件夹标题
     *
     * @param context
     */
    public static void launch(@NonNull Context context,
                              FolderDocumentEntity folderDocumentEntity,
                              String seaFileRepoId,
                              String seaFileDirPath) {
        if (context == null) return;
        if (folderDocumentEntity == null) return;
        Intent intent = new Intent(context, FolderRenameActivity.class);
        intent.putExtra(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_DIR_PATH, seaFileDirPath);
        intent.putExtra("data", folderDocumentEntity);
        context.startActivity(intent);
    }

    FolderDocumentEntity folderDocumentEntity;
    String fileSuffix = "";//文件后缀

    @Override
    protected void initView() {
        super.initView();
        folderDocumentEntity = (FolderDocumentEntity) getIntent().getSerializableExtra("data");
        if (folderDocumentEntity == null) {
            finish();
        }
        if (!folderDocumentEntity.isDir()) {
            inputNameEt.setHint("文件名称");
            setTitle("重命名文件");
            inputTypeIv.setImageResource(FileUtils.getSFileIcon(folderDocumentEntity.name));
            fileSuffix = FileUtils.getFileSuffix(folderDocumentEntity.name);
        } else {
            inputNameEt.setHint("文件夹名称");
            setTitle("重命名文件夹");
            inputTypeIv.setImageResource(R.mipmap.folder);
        }
        inputNameEt.setText(getFolderEditNamePart());
        inputNameEt.setSelection(inputNameEt.getText().length());
    }

    /**
     * 获取可以编辑的部分
     *
     * @return
     */
    private String getFolderEditNamePart() {
        if (folderDocumentEntity != null) {
            if (folderDocumentEntity.isDir()) {
                return folderDocumentEntity.name;
            } else {
                return FileUtils.getFileNameWithoutSuffix(folderDocumentEntity.name);
            }
        }
        return "";
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            if (folderDocumentEntity == null) return;
            String fileName = et.getText().toString().concat(fileSuffix);
            //无需提交
            if (TextUtils.equals(fileName, folderDocumentEntity.name)) {
                finish();
                return;
            }
            showLoadingDialog("更改中...");
            if (folderDocumentEntity.isDir()) {
                getSFileApi()
                        .folderRename(
                                getSeaFileRepoId(),
                                String.format("%s%s", getSeaFileDirPath(), folderDocumentEntity.name),
                                "rename",
                                fileName)
                        .enqueue(new SFileCallBack<String>() {
                            @Override
                            public void onSuccess(Call<String> call, Response<String> response) {
                                dismissLoadingDialog();
                                if (TextUtils.equals("success", response.body())) {
                                    showToast("更改标题成功");
                                    finish();
                                } else {
                                    showTopSnackBar("更改标题失败");
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });
            } else {
                getSFileApi()
                        .fileRename(
                                getSeaFileRepoId(),
                                String.format("%s%s", getSeaFileDirPath(), folderDocumentEntity.name),
                                "rename",
                                fileName)
                        .enqueue(new SFileCallBack<FolderDocumentEntity>() {
                            @Override
                            public void onSuccess(Call<FolderDocumentEntity> call, Response<FolderDocumentEntity> response) {
                                dismissLoadingDialog();
                                showToast("更改标题成功");
                                // EventBus.getDefault().post(paramDocumentRootEntity);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<FolderDocumentEntity> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });
            }
        }
    }

    @Override
    protected boolean onCancelSubmitInput(EditText et) {
        if (!TextUtils.isEmpty(et.getText())) {
            String fileName = et.getText().toString().concat(fileSuffix);
            //无需提交
            if (TextUtils.equals(fileName, folderDocumentEntity.name)) {
                finish();
                return false;
            }
        }
        return super.onCancelSubmitInput(et);
    }

    @Override
    protected boolean checkInput(EditText et) {
        if (et.getText().toString().endsWith(" ")) {
            if (folderDocumentEntity.isDir()) {
                showTopSnackBar("文件夹名称末尾不得有空格");
            } else {
                showTopSnackBar("文件名称末尾不得有空格");
            }
            return false;
        }
        return super.checkInput(et);
    }
}
