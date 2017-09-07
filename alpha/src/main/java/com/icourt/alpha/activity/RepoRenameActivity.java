package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;

import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.widget.filter.SFileNameFilter;

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
                              RepoEntity repoEntity) {
        if (context == null) return;
        if (repoEntity == null) return;
        Intent intent = new Intent(context, RepoRenameActivity.class);
        intent.putExtra("data", repoEntity);
        context.startActivity(intent);
    }

    RepoEntity paramRepoEntity;

    @Override
    protected void initView() {
        super.initView();
        setTitle("重命名资料库");
        setTitleActionTextView("保存");
        inputNameEt.setHint("资料库名称");
        inputNameEt.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getMaxInputLimitNum()),
                new SFileNameFilter()});
        paramRepoEntity = (RepoEntity) getIntent().getSerializableExtra("data");
        if (paramRepoEntity == null) {
            finish();
        }
        inputNameEt.setText(paramRepoEntity.repo_name);
        inputNameEt.setSelection(inputNameEt.getText().length());
    }

    @Override
    protected boolean onCancelSubmitInput(final EditText et) {
        if (TextUtils.equals(paramRepoEntity.repo_name, et.getText())) {
            finish();
            return true;
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
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            if (paramRepoEntity == null) return;
            showLoadingDialog("更改中...");
            paramRepoEntity.repo_name = et.getText().toString().trim();
            paramRepoEntity.last_modified = DateUtils.millis();
            callEnqueue(getSFileApi().documentRootUpdateName(
                    paramRepoEntity.repo_id,
                    "rename",
                    paramRepoEntity.repo_name),
                    new SFileCallBack<String>() {
                        @Override
                        public void onSuccess(Call<String> call, Response<String> response) {
                            dismissLoadingDialog();
                            if (TextUtils.equals("success", response.body())) {
                                showToast("更改资料库标题成功");
                                EventBus.getDefault().post(paramRepoEntity);
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
