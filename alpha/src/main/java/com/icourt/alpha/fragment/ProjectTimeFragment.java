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
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TimeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.ProjectActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.recyclerviewDivider.TimerItemDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.manager.TimerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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

public class ProjectTimeFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_PROJECT_ID = "key_project_id";
    Unbinder unbinder;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    String projectId;
    TimeAdapter timeAdapter;
    private int pageIndex = 1;
    private long sumTime;

    public static ProjectTimeFragment newInstance(@NonNull String projectId) {
        ProjectTimeFragment projectTimeFragment = new ProjectTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        projectTimeFragment.setArguments(bundle);
        return projectTimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_timing, "暂无计时");
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(timeAdapter = new TimeAdapter());
        timeAdapter.setSumTime(sumTime);
        recyclerView.addItemDecoration(new TimerItemDecoration(getActivity(), timeAdapter));
        recyclerView.setHasFixedSize(true);
        timeAdapter.setOnItemClickListener(this);
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
        if (isRefresh) {
            pageIndex = 1;
        }
        getApi().projectQueryTimerList(projectId, pageIndex, ActionConstants.DEFAULT_PAGE_SIZE).enqueue(new SimpleCallBack<TimeEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                stopRefresh();
                if (response.body().result != null) {
                    if (response.body().result.items != null) {
                        if (response.body().result.items.size() > 0) {
                            response.body().result.items.add(0, new TimeEntity.ItemEntity());
                        }
                        timeAdapter.setSumTime(sumTime);
                        timeAdapter.bindData(isRefresh, response.body().result.items);
                        pageIndex += 1;
                        enableLoadMore(response.body().result.items);
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

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     *
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSumTime(ProjectActionEvent event) {
        if (event == null) return;
        if (event.action == ProjectActionEvent.PROJECT_TIMER_ACTION)
            sumTime = event.sumTime;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (holder.getItemViewType() == 1) {
            TimeEntity.ItemEntity itemEntity = (TimeEntity.ItemEntity) adapter.getItem(position);
            if (itemEntity != null && StringUtils.equalsIgnoreCase(itemEntity.pkId, TimerManager.getInstance().getTimerId(), false)) {
                TimerTimingActivity.launch(view.getContext(), itemEntity);
            } else {
                TimerDetailActivity.launch(view.getContext(), itemEntity);
            }
        }
    }
}
