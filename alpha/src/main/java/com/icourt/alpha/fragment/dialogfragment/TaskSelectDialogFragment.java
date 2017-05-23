package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

    /**
     * @param projectId 为空 为我的任务
     * @return
     */
    public static TaskSelectDialogFragment newInstance(@Nullable String projectId) {
        TaskSelectDialogFragment workTypeSelectDialogFragment = new TaskSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        workTypeSelectDialogFragment.setArguments(args);
        return workTypeSelectDialogFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;
    String projectId;

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
                            if (response.body().result != null) {
                                taskSelectAdapter.bindData(isRefresh, response.body().result.items);
                            }
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
                    if (response.body().result != null) {
                        taskSelectAdapter.bindData(isRefresh, response.body().result.items);
                    }
                }
            });
        }
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
    }
}
