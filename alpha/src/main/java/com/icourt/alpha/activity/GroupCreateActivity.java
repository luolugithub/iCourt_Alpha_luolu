package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.fragment.dialogfragment.ContactSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.filter.LengthListenFilter;
import com.icourt.api.RequestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/26
 * version 1.0.0
 */
public class GroupCreateActivity extends ListenBackActivity implements OnFragmentCallBackListener {
    private static final int REQ_CODE_DEL_USER = 1002;
    private static final String KEY_CACHE_TITLE = String.format("%s_%s", GroupCreateActivity.class.getSimpleName(), "cacheTitle");
    private static final String KEY_CACHE_DESC = String.format("%s_%s", GroupCreateActivity.class.getSimpleName(), "cacheDesc");

    private static final int MAX_GROUP_NAME_LENGTH = 50;
    private static final int MAX_GROUP_DESC_LENGTH = 140;


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
            memberLayout.setVisibility(imContactAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            groupMemberNumTv.setText(String.format("成员(%s)", imContactAdapter.getItemCount()));
        }
    };
    @BindView(R.id.group_member_num_tv)
    TextView groupMemberNumTv;
    @BindView(R.id.member_layout)
    FrameLayout memberLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        ButterKnife.bind(this);
        initView();
    }

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupCreateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.chat_create_group);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        groupMemberRecyclerView.setLayoutManager(linearLayoutManager);
        groupMemberRecyclerView.setNestedScrollingEnabled(false);
        groupMemberRecyclerView.setAdapter(imContactAdapter = new IMContactAdapter(Const.VIEW_TYPE_GRID));
        memberLayout.setVisibility(View.GONE);
        imContactAdapter.registerAdapterDataObserver(dataChangeAdapterObserver);
        imContactAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                ArrayList<GroupContactBean> data = new ArrayList<GroupContactBean>(imContactAdapter.getData());
                if (!data.isEmpty()) {
                    data.remove(getMyAsContactBean());
                }
                GroupMemberDelActivity.launchForResult(getActivity(),
                        null,
                        data,
                        false,
                        REQ_CODE_DEL_USER);
            }
        });

        groupNameEt.setFilters(LengthListenFilter.createSingleInputFilter(new LengthListenFilter(MAX_GROUP_NAME_LENGTH) {
            @Override
            public void onInputOverLength(int maxLength) {
                showToast(String.format("讨论组名称不能超过%s个字符", maxLength));
            }
        }));
        groupDescEt.setFilters(LengthListenFilter.createSingleInputFilter(new LengthListenFilter(MAX_GROUP_DESC_LENGTH) {
            @Override
            public void onInputOverLength(int maxLength) {
                showToast(String.format("讨论组目标不能超过%s个字符", maxLength));
            }
        }));
        groupNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                titleAction.setEnabled(!StringUtils.isEmpty(s));
            }
        });

        //恢复记录的输入
        groupNameEt.setText(SpUtils.getTemporaryCache().getStringData(KEY_CACHE_TITLE, ""));
        groupNameEt.setSelection(StringUtils.length(groupNameEt.getText()));
        groupDescEt.setText(SpUtils.getTemporaryCache().getStringData(KEY_CACHE_DESC, ""));

        imContactAdapter.addItem(0, getMyAsContactBean());
    }

    private GroupContactBean getMyAsContactBean() {
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        if (loginUserInfo != null) {
            GroupContactBean my = new GroupContactBean();
            my.name = loginUserInfo.getName();
            my.pic = loginUserInfo.getPic();
            my.accid = StringUtils.toLowerCase(loginUserInfo.getUserId());
            return my;
        }
        return null;
    }

    @OnClick({R.id.group_member_invite_tv,
            R.id.group_member_arrow_iv,
            R.id.member_layout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                groupCreate(groupNameEt.getText().toString(),
                        getTextString(groupDescEt, ""),
                        groupPrivateSwitch.isChecked());
                break;
            case R.id.group_member_invite_tv:
                showMemberSelectDialogFragment();
                break;
            case R.id.member_layout:
            case R.id.group_member_arrow_iv:
                ArrayList<GroupContactBean> data = new ArrayList<>(imContactAdapter.getData());
                data.remove(getMyAsContactBean());
                GroupMemberDelActivity.launchForResult(getActivity(),
                        null,
                        data,
                        false,
                        REQ_CODE_DEL_USER);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected boolean onPageBackClick(@FROM_BACK int from) {
        //记录输入历史:名字和目标
        SpUtils.getTemporaryCache().putData(KEY_CACHE_TITLE, getTextString(groupNameEt, ""));
        SpUtils.getTemporaryCache().putData(KEY_CACHE_DESC, getTextString(groupDescEt, ""));
        return false;
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
        ArrayList<GroupContactBean> data = (ArrayList<GroupContactBean>) imContactAdapter.getData();
        ContactSelectDialogFragment.newInstance(data)
                .show(mFragTransaction, tag);
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
        GroupContactBean myAsContactBean = getMyAsContactBean();
        for (GroupContactBean groupContactBean : imContactAdapter.getData()) {
            if (groupContactBean != null &&
                    !groupContactBean.equals(myAsContactBean)) {
                memberArray.add(groupContactBean.accid);
            }
        }
        groupJsonObject.add("members", memberArray);

        callEnqueue(
                getChatApi().groupCreate(RequestUtils.createJsonBody(groupJsonObject.toString())),
                new SimpleCallBack<GroupEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<GroupEntity>> call, Response<ResEntity<GroupEntity>> response) {
                        dismissLoadingDialog();

                        //清除历史记录
                        SpUtils.getTemporaryCache().remove(KEY_CACHE_TITLE);
                        SpUtils.getTemporaryCache().remove(KEY_CACHE_DESC);

                        if (response.body().result != null) {
                            ChatActivity.launchTEAM(
                                    getContext(),
                                    response.body().result.tid,
                                    response.body().result.name,
                                    0,
                                    0, false);
                        } else {
                            showToast("创建成功");
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<GroupEntity>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                        if (t instanceof HttpException
                                && ((HttpException) t).code() == 400) {
                            showToast("资料库名字可能太长啦");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_DEL_USER:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    //删除后的数据
                    List<GroupContactBean> result = (List<GroupContactBean>) data.getSerializableExtra(KEY_ACTIVITY_RESULT);
                    if (result == null) {
                        result = Arrays.asList(getMyAsContactBean());
                    } else {
                        if (!result.contains(getMyAsContactBean())) {
                            result.add(0, getMyAsContactBean());
                        }
                    }
                    imContactAdapter.bindData(true, result);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof ContactSelectDialogFragment && params != null) {
            //选中的成员
            List<GroupContactBean> result = (List<GroupContactBean>) params.getSerializable(BaseDialogFragment.KEY_FRAGMENT_RESULT);
            if (result != null) {
                for (int i = result.size() - 1; i >= 0; i--) {
                    GroupContactBean contactBean = result.get(i);
                    if (imContactAdapter.getData().contains(contactBean)) {
                        result.remove(i);
                    }
                }
                imContactAdapter.addItems(result);
            }
        }
    }
}
