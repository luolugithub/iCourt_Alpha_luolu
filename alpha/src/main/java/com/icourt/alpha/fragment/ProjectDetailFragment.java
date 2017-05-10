package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectMembersAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 项目概览
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectDetailFragment extends BaseFragment {

    private static final String KEY_PROJECT_ID = "key_project_id";
    Unbinder unbinder;
    @BindView(R.id.project_name)
    TextView projectName;
    @BindView(R.id.project_client)
    TextView projectClient;
    @BindView(R.id.project_type)
    TextView projectType;
    @BindView(R.id.project_branch)
    TextView projectBranch;
    @BindView(R.id.project_member_recyclerview)
    RecyclerView projectMemberRecyclerview;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.project_member_count)
    TextView projectMemberCount;
    @BindView(R.id.project_add_routine)
    ImageView projectAddRoutine;
    @BindView(R.id.project_viewpager)
    ViewPager projectViewpager;

    private String projectId;
    ProjectMembersAdapter projectMemberAdapter;
    BaseFragmentAdapter baseFragmentAdapter;
    OnFragmentCallBackListener onFragmentCallBackListener;

    public static ProjectDetailFragment newInstance(@NonNull String projectId) {
        ProjectDetailFragment projectDetailFragment = new ProjectDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        projectDetailFragment.setArguments(bundle);
        return projectDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_project_detail_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentCallBackListener = (OnFragmentCallBackListener) context;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString(KEY_PROJECT_ID);

        refreshLayout.setMoveForHorizontal(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        projectMemberRecyclerview.setLayoutManager(layoutManager);
        projectMemberRecyclerview.addItemDecoration(ItemDecorationUtils.getCommFull5VerticalDivider(getContext(), true));
        projectMemberRecyclerview.setHasFixedSize(true);
        projectMemberRecyclerview.setAdapter(projectMemberAdapter = new ProjectMembersAdapter());
        projectMemberAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, projectMemberAdapter));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    /**
     * 设置数据
     *
     * @param projectDetailBean
     */
    private void setDataToView(ProjectDetailEntity projectDetailBean) {
        if (projectName == null)
            return;

        if (projectDetailBean != null) {
            projectName.setText(projectDetailBean.name);
            if (onFragmentCallBackListener != null) {
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(projectDetailBean.myStar)) {
                    bundle.putInt("myStar", 0);
                } else {
                    bundle.putInt("myStar", Integer.valueOf(projectDetailBean.myStar));
                }
                onFragmentCallBackListener.onFragmentCallBack(this,0, bundle);
            }
            if (projectDetailBean.clients != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ProjectDetailEntity.ClientsBean clientsBean : projectDetailBean.clients) {
                    stringBuilder.append(clientsBean.contactName + " ");
                }
                projectClient.setText(stringBuilder.toString());
            }
            projectType.setText(projectDetailBean.matterTypeName);
            if (projectDetailBean.groups != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ProjectDetailEntity.GroupsBean groupBean : projectDetailBean.groups) {
                    stringBuilder.append(groupBean.getName() + " ");
                }
                projectBranch.setText(stringBuilder.toString());
            }

            if (projectDetailBean.members != null) {
                projectMemberCount.setText("项目成员（" + projectDetailBean.members.size() + "）");
                projectMemberAdapter.bindData(false, projectDetailBean.members);
            }
            baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
            projectViewpager.setAdapter(baseFragmentAdapter);
            baseFragmentAdapter.bindData(true,
                    Arrays.asList(
                            ProjectRangeFragment.newInstance(projectDetailBean)
                    ));
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        getApi().projectDetail(projectId).enqueue(new SimpleCallBack<List<ProjectDetailEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<ProjectDetailEntity>>> call, Response<ResEntity<List<ProjectDetailEntity>>> response) {
                stopRefresh();
                if (response.body().result != null) {
                    if (response.body().result.size() > 0) {
                        setDataToView(response.body().result.get(0));
                    }
                }
            }
        });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.project_add_routine})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_add_routine:
                showTopSnackBar("添加程序信息");
                break;
        }
    }
}
