package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.api.RequestUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.R.id.group_member_num_tv;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/26
 * version 1.0.0
 */
public class GroupCreateActivity extends BaseActivity {

    private static final int REQ_CODE_SELECT_USER = 1001;

    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.group_name_et)
    EditText groupNameEt;
    @BindView(R.id.group_desc_et)
    EditText groupDescEt;
    @BindView(R.id.group_private_switch)
    Switch groupPrivateSwitch;
    @BindView(R.id.group_disturb_ll)
    LinearLayout groupDisturbLl;
    @BindView(group_member_num_tv)
    TextView groupMemberNumTv;
    @BindView(R.id.group_member_invite_tv)
    TextView groupMemberInviteTv;
    @BindView(R.id.group_title_divider)
    View groupTitleDivider;
    @BindView(R.id.group_member_recyclerView)
    RecyclerView groupMemberRecyclerView;
    @BindView(R.id.group_member_arrow_iv)
    ImageView groupMemberArrowIv;
    IMContactAdapter imContactAdapter;
    DataChangeAdapterObserver dataChangeAdapterObserver = new DataChangeAdapterObserver() {
        @Override
        protected void updateUI() {
            groupMemberNumTv.setText(String.format("成员(%s)", imContactAdapter.getItemCount()));
        }
    };

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupCreateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("创建讨论组");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("完成");
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setAutoMeasureEnabled(true);
        groupMemberRecyclerView.setLayoutManager(linearLayoutManager);
        groupMemberRecyclerView.setNestedScrollingEnabled(false);
        groupMemberRecyclerView.setAdapter(imContactAdapter = new IMContactAdapter(Const.VIEW_TYPE_GRID));
        imContactAdapter.registerAdapterDataObserver(dataChangeAdapterObserver);
    }

    @OnClick({R.id.group_member_invite_tv, R.id.group_member_arrow_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                if (TextUtils.isEmpty(groupNameEt.getText())) {
                    showTopSnackBar("请输入讨论组名称!");
                    return;
                }
                if (groupNameEt.getText().length() < 2) {
                    showTopSnackBar("讨论组名称太短!");
                    return;
                }
                groupCreate(groupNameEt.getText().toString(),
                        TextUtils.isEmpty(groupDescEt.getText()) ? "" : groupDescEt.getText().toString(),
                        groupPrivateSwitch.isChecked());
                break;
            case R.id.group_member_invite_tv:
                ContactListActivity.launchSelect(getContext(),
                        Const.CHOICE_TYPE_MULTIPLE,
                        REQ_CODE_SELECT_USER);
                break;
            case R.id.group_member_arrow_iv:
             /*   ContactListActivity.launchSelect(getContext(),
                        REQ_CODE_SELECT_USER);*/
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 创建群组
     *
     * @param groupName
     * @param desc
     * @param is_private 是否私密
     */
    private void groupCreate(@NonNull String groupName,
                             @Nullable String desc,
                             boolean is_private) {
        showLoadingDialog(null);
        JsonObject groupJsonObject = new JsonObject();

        groupJsonObject.addProperty("name", groupName);
        groupJsonObject.addProperty("intro", desc);
        groupJsonObject.addProperty("is_private", is_private);
       /* groupJsonObject.addProperty("member_invite", true);
        groupJsonObject.addProperty("chat_history", true);*/

        JsonArray memberArray = new JsonArray();
        for (GroupContactBean groupContactBean : imContactAdapter.getData()) {
            if (groupContactBean != null) {
                memberArray.add(groupContactBean.accid);
            }
        }
        groupJsonObject.add("members", memberArray);

        getApi().groupCreate(RequestUtils.createJsonBody(groupJsonObject.toString()))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        showToast("创建成功");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_SELECT_USER:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    List<GroupContactBean> result = (List<GroupContactBean>) data.getSerializableExtra(KEY_ACTIVITY_RESULT);
                    if (result != null) {
                        imContactAdapter.bindData(true, result);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }
}
