package com.icourt.alpha.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TimeAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description 项目计时
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class ProjectTimeFragment extends BaseFragment {

    private static final String KEY_PROJECT_ID = "key_project_id";
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    TimeAdapter timeAdapter;
    List<TimeEntity> timeEntitys = new ArrayList<>();

    public static ProjectTimeFragment newInstance(@NonNull String projectId) {
        ProjectTimeFragment projectTimeFragment = new ProjectTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        projectTimeFragment.setArguments(bundle);
        return projectTimeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_project_mine, inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.null_project);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans10Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(timeAdapter = new TimeAdapter());
        timeAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, timeAdapter));

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

    @Override
    protected void getData(boolean isRefresh) {
        if (timeEntitys != null) {
            timeEntitys.clear();
        }
        for (int i = 0; i < 3; i++) {
            TimeEntity timeEntity = new TimeEntity();
            if (i == 0) {
                timeEntitys.add(timeEntity);
            } else {
                List<TimeEntity.ItemEntity> itemEntitys = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    TimeEntity.ItemEntity itemEntity = new TimeEntity.ItemEntity();
                    itemEntity.timeDes = "项目类型图标的重新设计";
                    itemEntity.timeUserName = "李妍熙sally";
                    itemEntity.timeUserPic = "";
                    itemEntity.timeType = "内部会议";
                    itemEntitys.add(itemEntity);
                }
                timeEntity.itemEntities = itemEntitys;
                timeEntitys.add(timeEntity);
            }
        }
        timeAdapter.bindData(false, timeEntitys);
        stopRefresh();
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
}
