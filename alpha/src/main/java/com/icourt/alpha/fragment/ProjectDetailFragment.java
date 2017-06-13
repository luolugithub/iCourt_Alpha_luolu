package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectBasicInfoAdapter;
import com.icourt.alpha.adapter.ProjectMembersAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.entity.event.ProjectActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.WrapContentHeightViewPager;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
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
    @BindView(R.id.project_member_recyclerview)
    RecyclerView projectMemberRecyclerview;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.project_member_count)
    TextView projectMemberCount;
    @BindView(R.id.project_add_routine)
    ImageView projectAddRoutine;
    @BindView(R.id.project_viewpager)
    WrapContentHeightViewPager projectViewpager;
    @BindView(R.id.basic_top_recyclerview)
    RecyclerView basicTopRecyclerview;
    @BindView(R.id.project_member_layout)
    LinearLayout projectMemberLayout;
    @BindView(R.id.project_service_content)
    TextView projectServiceContent;
    @BindView(R.id.service_content_layout)
    LinearLayout serviceContentLayout;
    @BindView(R.id.procedure_layout)
    LinearLayout procedureLayout;

    private String projectId;
    ProjectMembersAdapter projectMemberAdapter;
    ProjectBasicInfoAdapter projectBasicInfoAdapter;
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

        basicTopRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        basicTopRecyclerview.addItemDecoration(ItemDecorationUtils.getCommMagin5Divider(getContext(), false));
        basicTopRecyclerview.setAdapter(projectBasicInfoAdapter = new ProjectBasicInfoAdapter());

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
        if (projectMemberLayout == null) return;
        if (projectDetailBean != null) {
            EventBus.getDefault().post(new ProjectActionEvent(ProjectActionEvent.PROJECT_TIMER_ACTION, projectDetailBean.sumTime));
            if (onFragmentCallBackListener != null) {
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(projectDetailBean.myStar)) {
                    bundle.putInt("myStar", 0);
                } else {
                    bundle.putInt("myStar", Integer.valueOf(projectDetailBean.myStar));
                }
                onFragmentCallBackListener.onFragmentCallBack(this, 0, bundle);
            }

            List<ProjectBasicItemEntity> basicItemEntities = new ArrayList<>();
            if (!TextUtils.isEmpty(projectDetailBean.name)) {//项目名称
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                itemEntity.key = "项目名称";
                itemEntity.value = projectDetailBean.name;
                itemEntity.type = ProjectBasicItemEntity.PROJECT_NAME_TYPE;
                basicItemEntities.add(itemEntity);
            }
            if (!TextUtils.isEmpty(projectDetailBean.matterTypeName)) {//项目类型
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                itemEntity.key = "项目类型";
                itemEntity.value = projectDetailBean.matterTypeName;
                itemEntity.type = ProjectBasicItemEntity.PROJECT_TYPE_TYPE;
                basicItemEntities.add(itemEntity);
            }
            if (projectDetailBean.groups != null) {//负责部门
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                if (projectDetailBean.groups.size() > 1) {
                    itemEntity.key = "负责部门 (" + projectDetailBean.groups.size() + ")";
                } else {
                    itemEntity.key = "负责部门";
                }
                StringBuffer buffer = new StringBuffer();
                for (ProjectDetailEntity.GroupsBean group : projectDetailBean.groups) {
                    buffer.append(group.name).append(",");
                }
                itemEntity.value = buffer.toString();
                if (itemEntity.value.length() > 0) {
                    itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
                }
                itemEntity.type = ProjectBasicItemEntity.PROJECT_DEPARTMENT_TYPE;
                basicItemEntities.add(itemEntity);
            }
            if (projectDetailBean.clients != null) {//客户
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                if (projectDetailBean.clients.size() > 1) {
                    itemEntity.key = "客户 (" + projectDetailBean.clients.size() + ")";
                } else {
                    itemEntity.key = "客户";
                }
                StringBuffer buffer = new StringBuffer();
                for (ProjectDetailEntity.ClientsBean client : projectDetailBean.clients) {
                    buffer.append(client.contactName).append(",");
                }
                itemEntity.value = buffer.toString();
                if (itemEntity.value.length() > 0) {
                    itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
                }
                itemEntity.type = ProjectBasicItemEntity.PROJECT_CLIENT_TYPE;
                basicItemEntities.add(itemEntity);
            }

            if (projectDetailBean.litigants != null) {//其他当事人
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                if (projectDetailBean.litigants.size() > 1) {
                    itemEntity.key = "其他当事人 (" + projectDetailBean.litigants.size() + ")";
                } else {
                    itemEntity.key = "其他当事人";
                }
                StringBuffer buffer = new StringBuffer();
                for (ProjectDetailEntity.LitigantsBean litigant : projectDetailBean.litigants) {
                    buffer.append(litigant.contactName).append(",");
                }
                itemEntity.value = buffer.toString();
                if (itemEntity.value.length() > 0) {
                    itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
                }
                itemEntity.type = ProjectBasicItemEntity.PROJECT_OTHER_PERSON_TYPE;
                basicItemEntities.add(itemEntity);
            }

            if (projectDetailBean.beginDate > 0 && projectDetailBean.endDate > 0) {//项目时间
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                itemEntity.key = "项目时间";
                itemEntity.value = DateUtils.getTimeDateFormatYearDot(projectDetailBean.beginDate) + " - " + DateUtils.getTimeDateFormatYearDot(projectDetailBean.endDate);
                itemEntity.type = ProjectBasicItemEntity.PROJECT_TIME_TYPE;
                basicItemEntities.add(itemEntity);
            }

            if (projectDetailBean.originatingAttorneys != null) {//案源律师
                ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
                if (projectDetailBean.originatingAttorneys.size() > 1) {
                    itemEntity.key = "案源律师 (" + projectDetailBean.originatingAttorneys.size() + ")";
                } else {
                    itemEntity.key = "案源律师";
                }
                StringBuffer buffer = new StringBuffer();
                for (ProjectDetailEntity.OriginatingAttorneyBean originat : projectDetailBean.originatingAttorneys) {
                    buffer.append(originat.attorneyName).append(",");
                }
                itemEntity.value = buffer.toString();
                if (itemEntity.value.length() > 0) {
                    itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
                }
                itemEntity.type = ProjectBasicItemEntity.PROJECT_ANYUAN_LAWYER_TYPE;
                basicItemEntities.add(itemEntity);
            }

            projectBasicInfoAdapter.bindData(true, basicItemEntities);

            if (projectDetailBean.participants != null) {//项目成员
                projectMemberLayout.setVisibility(View.VISIBLE);
                projectMemberCount.setText("项目成员（" + projectDetailBean.participants.size() + "）");
                projectMemberAdapter.bindData(false, projectDetailBean.participants);
            } else {
                projectMemberLayout.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(projectDetailBean.logDescription)) {//服务内容
                serviceContentLayout.setVisibility(View.VISIBLE);
                projectServiceContent.setText(projectDetailBean.logDescription);
            } else {
                serviceContentLayout.setVisibility(View.GONE);
            }
            if (projectDetailBean.matterType == 0) {//争议解决
                procedureLayout.setVisibility(View.VISIBLE);
                baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
                projectViewpager.setAdapter(baseFragmentAdapter);
                baseFragmentAdapter.bindData(true,
                        Arrays.asList(
                                ProjectRangeFragment.newInstance(projectDetailBean)
                        ));
            } else {
                procedureLayout.setVisibility(View.GONE);
            }
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
    public void onDestroy() {
        super.onDestroy();
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
