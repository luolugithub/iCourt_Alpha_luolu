package com.icourt.alpha.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectListAdapter;
import com.icourt.alpha.adapter.TaskItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 搜索项目
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/27
 * version 2.0.0
 */

public class SearchProjectActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {
    private static final String KEY_SEARCH_PRIORITY = "search_priority";
    private static final String KEY_SEARCH_PROJECT_TYPE = "search_search_project_type";
    private static final String KEY_SEARCH_TASK_TYPE = "search_search_task_type";
    private static final String KEY_SEARCH_TASK_STATUS_TYPE = "search_search_task_status_type";
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

    ProjectListAdapter projectListAdapter;
    TaskItemAdapter taskItemAdapter;

    public static final int SEARCH_PROJECT = 1;//搜索项目
    public static final int SEARCH_TASK = 2;//搜索任务

    @IntDef({SEARCH_PROJECT,
            SEARCH_TASK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SEARCH_PRIORITY {

    }

    int search_priority;
    int searchProjectType;//搜索项目type
    int searchTaskType;//搜索任务type

    int taskStatuType;//搜索任务状态type
    String projectId, assignTos;

    /**
     * 搜索未完成（全部、新任务、我关注的）的任务
     *
     * @param context
     * @param searchTaskType
     * @param search_priority
     */
    public static void launchTask(@NonNull Context context, String assignTos, int searchTaskType, @SEARCH_PRIORITY int search_priority) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchProjectActivity.class);
        intent.putExtra(KEY_SEARCH_PRIORITY, search_priority);
        intent.putExtra(KEY_SEARCH_TASK_TYPE, searchTaskType);
        intent.putExtra("assignTos", assignTos);
        context.startActivity(intent);
    }

