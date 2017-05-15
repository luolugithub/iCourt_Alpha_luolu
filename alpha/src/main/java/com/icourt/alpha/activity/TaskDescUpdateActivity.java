package com.icourt.alpha.activity;

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
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.event.TaskActionEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  修改任务描述
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class TaskDescUpdateActivity extends BaseActivity {
    private static final String KEY_TASK_DESC = "key_task_desc";
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.desc_editText)
    EditText descEditText;

    String taskDesc;

    public static void launch(@NonNull Context context, @NonNull String taskDesc) {
        if (context == null) return;
        Intent intent = new Intent(context, TaskDescUpdateActivity.class);
        intent.putExtra(KEY_TASK_DESC, taskDesc);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_desc_update_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("修改任务详情");
        taskDesc = getIntent().getStringExtra(KEY_TASK_DESC);
        if (!TextUtils.isEmpty(taskDesc)) {
            descEditText.setText(taskDesc);
        }
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_UPDATE_ACTION, null, descEditText.getText().toString()));
                this.finish();
                break;
        }
    }
}
