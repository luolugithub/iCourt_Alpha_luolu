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

import com.icourt.alpha.R;
import com.icourt.alpha.activity.CustomerCompanyDetailActivity;
import com.icourt.alpha.activity.CustomerPersonDetailActivity;
import com.icourt.alpha.activity.ProjectBasicTextInfoActivity;
import com.icourt.alpha.activity.ProjectJudgeActivity;
import com.icourt.alpha.activity.ProjectMembersActivity;
import com.icourt.alpha.adapter.ProjectBasicInfoAdapter;
import com.icourt.alpha.adapter.ProjectClientAdapter;
import com.icourt.alpha.adapter.ProjectMembersAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.entity.bean.ProjectProcessesEntity;
import com.icourt.alpha.entity.event.ProjectActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.WrapContentHeightViewPager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.umeng.analytics.MobclickAgent;

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

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;
import static com.icourt.alpha.activity.ProjectDetailActivity.KEY_PROJECT_ID;
import static com.icourt.alpha.activity.ProjectDetailActivity.KEY_PROJECT_MYSTAR;
import static com.icourt.alpha.activity.ProjectDetailActivity.KEY_PROJECT_PROCESSES;

/**
 * Description 项目概览
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/3
 * version 2.0.0
 */

public class ProjectDetailFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    Unbinder unbinder;
    @BindView(R.id.project_member_recyclerview)
    RecyclerView projectMemberRecyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
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
    @BindView(R.id.procedure_layout)
    LinearLayout procedureLayout;

    private String projectId;
    ProjectMembersAdapter projectMemberAdapter;
    ProjectBasicInfoAdapter projectBasicInfoAdapter;
    BaseFragmentAdapter baseFragmentAdapter;
    OnFragmentCallBackListener onFragmentCallBackListener;
    ProjectDetailEntity projectDetailBean;
    private CustomerDbService customerDbService = null;
    ProjectRangeFragment projectRangeFragment;

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
        customerDbService = new CustomerDbService(LoginInfoUtils.getLoginUserId());

        basicTopRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        basicTopRecyclerview.addItemDecoration(ItemDecorationUtils.getCommMagin5Divider(getContext(), false));
        basicTopRecyclerview.setAdapter(projectBasicInfoAdapter = new ProjectBasicInfoAdapter());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        projectMemberRecyclerview.setLayoutManager(layoutManager);
        projectMemberRecyclerview.addItemDecoration(ItemDecorationUtils.getCommFull5VerticalDivider(getContext(), true));
        projectMemberRecyclerview.setHasFixedSize(true);
        projectMemberRecyclerview.setAdapter(projectMemberAdapter = new ProjectMembersAdapter());
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

            }
        });
        refreshLayout.autoRefresh();
    }

    /**
     * 设置数据
     *
     * @param projectDetailBean 项目model
     */
    private void setDataToView(ProjectDetailEntity projectDetailBean) {
        if (projectMemberLayout == null) return;
        if (projectDetailBean != null) {
            this.projectDetailBean = projectDetailBean;
            EventBus.getDefault().post(new ProjectActionEvent(ProjectActionEvent.PROJECT_TIMER_ACTION, projectDetailBean.sumTime));
            if (onFragmentCallBackListener != null) {
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(projectDetailBean.myStar)) {
                    bundle.putInt(KEY_PROJECT_MYSTAR, 0);
                } else {
                    bundle.putInt(KEY_PROJECT_MYSTAR, Integer.valueOf(projectDetailBean.myStar));
                }
                onFragmentCallBackListener.onFragmentCallBack(this, 0, bundle);
            }

            List<ProjectBasicItemEntity> basicItemEntities = new ArrayList<>();
            setKeyValueData(basicItemEntities, getString(R.string.project_type), projectDetailBean.matterTypeName, Const.PROJECT_TYPE_TYPE);
            setKeyValueData(basicItemEntities, getString(R.string.project_name), projectDetailBean.name, Const.PROJECT_NAME_TYPE);
            setKeyValueData(basicItemEntities, getString(R.string.project_number), projectDetailBean.matterNo, Const.PROJECT_NUMBER_TYPE);

            setClientData(basicItemEntities);//客户
            if (projectDetailBean.matterType != Const.PROJECT_TRANSACTION_TYPE) { //所内事务不显示当事人item
                setLitigantData(basicItemEntities);//当事人
            }
            setGroupsData(basicItemEntities);//负责部门
            setAttorneysData(basicItemEntities);//案源律师

            if (projectDetailBean.beginDate > 0) {
                setKeyValueData(basicItemEntities, getString(R.string.project_start_date), String.format("%s",
                        DateUtils.getFormatDate(projectDetailBean.beginDate, DateUtils.DATE_YYYYMMDD_STYLE3)), Const.PROJECT_TIME_TYPE);
            }
            if (projectDetailBean.endDate > 0) {
                setKeyValueData(basicItemEntities, getString(R.string.project_end_date), String.format("%s",
                        DateUtils.getFormatDate(projectDetailBean.endDate, DateUtils.DATE_YYYYMMDD_STYLE3)), Const.PROJECT_TIME_TYPE);
            }
            if (projectDetailBean.matterType == Const.PROJECT_NON_LAWSUIT_TYPE) {
                if (!TextUtils.isEmpty(projectDetailBean.lawField)) {
                    setKeyValueData(basicItemEntities, getString(R.string.project_server_content), projectDetailBean.lawField, Const.PROJECT_SERVER_CONTENT_TYPE);
                }
            }
            setKeyValueData(basicItemEntities, getString(R.string.project_remark), projectDetailBean.remark, Const.PROJECT_REMARK_TYPE);


            projectBasicInfoAdapter.setClientsBeens(projectDetailBean.clients);
            projectBasicInfoAdapter.bindData(true, basicItemEntities);
            projectBasicInfoAdapter.setOnItemClickListener(this);

            if (projectDetailBean.members != null) {//项目成员
                if (projectDetailBean.members.size() > 0) {
                    projectMemberLayout.setVisibility(View.VISIBLE);
                    projectMemberCount.setText(String.format(getString(R.string.project_members_format), projectDetailBean.members.size()));
                    projectMemberAdapter.bindData(true, projectDetailBean.members);
                }
            } else {
                projectMemberLayout.setVisibility(View.GONE);
            }
            projectMemberAdapter.setOnItemClickListener(this);
            if (projectDetailBean.matterType == 0) {//争议解决
                getRangeData(projectDetailBean.pkId);
            } else {
                procedureLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置单一的数据类型：value为string
     *
     * @param basicItemEntities 列表默认model
     * @param key               列表显示title
     * @param value             列表显示值
     * @param type              类型
     */
    private void setKeyValueData(List<ProjectBasicItemEntity> basicItemEntities, String key, String value, int type) {
        if (TextUtils.isEmpty(value)) return;
        ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
        itemEntity.key = key;
        itemEntity.value = value;
        itemEntity.type = type;
        basicItemEntities.add(itemEntity);
    }

    /**
     * 设置客户信息
     *
     * @param basicItemEntities 列表默认model集合
     */
    private void setClientData(List<ProjectBasicItemEntity> basicItemEntities) {
        if (projectDetailBean.clients == null || projectDetailBean.clients.size() <= 0) return;
        ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
        if (projectDetailBean.clients.size() > 1) {
            itemEntity.key = String.format(getString(R.string.project_clients_format), projectDetailBean.clients.size());
        } else {
            itemEntity.key = getString(R.string.project_clients);
        }
        StringBuilder builder = new StringBuilder();
        for (ProjectDetailEntity.ClientsBean client : projectDetailBean.clients) {
            builder.append(client.contactName).append("、");
        }
        itemEntity.value = builder.toString();
        if (itemEntity.value.length() > 0) {
            itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
        }
        itemEntity.type = Const.PROJECT_CLIENT_TYPE;
        basicItemEntities.add(itemEntity);
    }

    /**
     * 设置当事人信息
     *
     * @param basicItemEntities 列表默认model集合
     */
    private void setLitigantData(List<ProjectBasicItemEntity> basicItemEntities) {
        if (projectDetailBean.litigants == null || projectDetailBean.litigants.size() <= 0) return;
        ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
        if (projectDetailBean.litigants.size() > 1) {
            itemEntity.key = String.format(getString(R.string.project_litigants_format), projectDetailBean.litigants.size());
        } else {
            itemEntity.key = getString(R.string.project_litigants);
        }
        StringBuilder builder = new StringBuilder();
        for (ProjectDetailEntity.LitigantsBean litigant : projectDetailBean.litigants) {
            builder.append(litigant.contactName).append(",");
        }
        itemEntity.value = builder.toString();
        if (itemEntity.value.length() > 0) {
            itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
        }
        itemEntity.type = Const.PROJECT_PERSON_TYPE;
        basicItemEntities.add(itemEntity);
    }

    /**
     * 设置部门信息
     *
     * @param basicItemEntities 列表默认model集合
     */
    private void setGroupsData(List<ProjectBasicItemEntity> basicItemEntities) {
        if (projectDetailBean.groups == null || projectDetailBean.groups.size() <= 0) return;
        ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
        if (projectDetailBean.groups.size() > 1) {
            itemEntity.key = String.format(getString(R.string.project_groups_format), projectDetailBean.groups.size());
        } else {
            itemEntity.key = getString(R.string.project_groups);
        }
        StringBuilder builder = new StringBuilder();
        for (ProjectDetailEntity.GroupsBean group : projectDetailBean.groups) {
            builder.append(group.name).append("、");
        }
        itemEntity.value = builder.toString();
        if (itemEntity.value.length() > 0) {
            itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
        }
        itemEntity.type = Const.PROJECT_DEPARTMENT_TYPE;
        basicItemEntities.add(itemEntity);

    }

    /**
     * 设置案源律师数据
     *
     * @param basicItemEntities 列表默认model集合
     */
    private void setAttorneysData(List<ProjectBasicItemEntity> basicItemEntities) {
        if (projectDetailBean.attorneys == null || projectDetailBean.attorneys.size() <= 0) return;
        ProjectBasicItemEntity itemEntity = new ProjectBasicItemEntity();
        if (projectDetailBean.attorneys.size() > 1) {
            itemEntity.key = String.format(getString(R.string.project_attorneys_format), projectDetailBean.attorneys.size());
        } else {
            itemEntity.key = getString(R.string.project_attorneys);
        }
        StringBuilder builder = new StringBuilder();
        for (ProjectDetailEntity.AttorneysBean attorneysBean : projectDetailBean.attorneys) {
            builder.append(attorneysBean.attorneyName).append("、");
        }
        itemEntity.value = builder.toString();
        if (itemEntity.value.length() > 0) {
            itemEntity.value = itemEntity.value.substring(0, itemEntity.value.length() - 1);
        }
        itemEntity.type = Const.PROJECT_ANYUAN_LAWYER_TYPE;
        basicItemEntities.add(itemEntity);

    }

    @Override
    protected void getData(boolean isRefresh) {
        callEnqueue(
                getApi().projectDetail(projectId),
                new SimpleCallBack<List<ProjectDetailEntity>>() {
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

    /**
     * 获取程序信息
     *
     * @param projectId
     */
    private void getRangeData(String projectId) {
        callEnqueue(getApi().projectProcessesQuery(projectId),
                new SimpleCallBack<List<ProjectProcessesEntity>>() {
                    public void onSuccess(Call<ResEntity<List<ProjectProcessesEntity>>> call, Response<ResEntity<List<ProjectProcessesEntity>>> response) {
                        if (procedureLayout != null) {
                            if (response.body().result != null && response.body().result.size() > 0) {
                                procedureLayout.setVisibility(View.VISIBLE);
                                setRangeDataToView(response.body().result.get(0));
                            } else {
                                procedureLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    /**
     * 设置程序信息
     *
     * @param projectProcessesEntity
     */
    private void setRangeDataToView(ProjectProcessesEntity projectProcessesEntity) {
        if (baseFragmentAdapter == null) {
            baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
            projectViewpager.setAdapter(baseFragmentAdapter);
            baseFragmentAdapter.bindData(true,
                    Arrays.asList(
                            projectRangeFragment == null ? projectRangeFragment = ProjectRangeFragment.newInstance(projectProcessesEntity) : projectRangeFragment)
            );
        } else {
            if (projectRangeFragment != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_PROJECT_PROCESSES, projectProcessesEntity);
                projectRangeFragment.notifyFragmentUpdate(projectRangeFragment, 0, bundle);
            }
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @OnClick({R.id.project_add_routine,
            R.id.project_member_layout,
            R.id.project_member_recyclerview,
            R.id.project_member_childlayout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.project_add_routine:
                showTopSnackBar(R.string.project_add_range_content);
                break;
            case R.id.project_member_layout:
            case R.id.project_member_childlayout:
                if (projectDetailBean != null)
                    ProjectMembersActivity.launch(getContext(), projectDetailBean.members, Const.PROJECT_MEMBER_TYPE);
                break;
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (projectDetailBean == null) return;
        if (adapter instanceof ProjectMembersAdapter) {
            ProjectMembersActivity.launch(getContext(), projectDetailBean.members, Const.PROJECT_MEMBER_TYPE);
        } else if (adapter instanceof ProjectBasicInfoAdapter) {
            ProjectBasicItemEntity entity = (ProjectBasicItemEntity) adapter.getItem(position);
            switch (entity.type) {
                case Const.PROJECT_NUMBER_TYPE:
                    ProjectBasicTextInfoActivity.launch(view.getContext(), entity.key, entity.value, entity.type);
                    break;
                case Const.PROJECT_ANYUAN_LAWYER_TYPE://案源律师
                    ProjectMembersActivity.launch(view.getContext(), projectDetailBean.attorneys, Const.PROJECT_ANYUAN_LAWYER_TYPE);
                    break;
                case Const.PROJECT_DEPARTMENT_TYPE://负责部门
                    ProjectJudgeActivity.launch(getContext(), entity.key, projectDetailBean.groups, entity.type);
                    break;
                case Const.PROJECT_PERSON_TYPE://当事人
                    ProjectJudgeActivity.launch(getContext(), entity.key, projectDetailBean.litigants, entity.type);
                    break;
                case Const.PROJECT_SERVER_CONTENT_TYPE://服务内容
                    ProjectBasicTextInfoActivity.launch(view.getContext(), entity.key, entity.value, entity.type);
                    break;
            }
        } else if (adapter instanceof ProjectClientAdapter) {
            Object object = adapter.getItem(position);
            if (object instanceof ProjectDetailEntity.ClientsBean) {
                ProjectDetailEntity.ClientsBean clientsBean = (ProjectDetailEntity.ClientsBean) object;
                if (!hasCustomerPermission()) return;
                if (customerDbService == null) return;
                if (!TextUtils.isEmpty(clientsBean.contactPkid) && !TextUtils.isEmpty(clientsBean.type)) {
                    gotoCustiomer(clientsBean);
                } else {
                    ProjectBasicTextInfoActivity.launch(view.getContext(), getString(R.string.project_clients), clientsBean.contactName, Const.PROJECT_CLIENT_TYPE);
                }
            } else if (object instanceof ProjectBasicItemEntity) {
                ProjectBasicItemEntity basicItemEntity = (ProjectBasicItemEntity) object;
                ProjectBasicTextInfoActivity.launch(view.getContext(), basicItemEntity.key, basicItemEntity.value, basicItemEntity.type);
            }
        }
    }

    /**
     * 跳转到客户详情
     *
     * @param clientsBean
     */
    private void gotoCustiomer(ProjectDetailEntity.ClientsBean clientsBean) {
        if (!hasCustomerPermission()) return;
        if (customerDbService == null) return;
        CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", clientsBean.contactPkid);
        if (customerDbModel == null) {
            showTopSnackBar(R.string.project_not_look_info_premission);
            return;
        }
        CustomerEntity customerEntity = customerDbModel.convert2Model();
        if (customerEntity == null) {
            showTopSnackBar(R.string.project_not_look_info_premission);
            return;
        }
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
     * 是否有查看联系人权限
     *
     * @return
     */
    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }
}
