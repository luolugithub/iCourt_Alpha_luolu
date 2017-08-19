package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectBasicInfoAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.ProjectBasicItemEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  项目概览：程序信息
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/5
 * version 2.0.0
 */

public class ProjectRangeFragment extends BaseFragment {

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
        projectDetailEntity = (ProjectDetailEntity) getArguments().getSerializable(KEY_PROJECT);
        if (projectDetailEntity != null) {
            List<ProjectBasicItemEntity> projectBasicItemEntities = new ArrayList<>();
            if (!TextUtils.isEmpty(projectDetailEntity.caseProcessName) && !TextUtils.equals("null", projectDetailEntity.caseProcessName)) {
                projectBasicItemEntities.add(new ProjectBasicItemEntity("程序", projectDetailEntity.caseProcessName, Const.PROJECT_CASEPROCESS_TYPE));
            }
            if (!TextUtils.isEmpty(projectDetailEntity.matterCaseName) && !TextUtils.equals("null", projectDetailEntity.matterCaseName)) {
                projectBasicItemEntities.add(new ProjectBasicItemEntity("案由", projectDetailEntity.matterCaseName, Const.PROJECT_CASE_TYPE));
            }
            if (!TextUtils.isEmpty(projectDetailEntity.matterNumber) && !TextUtils.equals("null", projectDetailEntity.matterNumber)) {
                projectBasicItemEntities.add(new ProjectBasicItemEntity("案号", projectDetailEntity.matterNumber, Const.PROJECT_CASENUMBER_TYPE));
            }
            if (!TextUtils.isEmpty(projectDetailEntity.competentCourt) && !TextUtils.equals("null", projectDetailEntity.competentCourt)) {
                String key = null;
                if (TextUtils.equals("4", projectDetailEntity.caseProcess) || TextUtils.equals("5", projectDetailEntity.caseProcess)) {
                    key = "仲裁庭";
                } else {
                    key = "法院";
                }
                projectBasicItemEntities.add(new ProjectBasicItemEntity(key, projectDetailEntity.competentCourt, Const.PROJECT_COMPETENT_TYPE));
            }
            if (projectDetailEntity.judges != null && projectDetailEntity.judges.size() > 0) {
                String name = getJudgeName(projectDetailEntity.judges);
                if (!TextUtils.isEmpty(name)) {
                    String key = null;
                    if (TextUtils.equals("4", projectDetailEntity.caseProcess) || TextUtils.equals("5", projectDetailEntity.caseProcess)) {
                        key = "仲裁员";
                    } else {
                        key = "法官";
                    }
                    projectBasicItemEntities.add(new ProjectBasicItemEntity(key, getJudgeName(projectDetailEntity.judges), Const.PROJECT_JUDGE_TYPE));
                }
            }
            if (projectDetailEntity.clerks != null && projectDetailEntity.clerks.size() > 0) {
                String name = getClerkName(projectDetailEntity.clerks);
                if (!TextUtils.isEmpty(name)) {
                    String key = null;
                    if (TextUtils.equals("4", projectDetailEntity.caseProcess) || TextUtils.equals("5", projectDetailEntity.caseProcess)) {
                        key = "仲裁秘书";
                    } else {
                        key = "书记员";
                    }
                    projectBasicItemEntities.add(new ProjectBasicItemEntity(key, getClerkName(projectDetailEntity.clerks), Const.PROJECT_CLERK_TYPE));
                }
            }
            if (projectDetailEntity.litigants != null) {
                for (ProjectDetailEntity.LitigantsBean litigant : projectDetailEntity.litigants) {
                    if (!TextUtils.isEmpty(litigant.contactName))
                        if (!TextUtils.isEmpty(litigant.customerPositionName)) {
                            projectBasicItemEntities.add(new ProjectBasicItemEntity(litigant.customerPositionName, litigant.contactName, Const.PROJECT_OTHER_PERSON_TYPE));
                        } else {
                            projectBasicItemEntities.add(new ProjectBasicItemEntity("当事人", litigant.contactName, Const.PROJECT_OTHER_PERSON_TYPE));
                        }
                }
            }
            rangeRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
            rangeRecyclerview.addItemDecoration(ItemDecorationUtils.getCommMagin5Divider(getContext(), false));
            rangeRecyclerview.setHasFixedSize(true);
            rangeRecyclerview.setAdapter(projectBasicInfoAdapter = new ProjectBasicInfoAdapter());
            projectBasicInfoAdapter.bindData(false, projectBasicItemEntities);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
