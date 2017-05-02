package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupMemberAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupMemberEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.PinyinComparator;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  讨论组成员列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/23
 * version 1.0.0
 */
public class GroupMemberActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String STRING_TOP = "↑︎";
    private static final String KEY_GROUP_ID = "key_tid";
    private static final String ACTION_SELECT = "action_select";
    private static final String ACTION_INVITATION = "action_invitation";
    private static final String KEY_GROUP_NAME = "key_groupName";
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
    LinearLayoutManager linearLayoutManager;
    GroupMemberAdapter groupMemberAdapter;
    HeaderFooterAdapter<GroupMemberAdapter> headerFooterAdapter;
    @BindView(R.id.recyclerIndexBar)
    WaveSideBar recyclerIndexBar;
    SuspensionDecoration mDecoration;

    /**
     * 浏览
     *
     * @param context
     * @param groupId
     * @param groupName
     */
    public static void launch(
            @NonNull Context context,
            @NonNull String groupId,
            @Nullable String groupName) {
        if (context == null) return;
        if (TextUtils.isEmpty(groupId)) return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_GROUP_NAME, groupName);
        context.startActivity(intent);
    }

    /**
     * 选择
     *
     * @param context
     * @param groupId
     * @param reqCode
     */
    public static void launchSelect(
            @NonNull Activity context,
            @NonNull String groupId,
            int reqCode) {
        if (context == null) return;
        if (TextUtils.isEmpty(groupId)) return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.setAction(ACTION_SELECT);
        context.startActivityForResult(intent, reqCode);
    }

    /**
     * 邀请
     *
     * @param context
     * @param groupId
     * @param reqCode
     */
    public static void launchInvitation(
            @NonNull Activity context,
            @NonNull String groupId,
            int reqCode) {
        if (context == null) return;
        if (TextUtils.isEmpty(groupId)) return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.setAction(ACTION_INVITATION);
        context.startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        if (TextUtils.equals(getIntent().getAction(), ACTION_SELECT)) {
            setTitle("选择成员");
        } else {
            String groupName = getIntent().getStringExtra(KEY_GROUP_NAME);
            setTitle(TextUtils.isEmpty(groupName) ? "成员" : groupName);
        }
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<>(groupMemberAdapter = new GroupMemberAdapter(Const.VIEW_TYPE_ITEM));
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_input_text, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        EditText header_input_et = (EditText) headerView.findViewById(R.id.header_input_et);
        header_input_et.clearFocus();
        header_input_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    getData(true);
                } else {
                    serachGroupMember(s.toString());
                }
            }
        });


        recyclerView.setAdapter(headerFooterAdapter);

        //可以选择的
        if (TextUtils.equals(ACTION_INVITATION, getIntent().getAction())) {
            groupMemberAdapter.setSelectable(true);
        }


        groupMemberAdapter.setOnItemClickListener(this);
        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
        mDecoration.setHeaderViewCount(headerFooterAdapter.getHeaderCount());
        recyclerView.addItemDecoration(mDecoration);

        recyclerIndexBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                if (TextUtils.equals(index, STRING_TOP)) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                    return;
                }
                for (int i = 0; i < groupMemberAdapter.getItemCount(); i++) {
                    GroupMemberEntity item = groupMemberAdapter.getItem(i);
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

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().getGroupMemeber(getIntent().getStringExtra(KEY_GROUP_ID))
                .enqueue(new SimpleCallBack<List<GroupMemberEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupMemberEntity>>> call, Response<ResEntity<List<GroupMemberEntity>>> response) {
                        stopRefresh();
                        if (response.body().result != null) {
                            IndexUtils.setSuspensions(getContext(), response.body().result);
                            Collections.sort(response.body().result, new PinyinComparator<GroupMemberEntity>());

                            if (TextUtils.equals(ACTION_SELECT, getIntent().getAction())) {
                                GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
                                groupMemberEntity.isShowSuspension = false;
                                groupMemberEntity.name = "所有人";
                                response.body().result.add(0, groupMemberEntity);
                            }


                            groupMemberAdapter.bindData(true, response.body().result);
                            updateIndexBar(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<GroupMemberEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    /**
     * 搜索用户
     *
     * @param name
     */
    private void serachGroupMember(String name) {
        getApi().queryGroupContacts(name)
                .enqueue(new SimpleCallBack<List<GroupMemberEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupMemberEntity>>> call, Response<ResEntity<List<GroupMemberEntity>>> response) {
                        if (response.body().result == null) return;
                        groupMemberAdapter.clearSelected();
                        IndexUtils.setSuspensions(getContext(), response.body().result);
                        Collections.sort(response.body().result, new PinyinComparator<GroupMemberEntity>());
                        groupMemberAdapter.bindData(true, response.body().result);
                        updateIndexBar(response.body().result);
                    }
                });
    }

    /**
     * 更新indextBar
     *
     * @param data
     */
    private void updateIndexBar(List<GroupMemberEntity> data) {
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
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (TextUtils.equals(getIntent().getAction(), ACTION_SELECT)) {
            Intent intent = getIntent();
            intent.putExtra("key_member", groupMemberAdapter.getItem(groupMemberAdapter.getRealPos(position)));
            setResult(RESULT_OK, intent);
            finish();
        } else if (TextUtils.equals(getIntent().getAction(), ACTION_INVITATION)) {
            groupMemberAdapter.toggleSelected(position);
        }
    }
}
