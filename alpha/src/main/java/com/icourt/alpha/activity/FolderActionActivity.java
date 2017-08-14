package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.api.RequestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  文件夹创建/更新标题/页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class FolderActionActivity extends FolderBaseActivity {

    public static final String ACTION_CREATE = "action_create";//创建资料库
    public static final String ACTION_UPDATE_TITLE = "action_update_title";//更新资料库标题
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.document_name_et)
    EditText documentNameEt;
    FolderDocumentEntity folderDocumentEntity;

    /**
     * 创建文件夹
     *
     * @param context
     * @param seaFileRepoId
     * @param seaFileDirPath
     */
    public static void launchCreate(
            @NonNull Context context,
            String seaFileRepoId,
            String seaFileDirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderActionActivity.class);
        intent.putExtra(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_DIR_PATH, seaFileDirPath);
        intent.setAction(ACTION_CREATE);
        context.startActivity(intent);
    }

    /**
     * 更新文件／文件夹标题
     *
     * @param context
     */
    public static void launchUpdateTitle(@NonNull Context context,
                                         FolderDocumentEntity folderDocumentEntity,
                                         String seaFileRepoId,
                                         String seaFileDirPath) {
        if (context == null) return;
        if (folderDocumentEntity == null) return;
        Intent intent = new Intent(context, FolderActionActivity.class);
        intent.setAction(ACTION_UPDATE_TITLE);
        intent.putExtra(KEY_SEA_FILE_REPO_ID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_DIR_PATH, seaFileDirPath);
        intent.putExtra("data", folderDocumentEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_create);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("创建文件夹");
        documentNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            }
        });
        if (TextUtils.equals(getIntent().getAction(), ACTION_UPDATE_TITLE)) {
            setTitle("更新文件夹");
            folderDocumentEntity = (FolderDocumentEntity) getIntent().getSerializableExtra("data");
            documentNameEt.setText(folderDocumentEntity.name);
            documentNameEt.setSelection(documentNameEt.getText().length());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction: {
                /**
                 * 创建资料库
                 * 资料库名称限制：
                 * 文件名长度不得超过256个字节
                 * 文件名末尾不得有空格
                 * 特殊字符不能作为资料库名称：'\\', '/', ':', '*', '?', '"', '<', '>', '|', '\b', '\t'
                 * 超过一行的，输入框变高，折行显示
                 */
                if (StringUtils.isEmpty(documentNameEt.getText())) {
                    showTopSnackBar("文件名称不能为空");
                    return;
                }

                if (documentNameEt.getText().toString().endsWith(" ")) {
                    showTopSnackBar("文件名末尾不得有空格");
                    return;
                }

                if (documentNameEt.getText().toString().getBytes().length > 256) {
                    showTopSnackBar("文件名称过长");
                    return;
                }
                /* Pattern pattern = Pattern.compile(targetText, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(originalText);
        SpannableUtils.setTextForegroundColorSpan();*/
                if (TextUtils.equals(getIntent().getAction(), ACTION_CREATE)) {
                    getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
                        @Override
                        public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                            dismissLoadingDialog();
                            if (TextUtils.isEmpty(response.body().authToken)) {
                                showTopSnackBar("sfile authToken返回为null");
                                return;
                            }
                            createFolder(response.body().authToken);
                        }

                        @Override
                        public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
                } else if (TextUtils.equals(getIntent().getAction(), ACTION_UPDATE_TITLE)) {
                    getSFileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
                        @Override
                        public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                            dismissLoadingDialog();
                            if (TextUtils.isEmpty(response.body().authToken)) {
                                showTopSnackBar("sfile authToken返回为null");
                                return;
                            }
                            updateFolderTitle(response.body().authToken);
                        }

                        @Override
                        public void onFailure(Call<SFileTokenEntity<String>> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
                }
            }
            break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 创建文件夹
     *
     * @param sfileToken
     */
    private void createFolder(String sfileToken) {
        showLoadingDialog("创建中...");
        JsonObject operationJsonObject = new JsonObject();
        operationJsonObject.addProperty("operation", "mkdir");
        getSFileApi().folderCreate(
                String.format("Token %s", sfileToken),
                getSeaFileRepoId(),
                String.format("%s%s", getSeaFileDirPath(), documentNameEt.getText().toString()),
                RequestUtils.createJsonBody(operationJsonObject.toString()))
                .enqueue(new SimpleCallBack2<DocumentRootEntity>() {
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

    /**
     * 更新文件夹标题
     *
     * @param sfileToken
     */
    private void updateFolderTitle(String sfileToken) {
        if (folderDocumentEntity == null) return;
        showLoadingDialog("更改中...");
        if (folderDocumentEntity.isDir()) {
            getSFileApi()
                    .folderRename(String.format("Token %s", sfileToken),
                            getSeaFileRepoId(),
                            String.format("%s%s", getSeaFileDirPath(), folderDocumentEntity.name),
                            "rename",
                            documentNameEt.getText().toString())
                    .enqueue(new SimpleCallBack2<String>() {
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
            //修改文件标题 "error_msg": "The new name is the same to the old"
            if (TextUtils.equals(documentNameEt.getText().toString(), folderDocumentEntity.name)) {
                finish();
                return;
            }
            getSFileApi()
                    .fileRename(String.format("Token %s", sfileToken),
                            getSeaFileRepoId(),
                            String.format("%s%s", getSeaFileDirPath(), folderDocumentEntity.name),
                            "rename",
                            documentNameEt.getText().toString())
                    .enqueue(new SimpleCallBack2<FolderDocumentEntity>() {
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
