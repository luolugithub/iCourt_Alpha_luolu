package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectListAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class MyProjectFragment extends BaseFragment {

    public static final int TYPE_ALL_PROJECT = 0;//全部
    public static final int TYPE_MY_ATTENTION_PROJECT = 1;//我关注的
    public static final int TYPE_MY_PARTIC_PROJECT = 2;//我参与的
    private static final String KEY_PROJECT_TYPE = "key_project_type";
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @IntDef({TYPE_ALL_PROJECT,
            TYPE_MY_ATTENTION_PROJECT, TYPE_MY_PARTIC_PROJECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QueryProjectType {

    }

    Unbinder unbinder;
    private int pageIndex = 1;
    private int projectType;
    private String attorneyType, myStar;
    ProjectListAdapter projectListAdapter;

    public static MyProjectFragment newInstance(@QueryProjectType int projectType) {
        MyProjectFragment myProjectFragment = new MyProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PROJECT_TYPE, projectType);
        myProjectFragment.setArguments(bundle);
        return myProjectFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_project_mine, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectType = getArguments().getInt(KEY_PROJECT_TYPE);
        if (projectType == TYPE_ALL_PROJECT) {
            attorneyType = "";
            myStar = "";
        } else if (projectType == TYPE_MY_ATTENTION_PROJECT) {
            attorneyType = "";
            myStar = "1";
        } else if (projectType == TYPE_MY_PARTIC_PROJECT) {
            attorneyType = "O";
            myStar = "";
        }
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.null_project);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans5Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(projectListAdapter = new ProjectListAdapter());
        projectListAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, projectListAdapter));
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
        getApi().projectQueryAll(pageIndex, ActionConstants.DEFAULT_PAGE_SIZE, "", "", "", "", attorneyType, myStar)
                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        projectListAdapter.bindData(isRefresh, response.body().result);
                        enableEmptyView(response.body().result);
                        stopRefresh();
                        pageIndex += 1;
                        enableLoadMore(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<ProjectEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void enableEmptyView(List result) {
        if (refreshLayout != null) {
            if (result != null) {
                if (result.size() > 0) {
                    refreshLayout.enableEmptyView(false);
                } else {
                    refreshLayout.enableEmptyView(true);
                }
            }
        }
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

//    @Override
//    protected void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
