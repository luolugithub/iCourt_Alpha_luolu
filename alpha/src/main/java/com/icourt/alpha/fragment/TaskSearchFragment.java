package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.activity.TimerDetailActivity;
import com.icourt.alpha.activity.TimerTimingActivity;
import com.icourt.alpha.adapter.TaskAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 搜索任务
 * Company Beijing icourt
 * author  zhaodanyang  E-mail:zhaodanyang@icourt.cc
 * date createTime：2017/9/7
 * version 2.0.0
 */

public class TaskSearchFragment extends BaseTaskFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    public static final String KEY_SEARCH_TASK_TYPE = "search_task_type";
    public static final String KEY_SEARCH_TASK_STATUS_TYPE = "search_task_status_type";
    public static final String KEY_PROJECT_ID = "projectId";
    public static final String KEY_ASSIGN_TOS = "assignTos";

    private Unbinder unbinder;

    @BindView(R.id.et_search_name)
    EditText etSearchName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;

    /**
     * 搜索任务type
     */
    int searchTaskType;
    /**
     * 搜索任务状态type
     */
    int taskStatuType;
    /**
     * 项目的id
     */
    String projectId;
    /**
     * 负责人的id的集合
     */
    String assignTos;

    private TaskAdapter taskAdapter;

    /**
     * 搜索已完成的全部任务
     *
     * @param context
     * @param taskStatuType  0:未完成；1：已完成；2：已删除
     * @param searchTaskType 0:全部；1：新任务；2：我关注的；3我部门的
     * @param projectId      项目id
     */
    public static TaskSearchFragment newInstance(@NonNull Context context, String assignTos, int searchTaskType, int taskStatuType, String projectId) {
        TaskSearchFragment taskSearchFragment = new TaskSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_SEARCH_TASK_TYPE, searchTaskType);
        bundle.putInt(KEY_SEARCH_TASK_STATUS_TYPE, taskStatuType);
        bundle.putString(KEY_PROJECT_ID, projectId);
        bundle.putString(KEY_ASSIGN_TOS, assignTos);
        taskSearchFragment.setArguments(bundle);
        return taskSearchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_search_task, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            searchTaskType = getArguments().getInt(KEY_SEARCH_TASK_TYPE);
            taskStatuType = getArguments().getInt(KEY_SEARCH_TASK_STATUS_TYPE);
            projectId = getArguments().getString(KEY_PROJECT_ID);
            assignTos = getArguments().getString(KEY_ASSIGN_TOS);
        }
        return view;
    }

    @Override
    protected void initView() {
        contentEmptyText.setText(R.string.empty_list_task_search_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        taskAdapter.setOnItemClickListener(this);
        taskAdapter.setOnItemChildClickListener(this);
        taskAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (contentEmptyText != null) {
                    if (!TextUtils.isEmpty(etSearchName.getText())) {
                        contentEmptyText.setVisibility(taskAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                    } else {
                        contentEmptyText.setVisibility(View.GONE);
                    }
                }
            }
        });
        recyclerView.setAdapter(taskAdapter);
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    cancelAllCall();
                    taskAdapter.clearData();
                } else {
                    getData(true);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                SystemUtils.hideSoftKeyBoard(getActivity());
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TaskActionEvent.TASK_REFRESG_ACTION:
                getData(true);
                break;
            case TaskActionEvent.TASK_DELETE_ACTION:
                if (event.entity == null) {
                    return;
                }
                if (taskAdapter != null) {
                    taskAdapter.removeItem(event.entity);
                }
                break;
            case TaskActionEvent.TASK_ADD_ITEM_ACITON:
                if (event.entity == null) {
                    return;
                }
                if (taskAdapter != null) {
                    taskAdapter.addData(event.entity);
                }
                break;
            case TaskActionEvent.TASK_UPDATE_ITEM:
                if (event.entity == null) {
                    return;
                }
                getData(true);
                break;
            default:
                break;
        }
    }


    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                getActivity().finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        if (etSearchName == null) {
            return;
        }
        String keyword = etSearchName.getText().toString();
        searchTask(keyword);
    }

    /**
     * 搜索任务
     */
    private void searchTask(String keyword) {
        //搜索全部状态的任务
        int statusType = -1;
        //我关注的，新任务，都搜索全部
        searchTaskType = 0;
        if (!TextUtils.isEmpty(assignTos)) {
            callEnqueue(getApi().taskQueryByName(
                    assignTos,
                    keyword,
                    statusType,
                    searchTaskType,
                    projectId),
                    new SimpleCallBack<TaskEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                            if (response.body().result != null && recyclerView != null) {
                                taskAdapter.setNewData(response.body().result.items);
                            }
                        }
                    });
        } else {
            callEnqueue(getApi().taskQueryByNameFromMatter(
                    keyword,
                    statusType,
                    searchTaskType,
                    projectId),
                    new SimpleCallBack<TaskEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                            if (response.body().result != null && recyclerView != null) {
                                taskAdapter.setNewData(response.body().result.items);
                            }
                        }
                    });
        }
    }

    /**
     * 恢复已删除任务（已删除任务列表会调用此接口）
     *
     * @param itemEntity
     */
    private void recoverTaskById(final TaskEntity.TaskItemEntity itemEntity) {
        if (itemEntity == null) {
            return;
        }
        showLoadingDialog(null);
        callEnqueue(getApi().taskRecoverById(itemEntity.id),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        getData(true);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        TaskEntity.TaskItemEntity taskItemEntity = taskAdapter.getItem(i);
        TaskDetailActivity.launch(getContext(), taskItemEntity.id);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        final TaskEntity.TaskItemEntity itemEntity = taskAdapter.getItem(i);
        switch (view.getId()) {
            case R.id.task_item_start_timming:
                if (itemEntity == null) {
                    return;
                }
                if (!itemEntity.isTiming) {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
                    startTiming(itemEntity);
                } else {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    stopTiming(itemEntity);
                }
                break;
            case R.id.task_item_checkbox:
                if (itemEntity == null) {
                    return;
                }
                //说明是已删除的任务，要恢复任务
                if (!itemEntity.valid) {
                    recoverTaskById(itemEntity);
                } else {
                    //完成任务
                    if (!itemEntity.state) {
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(getActivity(), getString(R.string.task_is_confirm_complete_task), itemEntity, SHOW_FINISH_DIALOG);
                            } else {
                                updateTaskState(itemEntity, true);
                            }
                        } else {
                            updateTaskState(itemEntity, true);
                        }
                    } else {
                        //取消完成任务
                        updateTaskState(itemEntity, false);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void startTimingBack(TaskEntity.TaskItemEntity requestEntity, Response<TimeEntity.ItemEntity> response) {
        taskAdapter.updateItem(requestEntity);
        if (response.body() != null) {
            TimerTimingActivity.launch(getActivity(), response.body());
        }
    }

    @Override
    protected void stopTimingBack(TaskEntity.TaskItemEntity requestEntity) {
        taskAdapter.updateItem(requestEntity);
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        if (timer != null) {
            TimerDetailActivity.launch(getActivity(), timer);
        }
    }

    @Override
    protected void taskDeleteBack(@NonNull TaskEntity.TaskItemEntity itemEntity) {
        getData(true);
    }

    @Override
    protected void taskUpdateBack(@ChangeType int actionType, @NonNull TaskEntity.TaskItemEntity itemEntity) {
        getData(true);
    }

    @Override
    protected void taskTimingUpdateEvent(String taskId) {
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