    /**
     * 搜索已完成的全部任务
     *
     * @param context
     * @param taskStatuType   0:未完成；1：已完成；2：已删除
     * @param searchTaskType  0:全部；1：新任务；2：我关注的；3我部门的
     * @param projectId       项目id
     * @param search_priority
     */
    public static void launchFinishTask(@NonNull Context context, String assignTos, int searchTaskType, int taskStatuType, @SEARCH_PRIORITY int search_priority, String projectId) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchProjectActivity.class);
        intent.putExtra(KEY_SEARCH_PRIORITY, search_priority);
        intent.putExtra(KEY_SEARCH_TASK_TYPE, searchTaskType);
        intent.putExtra(KEY_SEARCH_TASK_STATUS_TYPE, taskStatuType);
        intent.putExtra("projectId", projectId);
        intent.putExtra("assignTos", assignTos);
        context.startActivity(intent);
    }

    public static void launchProject(@NonNull Context context, int searchProjectType, @SEARCH_PRIORITY int search_priority) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchProjectActivity.class);
        intent.putExtra(KEY_SEARCH_PRIORITY, search_priority);
        intent.putExtra(KEY_SEARCH_PROJECT_TYPE, searchProjectType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_project);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        search_priority = getIntent().getIntExtra(KEY_SEARCH_PRIORITY, -1);
        searchProjectType = getIntent().getIntExtra(KEY_SEARCH_PROJECT_TYPE, -1);
        searchTaskType = getIntent().getIntExtra(KEY_SEARCH_TASK_TYPE, -1);
        taskStatuType = getIntent().getIntExtra(KEY_SEARCH_TASK_STATUS_TYPE, -1);
        projectId = getIntent().getStringExtra("projectId");
        assignTos = getIntent().getStringExtra("assignTos");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (search_priority == SEARCH_PROJECT) {
            recyclerView.setAdapter(projectListAdapter = new ProjectListAdapter());
            projectListAdapter.setOnItemClickListener(this);
        } else if (search_priority == SEARCH_TASK) {
            recyclerView.setAdapter(taskItemAdapter = new TaskItemAdapter());
            taskItemAdapter.setOnItemClickListener(this);
            taskItemAdapter.setOnItemChildClickListener(this);
        }
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
                    if (search_priority == SEARCH_PROJECT) {
                        projectListAdapter.clearData();
                    } else if (search_priority == SEARCH_TASK) {
                        taskItemAdapter.clearData();
                    }
                } else {
                    getData(true);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                SystemUtils.hideSoftKeyBoard(SearchProjectActivity.this);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        String keyword = etSearchName.getText().toString();
        if (search_priority == SEARCH_PROJECT) {
            searchProject(keyword);
        } else if (search_priority == SEARCH_TASK) {
            searchTask(keyword);
        }
    }

    /**
     * 搜索项目
     */
    private void searchProject(String keyword) {
        getApi().projectQueryByName(keyword, searchProjectType).enqueue(new SimpleCallBack<List<ProjectEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                projectListAdapter.bindData(true, response.body().result);
            }
        });
    }

    /**
     * 搜索任务
     */
    private void searchTask(String keyword) {
        int statusType = -1;
        if (taskStatuType == -1) {
            statusType = 0;
        } else if (taskStatuType == 0) {
            statusType = taskStatuType;
        } else {
            statusType = 1;
        }
        searchTaskType = 0;//我关注的，新任务，都搜索全部
        if (!TextUtils.isEmpty(assignTos)) {
            getApi().taskQueryByName(assignTos, keyword, statusType, searchTaskType, projectId).enqueue(new SimpleCallBack<TaskEntity>() {
                @Override
                public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                    if (response.body().result != null) {
                        taskItemAdapter.bindData(true, response.body().result.items);
                    }
                }
            });
        } else {
            getApi().taskQueryByNameFromMatter(keyword, statusType, searchTaskType, projectId).enqueue(new SimpleCallBack<TaskEntity>() {
                @Override
                public void onSuccess(Call<ResEntity<TaskEntity>> call, Response<ResEntity<TaskEntity>> response) {
                    if (response.body().result != null) {
                        taskItemAdapter.bindData(true, response.body().result.items);
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof ProjectListAdapter) {
            ProjectEntity projectEntity = (ProjectEntity) adapter.getItem(position);
            ProjectDetailActivity.launch(this, projectEntity.pkId, projectEntity.name);
        } else if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity taskItemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            TaskDetailActivity.launch(this, taskItemEntity.id);
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskItemAdapter) {
            TaskEntity.TaskItemEntity itemEntity = (TaskEntity.TaskItemEntity) adapter.getItem(position);
            switch (view.getId()) {
                case R.id.task_item_start_timming:
                    if (itemEntity.isTiming) {
                        TimerManager.getInstance().stopTimer();
                        ((ImageView) view).setImageResource(R.mipmap.icon_start_20);
                    } else {
                        TimerManager.getInstance().addTimer(getTimer(itemEntity));
                        ((ImageView) view).setImageResource(R.drawable.orange_side_dot_bg);
                    }
                    break;
                case R.id.task_item_checkbox:
                    CheckBox checkbox = (CheckBox) view;
                    if (checkbox.isChecked()) {//完成任务
                        if (itemEntity.attendeeUsers != null) {
                            if (itemEntity.attendeeUsers.size() > 1) {
                                showFinishDialog(view.getContext(), "该任务由多人负责,确定完成?", itemEntity, checkbox);
                            } else {
                                updateTask(itemEntity, true, checkbox);
                            }
                        } else {
                            updateTask(itemEntity, true, checkbox);
                        }
                    } else {
                        updateTask(itemEntity, false, checkbox);
                    }
                    break;

            }
        }
    }

    /**
     * 获取添加计时实体
     *
     * @return
     */
    private TimeEntity.ItemEntity getTimer(TaskEntity.TaskItemEntity taskItemEntity) {
        TimeEntity.ItemEntity itemEntity = new TimeEntity.ItemEntity();
        if (taskItemEntity != null) {
            itemEntity.taskPkId = taskItemEntity.id;
            itemEntity.name = taskItemEntity.name;
            itemEntity.workDate = DateUtils.millis();
            itemEntity.createUserId = getLoginUserId();
            itemEntity.username = getLoginUserInfo().getName();
            itemEntity.startTime = DateUtils.millis();
            if (taskItemEntity.matter != null) {
                itemEntity.matterPkId = taskItemEntity.matter.id;
            }
        }
        return itemEntity;
    }

    /**
     * 显示多人任务提醒
     *
     * @param context
     * @param message
     * @param itemEntity
     * @param checkbox
     */
    private void showFinishDialog(final Context context, String message, final TaskEntity.TaskItemEntity itemEntity, final CheckBox checkbox) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE://确定
                        updateTask(itemEntity, true, checkbox);
                        break;
                    case Dialog.BUTTON_NEGATIVE://取消
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     * @param state
     * @param checkbox
     */
    private void updateTask(final TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckBox checkbox) {
        if (state) {
            showLoadingDialog("完成任务...");
        } else {
            showLoadingDialog("取消完成任务...");
        }
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                checkbox.setChecked(state);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                checkbox.setChecked(!state);
            }
        });
    }

    /**
     * 获取任务json
     *
     * @param itemEntity
     * @param state
     * @return
     */
    private String getTaskJson(TaskEntity.TaskItemEntity itemEntity, boolean state) {
        try {
            itemEntity.state = state;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("state", itemEntity.state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteTaskEvent(TaskActionEvent event) {
        if (event == null) return;
        if (event.action == TaskActionEvent.TASK_REFRESG_ACTION) {
            getData(true);
        }
    }

    /**
     * 计时事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_ADD:

                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
                if (updateItem != null) {
                    getChildPositon(updateItem.taskPkId);
                    updateChildTimeing(updateItem.taskPkId, true);
                }
                break;
            case TimingEvent.TIMING_STOP:
                if (lastEntity != null) {
                    lastEntity.isTiming = false;
                    taskItemAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 获取item所在子容器position
     *
     * @param taskId
     * @return
     */
    private int getChildPositon(String taskId) {
        if (taskItemAdapter.getData() != null) {
            for (int i = 0; i < taskItemAdapter.getData().size(); i++) {
                TaskEntity.TaskItemEntity item = taskItemAdapter.getData().get(i);
                if (item != null) {
                    if (TextUtils.equals(item.id, taskId)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    TaskEntity.TaskItemEntity lastEntity;

    /**
     * 更新item
     *
     * @param taskId
     */
    private void updateChildTimeing(String taskId, boolean isTiming) {
        int childPos = getChildPositon(taskId);
        if (childPos >= 0) {
            TaskEntity.TaskItemEntity entity = taskItemAdapter.getItem(childPos);
            if (entity != null) {
                if (lastEntity != null)
                    if (!TextUtils.equals(entity.id, lastEntity.id)) {
                        lastEntity.isTiming = false;
                        taskItemAdapter.notifyDataSetChanged();
                    }
                if (entity.isTiming != isTiming) {
                    entity.isTiming = isTiming;
                    taskItemAdapter.updateItem(entity);
                    lastEntity = entity;
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
