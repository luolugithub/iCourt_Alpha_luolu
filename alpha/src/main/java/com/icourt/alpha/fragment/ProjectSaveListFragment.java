package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class ProjectSaveListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    ProjectAdapter projectAdapter;
    HeaderFooterAdapter<ProjectAdapter> headerFooterAdapter;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.header_comm_search_input_et)
    EditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;


    public static ProjectSaveListFragment newInstance() {
        return new ProjectSaveListFragment();
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
        recyclerView.setHasFixedSize(true);
        headerFooterAdapter = new HeaderFooterAdapter<>(projectAdapter = new ProjectAdapter(true));
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);
        projectAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout == null) return;
                emptyLayout.setVisibility(projectAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
            }
        });
        projectAdapter.setOnItemClickListener(this);
        projectAdapter.setSelectable(false);
        headerCommSearchInputEt.addTextChangedListener(new TextWatcher() {
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
                    searchProjectByName(s.toString());
                }
            }
        });
        headerCommSearchInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                        if (!TextUtils.isEmpty(headerCommSearchInputEt.getText())) {
                            searchProjectByName(headerCommSearchInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        headerCommSearchInputLl.setVisibility(View.GONE);
        getData(true);
    }

    @OnClick({R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
                break;
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
        }
    }


    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().projectPmsSelectListQuery("MAT:matter.document:readwrite")
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

    /**
     * 按名称搜索项目
     *
     * @param projectName
     */
    private void searchProjectByName(final String projectName) {
        if (TextUtils.isEmpty(projectName)) return;
        //本地搜索
        List<ProjectEntity> projectEntities = new ArrayList<>();
        if (projectAdapter.getData() != null && projectAdapter.getData().size() > 0) {
            for (ProjectEntity projectEntity : projectAdapter.getData()) {
                if (projectEntity.name.contains(projectName)) {
                    projectEntities.add(projectEntity);
                }
            }
        }
        projectAdapter.clearData();
        projectAdapter.bindData(true, projectEntities);


        //pms独有 带权限
//        getApi().projectSelectByTask("0,2,7", projectName)
//                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
//                    @Override
//                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
//                        projectAdapter.clearData();
//                        projectAdapter.bindData(true, response.body().result);
//                    }
//                });
        //不带权限的
      /*  getApi().projectQueryByName(projectName, 1)
                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        projectAdapter.clearData();
                        projectAdapter.bindData(true, response.body().result);
                        setSelectedProject();
                    }
                });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
        }
        if (onFragmentCallBackListener != null) {
            Bundle bundle = new Bundle();

            bundle.putString("projectId", projectAdapter.getItem(adapter.getRealPos(position)).pkId);
            bundle.putString("projectName", projectAdapter.getItem(adapter.getRealPos(position)).name);

            onFragmentCallBackListener.onFragmentCallBack(ProjectSaveListFragment.this, 1, bundle);
        }
    }
}
