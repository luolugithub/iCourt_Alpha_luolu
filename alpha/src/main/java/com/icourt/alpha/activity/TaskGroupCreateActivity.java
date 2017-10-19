package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.api.RequestUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  新建任务组
 * Company Beijing icourt
 * @author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class TaskGroupCreateActivity extends BaseActivity {
    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ENTITY = "entity";
    /**
     * 新建任务组
     */
    public static final int CREAT_TASK_GROUP_TYPE = 1;
    /**
     * 编辑任务组
     */
    public static final int UPDATE_TASK_GROUP_TYPE = 2;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.group_name_edittext)
    EditText groupNameEdittext;
    @BindView(R.id.edit_clear_tv)
    ImageView editClearTv;
    @BindView(R.id.edit_length_tv)
    TextView editLengthTv;

    @IntDef({CREAT_TASK_GROUP_TYPE,
            UPDATE_TASK_GROUP_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TASK_GROUP_TYPE {
    }

    String projectId;
    int type;
    TaskGroupEntity entity;

    public static void launchForResult(@NonNull Activity activity, @NonNull String projectId, @TASK_GROUP_TYPE int type, int requestCode) {
        if (activity == null) {return;}
        if (TextUtils.isEmpty(projectId)) {return;}
        Intent intent = new Intent(activity, TaskGroupCreateActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        intent.putExtra(KEY_TYPE, type);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchForResult(@NonNull Activity activity, @NonNull TaskGroupEntity entity, @TASK_GROUP_TYPE int type, int requestCode) {
        if (activity == null) {return;}
        Intent intent = new Intent(activity, TaskGroupCreateActivity.class);
        intent.putExtra(KEY_ENTITY, entity);
        intent.putExtra(KEY_TYPE, type);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task_group_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
        type = getIntent().getIntExtra(KEY_TYPE, -1);
        entity = (TaskGroupEntity) getIntent().getSerializableExtra(KEY_ENTITY);
        titleAction.setText(getString(R.string.task_finish));
        if (type == UPDATE_TASK_GROUP_TYPE) {
            setTitle(getString(R.string.task_edit_group));
            if (entity != null) {
                groupNameEdittext.setText(entity.name);
                groupNameEdittext.setSelection(groupNameEdittext.getText().length());
            }
        } else {
            setTitle(getString(R.string.task_new_group));
        }
        setSaveBtnState();
        groupNameEdittext.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        groupNameEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSaveBtnState();
            }
        });
    }

    /**
     * 设置保存按钮/clear/数量状态
     */
    private void setSaveBtnState() {
        int length = groupNameEdittext.getText().length();
        editLengthTv.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
        editClearTv.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
        editLengthTv.setText(String.format("%s/%s", length, 200));

        titleAction.setTextColor(length > 0 ? SystemUtils.getColor(this, R.color.alpha_font_color_orange) : SystemUtils.getColor(this, R.color.alpha_font_color_gray));
        titleAction.setClickable(length > 0);
    }

    @OnClick({R.id.titleAction,
            R.id.edit_clear_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                if (type == UPDATE_TASK_GROUP_TYPE) {
                    updateGroup();
                } else {
                    createGroup();
                }
                break;
            case R.id.edit_clear_tv:
                groupNameEdittext.setText("");
                setSaveBtnState();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 新建任务组
     */
    private void createGroup() {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskGroupCreate(RequestUtils.createJsonBody(getAddGroupJson())),
                new SimpleCallBack<TaskGroupEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskGroupEntity>> call, Response<ResEntity<TaskGroupEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            ProjectTaskGroupActivity.launchSetResult(TaskGroupCreateActivity.this, response.body().result);
                            TaskGroupCreateActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskGroupEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 修改任务组
     */
    private void updateGroup() {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskUpdate(RequestUtils.createJsonBody(updateGroupJson())),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            entity.name = groupNameEdittext.getText().toString();
                            ProjectTaskGroupActivity.launchSetResult(TaskGroupCreateActivity.this, entity);
                            TaskGroupCreateActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 获取添加任务组json
     * {name: "测试一", assignTo: "", type: 1, valid: 1, state: 0, matterId: "5EC208BAB0D311E6992300163E162ADD"}
     *
     * @return
     */
    private String getAddGroupJson() {
        String json = null;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", groupNameEdittext.getText().toString().trim());
            jsonObject.addProperty("assignTo", "");
            jsonObject.addProperty("type", 1);
            jsonObject.addProperty("valid", 1);
            jsonObject.addProperty("state", 0);
            jsonObject.addProperty("matterId", projectId);
            json = jsonObject.toString();
            return json;
        } catch (JsonIOException e) {
            e.printStackTrace();
            bugSync("获取添加任务组json失败", e);
        }
        return json;
    }

    /**
     * 修改任务组json
     * {id: "1F82393CB0D511E6992300163E162ADD", name: "勿忘我1", valid: true}
     *
     * @return
     */
    private String updateGroupJson() {
        String json = null;
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", entity.id);
            jsonObject.addProperty("name", groupNameEdittext.getText().toString().trim());
            jsonObject.addProperty("valid", true);
            json = jsonObject.toString();
            return json;
        } catch (JsonIOException e) {
            e.printStackTrace();
            bugSync("获取修改任务组json失败", e);
        }
        return json;
    }
}
