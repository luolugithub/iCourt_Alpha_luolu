package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupMemberAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupDetailEntity;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.bean.GroupMemberEntity;
import com.icourt.alpha.entity.bean.SetTopEntity;
import com.icourt.alpha.entity.event.GroupActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  群组详情
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public class GroupDetailActivity extends BaseActivity {
    private static final String KEY_GROUP_ID = "key_group_id";
    private static final String KEY_TID = "key_tid";//云信id
    private static final String KEY_GROUP = "key_group";

    @BindView(R.id.group_name_tv)
    TextView groupNameTv;
    @BindView(R.id.group_desc_tv)
    TextView groupDescTv;
    @BindView(R.id.group_ding_tv)
    TextView groupDingTv;
    @BindView(R.id.group_file_tv)
    TextView groupFileTv;
    @BindView(R.id.group_member_iv)
    ImageView groupMemberIv;
    @BindView(R.id.group_member_num_tv)
    TextView groupMemberNumTv;
    @BindView(R.id.group_member_invite_tv)
    TextView groupMemberInviteTv;
    @BindView(R.id.group_title_divider)
    View groupTitleDivider;
    @BindView(R.id.group_member_recyclerView)
    RecyclerView groupMemberRecyclerView;
    @BindView(R.id.group_member_arrow_iv)
    ImageView groupMemberArrowIv;
    @BindView(R.id.group_setTop_switch)
    Switch groupSetTopSwitch;
    @BindView(R.id.group_setTop_ll)
    LinearLayout groupSetTopLl;
    @BindView(R.id.group_not_disturb_switch)
    Switch groupNotDisturbSwitch;
    @BindView(R.id.group_disturb_ll)
    LinearLayout groupDisturbLl;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    GroupEntity groupEntity;
    GroupMemberAdapter groupMemberAdapter;
    @BindView(R.id.group_join_or_quit_btn)
    Button groupJoinOrQuitBtn;

    public static void launch(@NonNull Context context, String groupId, String tid) {
        if (context == null) return;
        if (TextUtils.isEmpty(groupId)) return;
        if (TextUtils.isEmpty(tid)) return;
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_TID, tid);
        context.startActivity(intent);
    }

    public static void launch(@NonNull Context context, @NonNull GroupEntity groupEntity) {
        if (context == null) return;
        if (groupEntity == null) return;
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupEntity.id);
        intent.putExtra(KEY_TID, groupEntity.tid);
        intent.putExtra(KEY_GROUP, groupEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        Serializable serializableExtra = getIntent().getSerializableExtra(KEY_GROUP);
        if (serializableExtra instanceof GroupEntity) {
            groupEntity = (GroupEntity) serializableExtra;
            groupNameTv.setText(groupEntity.name);
            groupDescTv.setText(groupEntity.description);
        }
        groupMemberRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        groupMemberRecyclerView.setAdapter(groupMemberAdapter = new GroupMemberAdapter(GroupMemberAdapter.VIEW_TYPE_GRID));
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        getApi().getGroupByTid(getIntent().getStringExtra(KEY_TID))
                .enqueue(new SimpleCallBack<GroupDetailEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<GroupDetailEntity>> call, Response<ResEntity<GroupDetailEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            groupNameTv.setText(response.body().result.name);
                            groupDescTv.setText(response.body().result.description);
                            groupJoinOrQuitBtn.setSelected(response.body().result.isJoin == 1);
                            groupJoinOrQuitBtn.setText(groupJoinOrQuitBtn.isSelected() ? "退出讨论组" : "加入讨论组");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<GroupDetailEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
        getGroupMembers();
        getIsSetGroupTop();
        getIsSetGroupNoDisturbing();
    }

    /**
     * 获取讨论组成员列表
     */
    private void getGroupMembers() {
        getApi().getGroupMemeber(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<List<GroupMemberEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupMemberEntity>>> call, Response<ResEntity<List<GroupMemberEntity>>> response) {
                        groupMemberAdapter.bindData(true, response.body().result);
                        if (groupMemberNumTv != null) {
                            groupMemberNumTv.setText(String.format("成员(%s)", groupMemberAdapter.getItemCount()));
                        }
                    }
                });
    }

    @OnClick({R.id.group_ding_tv,
            R.id.group_file_tv,
            R.id.group_member_invite_tv,
            R.id.group_member_arrow_iv,
            R.id.group_setTop_switch,
            R.id.group_not_disturb_switch,
            R.id.group_join_or_quit_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_ding_tv:
                //TODO  钉的消息列表
                showTopSnackBar("未完成");
                break;
            case R.id.group_file_tv:
                //TODO  文件列表
                showTopSnackBar("未完成");
                break;
            case R.id.group_member_invite_tv:
                GroupMemberActivity.launch(getContext(),
                        getIntent().getStringExtra(KEY_GROUP_ID),
                        groupEntity != null ? groupEntity.name : null);
                break;
            case R.id.group_member_arrow_iv:
                GroupMemberActivity.launch(getContext(),
                        getIntent().getStringExtra(KEY_GROUP_ID),
                        groupEntity != null ? groupEntity.name : null);
                break;
            case R.id.group_setTop_switch:
                setGroupTop();
                break;
            case R.id.group_not_disturb_switch:
                setGroupNoDisturbing();
                break;
            case R.id.group_join_or_quit_btn:
                if (v.isSelected()) {
                    quitGroup();
                } else {
                    joinGroup();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取讨论组 是否被置顶
     */
    private void getIsSetGroupTop() {
        getApi().isGroupSetTop(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<Integer>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Integer>> call, Response<ResEntity<Integer>> response) {
                        groupSetTopSwitch.setChecked(response.body().result != null && response.body().result == 1);
                    }
                });
    }

    /**
     * 云信状态码  http://dev.netease.im/docs?doc=nim_status_code
     * 获取讨论组 是否免打扰
     */
    private void getIsSetGroupNoDisturbing() {
        NIMClient.getService(TeamService.class)
                .queryTeam(getIntent().getStringExtra(KEY_TID))
                .setCallback(new RequestCallback<Team>() {
                    @Override
                    public void onSuccess(Team param) {
                        groupNotDisturbSwitch.setChecked(param != null && param.mute());
                    }

                    @Override
                    public void onFailed(int code) {
                        log("-------->onFailed:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        log("-------->onException:" + exception);
                    }
                });
    }

    /**
     * 讨论组聊天置顶
     */
    private void setGroupTop() {
        showLoadingDialog(null);
        getApi().setGroupTop(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<List<SetTopEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<SetTopEntity>>> call, Response<ResEntity<List<SetTopEntity>>> response) {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<SetTopEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 讨论组聊天免打扰
     */
    private void setGroupNoDisturbing() {
        showLoadingDialog(null);
        getApi().setNoDisturbing(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<Integer>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Integer>> call, Response<ResEntity<Integer>> response) {
                        dismissLoadingDialog();
                        boolean checked = response.body().result != null && response.body().result == 1;
                        groupNotDisturbSwitch.setChecked(checked);
                        NIMClient.getService(TeamService.class).muteTeam(getIntent().getStringExtra(KEY_TID), checked);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Integer>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 加入该讨论组
     */
    private void joinGroup() {
        showLoadingDialog(null);
        getApi().joinGroup(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        getData(true);
                        EventBus.getDefault().post(
                                new GroupActionEvent(GroupActionEvent.GROUP_ACTION_JOIN, getIntent().getStringExtra(KEY_GROUP_ID)));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 退出讨论组
     */
    private void quitGroup() {
        showLoadingDialog(null);
        getApi().quitGroup(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<Integer>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Integer>> call, Response<ResEntity<Integer>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null
                                && response.body().result == 1) {
                            EventBus.getDefault().post(
                                    new GroupActionEvent(GroupActionEvent.GROUP_ACTION_QUIT, getIntent().getStringExtra(KEY_GROUP_ID)));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Integer>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }
}
