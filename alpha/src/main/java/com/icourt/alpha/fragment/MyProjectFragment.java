package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectSearchActivity;
import com.icourt.alpha.adapter.ProjectListAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.event.ProjectActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @IntDef({TYPE_ALL_PROJECT,
            TYPE_MY_ATTENTION_PROJECT, TYPE_MY_PARTIC_PROJECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QueryProjectType {
    }

    Unbinder unbinder;

    private int pageIndex = 1;
    private int projectType;
    private String attorneyType, myStar;
    HeaderFooterAdapter<ProjectListAdapter> headerFooterAdapter;
    ProjectListAdapter projectListAdapter;

    boolean isFirstTimeIntoPage = true;
    int status = 2;//默认为进行中
    String matterType = "";
    LinearLayoutManager linearLayoutManager;

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
        EventBus.getDefault().register(this);
        if (projectType == TYPE_ALL_PROJECT) {
            attorneyType = "";
            myStar = "";
            recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.empty_list_project);
        } else if (projectType == TYPE_MY_ATTENTION_PROJECT) {
            attorneyType = "";
            myStar = "1";
            status = -1;
            matterType = "";
            recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.empty_list_project_follow);
        } else if (projectType == TYPE_MY_PARTIC_PROJECT) {
            attorneyType = "O";
            myStar = "";
            recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.empty_list_project_joined);
        }
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));

        headerFooterAdapter = new HeaderFooterAdapter<>(projectListAdapter = new ProjectListAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView.getRecyclerView());
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);


        recyclerView.setAdapter(headerFooterAdapter);
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
        refreshLayout.autoRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                ProjectSearchActivity.launchProject(getContext(), projectType);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取空文案
     *
     * @param stateType
     * @return
     */
    //TODO 加字符串资源注解
    private int getEmptyContentId(int stateType) {
        switch (stateType) {
            case Const.PROJECT_STATUS_ING:
                return R.string.empty_list_project_executing;
            case Const.PROJECT_STATUS_FINISH:
                return R.string.empty_list_project_finished;
            case Const.PROJECT_STATUS_END:
                return R.string.empty_list_project_shelve;
        }
        return R.string.empty_list_project;
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament instanceof MyProjectFragment) {
            if (type == 100) {//根据状态筛选
                if (bundle != null) {
                    status = bundle.getInt("status");
                    recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, getEmptyContentId(status));
                }
            } else if (type == 101) {//根据类型筛选
                if (bundle != null) {
                    List<Integer> paramList = bundle.getIntegerArrayList(KEY_FRAGMENT_RESULT);
                    if (paramList != null && paramList.size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Integer integer : paramList) {
                            stringBuilder.append(String.valueOf(integer)).append(",");
                        }
                        matterType = stringBuilder.substring(0, stringBuilder.length() - 1);
                    } else {
                        matterType = "";
                    }
                }
            }
            if (projectType == TYPE_MY_ATTENTION_PROJECT) {
                status = -1;
                matterType = "";
                attorneyType = "";
            }
            getData(true);
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        if (isRefresh) {
            pageIndex = 1;
        }
        String sta = "";
        if (status >= 0) {
            sta = String.valueOf(status);
        }
        callEnqueue(
                getApi().projectQueryAll(
                        pageIndex,
                        ActionConstants.DEFAULT_PAGE_SIZE,
                        "name",
                        "",
                        sta,
                        matterType,
                        attorneyType,
                        myStar),
                new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        projectListAdapter.bindData(isRefresh, response.body().result);

                        //第一次进入 隐藏搜索框
                        if (isFirstTimeIntoPage) {
                            linearLayoutManager.scrollToPositionWithOffset(headerFooterAdapter.getHeaderCount(), 0);
                            isFirstTimeIntoPage = false;
                        }

                        if (isRefresh)
                            recyclerView.enableEmptyView(response.body().result);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefrshEvent(ProjectActionEvent event) {
        if (event == null) return;
        if (event.action == ProjectActionEvent.PROJECT_REFRESG_ACTION) {
            refreshLayout.autoRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}
