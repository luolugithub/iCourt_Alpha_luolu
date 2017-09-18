package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.CustomerCompanyDetailActivity;
import com.icourt.alpha.activity.CustomerPersonDetailActivity;
import com.icourt.alpha.activity.ProjecTacceptanceActivity;
import com.icourt.alpha.activity.ProjectBasicTextInfoActivity;
import com.icourt.alpha.activity.ProjectJudgeActivity;
import com.icourt.alpha.adapter.ProjectBasicInfoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;
import com.icourt.alpha.entity.bean.ProjectProcessesEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;

/**
 * Description  项目概览：程序信息
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/5
 * version 2.0.0
 */

public class ProjectRangeFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_PROJECT_PROCESSES = "key_project_processes";

    @BindView(R.id.range_name_tv)
    TextView rangeNameTv;
    @BindView(R.id.range_now_tv)
    TextView rangeNowTv;
    @BindView(R.id.range_recyclerview)
    RecyclerView rangeRecyclerview;
    @BindView(R.id.caseProcess_layout)
    LinearLayout caseProcessLayout;
    private ProjectProcessesEntity projectProcessesEntity;
    Unbinder unbinder;
    ProjectBasicInfoAdapter projectBasicInfoAdapter;
    private CustomerDbService customerDbService = null;

    public static ProjectRangeFragment newInstance(@NonNull ProjectProcessesEntity projectProcessesEntity) {
        ProjectRangeFragment projectRangeFragment = new ProjectRangeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PROJECT_PROCESSES, projectProcessesEntity);
        projectRangeFragment.setArguments(bundle);
        return projectRangeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_project_range_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        customerDbService = new CustomerDbService(LoginInfoUtils.getLoginUserId());
        projectProcessesEntity = (ProjectProcessesEntity) getArguments().getSerializable(KEY_PROJECT_PROCESSES);
        setDataToView(projectProcessesEntity);
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (bundle == null) return;
        projectProcessesEntity = (ProjectProcessesEntity) bundle.getSerializable(KEY_PROJECT_PROCESSES);
        setDataToView(projectProcessesEntity);
    }

    private void setDataToView(ProjectProcessesEntity projectProcessesEntity) {
        if (projectProcessesEntity == null) return;
        List<ProjectBasicItemEntity> projectBasicItemEntities = new ArrayList<>();

        //程序名称
        if (!TextUtils.isEmpty(projectProcessesEntity.processName)) {
            projectBasicItemEntities.add(new ProjectBasicItemEntity(
                    getString(R.string.project_range),
                    projectProcessesEntity.legalName + projectProcessesEntity.processName,
                    Const.PROJECT_CASEPROCESS_TYPE));
        }

        //标的
        if (!TextUtils.isEmpty(projectProcessesEntity.priceStr)) {
            String keyName = getString(R.string.project_priceStr);
            if (projectProcessesEntity.legalType == Const.LEGAL_PENAL_TYPE) {
                keyName = getString(R.string.project_connected_price);
            }

            projectBasicItemEntities.add(new ProjectBasicItemEntity(
                    keyName,
                    projectProcessesEntity.priceStr,
                    Const.PROJECT_PRICE_TYPE));
        }

        //案由
        if (projectProcessesEntity.caseCodes != null && projectProcessesEntity.caseCodes.size() > 0) {
            String keyName = getString(R.string.project_case);
            if (projectProcessesEntity.legalType == Const.LEGAL_PENAL_TYPE) {
                keyName = getString(R.string.project_charge);
            }
            projectBasicItemEntities.add(new ProjectBasicItemEntity(
                    keyName,
                    getCaseCodeName(projectProcessesEntity.caseCodes),
                    Const.PROJECT_CASE_TYPE));
        }

        //其他当事人
        if (projectProcessesEntity.position != null && projectProcessesEntity.position.size() > 0) {
            for (ProjectProcessesEntity.PositionBean positionBean : projectProcessesEntity.position) {
                if (!TextUtils.isEmpty(positionBean.contactName))
                    projectBasicItemEntities.add(
                            new ProjectBasicItemEntity(
                            TextUtils.isEmpty(positionBean.partyName) ? getString(R.string.project_litigants) : positionBean.partyName,
                            positionBean.contactName,
                            Const.PROJECT_OTHER_PERSON_TYPE,
                            positionBean));
            }
        }

        //其他信息
        if (projectProcessesEntity.extra != null && projectProcessesEntity.extra.size() > 0) {
            for (ProjectProcessesEntity.ExtraBean extra : projectProcessesEntity.extra) {
                String value = getExtraName(extra.values);
                if (!TextUtils.isEmpty(value))
                    projectBasicItemEntities.add(new ProjectBasicItemEntity(
                            extra.name,
                            getExtraName(extra.values),
                            Const.PROJECT_ACCEPTANCE_TYPE, extra));
            }
        }

        rangeRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        rangeRecyclerview.addItemDecoration(ItemDecorationUtils.getCommMagin5Divider(getContext(), false));
        rangeRecyclerview.setHasFixedSize(true);
        rangeRecyclerview.setAdapter(projectBasicInfoAdapter = new ProjectBasicInfoAdapter());
        projectBasicInfoAdapter.setOnItemClickListener(this);
        projectBasicInfoAdapter.bindData(false, projectBasicItemEntities);
    }


    /**
     * 获取caseCodes名称
     *
     * @param caseCodes  caseCodes集合
     * @return caseCodes名称
     */
    private String getCaseCodeName(List<ProjectProcessesEntity.CaseCodesBean> caseCodes) {
        if (caseCodes == null || caseCodes.size() <= 0) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ProjectProcessesEntity.CaseCodesBean code : caseCodes) {
            stringBuilder.append(code.name).append("、");
        }
        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    /**
     * 获取acceptance名称
     *
     * @param values acceptance集合
     * @return acceptance名称
     */
    private String getExtraName(List<ProjectProcessesEntity.ExtraBean.ValuesBean> values) {
        if (values == null || values.size() <= 0) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ProjectProcessesEntity.ExtraBean.ValuesBean value : values) {
            stringBuilder.append(value.text).append("、");
        }
        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (projectProcessesEntity == null) return;
        ProjectBasicItemEntity itemEntity = (ProjectBasicItemEntity) adapter.getItem(position);

        switch (itemEntity.type) {
            case Const.PROJECT_CASE_TYPE://案由
                ProjectJudgeActivity.launch(getContext(), itemEntity.key, projectProcessesEntity.caseCodes, Const.PROJECT_CASE_TYPE);
                break;
            case Const.PROJECT_CASEPROCESS_TYPE:
            case Const.PROJECT_PRICE_TYPE:
                ProjectBasicTextInfoActivity.launch(view.getContext(), itemEntity.key, itemEntity.value, itemEntity.type);
                break;
            case Const.PROJECT_ACCEPTANCE_TYPE:
                ProjecTacceptanceActivity.launch(getContext(), itemEntity.extraBean);
                break;
            case Const.PROJECT_OTHER_PERSON_TYPE:
                if (itemEntity.positionBean == null) return;
                if (!hasCustomerPermission()) return;
                if (customerDbService == null) return;
                CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", itemEntity.positionBean.contactPkid);
                if (customerDbModel == null) return;
                CustomerEntity customerEntity = customerDbModel.convert2Model();
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

                break;
        }
    }

    /**
     * 是否有查看联系人权限
     *
     * @return true:有权限 false：无权限
     */
    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }
}
