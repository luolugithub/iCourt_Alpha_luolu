package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SelectGroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.SelectGroupBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择团队
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/18
 * version 2.0.0
 */

//TODO 改名字 改成TeamSelectActivity  GroupSelectActivity太像关联讨论组

public class GroupSelectActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    SelectGroupAdapter selectGroupAdapter;
    final List<SelectGroupBean> groupBeanList = new ArrayList<>();
    List<SelectGroupBean> userGroups;

    public static void launchForResult(@NonNull Activity context, List<SelectGroupBean> groupBeanList, int requestCode) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupSelectActivity.class);
        intent.putExtra("groupBeanList", (Serializable) groupBeanList);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("负责团队");
        if (getLoginUserInfo() != null)
            userGroups = getLoginUserInfo().getGroups();
        groupBeanList.clear();
        List<SelectGroupBean> groupList = (List<SelectGroupBean>) getIntent().getSerializableExtra("groupBeanList");
        if (groupList != null) {
            groupBeanList.addAll(groupList);
        }
        recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_user, R.string.empty_list_customer_team);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(this, true));
        recyclerView.setAdapter(selectGroupAdapter = new SelectGroupAdapter());
        selectGroupAdapter.setOnItemClickListener(this);
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

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                CustomerPersonCreateActivity.launchSetResultFromGroup(GroupSelectActivity.this, selectGroupAdapter.getSelectedData());
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        if (getLoginUserInfo() == null) return;
        callEnqueue(
                getApi().officeGroupsQuery(),
                new SimpleCallBack<List<SelectGroupBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<SelectGroupBean>>> call, Response<ResEntity<List<SelectGroupBean>>> response) {
                        stopRefresh();
                        if (response.body().result != null && response.body().result.size() <= 0) {
                            response.body().result.addAll(groupBeanList);
                        }
                        selectGroupAdapter.bindData(true, response.body().result);
                        if (response.body().result != null && groupBeanList != null) {
                            for (int i = 0; i < response.body().result.size(); i++) {
                                for (int j = 0; j < groupBeanList.size(); j++) {
                                    if (TextUtils.equals(response.body().result.get(i).groupId, groupBeanList.get(j).groupId)) {
                                        selectGroupAdapter.setSelected(i, true);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<SelectGroupBean>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        SelectGroupBean groupBean = (SelectGroupBean) adapter.getItem(position);
        int last = 0;
        if (selectGroupAdapter.getData() != null) {
            if (selectGroupAdapter.getData().contains(groupBean) && selectGroupAdapter.getSelectedData().contains(groupBean)) {
                if (selectGroupAdapter.getSelectedData() != null) {
                    for (SelectGroupBean selectGroupBean : selectGroupAdapter.getData()) {
                        if (selectGroupAdapter.getSelectedData().contains(selectGroupBean)) {
                            last += 1;
                        }
                    }
                    if (last > 1) {
                        selectGroupAdapter.toggleSelected(position);
                    } else {
                        showTopSnackBar("至少保留一个自己的所属团队");
                    }
                }
            } else {
                selectGroupAdapter.toggleSelected(position);
            }
        }
    }
}
