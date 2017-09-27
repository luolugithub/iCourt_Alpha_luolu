package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupContactBean;
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
    private static final int CODE_REQUEST_TRANSFER_ADMIN = 103;

    @BindView(R.id.titleContent)
    TextView titleContent;
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
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.group_set_private_child_per_ll)
    LinearLayout groupSetPrivateChildPerLl;

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
            if (groupDetailEntity.is_private)//私密
            {
                groupSetPrivateSwitch.setChecked(true);
                groupSetInviteSwitch.setChecked(groupDetailEntity.member_invite);
                groupSetLookSwitch.setChecked(groupDetailEntity.chat_history);
                groupSetPrivateChildPerLl.setVisibility(View.VISIBLE);
            } else {
                groupSetPrivateSwitch.setChecked(false);
                groupSetPrivateChildPerLl.setVisibility(View.GONE);
                groupSetInviteSwitch.setChecked(groupDetailEntity.member_invite);
                //groupSetLookSwitch.setChecked(groupDetailEntity.chat_history);
                groupSetLookSwitch.setChecked(false);
            }
        }
    }

    @OnClick({R.id.group_name_ll,
            R.id.group_desc_ll,
            R.id.group_transfer_admin_ll,
            R.id.group_set_private_switch,
            R.id.group_set_invite_switch,
            R.id.group_set_look_switch})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_name_ll:
                GroupChangeNameActivity.launchForResult(
                        getActivity(),
                        CODE_REQUEST_NAME,
                        groupDetailEntity);
                break;
            case R.id.group_desc_ll:
                GroupChangeDescActivity.launchForResult(
                        getActivity(),
                        CODE_REQUEST_DESC,
                        groupDetailEntity);
                break;
            case R.id.group_transfer_admin_ll:
                if (groupDetailEntity == null) return;
                GroupMemberListActivity.launchSelect(getActivity(),
                        groupDetailEntity.tid,
                        Const.CHOICE_TYPE_SINGLE,
                        CODE_REQUEST_TRANSFER_ADMIN,
                        false,
                        null, true);
                break;
            case R.id.group_set_private_switch:
                groupDetailEntity.is_private=groupSetPrivateSwitch.isChecked();
                groupSetPrivateChildPerLl.setVisibility(!groupSetPrivateSwitch.isChecked() ? View.GONE : View.VISIBLE);
                if (groupSetPrivateSwitch.isChecked()) {
                    groupDetailEntity.member_invite = true;
                    groupDetailEntity.chat_history = false;

                    groupSetInviteSwitch.setChecked(groupDetailEntity.member_invite);
                    groupSetLookSwitch.setChecked(groupDetailEntity.chat_history);
                }
                updateGroupInfo();
                break;
            case R.id.group_set_invite_switch://邀请开关
                groupDetailEntity.member_invite = groupSetInviteSwitch.isChecked();
                updateGroupInfo();
                break;
            case R.id.group_set_look_switch://查看加入前的消息
                groupDetailEntity.chat_history = groupSetLookSwitch.isChecked();
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
                    Serializable serializableExtra = data.getSerializableExtra(KEY_ACTIVITY_RESULT);
                    if (serializableExtra instanceof GroupDetailEntity) {
                        groupDetailEntity.intro = ((GroupDetailEntity) serializableExtra).intro;
                        groupDescTv.setText(groupDetailEntity.intro);
                    }
                }
                break;
            case CODE_REQUEST_NAME:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Serializable serializableExtra = data.getSerializableExtra(KEY_ACTIVITY_RESULT);
                    if (serializableExtra instanceof GroupDetailEntity) {
                        groupDetailEntity.name = ((GroupDetailEntity) serializableExtra).name;
                        groupNameTv.setText(groupDetailEntity.name);
                    }
                }
                break;
            case CODE_REQUEST_TRANSFER_ADMIN:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Parcelable serializableExtra = data.getParcelableExtra(KEY_ACTIVITY_RESULT);
                    if (serializableExtra instanceof GroupContactBean) {
                        showTransferAdminDialog((GroupContactBean) serializableExtra);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    /**
     * 展示确认转让管理员对话框
     *
     * @param target
     */
    private void showTransferAdminDialog(final GroupContactBean target) {
        if (target == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("转让管理员")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transferAdmin(target);
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    /**
     * 转让管理员
     *
     * @param target
     */
    private void transferAdmin(GroupContactBean target) {
        if (target == null) return;
        if (groupDetailEntity == null) return;
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().groupTransferAdmin(groupDetailEntity.tid, target.accid),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            finish();
                        } else {
                            showTopSnackBar("转让失败!");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });

    }

    /**
     * 修改讨论组信息
     */
    private void updateGroupInfo() {
        JsonObject param = new JsonObject();
        param.addProperty("name", getTextString(groupNameTv, ""));
        param.addProperty("intro", getTextString(groupDescTv, ""));
        if (groupSetPrivateSwitch.isChecked()) {
            param.addProperty("is_private", groupDetailEntity.is_private);
            param.addProperty("member_invite", groupDetailEntity.member_invite);
            param.addProperty("chat_history", groupDetailEntity.chat_history);
        } else {
            param.addProperty("is_private", false);
            param.addProperty("member_invite", true);
            param.addProperty("chat_history", false);
        }
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().groupUpdate(groupDetailEntity.tid, RequestUtils.createJsonBody(param.toString())),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }
}
