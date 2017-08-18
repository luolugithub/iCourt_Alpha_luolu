package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;

import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.widget.filter.SFileNameFilter;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;
/**
 * 创建资料库
 * 资料库名称限制：
 * 文件名长度不得超过256个字节
 * 文件名末尾不得有空格
 * 特殊字符不能作为资料库名称：'\\', '/', ':', '*', '?', '"', '<', '>', '|', '\b', '\t'
 * 超过一行的，输入框变高，折行显示
 */

/**
 * Description  资料库创建
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/18
 * version 2.1.0
 */
public class RepoCreateActivity extends SFileEditBaseActivity {
    /**
     * 资料库创建
     *
     * @param context
     */
    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, RepoCreateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("新建资料库");
        inputNameEt.setHint("资料库名称");
        inputNameEt.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(getMaxInputLimitNum()),
                new SFileNameFilter()});
    }

    @Override
    protected int getMaxInputLimitNum() {
        return 50;
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            showLoadingDialog("创建中...");
            getSFileApi().documentRootCreate(et.getText().toString())
                    .enqueue(new SFileCallBack<DocumentRootEntity>() {
                        @Override
                        public void onSuccess(Call<DocumentRootEntity> call, Response<DocumentRootEntity> response) {
                            dismissLoadingDialog();
                            showToast("创建资料库成功");
                            EventBus.getDefault().post(response.body());
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

    @Override
    protected boolean onCancelSubmitInput(final EditText et) {
        if (!TextUtils.isEmpty(et.getText())) {
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
        } else {
            finish();
        }
        return true;
    }


    protected boolean checkInput(EditText et) {
        if (et.getText().toString().endsWith(" ")) {
            showTopSnackBar("资料库名称末尾不得有空格");
            return false;
        }
        return true;
    }
}
