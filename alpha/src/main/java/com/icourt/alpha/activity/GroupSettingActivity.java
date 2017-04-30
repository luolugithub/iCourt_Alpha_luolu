package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupDetailEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.api.RequestUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/30
 * version 1.0.0
 */
public class GroupSettingActivity extends BaseActivity {
    private static final String KEY_GROUP = "key_group";
    private static final int CODE_REQUEST_NAME = 101;
    private static final int CODE_REQUEST_DESC = 102;

    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.group_name_tv)
    TextView groupNameTv;
    @BindView(R.id.group_name_ll)
    LinearLayout groupNameLl;
    @BindView(R.id.group_desc_tv)
    TextView groupDescTv;
    @BindView(R.id.group_desc_ll)
    LinearLayout groupDescLl;
    @BindView(R.id.group_set_private_switch)
    Switch groupSetPrivateSwitch;
    @BindView(R.id.group_set_private_ll)
    LinearLayout groupSetPrivateLl;
    @BindView(R.id.group_set_invite_switch)
    Switch groupSetInviteSwitch;
    @BindView(R.id.group_set_invite_ll)
    LinearLayout groupSetInviteLl;
    @BindView(R.id.group_set_look_switch)
    Switch groupSetLookSwitch;
    @BindView(R.id.group_set_look_msg_ll)
    LinearLayout groupSetLookMsgLl;
    @BindView(R.id.group_transfer_admin_ll)
    LinearLayout groupTransferAdminLl;
    GroupDetailEntity groupDetailEntity;

    public static void launch(@NonNull Context context, GroupDetailEntity groupDetailEntity) {
        if (context == null) return;
        if (groupDetailEntity == null) return;
        Intent intent = new Intent(context, GroupSettingActivity.class);
        intent.putExtra(KEY_GROUP, groupDetailEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("讨论组设置");
        Serializable serializableExtra = getIntent().getSerializableExtra(KEY_GROUP);
        if (serializableExtra instanceof GroupDetailEntity) {
            groupDetailEntity = (GroupDetailEntity) serializableExtra;
            groupNameTv.setText(groupDetailEntity.name);
            groupDescTv.setText(groupDetailEntity.intro);
            groupSetPrivateSwitch.setChecked(groupDetailEntity.is_private);
            groupSetInviteSwitch.setChecked(groupDetailEntity.member_invite);
            groupSetLookSwitch.setChecked(groupDetailEntity.chat_history);
        }
    }

    @OnClick({R.id.group_name_ll, R.id.group_desc_ll})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_name_ll:
                EditItemActivity.launchForResult(getActivity(),
                        "讨论组名称",
                        getTextString(groupNameTv, ""),
                        CODE_REQUEST_NAME);
                break;
            case R.id.group_desc_ll:
                EditItemActivity.launchForResult(getActivity(),
                        "讨论组目标",
                        getTextString(groupDescTv, ""),
                        CODE_REQUEST_DESC);
                break;
            case R.id.titleAction:
                updateGroupInfo();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_REQUEST_DESC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    groupDescTv.setText(data.getStringExtra(KEY_ACTIVITY_RESULT));
                }
                break;
            case CODE_REQUEST_NAME:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    groupNameTv.setText(data.getStringExtra(KEY_ACTIVITY_RESULT));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * 修改讨论组信息
     */
    private void updateGroupInfo() {
        JsonObject param = new JsonObject();
        param.addProperty("name", getTextString(groupNameTv, ""));
        param.addProperty("intro", getTextString(groupDescTv, ""));
        param.addProperty("is_private", getTextString(groupSetPrivateSwitch, ""));
        param.addProperty("member_invite", groupSetInviteSwitch.isChecked());
        param.addProperty("chat_history", groupSetLookSwitch.isChecked());
        showLoadingDialog(null);
        getApi().groupUpdate(groupDetailEntity.tid, RequestUtils.createJsonBody(param.toString()))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }
}
