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
import com.icourt.alpha.adapter.ProjectBasicInfoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.entity.bean.ProjectProcessesEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
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
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;

/**
 * Description  项目概览：程序信息
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/5
 * version 2.0.0
 */

public class ProjectRangeFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_PROJECT = "key_project";

    @BindView(R.id.range_name_tv)
    TextView rangeNameTv;
    @BindView(R.id.range_now_tv)
    TextView rangeNowTv;
    @BindView(R.id.range_recyclerview)
    RecyclerView rangeRecyclerview;
    @BindView(R.id.caseProcess_layout)
    LinearLayout caseProcessLayout;
    private ProjectDetailEntity projectDetailEntity;
    Unbinder unbinder;
    ProjectBasicInfoAdapter projectBasicInfoAdapter;
    private CustomerDbService customerDbService = null;

    public static ProjectRangeFragment newInstance(@NonNull ProjectDetailEntity projectDetailEntity) {
        ProjectRangeFragment projectRangeFragment = new ProjectRangeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_PROJECT, projectDetailEntity);
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
        projectDetailEntity = (ProjectDetailEntity) getArguments().getSerializable(KEY_PROJECT);
        if (projectDetailEntity != null) {
            getData(true);
        }
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (bundle == null) return;
        projectDetailEntity = (ProjectDetailEntity) bundle.getSerializable(KEY_PROJECT);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        getApi().projectProcessesQuery(projectDetailEntity.pkId).enqueue(new SimpleCallBack<List<ProjectProcessesEntity>>() {
            public void onSuccess(Call<ResEntity<List<ProjectProcessesEntity>>> call, Response<ResEntity<List<ProjectProcessesEntity>>> response) {
                if (response.body().result != null && response.body().result.size() > 0) {
                    setDataToView(response.body().result.get(0));
                }
            }
        });
    }

    private void setDataToView(ProjectProcessesEntity projectProcessesEntity) {
        if (projectProcessesEntity == null) return;
        List<ProjectBasicItemEntity> projectBasicItemEntities = new ArrayList<>();
        if (!TextUtils.isEmpty(projectProcessesEntity.processName)) {
            projectBasicItemEntities.add(new ProjectBasicItemEntity("程序", projectProcessesEntity.processName, Const.PROJECT_CASEPROCESS_TYPE));
        }
        if (!TextUtils.isEmpty(projectProcessesEntity.priceStr)) {
            projectBasicItemEntities.add(new ProjectBasicItemEntity("标的", projectProcessesEntity.priceStr, Const.PROJECT_PRICE_TYPE));
        }
        if (projectProcessesEntity.caseCodes != null && projectProcessesEntity.caseCodes.size() > 0) {
            projectBasicItemEntities.add(new ProjectBasicItemEntity("案由", getCaseCodeName(projectProcessesEntity.caseCodes), Const.PROJECT_CASE_TYPE));
        }
        if (projectProcessesEntity.extra != null && projectProcessesEntity.extra.size() > 0) {
            for (ProjectProcessesEntity.ExtraBean extra : projectProcessesEntity.extra) {
                projectBasicItemEntities.add(new ProjectBasicItemEntity(extra.name, getExtraName(extra.values), Const.PROJECT_ACCEPTANCE_TYPE, extra));
            }
        }
        if (projectProcessesEntity.position != null && projectProcessesEntity.position.size() > 0) {
            for (ProjectProcessesEntity.PositionBean positionBean : projectProcessesEntity.position) {
                projectBasicItemEntities.add(new ProjectBasicItemEntity(positionBean.partyName, positionBean.contactName, Const.PROJECT_OTHER_PERSON_TYPE, positionBean));
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
     * @param caseCodes
     * @return
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
     * @param values
     * @return
     */
    private String getExtraName(List<ProjectProcessesEntity.ExtraBean.ValuesBean> values) {
        if (values == null || values.size() <= 0) return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (ProjectProcessesEntity.ExtraBean.ValuesBean value : values) {
            stringBuilder.append(value.text).append("、");
        }
        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    private String getStringListName(List<String> list) {
        if (list == null || list.size() <= 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : list) {
            stringBuilder.append(str).append("、");
        }
        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    /**
     * 获取法官名字
     *
     * @param judgeBeens
     * @return
     */
    private String getJudgeName(List<ProjectDetailEntity.JudgeBean> judgeBeens) {
        if (judgeBeens.size() <= 0) return "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (ProjectDetailEntity.JudgeBean judgeBeen : judgeBeens) {
                stringBuilder.append(judgeBeen.name).append(",");
            }
            return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("获取法官名称失败", e);
        }
        return "";
    }

    /**
     * 获取书记员名字
     *
     * @param clerkBeanList
     * @return
     */
    private String getClerkName(List<ProjectDetailEntity.ClerkBean> clerkBeanList) {
        if (clerkBeanList.size() <= 0) return "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (ProjectDetailEntity.ClerkBean clerkBean : clerkBeanList) {
                stringBuilder.append(clerkBean.name).append(",");
            }
            return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("获取书记员名称失败", e);
        }
        return "";
    }

    /**
     * 获取仲裁员名字
     *
     * @param arbitratorBeens
     * @return
     */
    private String getArbitratorName(List<ProjectDetailEntity.ArbitratorBean> arbitratorBeens) {
        if (arbitratorBeens.size() <= 0) return "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (ProjectDetailEntity.ArbitratorBean arbitratorBean : arbitratorBeens) {
                stringBuilder.append(arbitratorBean.name).append(",");
            }
            return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("获取仲裁员名称失败", e);
        }
        return "";
    }

    /**
     * 获取仲裁秘书名字
     *
     * @param secretarieBeens
     * @return
     */
    private String getSecretarieName(List<ProjectDetailEntity.SecretarieBean> secretarieBeens) {
        if (secretarieBeens.size() <= 0) return "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (ProjectDetailEntity.SecretarieBean secretarieBean : secretarieBeens) {
                stringBuilder.append(secretarieBean.name).append(",");
            }
            return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("获取仲裁秘书名称失败", e);
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (projectDetailEntity == null) return;
        ProjectBasicItemEntity itemEntity = (ProjectBasicItemEntity) adapter.getItem(position);

        switch (itemEntity.type) {
            case Const.PROJECT_CASE_TYPE:
                ProjectBasicTextInfoActivity.launch(view.getContext(), itemEntity.value, itemEntity.type);
                break;
            case Const.PROJECT_ACCEPTANCE_TYPE:
                ProjecTacceptanceActivity.launch(getContext(), itemEntity.extraBean);
                break;
            case Const.PROJECT_OTHER_PERSON_TYPE:
                if (itemEntity.positionBean == null) return;
                if (!hasCustomerPermission()) return;
                if (customerDbService == null) return;
                CustomerEntity customerEntity = null;
                CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", itemEntity.positionBean.contactPkid);
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

                break;
        }
    }

    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }
}
