package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.GroupDetailEntity;
import com.icourt.alpha.entity.event.GroupActionEvent;
import com.icourt.alpha.entity.event.NoDisturbingEvent;
import com.icourt.alpha.entity.event.SetTopEvent;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ContactSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.api.RequestUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.TeamService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description  群组详情
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public class GroupDetailActivity extends BaseActivity
        implements BaseRecyclerAdapter.OnItemClickListener, OnFragmentCallBackListener {
    private static final String KEY_TID = "key_tid";//云信id


    @BindView(R.id.group_name_tv)
    TextView groupNameTv;
    @BindView(R.id.group_desc_tv)
    TextView groupDescTv;
    @BindView(R.id.group_ding_tv)
    TextView groupDingTv;
    @BindView(R.id.group_file_tv)
    TextView groupFileTv;
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
    IMContactAdapter contactAdapter;
    @BindView(R.id.group_join_or_quit_btn)
    Button groupJoinOrQuitBtn;
    final ArrayList<GroupContactBean> groupContactBeens = new ArrayList<>();
    ContactDbService contactDbService;
    GroupDetailEntity groupDetailEntity;
    boolean isAdmin;
    boolean joined;
    @BindView(R.id.group_data_ll)
    LinearLayout groupDataLl;
    @BindView(R.id.group_session_action_ll)
    LinearLayout groupSessionActionLl;


    public static void launchTEAM(@NonNull Context context, String tid) {
        if (context == null) return;
        if (TextUtils.isEmpty(tid)) return;
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(KEY_TID, tid);
        context.startActivity(intent);
    }

    protected String getIMChatId() {
        return getIntent().getStringExtra(KEY_TID);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        contactDbService = new ContactDbService(getLoginUserId());
        ImageView titleActionImage = getTitleActionImage();
        setViewVisible(titleActionImage, false);
        setViewVisible(groupMemberInviteTv, false);
        setViewVisible(groupJoinOrQuitBtn, false);
        setViewVisible(groupDataLl, false);
        setViewVisible(groupSessionActionLl, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        groupMemberRecyclerView.setLayoutManager(linearLayoutManager);
        groupMemberRecyclerView.setAdapter(contactAdapter = new IMContactAdapter(Const.VIEW_TYPE_GRID));
        contactAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().groupQueryDetail(getIntent().getStringExtra(KEY_TID)),
                new SimpleCallBack<GroupDetailEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<GroupDetailEntity>> call, Response<ResEntity<GroupDetailEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            groupDetailEntity = response.body().result;
                            groupNameTv.setText(response.body().result.name);
                            groupDescTv.setText(response.body().result.intro);
                            ImageView titleActionImage = getTitleActionImage();

                            isAdmin = StringUtils.equalsIgnoreCase(getLoginUserId(), response.body().result.admin_id, false);
                            if (isAdmin) {
                                //管理员设置按钮展示
                                setViewVisible(titleActionImage, true);
                                setViewVisible(groupJoinOrQuitBtn, false);
                                setViewVisible(groupMemberInviteTv, true);
                                setViewVisible(groupSessionActionLl, true);
                                setViewVisible(groupDataLl, true);
                            } else {
                                //非管理员
                                setViewVisible(titleActionImage, false);
                                joined = StringUtils.containsIgnoreCase(response.body().result.members, getLoginUserId());
                                setViewVisible(groupJoinOrQuitBtn, true);
                                groupJoinOrQuitBtn.setText(joined ? "退出讨论组" : "加入讨论组");
                                setViewVisible(groupSessionActionLl, joined);
                                setViewVisible(groupDataLl, joined);
                                setViewVisible(groupMemberInviteTv, joined && response.body().result.member_invite);
                            }
                            //查询本地uid对应的头像
                            queryMembersByUids(response.body().result.members);
                            groupMemberNumTv.setText(String.format("成员(%s)", groupDetailEntity.members != null ? groupDetailEntity.members.size() : 0));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<GroupDetailEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
        getSetTopSessions();
        getIsSetGroupNoDisturbing();
    }


    /**
     * 根据uid 查询本地联系人
     *
     * @param members
     */
    private void queryMembersByUids(List<String> members) {
        if (members != null) {
            groupContactBeens.clear();
            if (contactDbService != null) {
                //最多展示20个
                for (int i = 0; i < members.size(); i++) {
                    String uid = members.get(i);
                    if (!TextUtils.isEmpty(uid)) {
                        ContactDbModel contactDbModel = contactDbService.queryFirst("accid", uid);
                        if (contactDbModel != null) {
                            groupContactBeens.add(contactDbModel.convert2Model());
                        }
                    }
                }
            }
            contactAdapter.bindData(true, groupContactBeens);
        }
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
                ChatMsgClassfyActivity.launch(getContext(),
                        ChatMsgClassfyActivity.MSG_CLASSFY_CHAT_DING,
                        CHAT_TYPE_TEAM,
                        getIntent().getStringExtra(KEY_TID));
                break;
            case R.id.group_file_tv:
                ChatMsgClassfyActivity.launch(getContext(),
                        ChatMsgClassfyActivity.MSG_CLASSFY_CHAT_FILE,
                        CHAT_TYPE_TEAM,
                        getIntent().getStringExtra(KEY_TID));
                break;
            case R.id.group_member_invite_tv:
                showMemberSelectDialogFragment();
                break;
            case R.id.group_member_arrow_iv:
                gotoMembersList();
                break;
            case R.id.group_setTop_switch:
                if (!groupSetTopSwitch.isChecked()) {
                    setGroupTopCancel();
                } else {
                    setGroupTop();
                }
                break;
            case R.id.group_not_disturb_switch:
                if (!groupNotDisturbSwitch.isChecked()) {
                    setGroupNoDisturbingCancel();
                } else {
                    setGroupNoDisturbing();
                }
                break;
            case R.id.group_join_or_quit_btn:
                if (joined) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("提示")
                            .setMessage("是否离开讨论组?")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    quitGroup();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    joinGroup();
                }
                break;
            case R.id.titleAction:
                GroupSettingActivity.launch(getContext(), groupDetailEntity);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 到成员列表
     */
    private void gotoMembersList() {
        if (groupDetailEntity == null) return;
        if (isAdmin) {
            ArrayList<GroupContactBean> contactBeanArrayList = new ArrayList<>(contactAdapter.getData());

            //移除本人
            String loginUserId = getLoginUserId();
            if (!TextUtils.isEmpty(loginUserId)) {
                loginUserId = loginUserId.toLowerCase();
            }
            GroupContactBean contactBean = new GroupContactBean();
            contactBean.accid = loginUserId;
            contactBeanArrayList.remove(contactBean);
            GroupMemberDelActivity.launchForResult(getActivity(),
                    getIntent().getStringExtra(KEY_TID),
                    contactBeanArrayList,
                    true, 2001);
        } else {
            GroupMemberListActivity.launch(getContext(),
                    groupDetailEntity.tid,
                    false,
                    groupDetailEntity.admin_id);
        }
    }

    /**
     * 展示选择成员对话框
     */
    public void showMemberSelectDialogFragment() {
        String tag = ContactSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactSelectDialogFragment.newInstance((ArrayList<GroupContactBean>) contactAdapter.getData())
                .show(mFragTransaction, tag);
    }


    /**
     * 获取所有置顶的会话ids
     */
    private void getSetTopSessions() {
        callEnqueue(
                getChatApi().sessionQueryAllsetTopIds(),
                new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            boolean isTop = response.body()
                                    .result.contains(getIMChatId());
                            groupSetTopSwitch.setChecked(isTop);
                        } else {
                            groupSetTopSwitch.setChecked(false);
                        }
                    }
                });
    }


    /**
     * 云信状态码  http://dev.netease.im/docs?doc=nim_status_code
     * 获取讨论组 是否免打扰
     */
    private void getIsSetGroupNoDisturbing() {
        //先拿网络 保持三端一致
        callEnqueue(
                getChatApi().sessionQueryAllNoDisturbingIds(),
                new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            groupNotDisturbSwitch.setChecked(response.body().result.contains(getIMChatId()));
                        } else {
                            groupNotDisturbSwitch.setChecked(false);
                        }
                    }
                });
    }

    /**
     * 讨论组聊天置顶
     */
    private void setGroupTop() {
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().sessionSetTop(CHAT_TYPE_TEAM, getIMChatId()),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            groupSetTopSwitch.setChecked(true);
                            broadSetTopEvent();
                        } else {
                            groupSetTopSwitch.setChecked(false);
                            broadSetTopEvent();
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
     *
     */
    private void setGroupTopCancel() {
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().sessionSetTopCancel(CHAT_TYPE_TEAM, getIMChatId()),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            groupSetTopSwitch.setChecked(false);
                            broadSetTopEvent();
                        } else {
                            groupSetTopSwitch.setChecked(true);
                            broadSetTopEvent();
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
     * 讨论组聊天免打扰
     */
    private void setGroupNoDisturbing() {
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().sessionNoDisturbing(CHAT_TYPE_TEAM, getIMChatId()),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result) {
                            groupNotDisturbSwitch.setChecked(true);
                            NIMClient.getService(TeamService.class).muteTeam(getIntent().getStringExtra(KEY_TID), true);
                            broadNoDisturbingEvent();
                        } else {
                            groupNotDisturbSwitch.setChecked(false);
                            NIMClient.getService(TeamService.class).muteTeam(getIntent().getStringExtra(KEY_TID), false);
                            broadNoDisturbingEvent();
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
     * 广播通知其它页面更新置顶
     */
    private void broadSetTopEvent() {
        if (groupNotDisturbSwitch == null) return;
        EventBus.getDefault().post(new SetTopEvent(groupSetTopSwitch.isChecked(), getIMChatId()));
    }

    /**
     * 广播通知其它页面消息免打扰
     */
    private void broadNoDisturbingEvent() {
        if (groupNotDisturbSwitch == null) return;
        EventBus.getDefault().post(new NoDisturbingEvent(groupNotDisturbSwitch.isChecked(), getIMChatId()));
    }

    /**
     * 讨论组聊天取消免打扰
     */
    private void setGroupNoDisturbingCancel() {
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().sessionNoDisturbingCancel(CHAT_TYPE_TEAM, getIMChatId()),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result) {
                            groupNotDisturbSwitch.setChecked(false);
                            NIMClient.getService(TeamService.class).muteTeam(getIntent().getStringExtra(KEY_TID), false);
                            broadNoDisturbingEvent();
                        } else {
                            groupNotDisturbSwitch.setChecked(true);
                            NIMClient.getService(TeamService.class).muteTeam(getIntent().getStringExtra(KEY_TID), true);
                            broadNoDisturbingEvent();
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
     * 加入该讨论组
     */
    private void joinGroup() {
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().groupJoin(getIntent().getStringExtra(KEY_TID)),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        getData(true);
                        showTopSnackBar("加入成功");
                        EventBus.getDefault().post(
                                new GroupActionEvent(GroupActionEvent.GROUP_ACTION_JOIN, getIntent().getStringExtra(KEY_TID)));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
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
        callEnqueue(
                getChatApi().groupQuit(getIntent().getStringExtra(KEY_TID)),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            NIMClient.getService(MsgService.class)
                                    .clearChattingHistory(getIMChatId(), SessionTypeEnum.Team);
                            EventBus.getDefault().post(
                                    new GroupActionEvent(GroupActionEvent.GROUP_ACTION_QUIT, getIntent().getStringExtra(KEY_TID)));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }


    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof ContactSelectDialogFragment && params != null) {
            List<GroupContactBean> result = (List<GroupContactBean>) params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (result != null) {
                invitationMembers(result);
            }
        }
    }

    /**
     * 邀请成员
     *
     * @param contactBeanArrayList
     */
    private void invitationMembers(List<GroupContactBean> contactBeanArrayList) {
        if (contactBeanArrayList == null) return;
        JsonArray userIdArray = new JsonArray();//使用accid
        for (GroupContactBean groupContactBean : contactBeanArrayList) {
            if (groupContactBean != null) {
                userIdArray.add(groupContactBean.accid);
            }
        }
        JsonObject param = new JsonObject();
        param.add("members", userIdArray);
        String paramJsonStr = null;
        try {
            paramJsonStr = JsonUtils.Gson2String(param);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        showLoadingDialog(null);
        callEnqueue(
                getChatApi().groupMemberAdd(getIntent().getStringExtra(KEY_TID), RequestUtils.createJsonBody(paramJsonStr)),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        GroupContactBean item = contactAdapter.getItem(position);
        if (item == null) return;
//        showContactDialogFragment(item.accid, StringUtils.equalsIgnoreCase(item.accid, getLoginUserId(), false));
        gotoMembersList();
    }

    /**
     * 展示联系人对话框
     *
     * @param accid
     * @param hiddenChatBtn
     */
    public void showContactDialogFragment(String accid, boolean hiddenChatBtn) {
        String tag = ContactDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(accid, "成员资料", hiddenChatBtn)
                .show(mFragTransaction, tag);
    }
}
