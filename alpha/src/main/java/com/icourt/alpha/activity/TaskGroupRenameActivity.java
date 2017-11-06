package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.constants.TaskConfig;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.filter.InputActionNextFilter;
import com.icourt.alpha.widget.filter.ReturnKeyFilter;
import com.icourt.api.RequestUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author youxuan  E-mail:xuanyouwu@163.com
 * @version 2.2.1
 * @Description 编辑任务组名称
 * @Company Beijing icourt
 * @date createTime：2017/11/1
 */
public class TaskGroupRenameActivity extends EditItemBaseActivity {
    private static final String KEY_ENTITY = "entity";

    public static void launchForResult(@NonNull Activity activity,
                                       @NonNull TaskGroupEntity taskGroupEntity,
                                       int requestCode) {
        if (activity == null || taskGroupEntity == null) {
            return;
        }
        Intent intent = new Intent(activity, TaskGroupRenameActivity.class);
        intent.putExtra(KEY_ENTITY, taskGroupEntity);
        activity.startActivityForResult(intent, requestCode);
    }

    TaskGroupEntity taskGroupEntity;

    @Override
    protected void initView() {
        super.initView();
        setTitle(getString(R.string.task_edit_group));
        inputNameEt.setHint(R.string.task_group_name_hint);
        inputNameEt.setFilters(new InputFilter[]{
                new ReturnKeyFilter(),
                new InputFilter.LengthFilter(getMaxInputLimitNum())});
        inputNameEt.setOnEditorActionListener(new InputActionNextFilter() {
            @Override
            public boolean onInputActionNext(TextView v) {
                SystemUtils.hideSoftKeyBoard(getActivity(), v, true);
                return super.onInputActionNext(v);
            }
        });
        taskGroupEntity = (TaskGroupEntity) getIntent().getSerializableExtra(KEY_ENTITY);
        inputNameEt.setText(taskGroupEntity.name);
        inputNameEt.setSelection(inputNameEt.getText().length());
    }

    @Override
    protected int getMaxInputLimitNum() {
        return TaskConfig.TASK_GROUP_NAME_MAX_LENGTH;
    }

    @Override
    protected void onSubmitInput(EditText et) {
        updateTaskGroup(getTextString(et, ""));
    }

    /**
     * 修改任务组json
     * {id: "1F82393CB0D511E6992300163E162ADD", name: "勿忘我1", valid: true}
     *
     * @return
     */
    private String getUpdateGroupJson(@NonNull String taskGroupName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", taskGroupEntity.id);
        jsonObject.addProperty("name", taskGroupName.trim());
        jsonObject.addProperty("valid", true);
        return jsonObject.toString();
    }

    /**
     * 更新任务组标题
     *
     * @param taskGroupName
     */
    private void updateTaskGroup(final String taskGroupName) {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskUpdate(RequestUtils.createJsonBody(getUpdateGroupJson(taskGroupName))),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            taskGroupEntity.name = taskGroupName.trim();
                        }
                        setResult(taskGroupEntity);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    protected boolean onCancelSubmitInput(EditText et) {
        return false;
    }

    private void setResult(TaskGroupEntity taskGroupEntity) {
        if (taskGroupEntity == null) {
            return;
        }
        Intent intent = getIntent();
        intent.putExtra(KEY_ACTIVITY_RESULT, taskGroupEntity);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
