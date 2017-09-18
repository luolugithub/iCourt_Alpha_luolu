package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.widget.EditText;

import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
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
    private static final String KEY_CACHE_REPO = "key_cache_repo" + RepoCreateActivity.class.getSimpleName();//缓存的folder名字

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
        inputNameEt.setText(SpUtils.getInstance().getStringData(KEY_CACHE_REPO, ""));
        inputNameEt.setSelection(inputNameEt.getText().length());
    }

    @Override
    protected int getMaxInputLimitNum() {
        return 80;
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (checkInput(et)) {
            showLoadingDialog("创建中...");
            callEnqueue(getSFileApi().documentRootCreate(et.getText().toString().trim()),
                    new SFileCallBack<RepoEntity>() {
                        @Override
                        public void onSuccess(Call<RepoEntity> call, Response<RepoEntity> response) {
                            dismissLoadingDialog();
                            showToast("创建资料库成功");
                            SpUtils.getInstance().remove(KEY_CACHE_REPO);
                            EventBus.getDefault().post(response.body());
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
    protected boolean onCancelSubmitInput(final EditText et) {
        if (!StringUtils.isEmpty(et.getText())) {
            SpUtils.getInstance().putData(KEY_CACHE_REPO, et.getText().toString());
        } else {
            SpUtils.getInstance().remove(KEY_CACHE_REPO);
        }
        finish();
        return true;
    }


    protected boolean checkInput(EditText et) {
        if (StringUtils.isEmpty(et.getText())) {
            showTopSnackBar("资料库名称为空");
            return false;
        }
        return true;
    }
}
