package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.CustomIndexBarDataHelper;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 讨论组列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupListActivity extends BaseActivity {
    private static final String KEY_GROUP_QUERY_TYPE = "GroupQueryType";
    public static final int GROUP_TYPE_MY_JOIN = 0;
    public static final int GROUP_TYPE_TYPE_ALL = 1;
    @BindView(R.id.recyclerIndexBar)
    IndexBar recyclerIndexBar;

    @IntDef({GROUP_TYPE_MY_JOIN,
            GROUP_TYPE_TYPE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GroupQueryType {

    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    HeaderFooterAdapter<GroupAdapter> headerFooterAdapter;
    GroupAdapter groupAdapter;
    SuspensionDecoration mDecoration;

    public static void launch(@NonNull Context context, @GroupQueryType int type) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupListActivity.class);
        intent.putExtra(KEY_GROUP_QUERY_TYPE, type);
        context.startActivity(intent);
    }

    @GroupQueryType
    private int getGroupQueryType() {
        switch (getIntent().getIntExtra(KEY_GROUP_QUERY_TYPE, 0)) {
            case 0:
                return GROUP_TYPE_MY_JOIN;
            case 1:
                return GROUP_TYPE_TYPE_ALL;
        }
        return GROUP_TYPE_TYPE_ALL;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        switch (getGroupQueryType()) {
            case GROUP_TYPE_MY_JOIN:
                setTitle("我加入的讨论组");
                break;
            case GROUP_TYPE_TYPE_ALL:
                setTitle("所有讨论组");
                break;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
        recyclerView.addItemDecoration(mDecoration);

        headerFooterAdapter = new HeaderFooterAdapter<>(groupAdapter = new GroupAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);
        recyclerView.setAdapter(headerFooterAdapter);
        recyclerIndexBar
                //.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)
                .setmLayoutManager(linearLayoutManager);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.setPullRefreshEnable(true);
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        Call<ResEntity<List<GroupEntity>>> groupsCall;
        switch (getGroupQueryType()) {
            case GROUP_TYPE_MY_JOIN:
                groupsCall = getApi().getMyJoinedGroups();
                break;
            default:
                groupsCall = getApi().getAllGroups();
                break;
        }
        groupsCall.enqueue(new SimpleCallBack<List<GroupEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<GroupEntity>>> call, Response<ResEntity<List<GroupEntity>>> response) {
                stopRefresh();
                groupAdapter.bindData(isRefresh, response.body().result);
                updateIndexBar(groupAdapter.getData());
            }

            @Override
            public void onFailure(Call<ResEntity<List<GroupEntity>>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
            }
        });
    }

    /**
     * 更新indextBar
     *
     * @param data
     */
    private void updateIndexBar(List<GroupEntity> data) {
        List<GroupEntity> wrapDatas = new ArrayList<GroupEntity>(data);
        GroupEntity headerGroupEntity = new GroupEntity();
        headerGroupEntity.isNotNeedToPinyin = true;
        headerGroupEntity.setBaseIndexTag("↑︎");
        wrapDatas.add(0, headerGroupEntity);
        try {
            recyclerIndexBar.setDataHelper(new CustomIndexBarDataHelper()).setmSourceDatas(wrapDatas).invalidate();
            mDecoration.setmDatas(wrapDatas);
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                GroupSearchActivity.launch(getContext(),
                        v,
                        getGroupQueryType());
                break;
            default:
                super.onClick(v);
                break;
        }

    }
}
