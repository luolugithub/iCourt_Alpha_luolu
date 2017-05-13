package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;

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
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class ProjectListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    ProjectAdapter projectAdapter;

    public static ProjectListFragment newInstance() {
        return new ProjectListFragment();
    }

    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_list_project, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(projectAdapter = new ProjectAdapter());
        projectAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        getApi().projectSelectListQuery()
                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        dismissLoadingDialog();
                        projectAdapter.bindData(true, response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<ProjectEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_FRAGMENT_RESULT, projectAdapter.getItem(position));
            ((OnFragmentCallBackListener) getParentFragment()).onFragmentCallBack(ProjectListFragment.this, 1, bundle);
        } else {
            if (onFragmentCallBackListener != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_FRAGMENT_RESULT, projectAdapter.getItem(position));
                onFragmentCallBackListener.onFragmentCallBack(ProjectListFragment.this, 1, bundle);
            }
        }
    }
}
