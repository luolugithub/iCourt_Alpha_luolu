package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.event.TaskActionEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icourt.alpha.entity.event.TaskActionEvent.TASK_UPDATE_DESC_ACTION;
import static com.icourt.alpha.entity.event.TaskActionEvent.TASK_UPDATE_NAME_ACTION;

/**
 * Description  修改任务描述
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class TaskDescUpdateActivity extends BaseActivity {
    private static final String KEY_TASK_UPDATE = "key_task_update";
    private static final String KEY_TASK_TYPE = "key_task_type";

    public static final int UPDATE_TASK_DESC = 1;
    public static final int UPDATE_TASK_NAME = 2;
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

    String descOrName;
    int type;
    @BindView(R.id.content_length_tv)
    TextView contentLengthTv;

    @IntDef({UPDATE_TASK_DESC,
            UPDATE_TASK_NAME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UPDATE_TASK_TYPE {

    }


    public static void launch(@NonNull Context context, @NonNull String descOrName, @UPDATE_TASK_TYPE int type) {
        if (context == null) return;
        Intent intent = new Intent(context, TaskDescUpdateActivity.class);
        intent.putExtra(KEY_TASK_UPDATE, descOrName);
        intent.putExtra(KEY_TASK_TYPE, type);
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

        descOrName = getIntent().getStringExtra(KEY_TASK_UPDATE);
        type = getIntent().getIntExtra(KEY_TASK_TYPE, -1);
        if (type == UPDATE_TASK_DESC) {
            setTitle("修改任务详情");
            descEditText.setHint("添加任务详情");
        } else if (type == UPDATE_TASK_NAME) {
            setTitle("修改任务名称");
            descEditText.setHint("添加任务名称");
            descEditText.setMaxEms(200);
            descEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
            contentLengthTv.setVisibility(View.VISIBLE);
            descEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    contentLengthTv.setText("0/" + descEditText.getMaxEms());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    contentLengthTv.setText(s.toString().length() + "/" + descEditText.getMaxEms());
                }
            });
        }
        descEditText.setText(descOrName);
        descEditText.setSelection(descEditText.getText().length());
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.titleAction:
                if (type == UPDATE_TASK_DESC) {
                    EventBus.getDefault().post(new TaskActionEvent(TASK_UPDATE_DESC_ACTION, null, descEditText.getText().toString()));
                } else if (type == UPDATE_TASK_NAME) {
                    EventBus.getDefault().post(new TaskActionEvent(TASK_UPDATE_NAME_ACTION, null, descEditText.getText().toString()));
                }

                this.finish();
                break;
        }
    }
}
