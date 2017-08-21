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
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  项目法官列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class ProjectJudgeActivity extends BaseAppUpdateActivity {

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
    ProjectJudgeAdapter projectJudgeAdapter;
    HeaderFooterAdapter<ProjectJudgeAdapter> headerFooterAdapter;

    public static void launch(@NonNull Context context, List list, int type) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectJudgeActivity.class);
        intent.putExtra("list", (Serializable) list);
        intent.putExtra("type", type);
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

    private String getTitleText() {
        switch (type) {
            case Const.PROJECT_JUDGE_TYPE://法官
                return "法官";
            case Const.PROJECT_CLERK_TYPE://书记员
                return "书记员";
            case Const.PROJECT_ARBITRATORS_TYPE://仲裁员
                return "仲裁员";
            case Const.PROJECT_SECRETARIES_TYPE://仲裁秘书
                return "仲裁秘书";
        }
        return "";
    }

    @Override
    protected void initView() {
        super.initView();
//        judgeBeens = (List<ProjectDetailEntity.JudgeBean>) getIntent().getSerializableExtra("judgeBeens");
//        clerkBeens = (List<ProjectDetailEntity.ClerkBean>) getIntent().getSerializableExtra("clerkBeens");
        list = (List) getIntent().getSerializableExtra("list");
        type = getIntent().getIntExtra("type", -1);
        setTitle(getTitleText());
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
                    TextView phoneview = holder.obtainView(R.id.judge_phone_tv);
                    callPhone(phoneview.getText());
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
}
