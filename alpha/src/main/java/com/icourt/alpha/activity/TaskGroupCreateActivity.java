package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

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
 * Description  新建任务组
 * Company Beijing icourt
 *
 * @author lu.zhao  E-mail:zhaolu@icourt.cc
 *         date createTime：17/5/9
 *         version 2.0.0
 */

public class TaskGroupCreateActivity extends EditItemBaseActivity {

    private static final String KEY_PROJECT_ID = "key_project_id";

    public static void launchForResult(@NonNull Activity activity,
                                       @NonNull String projectId,
                                       int requestCode) {
        if (activity == null || TextUtils.isEmpty(projectId)) {
            return;
        }
        Intent intent = new Intent(activity, TaskGroupCreateActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        activity.startActivityForResult(intent, requestCode);
    }

    private String projectId;

    @Override
    protected void initView() {
        super.initView();
        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
        setTitle(R.string.task_new_group);
        inputNameEt.setFilters(new InputFilter[]{
                new ReturnKeyFilter(),
                new InputFilter.LengthFilter(getMaxInputLimitNum())});
        inputNameEt.setHint(R.string.task_group_name_hint);
        inputNameEt.setOnEditorActionListener(new InputActionNextFilter() {
            @Override
            public boolean onInputActionNext(TextView v) {
                SystemUtils.hideSoftKeyBoard(getActivity(), v, true);
                return super.onInputActionNext(v);
            }
        });
    }

    @Override
    protected int getMaxInputLimitNum() {
        return TaskConfig.TASK_GROUP_NAME_MAX_LENGTH;
    }

    @Override
    protected void onSubmitInput(EditText et) {
        taskGroupCreate(getTextString(et, ""));
    }

    @Override
    protected boolean onCancelSubmitInput(EditText et) {
        return false;
    }

    /**
     * 获取添加任务组json
     * {name: "测试一", assignTo: "", type: 1, valid: 1, state: 0, matterId: "5EC208BAB0D311E6992300163E162ADD"}
     *
     * @return
     */
    private String getAddGroupJson(@NonNull String taskGroupName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", taskGroupName.trim());
        jsonObject.addProperty("assignTo", "");
        jsonObject.addProperty("type", 1);
        jsonObject.addProperty("valid", 1);
        jsonObject.addProperty("state", 0);
        jsonObject.addProperty("matterId", projectId);
        return jsonObject.toString();
    }

    /**
     * 任务组创建
     */
    private void taskGroupCreate(@NonNull String taskGroupName) {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskGroupCreate(RequestUtils.createJsonBody(getAddGroupJson(taskGroupName))),
                new SimpleCallBack<TaskGroupEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskGroupEntity>> call, Response<ResEntity<TaskGroupEntity>> response) {
                        dismissLoadingDialog();
                        setResult(response.body().result);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskGroupEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
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
