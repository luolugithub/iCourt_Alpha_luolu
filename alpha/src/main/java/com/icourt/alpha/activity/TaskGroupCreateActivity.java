package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.api.RequestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  新建任务组
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class TaskGroupCreateActivity extends BaseActivity {
    private static final String KEY_PROJECT_ID = "key_project_id";
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

    String projectId;

    public static void launch(@NonNull Context context, @NonNull String projectId) {
        if (context == null) return;
        if (TextUtils.isEmpty(projectId)) return;
        Intent intent = new Intent(context, TaskGroupCreateActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        context.startActivity(intent);
    }

    public static void launchForResult(@NonNull Activity activity, @NonNull String projectId, int requestCode) {
        if (activity == null) return;
        if (TextUtils.isEmpty(projectId)) return;
        Intent intent = new Intent(activity, TaskGroupCreateActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
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
        setTitle("新建任务组");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                createGroup();
                break;
        }
    }

    /**
     * 新建任务组
     */
    private void createGroup() {
        showLoadingDialog("正在新建任务组...");
        getApi().taskGroupCreate(RequestUtils.createJsonBody(getAddGroupJson())).enqueue(new SimpleCallBack<TaskGroupEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskGroupEntity>> call, Response<ResEntity<TaskGroupEntity>> response) {
                if (response.body().result != null) {
                    dismissLoadingDialog();
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
        }
        return json;
    }
}
