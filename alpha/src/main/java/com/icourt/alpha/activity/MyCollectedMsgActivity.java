package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ImUserMessageAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.IMStringWrapEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 我收藏的消息
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyCollectedMsgActivity extends BaseActivity {
    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyCollectedMsgActivity.class);
        context.startActivity(intent);
    }

    ImUserMessageAdapter imUserMessageAdapter;
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
    private int pageIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ated);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("我收藏的消息");
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.my_center_null_collect_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imUserMessageAdapter = new ImUserMessageAdapter(getUserToken()));
        imUserMessageAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, imUserMessageAdapter));
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
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 0;
        }
        getApi().getMyCollectedMessages(pageIndex, ActionConstants.DEFAULT_PAGE_SIZE)
                .enqueue(new SimpleCallBack<List<IMStringWrapEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<IMStringWrapEntity>>> call, Response<ResEntity<List<IMStringWrapEntity>>> response) {
                        imUserMessageAdapter.bindData(isRefresh, response.body().result);
                        stopRefresh();
                        pageIndex += 1;
                        enableLoadMore(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<IMStringWrapEntity>>> call, Throwable t) {
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
}
