package com.icourt.alpha.activity;

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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectMemberAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icourt.alpha.constants.Const.PROJECT_ANYUAN_LAWYER_TYPE;
import static com.icourt.alpha.constants.Const.PROJECT_MEMBER_TYPE;

/**
 * Description  项目成员列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class ProjectMembersActivity extends BaseAppUpdateActivity {

    private static final String KEY_LIST = "key_list";
    private static final String KEY_TYPE = "key_type";
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
    ProjectMemberAdapter projectMemberAdapter;
    HeaderFooterAdapter<ProjectMemberAdapter> headerFooterAdapter;
    int type;

    public static void launch(@NonNull Context context, List list, int type) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectMembersActivity.class);
        intent.putExtra(KEY_LIST, (Serializable) list);
        intent.putExtra(KEY_TYPE, type);
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
            case PROJECT_ANYUAN_LAWYER_TYPE:
                return getString(R.string.project_attorneys);
            case PROJECT_MEMBER_TYPE:
                return getString(R.string.project_members);
        }
        return "";
    }

    @Override
    protected void initView() {
        super.initView();
        list = (List) getIntent().getSerializableExtra(KEY_LIST);
        type = getIntent().getIntExtra(KEY_TYPE, -1);
        setTitle(getTitleText());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<>(projectMemberAdapter = new ProjectMemberAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);

        projectMemberAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                if (adapter instanceof ProjectMemberAdapter) {
                    Object object = adapter.getItem(adapter.getRealPos(position));
                    if (object instanceof ProjectDetailEntity.MembersBean) {
                        ProjectDetailEntity.MembersBean membersBean = (ProjectDetailEntity.MembersBean) object;
                        if (!TextUtils.isEmpty(membersBean.userId))
                            showContactDialogFragment(membersBean.userId.toLowerCase());
                    } else if (object instanceof ProjectDetailEntity.AttorneysBean) {
                        ProjectDetailEntity.AttorneysBean attorneysBean = (ProjectDetailEntity.AttorneysBean) object;
                        if (!TextUtils.isEmpty(attorneysBean.attorneyPkid))
                            showContactDialogFragment(attorneysBean.attorneyPkid.toLowerCase());
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
                    projectMemberAdapter.bindData(true, list);
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
        if (list != null)
            projectMemberAdapter.bindData(true, list);
    }

    /**
     * 搜索
     *
     * @param name
     */
    private void searchUserByName(String name) {
        if (TextUtils.isEmpty(name)) return;
        if (list != null) {
            List memberEntities = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof ProjectDetailEntity.MembersBean) {
                    ProjectDetailEntity.MembersBean membersBean = (ProjectDetailEntity.MembersBean) list.get(i);
                    if (membersBean.userName.contains(name)) {
                        memberEntities.add(membersBean);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.AttorneysBean) {
                    ProjectDetailEntity.AttorneysBean attorneysBean = (ProjectDetailEntity.AttorneysBean) list.get(i);
                    if (attorneysBean.attorneyName.contains(name)) {
                        memberEntities.add(attorneysBean);
                    }
                }
            }
            projectMemberAdapter.bindData(true, memberEntities);
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
     * 展示联系人对话框
     *
     * @param accid
     */
    public void showContactDialogFragment(String accid) {
        SystemUtils.hideSoftKeyBoard(this);
        String tag = ContactDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(accid, getString(R.string.project_member_info), StringUtils.equalsIgnoreCase(accid, getLoginUserId(), false))
                .show(mFragTransaction, tag);
    }
}
