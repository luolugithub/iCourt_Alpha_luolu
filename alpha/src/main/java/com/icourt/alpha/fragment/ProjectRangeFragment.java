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
import com.icourt.alpha.adapter.ProjectRangeListAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.entity.bean.RangeItemEntity;
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
    ProjectRangeListAdapter projectRangeListAdapter;

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
            List<RangeItemEntity> rangeItemEntities = new ArrayList<>();
            if (!TextUtils.isEmpty(projectDetailEntity.caseProcessName) && !"null".equals(projectDetailEntity.caseProcessName)) {
                caseProcessLayout.setVisibility(View.VISIBLE);
                rangeNameTv.setText(projectDetailEntity.caseProcessName);
            } else {
                caseProcessLayout.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(projectDetailEntity.matterCaseName)) {
                rangeItemEntities.add(new RangeItemEntity("案由", projectDetailEntity.matterCaseName));
            }
            if (!TextUtils.isEmpty(projectDetailEntity.competentCourt)) {
                rangeItemEntities.add(new RangeItemEntity("法院", projectDetailEntity.competentCourt));
            }
            if (projectDetailEntity.judges != null) {
                rangeItemEntities.add(new RangeItemEntity("法官", getJudgeName(projectDetailEntity.judges)));
            }
//            if (projectDetailEntity.clients != null) {
//                for (ProjectDetailEntity.ClientsBean client : projectDetailEntity.clients) {
//                    if (!TextUtils.isEmpty(client.customerPositionName)) {
//                        rangeItemEntities.add(new RangeItemEntity(client.customerPositionName + "(客户)", client.contactName));
//                    } else {
//                        rangeItemEntities.add(new RangeItemEntity("客户", client.contactName));
//                    }
//                }
//            }
//            if (projectDetailEntity.litigants != null) {
//                for (ProjectDetailEntity.LitigantsBean litigant : projectDetailEntity.litigants) {
//                    if (!TextUtils.isEmpty(litigant.customerPositionName)) {
//                        rangeItemEntities.add(new RangeItemEntity(litigant.customerPositionName, litigant.contactName));
//                    } else {
//                        rangeItemEntities.add(new RangeItemEntity("当事人", litigant.contactName));
//                    }
//                }
//            }
            rangeRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
            rangeRecyclerview.addItemDecoration(ItemDecorationUtils.getCommMagin5Divider(getContext(), false));
            rangeRecyclerview.setHasFixedSize(true);
            rangeRecyclerview.setAdapter(projectRangeListAdapter = new ProjectRangeListAdapter());
            projectRangeListAdapter.bindData(false, rangeItemEntities);
        }
    }

    /**
     * 获取法官名字
     *
     * @param judgeBeens
     * @return
     */
    private String getJudgeName(List<ProjectDetailEntity.JudgeBean> judgeBeens) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ProjectDetailEntity.JudgeBean judgeBeen : judgeBeens) {
            stringBuilder.append(judgeBeen.name).append(",");
        }
        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
