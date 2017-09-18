package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UrlUtils;

import retrofit2.Call;
import retrofit2.HttpException;
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
            //图片格式 加载缩略图
            if (IMUtils.isPIC(folderDocumentEntity.name)) {
                GlideUtils.loadSFilePic(getContext(),
                        getSfileThumbnailImage(folderDocumentEntity.name),
                        inputTypeIv);
            } else {
                inputTypeIv.setImageResource(FileUtils.getSFileIcon(folderDocumentEntity.name));
            }
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
     * 获取缩略图地址
     *
     * @param name
     * @return
     */
    private String getSfileThumbnailImage(String name) {
        //https://test.alphalawyer.cn/ilaw/api/v2/documents/thumbnailImage?repoId=d4f82446-a37f-478c-b6b5-ed0e779e1768&seafileToken=%20d6c69d6f4fc208483c243246c6973d8eb141501c&p=//1502507774237.png&size=250
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                getSeaFileRepoId(),
                SFileTokenUtils.getSFileToken(),
                UrlUtils.encodeUrl(String.format("%s%s", getSeaFileDirPath(), name)),
                150);
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
            String fileName = et.getText().toString().trim().concat(fileSuffix);
            //无需提交
            if (TextUtils.equals(fileName, folderDocumentEntity.name)) {
                finish();
                return;
            }
            showLoadingDialog("更改中...");
            if (folderDocumentEntity.isDir()) {
                callEnqueue(getSFileApi()
                        .folderRename(
                                getSeaFileRepoId(),
                                String.format("%s%s", getSeaFileDirPath(), folderDocumentEntity.name),
                                "rename",
                                fileName), new SFileCallBack<String>() {
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
                        if (t instanceof HttpException
                                && ((HttpException) t).code() == 400) {
                            showToast("文件夹名字可能太长啦");
                        }
                    }
                });
            } else {
                callEnqueue(getSFileApi()
                                .fileRename(
                                        getSeaFileRepoId(),
                                        String.format("%s%s", getSeaFileDirPath(), folderDocumentEntity.name),
                                        "rename",
                                        fileName),
                        new SFileCallBack<FolderDocumentEntity>() {
                            @Override
                            public void onSuccess(Call<FolderDocumentEntity> call, Response<FolderDocumentEntity> response) {
                                dismissLoadingDialog();
                                showToast("重命名成功");
                                // EventBus.getDefault().post(paramDocumentRootEntity);
                                finish();
                            }

                            @Override
                            public void onFailure(Call<FolderDocumentEntity> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                                if (t instanceof HttpException
                                        && ((HttpException) t).code() == 400) {
                                    showToast("文件名字可能太长啦");
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected boolean onCancelSubmitInput(final EditText et) {
        if (TextUtils.isEmpty(et.getText())) {
            finish();
            return false;
        }
        if (!TextUtils.isEmpty(et.getText())) {
            String fileName = et.getText().toString().trim().concat(fileSuffix);
            //无需提交
            if (TextUtils.equals(fileName, folderDocumentEntity.name)) {
                finish();
                return false;
            }
        }
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("保存本次编辑?")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onSubmitInput(et);
                    }
                })
                .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).show();
        return true;
    }

    @Override
    protected boolean checkInput(EditText et) {
        if (StringUtils.isEmpty(et.getText())) {
            if (folderDocumentEntity.isDir()) {
                showTopSnackBar("文件夹名称为空");
            } else {
                showTopSnackBar("文件名称为空");
            }
            return false;
        }
        return super.checkInput(et);
    }
}
