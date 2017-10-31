package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.entity.bean.GroupDetailEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.api.RequestUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/27
 * version 2.1.0
 */
public class GroupChangeDescActivity extends EditItemBaseActivity {
    private static final String KEY_GROUP_DATA = "groupData";


    public static void launchForResult(@NonNull Activity context,
                                       int reqCode,
                                       GroupDetailEntity groupDetailEntity) {
        if (context == null) return;
        if (groupDetailEntity == null) return;
        Intent intent = new Intent(context, GroupChangeDescActivity.class);
        intent.putExtra(KEY_GROUP_DATA, groupDetailEntity);
        context.startActivityForResult(intent, reqCode);
    }

    GroupDetailEntity groupDetailEntity;

    @Override
    protected void initView() {
        super.initView();
        groupDetailEntity = (GroupDetailEntity) getIntent().getSerializableExtra(KEY_GROUP_DATA);
        setTitle("讨论组目标");
        setTitleActionTextView(getContextString(R.string.str_finish));
        inputNameEt.setText(groupDetailEntity.intro);
        inputNameEt.setSelection(inputNameEt.length());
    }

    @Override
    protected int getMaxInputLimitNum() {
        return 140;
    }

    @Override
    protected void onSubmitInput(EditText et) {
        updateGroupInfo();
    }

    @Override
    protected boolean onCancelSubmitInput(final EditText et) {
        if (StringUtils.isEmpty(et.getText())) {
            finish();
            return false;
        }
        //无需提交
        if (TextUtils.equals(et.getText(), groupDetailEntity.intro)) {
            finish();
            return false;
        }
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.str_notice)
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

    /**
     * 修改讨论组信息
     */
    private void updateGroupInfo() {
        JsonObject param = new JsonObject();
        final String groupDesc = getTextString(inputNameEt, "");
        param.addProperty("name", groupDetailEntity.name);
        param.addProperty("intro", groupDesc);
        param.addProperty("is_private", groupDetailEntity.is_private);
        param.addProperty("member_invite", groupDetailEntity.member_invite);
        param.addProperty("chat_history", groupDetailEntity.chat_history);
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().groupUpdate(groupDetailEntity.tid, RequestUtils.createJsonBody(param.toString())),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        groupDetailEntity.intro = groupDesc;
                        setResult();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    private void setResult() {
        Intent intent = getIntent();
        intent.putExtra(KEY_ACTIVITY_RESULT, groupDetailEntity);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
