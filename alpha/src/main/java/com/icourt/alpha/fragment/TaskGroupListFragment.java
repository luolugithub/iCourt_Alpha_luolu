package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectTaskGroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
public class TaskGroupListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

    public static TaskGroupListFragment newInstance(String projectId) {
        TaskGroupListFragment taskGroupListFragment = new TaskGroupListFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        taskGroupListFragment.setArguments(args);
        return taskGroupListFragment;
    }

    ProjectTaskGroupAdapter projectTaskGroupAdapter;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_group_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(projectTaskGroupAdapter = new ProjectTaskGroupAdapter(true));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), false, R.color.alpha_divider_color));
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
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle arguments = getArguments();
        arguments.putSerializable(KEY_FRAGMENT_RESULT, projectTaskGroupAdapter.getItem(projectTaskGroupAdapter.getSelectedPos()));
        return arguments;
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
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (projectTaskGroupAdapter.isSelectable()) {
            projectTaskGroupAdapter.setSelectedPos(position);
        } else {

        }
    }
}
