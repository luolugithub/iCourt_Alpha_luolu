package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.icourt.alpha.R;
import com.icourt.alpha.constants.TaskConfig;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/10/10
 * version 2.1.0
 */
public class TaskDescChangeActivity extends EditItemBaseActivity {
    private static final String KEY_TASK = "task";

    public static void launch(@NonNull Context context, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        if (context == null) return;
        if (itemEntity == null) return;
        Intent intent = new Intent(context, TaskDescChangeActivity.class);
        intent.putExtra(KEY_TASK, itemEntity);
        context.startActivity(intent);
    }

    TaskEntity.TaskItemEntity itemEntity;

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.task_change_desc);
        itemEntity = (TaskEntity.TaskItemEntity) getIntent().getSerializableExtra(KEY_TASK);
        inputNameEt.setHint(R.string.task_change_desc);
        inputNameEt.setText(itemEntity.description);
        inputNameEt.setSelection(StringUtils.length(inputNameEt.getText()));
    }

    @Override
    protected int getMaxInputLimitNum() {
        return TaskConfig.TASK_DESC_MAX_LENGTH;
    }

    private boolean isSameInput() {
        return TextUtils.equals(itemEntity.description, inputNameEt.getText());
    }

    @Override
    protected void onSubmitInput(EditText et) {
        if (isSameInput()) {
            finish();
        } else {
            update();
        }
    }

    private void update() {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskUpdateNew(
                        RequestUtils.createJsonBody(
                                TaskEntity.TaskItemEntity.createUpdateDescParam(itemEntity, inputNameEt.getText().toString()).toString())),
                new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            EventBus.getDefault().post(
                                    new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION, response.body().result));
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();

                    }
                });
    }

    @Override
    protected boolean onCancelSubmitInput(EditText et) {
        return false;
    }
}
