package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.TimerAddActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTaskAddActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TimeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.recyclerviewDivider.TimerItemDecoration;
import com.icourt.alpha.widget.manager.TimerManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/5
 * version 1.0.0
 */
public class TaskTimersDialogFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {

    Unbinder unbinder;
    TaskEntity.TaskItemEntity taskItemEntity;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    TimeAdapter timeAdapter;
    int pageIndex = 1;

    public static TaskTimersDialogFragment newInstance(@NonNull TaskEntity.TaskItemEntity taskItemEntity) {
        TaskTimersDialogFragment contactDialogFragment = new TaskTimersDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("taskItemEntity", taskItemEntity);
        contactDialogFragment.setArguments(args);
        return contactDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_task_timers, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.AppThemeSlideAnimation);
                WindowManager.LayoutParams p = window.getAttributes();
                p.width = ViewGroup.LayoutParams.MATCH_PARENT;
                p.height = ViewGroup.LayoutParams.MATCH_PARENT;
                window.setAttributes(p);
                window.setGravity(Gravity.BOTTOM);
            }
        }
        taskItemEntity = (TaskEntity.TaskItemEntity) getArguments().getSerializable("taskItemEntity");
        titleBack.setImageResource(R.mipmap.header_icon_close);
        titleContent.setText("查看计时");
        if (taskItemEntity != null) {
            titleAction.setVisibility(taskItemEntity.valid ? View.VISIBLE : View.GONE);
        }
        refreshLayout.setEnableLoadmore(false);
        recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_timing, R.string.empty_list_timing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null);

        recyclerView.setAdapter(timeAdapter = new TimeAdapter());
        recyclerView.addItemDecoration(new TimerItemDecoration(getActivity(), timeAdapter));
        timeAdapter.setOnItemClickListener(this);
        timeAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(recyclerView, timeAdapter));

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(true);
    }

    @OnClick({R.id.titleBack,
            R.id.titleAction})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                dismiss();
                break;
            case R.id.titleAction:
                if (taskItemEntity != null)
                    TimerTaskAddActivity.launch(getContext(), taskItemEntity);
                break;
            default:

                break;
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        if (taskItemEntity == null) {
            return;
        }
        if (isRefresh) {
            pageIndex = 1;
        }
        callEnqueue(
                getApi().taskTimesByIdQuery(taskItemEntity.id),
                new SimpleCallBack<TimeEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TimeEntity>> call, Response<ResEntity<TimeEntity>> response) {
                        stopRefresh();
                        if (response.body().result != null) {
                            if (response.body().result.items != null) {
                                if (response.body().result.items.size() > 0) {
                                    response.body().result.items.add(0, new TimeEntity.ItemEntity());
                                }
                                timeAdapter.bindData(isRefresh, response.body().result.items);
                                timeAdapter.setSumTime(response.body().result.timingSum);

                                pageIndex += 1;
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
            refreshLayout.setEnableLoadmore(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (holder.getItemViewType() == 1) {
            TimeEntity.ItemEntity itemEntity = (TimeEntity.ItemEntity) adapter.getItem(adapter.getRealPos(position));
            if (itemEntity == null) return;
            if (taskItemEntity != null) {
                itemEntity.taskName = taskItemEntity.name;
            }
            if (TextUtils.equals(itemEntity.createUserId, getLoginUserId())) {
                if (StringUtils.equalsIgnoreCase(itemEntity.pkId, TimerManager.getInstance().getTimerId(), false)) {
                    TimerTimingActivity.launch(view.getContext(), itemEntity);
                } else {
                    TimerDetailActivity.launch(view.getContext(), itemEntity);
                }
            }
        }
    }
}
