package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectJudgeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.ClearEditText;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;

/**
 * Description  项目法官列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class ProjectJudgeActivity extends BaseActivity {
    private static final String KEY_LIST = "key_list";
    private static final String KEY_TYPE = "key_type";
    private static final String KEY_MKEY = "key_mkey";

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.header_comm_search_input_et)
    ClearEditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;
    private List list = new ArrayList<>();
    int type;
    String key;
    ProjectJudgeAdapter projectJudgeAdapter;
    HeaderFooterAdapter<ProjectJudgeAdapter> headerFooterAdapter;
    private CustomerDbService customerDbService = null;

    public static void launch(@NonNull Context context, String key,List list, int type) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectJudgeActivity.class);
        intent.putExtra(KEY_LIST, (Serializable) list);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_MKEY, key);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_members_layout);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        list = (List) getIntent().getSerializableExtra(KEY_LIST);
        type = getIntent().getIntExtra(KEY_TYPE, -1);
        key = getIntent().getStringExtra(KEY_MKEY);
        customerDbService = new CustomerDbService(LoginInfoUtils.getLoginUserId());
        setTitle(key);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        headerFooterAdapter = new HeaderFooterAdapter<>(projectJudgeAdapter = new ProjectJudgeAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);

        projectJudgeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                if (adapter instanceof ProjectJudgeAdapter) {
                    if (type == Const.PROJECT_JUDGE_TYPE ||//法官
                            type == Const.PROJECT_CLERK_TYPE ||//书记员
                            type == Const.PROJECT_ARBITRATORS_TYPE ||//仲裁员
                            type == Const.PROJECT_SECRETARIES_TYPE) {//仲裁秘书
                        TextView phoneview = holder.obtainView(R.id.judge_phone_tv);
                        callPhone(phoneview.getText());
                    } else if (type == Const.PROJECT_PERSON_TYPE) {//当事人
                        gotoContactActivity(adapter.getRealPos(position));
                    }
                }
            }
        });
        headerCommSearchInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    bindData();
                } else {
                    searchUserByName(s.toString());
                }
            }
        });
        headerCommSearchInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                        if (!TextUtils.isEmpty(headerCommSearchInputEt.getText())) {
                            searchUserByName(headerCommSearchInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        headerCommSearchInputLl.setVisibility(View.GONE);
        bindData();
    }

    private void bindData() {
        if (list != null) {
            projectJudgeAdapter.bindData(true, list);
        }
    }

    /**
     * 跳转到联系人详情
     *
     * @param position
     */
    private void gotoContactActivity(int position) {
        if (!hasCustomerPermission()) return;
        if (customerDbService == null) return;
        ProjectDetailEntity.LitigantsBean litigantsBean = (ProjectDetailEntity.LitigantsBean) projectJudgeAdapter.getItem(position);
        CustomerEntity customerEntity = null;
        CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", litigantsBean.contactPkid);
        if (customerDbModel == null) return;
        customerEntity = customerDbModel.convert2Model();
        if (customerEntity == null) return;
        if (!TextUtils.isEmpty(customerEntity.contactType)) {
            MobclickAgent.onEvent(getContext(), UMMobClickAgent.look_client_click_id);
            //公司
            if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "C")) {
                CustomerCompanyDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, false);
            } else if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "P")) {
                CustomerPersonDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, false);
            }
        }
    }

    /**
     * 搜索
     *
     * @param name
     */
    private void searchUserByName(String name) {
        if (TextUtils.isEmpty(name)) return;
        if (list != null) {
            List entities = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof ProjectDetailEntity.JudgeBean) {
                    ProjectDetailEntity.JudgeBean judge = (ProjectDetailEntity.JudgeBean) list.get(i);
                    if (judge.name.contains(name)) {
                        entities.add(judge);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.ClerkBean) {
                    ProjectDetailEntity.ClerkBean clerk = (ProjectDetailEntity.ClerkBean) list.get(i);
                    if (clerk.name.contains(name)) {
                        entities.add(clerk);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.ArbitratorBean) {
                    ProjectDetailEntity.ArbitratorBean arbitrator = (ProjectDetailEntity.ArbitratorBean) list.get(i);
                    if (arbitrator.name.contains(name)) {
                        entities.add(arbitrator);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.SecretarieBean) {
                    ProjectDetailEntity.SecretarieBean secretar = (ProjectDetailEntity.SecretarieBean) list.get(i);
                    if (secretar.name.contains(name)) {
                        entities.add(secretar);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.GroupsBean) {
                    ProjectDetailEntity.GroupsBean groupsBean = (ProjectDetailEntity.GroupsBean) list.get(i);
                    if (groupsBean.name.contains(name)) {
                        entities.add(groupsBean);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.LitigantsBean) {
                    ProjectDetailEntity.LitigantsBean litigantsBean = (ProjectDetailEntity.LitigantsBean) list.get(i);
                    if (litigantsBean.contactName.contains(name)) {
                        entities.add(litigantsBean);
                    }
                }
            }
            projectJudgeAdapter.bindData(true, entities);
        }
    }

    @OnClick({R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    /**
     * 打电话
     *
     * @param phone
     */
    private void callPhone(CharSequence phone) {
        if (!TextUtils.isEmpty(phone)) {
            if (!SystemUtils.checkPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE})) {
                SystemUtils.reqPermission(getActivity(), new String[]{Manifest.permission.CALL_PHONE,}, 12345);
            } else {
                SystemUtils.callPhone(getContext(), phone.toString());
            }
        }
    }

    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }
}
