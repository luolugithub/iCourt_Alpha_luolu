package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectTaskGroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/9
 * version 2.0.0
 */

public class ProjectTaskGroupActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final String PROJECT_ADD_TASK_PERMISSION = "MAT:matter.task:add";
    private static final String PROJECT_EDIT_TASK_PERMISSION = "MAT:matter.task:edit";
    /**
     * 新建
     */
    private static final int CREATE_GROUP_REQUEST_CODE = 0;
    /**
     * 编辑
     */
    private static final int UPDATE_GROUP_REQUEST_CODE = 1;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.titleAction)
    ImageView titleAction;

    String projectId;
    ProjectTaskGroupAdapter projectTaskGroupAdapter;
    /**
     * 是否可以添加任务组
     */
    boolean isCanAddGroup = false;
    /**
     * 是否可以编辑任务组
     */
    boolean isCanEditGroup = false;

    public static void launch(@NonNull Context context, @NonNull String projectId) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(projectId)) {
            return;
        }
        Intent intent = new Intent(context, ProjectTaskGroupActivity.class);
        intent.putExtra(KEY_PROJECT_ID, projectId);
        context.startActivity(intent);
    }

    public static void launchSetResult(@NonNull Activity activity, @NonNull TaskGroupEntity taskGroupEntity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ProjectTaskGroupActivity.class);
        intent.putExtra(KEY_ACTIVITY_RESULT, taskGroupEntity);
        activity.setResult(RESULT_OK, intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_group_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        projectId = getIntent().getStringExtra(KEY_PROJECT_ID);
        setTitle(R.string.manage_task_group_text);
        recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, R.string.task_list_group_null_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(this, true));
        recyclerView.setAdapter(projectTaskGroupAdapter = new ProjectTaskGroupAdapter(false));
        projectTaskGroupAdapter.setOnItemClickListener(this);
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
        titleAction.setVisibility(View.INVISIBLE);
        checkProjectPms();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //添加任务组
            case R.id.titleAction:
                TaskGroupCreateActivity.launchForResult(this, projectId, TaskGroupCreateActivity.CREAT_TASK_GROUP_TYPE, CREATE_GROUP_REQUEST_CODE);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取项目权限
     */
    private void checkProjectPms() {
        callEnqueue(
                getApi().permissionQuery(getLoginUserId(), "MAT", projectId),
                new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {

                        if (response.body().result != null) {
                            if (response.body().result.contains(PROJECT_ADD_TASK_PERMISSION)) {
                                isCanAddGroup = true;
                                titleAction.setVisibility(View.VISIBLE);
                            }
                            if (response.body().result.contains(PROJECT_EDIT_TASK_PERMISSION)) {
                                isCanEditGroup = true;
                            }
                            if (projectTaskGroupAdapter != null) {
                                projectTaskGroupAdapter.setCanEditGroup(isCanEditGroup);
                                projectTaskGroupAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            TaskGroupEntity taskGroupEntity = (TaskGroupEntity) data.getSerializableExtra(KEY_ACTIVITY_RESULT);
            if (requestCode == CREATE_GROUP_REQUEST_CODE) {
                if (taskGroupEntity != null) {
                    projectTaskGroupAdapter.addItem(0, taskGroupEntity);
                }
            } else if (requestCode == UPDATE_GROUP_REQUEST_CODE) {
                getData(true);
            }
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(
                getApi().projectQueryTaskGroupList(projectId),
                new SimpleCallBack<List<TaskGroupEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<TaskGroupEntity>>> call, Response<ResEntity<List<TaskGroupEntity>>> response) {
                        stopRefresh();
                        if (response.body().result != null) {
                            projectTaskGroupAdapter.bindData(isRefresh, response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<TaskGroupEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (isCanEditGroup) {
            TaskGroupEntity entity = (TaskGroupEntity) adapter.getItem(position);
            TaskGroupCreateActivity.launchForResult(this, entity, TaskGroupCreateActivity.UPDATE_TASK_GROUP_TYPE, UPDATE_GROUP_REQUEST_CODE);
        }
    }
}
