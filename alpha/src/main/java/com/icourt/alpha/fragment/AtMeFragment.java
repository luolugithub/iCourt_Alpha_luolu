package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.MyAtedAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

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
 * date createTime：2017/5/13
 * version 1.0.0
 */
public class AtMeFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.contentEmptyImage)
    ImageView contentEmptyImage;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    MyAtedAdapter myAtedAdapter;

    public static AtMeFragment newInstance() {
        return new AtMeFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.layout_refresh_recyclerview5, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        contentEmptyImage.setImageResource(R.mipmap.bg_no_task);
        contentEmptyText.setText(R.string.empty_list_im_at_me_msg);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAtedAdapter = new MyAtedAdapter());
        myAtedAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout != null) {
                    emptyLayout.setVisibility(myAtedAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
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
    public void onResume() {
        super.onResume();
        getData(true);
    }

    private long getEndlyId() {
        long msg_id = 0;
        if (!myAtedAdapter.getData().isEmpty()) {
            IMMessageCustomBody imMessageCustomBody = myAtedAdapter.getData().get(myAtedAdapter.getData().size() - 1);
            if (imMessageCustomBody != null) {
                msg_id = imMessageCustomBody.id;
            }
        }
        return msg_id;
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        Call<ResEntity<List<IMMessageCustomBody>>> call = null;
        if (isRefresh) {
            call = getChatApi().getAtMeMsg();
        } else {
            call = getChatApi().getAtMeMsg(getEndlyId());
        }
        callEnqueue(call, new SimpleCallBack<List<IMMessageCustomBody>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                myAtedAdapter.bindData(isRefresh, response.body().result);
                stopRefresh();
                enableLoadMore(response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<List<IMMessageCustomBody>>> call, Throwable t) {
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
}
