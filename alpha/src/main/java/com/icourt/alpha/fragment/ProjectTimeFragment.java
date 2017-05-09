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
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.recyclerviewDivider.TimerItemDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

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

    String projectId;
    TimeAdapter timeAdapter;

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
        projectId = getArguments().getString(KEY_PROJECT_ID);
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.null_project);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(timeAdapter = new TimeAdapter());
        recyclerView.addItemDecoration(new TimerItemDecoration(getActivity(), timeAdapter));
        recyclerView.setHasFixedSize(true);

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
    protected void getData(final boolean isRefresh) {
        getApi().projectQueryTimerList(projectId, -1).enqueue(new SimpleCallBack<TimeEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                stopRefresh();
                if (response.body().result != null) {
                    if (response.body().result.items != null) {
                        if (response.body().result.items.size() > 0) {
                            response.body().result.items.add(0, new TimeEntity.ItemEntity());
                        }
                        timeAdapter.bindData(isRefresh, response.body().result.items);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResEntity<TimeEntity>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
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
}
