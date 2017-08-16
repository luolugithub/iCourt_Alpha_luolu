package com.icourt.alpha.fragment;

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
import com.icourt.alpha.adapter.TaskSelectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 未完成任务
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/8/15
 * version 2.0.0
 */

public class FinishedTaskFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {

    String projectId = null;
    String selectedTaskId;
    Unbinder unbinder;
    TaskSelectAdapter taskSelectAdapter;
    HeaderFooterAdapter<TaskSelectAdapter> headerFooterAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.header_comm_search_input_et)
    EditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;

    /**
     * @param projectId 为空 为我的任务
     * @return
     */
    public static FinishedTaskFragment newInstance(@Nullable String projectId, String selectedTaskId) {
        FinishedTaskFragment unFinishTaskFragment = new FinishedTaskFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putString("selectedTaskId", selectedTaskId);
        unFinishTaskFragment.setArguments(args);
        return unFinishTaskFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_timer_relation_task, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString("projectId");
        selectedTaskId = getArguments().getString("selectedTaskId");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), true));
        headerFooterAdapter = new HeaderFooterAdapter<>(taskSelectAdapter = new TaskSelectAdapter(true));
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);
        taskSelectAdapter.setOnItemClickListener(this);
        taskSelectAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (contentEmptyText == null) return;
                contentEmptyText.setVisibility(taskSelectAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
            }
        });

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
                    searchTaskByName(s.toString());
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
                            searchTaskByName(headerCommSearchInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        headerCommSearchInputLl.setVisibility(View.GONE);
        showLoadingDialog(null);
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        taskSelectAdapter.clearSelected();
        if (!TextUtils.isEmpty(projectId)) {
            getApi().projectQueryTaskList(projectId, 1, 0, 1, -1)
                    .enqueue(new SimpleCallBack<TaskEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                            dismissLoadingDialog();
                            if (response.body().result != null) {
                                taskSelectAdapter.bindData(isRefresh, response.body().result.items);
                                setSelectedTask();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
        } else {
            getApi().taskListQuery(0,
                    getLoginUserId(),
                    1,
                    0,
                    "dueTime",
                    1,
                    -1,
                    0).enqueue(new SimpleCallBack<TaskEntity>() {
                @Override
                public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                    dismissLoadingDialog();
                    if (response.body().result != null) {
                        taskSelectAdapter.bindData(isRefresh, response.body().result.items);
                        setSelectedTask();
                    }
                }

                @Override
                public void onFailure(Call<ResEntity<TaskEntity>> call, Throwable t) {
                    super.onFailure(call, t);
                    dismissLoadingDialog();
                }
            });
        }
    }

    /**
     * 设置选中的item
     */
    private void setSelectedTask() {
        List<TaskEntity.TaskItemEntity> data = taskSelectAdapter.getData();
        TaskEntity.TaskItemEntity itemEntity = new TaskEntity.TaskItemEntity();
        itemEntity.id = selectedTaskId;
        int indexOf = data.indexOf(itemEntity);
        if (indexOf >= 0) {
            taskSelectAdapter.setSelectedPos(indexOf);
        }
    }

    /**
     * 按名称搜索任务
     *
     * @param taskName
     */
    private void searchTaskByName(final String taskName) {
        if (TextUtils.isEmpty(taskName)) return;
        taskSelectAdapter.clearSelected();
        if (!TextUtils.isEmpty(projectId)) {
            //pms 环境有
            getApi().taskQueryByName(null, taskName, 0, 0, projectId)
                    .enqueue(new SimpleCallBack<TaskEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                            if (response.body().result != null) {
                                taskSelectAdapter.bindData(true, response.body().result.items);
                                setSelectedTask();
                            }
                        }
                    });
        } else {
            //pms 环境有
            getApi().taskQueryByName(getLoginUserId(), taskName, 0, 0)
                    .enqueue(new SimpleCallBack<TaskEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                            if (response.body().result != null) {
                                taskSelectAdapter.bindData(true, response.body().result.items);
                                setSelectedTask();
                            }
                        }
                    });
        }

    }

    @OnClick({R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        taskSelectAdapter.setSelectedPos(adapter.getRealPos(position));
        if (getParentFragment() instanceof TaskSelectDialogFragment) {
            ((TaskSelectDialogFragment) getParentFragment()).unFinishClearSelected();
        }
    }

    /**
     * 获取选中的item
     *
     * @return
     */
    public TaskEntity.TaskItemEntity getSelectedTask() {
        int position = taskSelectAdapter.getSelectedPos();
        if (position >= 0) {
            return taskSelectAdapter.getItem(taskSelectAdapter.getSelectedPos());
        }
        return null;
    }

    public void clearSelected() {
        taskSelectAdapter.clearSelected();
    }
}
