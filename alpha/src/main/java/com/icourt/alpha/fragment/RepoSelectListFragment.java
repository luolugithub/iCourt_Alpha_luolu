package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.RepoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.filter.ListFilter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_R;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/19
 * version 2.1.0
 */
public class RepoSelectListFragment extends BaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    RepoAdapter repoAdapter;
    OnFragmentCallBackListener onFragmentCallBackListener;

    public static RepoSelectListFragment newInstance() {
        RepoSelectListFragment fragment = new RepoSelectListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
        } else {
            try {
                onFragmentCallBackListener = (OnFragmentCallBackListener) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.layout_refresh_recyclerview, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(repoAdapter = new RepoAdapter(SFileConfig.REPO_MINE) {
            @Override
            public void onBindHoder(ViewHolder holder, RepoEntity repoEntity, int position) {
                super.onBindHoder(holder, repoEntity, position);
                ImageView document_expand_iv = holder.obtainView(R.id.document_expand_iv);
                ImageView document_detail_iv = holder.obtainView(R.id.document_detail_iv);
                if (document_detail_iv != null) {
                    document_expand_iv.setVisibility(View.GONE);
                }
                if (document_detail_iv != null) {
                    document_detail_iv.setVisibility(View.GONE);
                }
            }
        });
        repoAdapter.setOnItemClickListener(this);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.startRefresh();
    }


    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(getSFileApi().repoQueryAll(),
                new SFileCallBack<List<RepoEntity>>() {
                    @Override
                    public void onSuccess(Call<List<RepoEntity>> call, Response<List<RepoEntity>> response) {
                        stopRefresh();
                        filterOnlyReadPermissionRepo(response.body());
                        repoAdapter.bindData(isRefresh, response.body());
                    }

                    @Override
                    public void onFailure(Call<List<RepoEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    /**
     * 过滤只读权限的资料库
     *
     * @param datas
     */
    private void filterOnlyReadPermissionRepo(List<RepoEntity> datas) {
        ListFilter.filterItems(datas, new ListFilter.ObjectFilterListener<RepoEntity>() {
            @Override
            public boolean isFilter(@Nullable RepoEntity repoEntity) {
                return repoEntity != null
                        && TextUtils.equals(repoEntity.permission, PERMISSION_R);
            }
        });
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
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        RepoEntity item = repoAdapter.getItem(position);
        if (item == null) return;
        if (onFragmentCallBackListener != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_FRAGMENT_RESULT, item);
            onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
        }
    }
}
