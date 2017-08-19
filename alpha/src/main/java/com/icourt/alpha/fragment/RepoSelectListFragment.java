package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.RepoAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.fragment.FolderTargetListFragment.KEY_SEA_FILE_DST_DIR_PATH;
import static com.icourt.alpha.fragment.FolderTargetListFragment.KEY_SEA_FILE_DST_REPO_ID;

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
    int pageIndex = 1;
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
        recyclerView.setAdapter(repoAdapter = new RepoAdapter(0));
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
        final int pageSize = ActionConstants.DEFAULT_PAGE_SIZE;
        getSFileApi().documentRootQuery(
                pageIndex,
                pageSize,
                null,
                null,
                null)
                .enqueue(new SFileCallBack<List<RepoEntity>>() {
                    @Override
                    public void onSuccess(Call<List<RepoEntity>> call, Response<List<RepoEntity>> response) {
                        stopRefresh();
                        repoAdapter.bindData(isRefresh, response.body());
                        pageIndex += 1;
                        enableLoadMore(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<RepoEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
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
            bundle.putString(KEY_SEA_FILE_DST_REPO_ID, item.repo_id);
            bundle.putString(KEY_SEA_FILE_DST_DIR_PATH, "/");
            onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
        }
    }
}
