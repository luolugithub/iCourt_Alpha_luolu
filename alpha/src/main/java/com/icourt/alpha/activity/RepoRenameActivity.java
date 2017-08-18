package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  资料库重命名
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class RepoRenameActivity extends RepoCreateActivity {
    /**
     * 资料库创建
     *
     * @param context
     */
    public static void launch(@NonNull Context context,
                              DocumentRootEntity documentRootEntity) {
        if (context == null) return;
        if (documentRootEntity == null) return;
        Intent intent = new Intent(context, RepoRenameActivity.class);
        intent.putExtra("data", documentRootEntity);
        context.startActivity(intent);
    }

    DocumentRootEntity paramDocumentRootEntity;

    @Override
    protected void initView() {
        super.initView();
        setTitle("重命名资料库");
        inputNameEt.setHint("资料库名称");
        paramDocumentRootEntity = (DocumentRootEntity) getIntent().getSerializableExtra("data");
        if (paramDocumentRootEntity == null) {
            finish();
        }
        inputNameEt.setText(paramDocumentRootEntity.repo_name);
        inputNameEt.setSelection(inputNameEt.getText().length());
    }

    @Override
    protected boolean onCancelSubmitInput(final EditText et) {
        if (TextUtils.equals(paramDocumentRootEntity.repo_name, et.getText())) {
            finish();
            return false;
        }
        return super.onCancelSubmitInput(et);
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            if (paramDocumentRootEntity == null) return;
            showLoadingDialog("更改中...");
            paramDocumentRootEntity.repo_name = et.getText().toString();
            paramDocumentRootEntity.last_modified = DateUtils.millis();
            getSFileApi().documentRootUpdateName(
                    paramDocumentRootEntity.repo_id,
                    "rename",
                    paramDocumentRootEntity.repo_name)
                    .enqueue(new SFileCallBack<String>() {
                        @Override
                        public void onSuccess(Call<String> call, Response<String> response) {
                            dismissLoadingDialog();
                            if (TextUtils.equals("success", response.body())) {
                                showToast("更改资料库标题成功");
                                EventBus.getDefault().post(paramDocumentRootEntity);
                                finish();
                            } else {
                                showToast("更改资料库标题失败");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
        }
    }

}
