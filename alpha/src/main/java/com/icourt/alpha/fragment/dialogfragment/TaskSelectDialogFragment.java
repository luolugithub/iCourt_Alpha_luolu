package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskSelectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
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
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class TaskSelectDialogFragment
        extends BaseDialogFragment
        implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    TaskSelectAdapter taskSelectAdapter;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.header_input_et)
    EditText headerInputEt;
    @BindView(R.id.rl_comm_search)
    RelativeLayout rlCommSearch;

    /**
     * @param projectId 为空 为我的任务
     * @return
     */
    public static TaskSelectDialogFragment newInstance(@Nullable String projectId, String selectedTaskId) {
        TaskSelectDialogFragment workTypeSelectDialogFragment = new TaskSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putString("selectedTaskId", selectedTaskId);
        workTypeSelectDialogFragment.setArguments(args);
        return workTypeSelectDialogFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;
    String projectId;
    String selectedTaskId;

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
        View view = super.onCreateView(R.layout.dialog_fragment_task, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        projectId = getArguments().getString("projectId");
        selectedTaskId = getArguments().getString("selectedTaskId");
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), true));
        recyclerView.setAdapter(taskSelectAdapter = new TaskSelectAdapter(true));
        taskSelectAdapter.setOnItemClickListener(this);

        headerInputEt.addTextChangedListener(new TextWatcher() {
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
        headerInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerInputEt);
                        if (!TextUtils.isEmpty(headerInputEt.getText())) {
                            searchTaskByName(headerInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerInputEt, true);
                    }
                    break;
                }
            }

        });

        rlCommSearch.setVisibility(View.GONE);//先隐藏

        showLoadingDialog(null);
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (!TextUtils.isEmpty(projectId)) {
            getApi().projectQueryTaskList(projectId, 0, 0, 1, -1)
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
                    0,
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
        itemEntity.matterId = selectedTaskId;
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

    @OnClick({R.id.bt_cancel,
            R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_FRAGMENT_RESULT, taskSelectAdapter.getItem(taskSelectAdapter.getSelectedPos()));
                    onFragmentCallBackListener.onFragmentCallBack(TaskSelectDialogFragment.this, 0, bundle);
                }
                dismiss();
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
        taskSelectAdapter.setSelectedPos(position);
        TaskEntity.TaskItemEntity item = taskSelectAdapter.getItem(position);
        if (item != null) {
            selectedTaskId = item.id;
        }
    }
}
