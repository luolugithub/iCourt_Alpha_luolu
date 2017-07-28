package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectTaskGroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 任务组列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class TaskGroupSelectFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

    public static TaskGroupSelectFragment newInstance(String projectId) {
        TaskGroupSelectFragment taskGroupListFragment = new TaskGroupSelectFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        taskGroupListFragment.setArguments(args);
        return taskGroupListFragment;
    }

    ProjectTaskGroupAdapter projectTaskGroupAdapter;
    OnFragmentCallBackListener onFragmentCallBackListener;

    private String getProjectId() {
        if (getArguments() != null) {
            return getArguments().getString("projectId");
        }
        return null;
    }

    private void setProjectId(String projectId) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            arguments = new Bundle();
            setArguments(arguments);
        }
        arguments.putString("projectId", projectId);
    }

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
        View view = super.onCreateView(R.layout.fragment_dialog_task_group_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(projectTaskGroupAdapter = new ProjectTaskGroupAdapter(true));
        projectTaskGroupAdapter.setOnItemClickListener(this);
        projectTaskGroupAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout == null) return;
                emptyLayout.setVisibility(projectTaskGroupAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
            }
        });
        getData(true);
    }

    @OnClick({R.id.bt_ok, R.id.bt_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_FRAGMENT_RESULT, projectTaskGroupAdapter.getItem(projectTaskGroupAdapter.getSelectedPos()));
                    ((OnFragmentCallBackListener) getParentFragment()).onFragmentCallBack(TaskGroupSelectFragment.this, 0, bundle);
                } else {
                    if (onFragmentCallBackListener != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KEY_FRAGMENT_RESULT, projectTaskGroupAdapter.getItem(projectTaskGroupAdapter.getSelectedPos()));
                        onFragmentCallBackListener.onFragmentCallBack(TaskGroupSelectFragment.this, 0, bundle);
                    }
                }
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament != this) return;
        if (bundle != null) {
            setProjectId(bundle.getString("projectId"));
        }
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        String projectId = getProjectId();
        if (TextUtils.isEmpty(projectId)) return;
        projectTaskGroupAdapter.clearSelected();
        getApi().projectQueryTaskGroupList(projectId)
                .enqueue(new SimpleCallBack<List<TaskGroupEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<TaskGroupEntity>>> call, Response<ResEntity<List<TaskGroupEntity>>> response) {
                        projectTaskGroupAdapter.bindData(true, response.body().result);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (projectTaskGroupAdapter.isSelectable()) {
            projectTaskGroupAdapter.setSelectedPos(position);
        } else {

        }
    }
}
