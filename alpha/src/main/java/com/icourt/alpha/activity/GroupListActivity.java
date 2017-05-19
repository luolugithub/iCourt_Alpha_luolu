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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.event.GroupActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.PinyinComparator;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.entity.event.GroupActionEvent.GROUP_ACTION_JOIN;
import static com.icourt.alpha.entity.event.GroupActionEvent.GROUP_ACTION_QUIT;

/**
 * Description 讨论组列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String STRING_TOP = "↑︎";
    private static final String KEY_GROUP_QUERY_TYPE = "GroupQueryType";
    public static final int GROUP_TYPE_MY_JOIN = 0;
    public static final int GROUP_TYPE_TYPE_ALL = 1;
    @BindView(R.id.recyclerIndexBar)
    WaveSideBar recyclerIndexBar;


    @IntDef({GROUP_TYPE_MY_JOIN,
            GROUP_TYPE_TYPE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GroupQueryType {

    }

    public static void launch(@NonNull Context context, @GroupQueryType int type) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupListActivity.class);
        intent.putExtra(KEY_GROUP_QUERY_TYPE, type);
        context.startActivity(intent);
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
    LinearLayoutManager linearLayoutManager;

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
        EventBus.getDefault().register(this);
        switch (getGroupQueryType()) {
            case GROUP_TYPE_MY_JOIN:
                setTitle("我加入的讨论组");
                break;
            case GROUP_TYPE_TYPE_ALL:
                setTitle("所有讨论组");
                break;
        }
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<>(groupAdapter = new GroupAdapter());
        groupAdapter.setOnItemClickListener(this);
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);

        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
        mDecoration.setHeaderViewCount(headerFooterAdapter.getHeaderCount());
        recyclerView.addItemDecoration(mDecoration);

        recyclerView.setAdapter(headerFooterAdapter);
        recyclerIndexBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                if (TextUtils.equals(index, STRING_TOP)) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                    return;
                }
                for (int i = 0; i < groupAdapter.getItemCount(); i++) {
                    GroupEntity item = groupAdapter.getItem(i);
                    if (item != null && TextUtils.equals(item.getSuspensionTag(), index)) {
                        linearLayoutManager
                                .scrollToPositionWithOffset(i + headerFooterAdapter.getHeaderCount(), 0);
                        return;
                    }
                }
            }
        });

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupEvent(GroupActionEvent event) {
        if (event == null) return;
        switch (event.action) {
            case GROUP_ACTION_JOIN:
                if (getGroupQueryType() == GROUP_TYPE_MY_JOIN) {
                    refreshLayout.startRefresh();
                }
                break;
            case GROUP_ACTION_QUIT:
                if (getGroupQueryType() == GROUP_TYPE_MY_JOIN) {
                    refreshLayout.startRefresh();
                }
                break;
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        final Call<ResEntity<List<GroupEntity>>> groupsCall;
        switch (getGroupQueryType()) {
            case GROUP_TYPE_MY_JOIN:
                groupsCall = getChatApi().groupsQueryJoind(0, true);
                break;
            default:
                groupsCall = getChatApi().groupsQuery(0, true);
                break;
        }
        groupsCall.enqueue(new SimpleCallBack<List<GroupEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<GroupEntity>>> call, Response<ResEntity<List<GroupEntity>>> response) {
                stopRefresh();
                if (response.body().result != null) {
                    IndexUtils.setSuspensions(getContext(), response.body().result);
                    Collections.sort(response.body().result, new PinyinComparator<GroupEntity>());
                    groupAdapter.bindData(true, response.body().result);
                    updateIndexBar(groupAdapter.getData());
                }
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
        try {
            ArrayList<String> suspensions = IndexUtils.getSuspensions(data);
            suspensions.add(0, STRING_TOP);
            recyclerIndexBar.setIndexItems(suspensions.toArray(new String[suspensions.size()]));
            mDecoration.setmDatas(data);
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

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter == groupAdapter) {
            GroupEntity item = groupAdapter.getItem(groupAdapter.getRealPos(position));
            if (item != null) {
                switch (getGroupQueryType()) {
                    case GROUP_TYPE_MY_JOIN:
                        ChatActivity.launchTEAM(getContext(),
                                item.tid,
                                item.name,
                                0);
                        break;
                    case GROUP_TYPE_TYPE_ALL:
                        if (isMyJionedGroup(item)) {
                            ChatActivity.launchTEAM(getContext(),
                                    item.tid,
                                    item.name,
                                    0);
                        } else {
                            GroupDetailActivity.launchTEAM(getContext(), item.tid);
                        }
                        break;
                }
            }
        }
    }


    /**
     * 是否是我加入的群组
     *
     * @param item
     * @return
     */
    private boolean isMyJionedGroup(GroupEntity item) {
        if (item != null) {
            String loginUserId = getLoginUserId();
            if (StringUtils.equalsIgnoreCase(loginUserId, item.admin_id, false)) {
                return true;
            }
            //创建者可能离开群组
           /* if (StringUtils.equalsIgnoreCase(loginUserId, item.create_id, false)) {
                return true;
            }*/
            return StringUtils.containsIgnoreCase(item.members, loginUserId);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
