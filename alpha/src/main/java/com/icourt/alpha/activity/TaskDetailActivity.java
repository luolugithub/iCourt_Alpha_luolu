package com.icourt.alpha.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskUsersAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRefreshFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.TaskAttachmentFragment;
import com.icourt.alpha.fragment.TaskCheckItemFragment;
import com.icourt.alpha.fragment.TaskDetailFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskAllotSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnUpdateTaskListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icourt.alpha.R.id.task_user_recyclerview;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/11
 * version 2.0.0
 */

public class TaskDetailActivity extends BaseActivity
        implements OnFragmentCallBackListener,
        BaseRecyclerAdapter.OnItemClickListener,
        OnUpdateTaskListener {

    private static final String KEY_TASK_ID = "key_task_id";
    private static final int SHOW_DELETE_DIALOG = 0;//删除提示对话框
    private static final int SHOW_FINISH_DIALOG = 1;//完成任务提示对话框
    private static final int START_COMMENT_FORRESULT_CODE = 0;//跳转评论code

    String taskId;
    BaseRefreshFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.task_checkbox)
    CheckBox taskCheckbox;
    @BindView(R.id.task_name)
    TextView taskName;
    @BindView(R.id.task_user_pic)
    ImageView taskUserPic;
    @BindView(R.id.task_user_name)
    TextView taskUserName;
    @BindView(task_user_recyclerview)
    RecyclerView taskUserRecyclerview;
    @BindView(R.id.task_time)
    TextView taskTime;
    @BindView(R.id.task_start_iamge)
    ImageView taskStartIamge;
    @BindView(R.id.task_tablayout)
    TabLayout taskTablayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.task_user_layout)
    LinearLayout taskUserLayout;
    @BindView(R.id.task_users_layout)
    LinearLayout taskUsersLayout;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.task_time_parent_layout)
    LinearLayout taskTimeParentLayout;
    @BindView(R.id.comment_tv)
    TextView commentTv;

    int myStar = -1;
    boolean isStrat = false;
    TaskEntity.TaskItemEntity taskItemEntity;
    TaskUsersAdapter usersAdapter;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;

    final SparseArray<CharSequence> tabTitles = new SparseArray<>();
    @BindView(R.id.task_tieming_image)
    ImageView taskTiemingImage;
    TaskDetailFragment taskDetailFragment;
//    boolean isEditTask = false;//编辑任务权限
//    boolean isDeleteTask = false;//删除任务权限
//    boolean isAddTime = false;//添加计时权限

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public static void launch(@NonNull Context context, @NonNull String taskId) {
        if (context == null) return;
        if (TextUtils.isEmpty(taskId)) return;
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(KEY_TASK_ID, taskId);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("");
        EventBus.getDefault().register(this);
        taskId = getIntent().getStringExtra(KEY_TASK_ID);
        baseFragmentAdapter = new BaseRefreshFragmentAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return TaskDetailFragment.newInstance(taskItemEntity);
                    case 1:
                        return TaskCheckItemFragment.newInstance(taskItemEntity.id, hasTaskEditPermission() && !isFinishedTask());
                    case 2:
                        return TaskAttachmentFragment.newInstance(taskItemEntity.id, (hasTaskAddDocument() && hasTaskEditPermission() && !isFinishedTask()));
                }
                return super.getItem(position);
            }

            @Override
            public int getCount() {
                return taskItemEntity == null ? 0 : 3;
            }
        };
        viewpager.setAdapter(baseFragmentAdapter);
        taskTablayout.setupWithViewPager(viewpager);
        taskTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) return;
                tab.setText(tabTitles.get(tab.getPosition(), ""));
                SystemUtils.hideSoftKeyBoard(TaskDetailActivity.this);
                taskTablayout.setFocusable(true);
                taskTablayout.setFocusableInTouchMode(true);
                taskTablayout.requestFocus();//请求焦点
                taskTablayout.findFocus();//获取焦点
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        titleAction2.setImageResource(R.mipmap.header_icon_more);
        getData(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData(true);
    }

    @OnClick({R.id.main_content,
            R.id.titleAction,
            R.id.titleAction2,
            R.id.task_name,
            R.id.task_user_recyclerview,
            R.id.comment_layout,
            R.id.comment_tv,
            R.id.task_checkbox,
            R.id.task_user_layout,
            R.id.task_users_layout,
            R.id.task_start_iamge})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        SystemUtils.hideSoftKeyBoard(this);
        mainContent.setFocusable(true);
        mainContent.setFocusableInTouchMode(true);
        mainContent.requestFocus();//请求焦点
        mainContent.findFocus();//获取焦点
        switch (v.getId()) {
            case R.id.titleAction://关注
                if (myStar == TaskEntity.UNATTENTIONED) {
                    addStar();
                } else {
                    deleteStar();
                }
                break;
            case R.id.titleAction2://更多
                showBottomMeau();
                break;
            case R.id.task_name:
                if (taskItemEntity != null)
                    if (!taskItemEntity.state) {
                        if (hasTaskEditPermission()) {
                            TaskDescUpdateActivity.launch(getContext(), taskName.getText().toString(), TaskDescUpdateActivity.UPDATE_TASK_NAME);
                        } else {
                            showTopSnackBar("您没有编辑任务的权限");
                        }
                    }
                break;
            case R.id.task_user_layout:
            case R.id.task_users_layout:
                if (taskItemEntity != null)
                    if (!taskItemEntity.state) {
                        if (hasTaskEditPermission()) {
                            if (taskItemEntity.matter != null) {
                                showTaskAllotSelectDialogFragment(taskItemEntity.matter.id);
                            } else {
                                showTopSnackBar("请先选择项目");
                            }
                        } else {
                            showTopSnackBar("您没有编辑任务的权限");
                        }
                    }
                break;
            case R.id.task_start_iamge://开始计时
                if (isStrat)
                    TimerManager.getInstance().stopTimer();
                else {
                    showLoadingDialog(null);
                    final TimeEntity.ItemEntity itemEntity = getTimer(taskItemEntity);
                    TimerManager.getInstance().addTimer(itemEntity, new Callback<TimeEntity.ItemEntity>() {
                        @Override
                        public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                            if (response.body() != null) {
                                dismissLoadingDialog();
                                itemEntity.pkId = response.body().pkId;
                                TimerTimingActivity.launch(TaskDetailActivity.this, itemEntity);
                            }
                        }

                        @Override
                        public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {
                            dismissLoadingDialog();
                        }
                    });
                }
                break;
            case R.id.task_checkbox://  完成／取消完成
                if (hasTaskEditPermission()) {
                    if (taskItemEntity.state) {
                        if (taskItemEntity.attendeeUsers != null) {
                            //去掉了取消完成的对话框，避免再次加回来。
//                            if (taskItemEntity.attendeeUsers.size() > 1) {
//                                showDeleteDialog("该任务为多人任务，确定要取消完成吗?", SHOW_FINISH_DIALOG);
//                            } else {
//                                updateTask(taskItemEntity, false, taskCheckbox);
//                            }
                            updateTask(taskItemEntity, false, taskCheckbox);
                        } else {
                            updateTask(taskItemEntity, false, taskCheckbox);
                        }
                    } else {
                        if (taskItemEntity.attendeeUsers != null) {
                            if (taskItemEntity.attendeeUsers.size() > 1) {
                                showDeleteDialog("该任务为多人任务，确定要完成吗?", SHOW_FINISH_DIALOG);
                            } else {
                                updateTask(taskItemEntity, true, taskCheckbox);
                            }
                        } else {
                            updateTask(taskItemEntity, true, taskCheckbox);
                        }
                    }
                } else {
                    taskCheckbox.setChecked(!taskCheckbox.isChecked());
                    showTopSnackBar("您没有编辑任务的权限");
                }
                break;
            case R.id.comment_tv:
                CommentListActivity.launchForResult(this,
                        taskItemEntity,
                        START_COMMENT_FORRESULT_CODE,
                        false);
                break;
            case R.id.comment_layout://更多评论动态
                CommentListActivity.launchForResult(this,
                        taskItemEntity,
                        START_COMMENT_FORRESULT_CODE,
                        true);
                break;
        }
    }

    /**
     * 获取添加计时实体
     *
     * @return
     */
    @Deprecated
    private TimeEntity.ItemEntity getTimer() {
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
     * 获取添加计时实体
     *
     * @return
     */
    private TimeEntity.ItemEntity getTimer(TaskEntity.TaskItemEntity taskItemEntity) {
        TimeEntity.ItemEntity itemEntity = new TimeEntity.ItemEntity();
        if (taskItemEntity != null) {
            itemEntity.taskPkId = taskItemEntity.id;
            itemEntity.taskName = taskItemEntity.name;
            itemEntity.name = taskItemEntity.name;
            itemEntity.workDate = DateUtils.millis();
            itemEntity.createUserId = getLoginUserId();
            if (LoginInfoUtils.getLoginUserInfo() != null) {
                itemEntity.username = LoginInfoUtils.getLoginUserInfo().getName();
            }
            itemEntity.startTime = DateUtils.millis();
            if (taskItemEntity.matter != null) {
                itemEntity.matterPkId = taskItemEntity.matter.id;
                itemEntity.matterName = taskItemEntity.matter.name;
            }
        }
        return itemEntity;
    }

    public String toTime(long times) {
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
    }

    public TaskEntity.TaskItemEntity getTaskItemEntity() {
        return taskItemEntity;
    }

    String timmingTaskId;

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
                TimeEntity.ItemEntity itemEntity = TimerManager.getInstance().getTimer();
                if (taskItemEntity != null && itemEntity != null) {
                    if (TextUtils.equals(itemEntity.taskPkId, taskItemEntity.id)) {
                        isStrat = true;
                        taskStartIamge.setImageResource(R.drawable.orange_side_dot_bg);
                        taskTiemingImage.setImageResource(R.mipmap.task_detail_timing);
                    }
                }
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity updateEntity = TimerManager.getInstance().getTimer();
                if (taskItemEntity != null && updateEntity != null) {
                    timmingTaskId = updateEntity.taskPkId;
                    if (TextUtils.equals(updateEntity.taskPkId, taskItemEntity.id)) {
                        isStrat = true;
                        taskStartIamge.setImageResource(R.drawable.orange_side_dot_bg);
                        taskTiemingImage.setImageResource(R.mipmap.task_detail_timing);
                        taskTime.setText(toTime(event.timingSecond));
                    }
                }
                break;
            case TimingEvent.TIMING_STOP:
                if (taskItemEntity != null) {
                    if (TextUtils.equals(timmingTaskId, taskItemEntity.id)) {
                        isStrat = false;
                        taskStartIamge.setImageResource(R.mipmap.time_start_orange);
                        taskTiemingImage.setImageResource(R.mipmap.ic_task_time);
                        long mis = event.timingSecond * 1000;
                        if (mis > 0 && mis / 1000 / 60 <= 0) {
                            mis = 60000;
                        }
                        if (taskItemEntity != null)
                            taskTime.setText(getHm(taskItemEntity.timingSum = (taskItemEntity.timingSum + mis)));
                    }
                }
                break;
        }
    }

    /**
     * 显示底部菜单
     */

    private void showBottomMeau() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("删除"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                if (taskItemEntity != null) {
                                    if (taskItemEntity.attendeeUsers != null) {
                                        if (taskItemEntity.attendeeUsers.size() > 1) {
                                            showDeleteDialog("该任务为多人任务，确定要删除吗?", SHOW_DELETE_DIALOG);
                                        } else {
                                            showDeleteDialog("是非成败转头空，确定要删除吗?", SHOW_DELETE_DIALOG);
                                        }
                                    } else {
                                        showDeleteDialog("是非成败转头空，确定要删除吗?", SHOW_DELETE_DIALOG);
                                    }
                                }
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 删除多人任务对话框
     *
     * @param message
     */
    private void showDeleteDialog(String message, final int type) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        if (type == SHOW_DELETE_DIALOG) {
                            deleteTask();
                        } else if (type == SHOW_FINISH_DIALOG) {
                            if (taskItemEntity != null) {
                                if (taskItemEntity.state) {
                                    updateTask(taskItemEntity, false, taskCheckbox);
                                } else {
                                    updateTask(taskItemEntity, true, taskCheckbox);
                                }
                            }
                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        if (type == SHOW_FINISH_DIALOG) {
                            if (taskCheckbox != null)
                                taskCheckbox.setChecked(taskItemEntity.state);
                        }
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage(message); //设置内容
        builder.setPositiveButton("确认", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    @Override
    protected void getData(boolean isRefresh) {
        //有返回权限
        getApi().taskQueryDetailWithRight(taskId).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                dismissLoadingDialog();
                taskItemEntity = response.body().result;
                setDataToView(response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 是否有任务删除权限
     */
    private boolean hasTaskDeletePermission() {
        if (taskItemEntity != null && taskItemEntity.right != null) {
            return taskItemEntity.right.contains("MAT:matter.task:delete");
        }
        return false;
    }

    /**
     * 是否有任务编辑权限
     */
    private boolean hasTaskEditPermission() {
        if (taskItemEntity != null && taskItemEntity.right != null) {
            return taskItemEntity.right.contains("MAT:matter.task:edit");
        }
        return false;
    }

    /**
     * 是否是已经完成的任务
     *
     * @return
     */
    private boolean isFinishedTask() {
        if (taskItemEntity != null) {
            return taskItemEntity.state;
        }
        return false;
    }

    /**
     * 是否有上传附件权限
     *
     * @return
     */
    private boolean hasTaskAddDocument() {
        if (taskItemEntity != null && taskItemEntity.right != null) {
            return taskItemEntity.right.contains("MAT:matter.document:readwrite");
        }
        return false;
    }

    /**
     * 是否有添加计时权限
     */
    private boolean hasAddTimerPermission() {
        if (taskItemEntity != null && taskItemEntity.right != null) {
            return taskItemEntity.right.contains("MAT:matter.timeLog:add");
        }
        return false;
    }

    /**
     * 展示选择负责人对话框
     */
    public void showTaskAllotSelectDialogFragment(String projectId) {
        String tag = TaskAllotSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        if (taskItemEntity != null)
            TaskAllotSelectDialogFragment.newInstance(projectId, taskItemEntity.attendeeUsers)
                    .show(mFragTransaction, tag);
    }

    public String getHm(long times) {
        times /= 1000;
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        if (second > 0) minute += 1;
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    /**
     * 设置数据到view
     *
     * @param taskItemEntity
     */
    private void setDataToView(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
            if (taskName == null) return;
            if (titleAction2 != null) {
                titleAction2.setVisibility(hasTaskDeletePermission() ? View.VISIBLE : View.GONE);
            }
            taskStartIamge.setVisibility(hasAddTimerPermission() ? View.VISIBLE : View.GONE);
            taskName.setText(taskItemEntity.name);
            myStar = taskItemEntity.attentioned;
            commentTv.setText(taskItemEntity.commentCount + "条动态");
            if (taskItemEntity.state) {
                taskCheckbox.setChecked(true);
            } else {
                taskCheckbox.setChecked(false);
            }
            if (myStar == TaskEntity.ATTENTIONED) {
                titleAction.setImageResource(R.mipmap.header_icon_star_solid);
            } else {
                titleAction.setImageResource(R.mipmap.header_icon_star_line);
            }
            if (taskItemEntity.timingSum > 0 && taskItemEntity.timingSum / 1000 / 60 <= 0) {
                taskTime.setText(getHm(60000));
            } else {
                taskTime.setText(getHm(taskItemEntity.timingSum));
            }
            String checkTargetStr = String.format("%s/%s", taskItemEntity.doneItemCount, taskItemEntity.itemCount);
            String checkOriginStr = "检查项 " + checkTargetStr;
            SpannableString checkTextForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(checkOriginStr, checkTargetStr, 0xFFCACACA);

            String attachTargetStr = String.valueOf(taskItemEntity.attachmentCount);
            String attachOriginStr = "附件 " + attachTargetStr;
            SpannableString attachTextForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(attachOriginStr, attachTargetStr, 0xFFCACACA);

            tabTitles.put(0, "任务详情");
            tabTitles.put(1, checkTextForegroundColorSpan);
            tabTitles.put(2, attachTextForegroundColorSpan);
            baseFragmentAdapter.bindTitle(true, Arrays.asList(tabTitles.get(0, ""),
                    tabTitles.get(1, ""),
                    tabTitles.get(2, "")));
            baseFragmentAdapter.notifyRefresh();


            if (taskItemEntity.attendeeUsers != null) {
                if (taskItemEntity.attendeeUsers.size() > 0) {
                    if (taskItemEntity.attendeeUsers.size() > 1) {
                        taskUsersLayout.setVisibility(View.VISIBLE);
                        taskUserLayout.setVisibility(View.GONE);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        layoutManager.setReverseLayout(true);
                        taskUserRecyclerview.setLayoutManager(layoutManager);
                        taskUserRecyclerview.setAdapter(usersAdapter = new TaskUsersAdapter(this));
                        usersAdapter.setOnItemClickListener(this);
                        Collections.reverse(taskItemEntity.attendeeUsers);
                        usersAdapter.bindData(true, taskItemEntity.attendeeUsers);
                    } else if (taskItemEntity.attendeeUsers.size() == 1) {
                        taskUsersLayout.setVisibility(View.GONE);
                        taskUserLayout.setVisibility(View.VISIBLE);
                        if (taskItemEntity.attendeeUsers.get(0) != null) {
                            GlideUtils.loadUser(this, taskItemEntity.attendeeUsers.get(0).pic, taskUserPic);
                            taskUserName.setText(taskItemEntity.attendeeUsers.get(0).userName);
                        } else {
                            setNoAllocation();
                        }
                    }
                } else {
                    setNoAllocation();
                }
            } else {
                setNoAllocation();
            }
        }
    }

    /**
     * 设置未分配
     */
    private void setNoAllocation() {
        taskUsersLayout.setVisibility(View.GONE);
        taskUserLayout.setVisibility(View.VISIBLE);
        taskUserPic.setVisibility(View.GONE);
        taskUserName.setText("未分配");
    }

    /**
     * 添加关注
     */

    private void addStar() {
        showLoadingDialog(null);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("taskId", taskId);
        getApi().taskAddStar(RequestUtils.createJsonBody(jsonObject.toString())).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                myStar = TaskEntity.ATTENTIONED;
                titleAction.setImageResource(R.mipmap.header_icon_star_solid);
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 取消关注
     */
    private void deleteStar() {
        showLoadingDialog(null);
        getApi().taskDeleteStar(taskId).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                myStar = TaskEntity.UNATTENTIONED;
                titleAction.setImageResource(R.mipmap.header_icon_star_line);
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 删除任务
     */
    private void deleteTask() {
        showLoadingDialog(null);
        getApi().taskDelete(taskId).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                TaskDetailActivity.this.finish();
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 修改任务
     *
     * @param itemEntity
     * @param state
     * @param checkbox
     */
    private void updateTask(TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckBox checkbox) {
        showLoadingDialog(null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                if (checkbox != null)
                    checkbox.setChecked(state);
                getData(true);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                if (checkbox != null)
                    checkbox.setChecked(!state);
            }

            @Override
            public void defNotify(String noticeStr) {
                showTopSnackBar(noticeStr);
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
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            JsonArray jsonarr = new JsonArray();
            if (itemEntity.attendeeUsers != null) {
                if (itemEntity.attendeeUsers.size() > 0) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        jsonarr.add(attendeeUser.userId);
                    }
                }
            }
            jsonObject.add("attendees", jsonarr);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("获取任务json失败", e);
        }
        return null;
    }

    GestureDetector gestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    boolean canFastScroll = e1.getRawX() > appbar.getBottom() && e2.getRawX() > appbar.getBottom();
                    if (!canFastScroll) return super.onFling(e1, e2, velocityX, velocityY);
                    int limit = DensityUtil.dip2px(getContext(), 3500);
                    if (velocityY > limit) {
                        appbar.setExpanded(true, true);
                    } else if (velocityY < -limit) {
                        appbar.setExpanded(false, true);
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Deprecated
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof TaskAllotSelectDialogFragment) {//选择负责人回调
            if (params != null) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (attusers != null && attusers.size() > 0) {
                    if (attusers.size() == 1) {
                        taskUsersLayout.setVisibility(View.GONE);
                        taskUserLayout.setVisibility(View.VISIBLE);
                        GlideUtils.loadUser(this, attusers.get(0).pic, taskUserPic);
                        taskUserName.setText(attusers.get(0).userName);
                    } else {
                        taskUsersLayout.setVisibility(View.VISIBLE);
                        taskUserLayout.setVisibility(View.GONE);
                        if (taskUserRecyclerview.getLayoutManager() == null) {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            layoutManager.setReverseLayout(true);
                            taskUserRecyclerview.setLayoutManager(layoutManager);
                            taskUserRecyclerview.setAdapter(usersAdapter = new TaskUsersAdapter(this));
                            usersAdapter.setOnItemClickListener(this);
                        }
                    }
                } else {
                    taskUsersLayout.setVisibility(View.GONE);
                    taskUserLayout.setVisibility(View.VISIBLE);
                    taskUserName.setText("未分配");
                    taskUserPic.setVisibility(View.GONE);
                }
                if (taskItemEntity.attendeeUsers != null) {
                    taskItemEntity.attendeeUsers.clear();
                    taskItemEntity.attendeeUsers.addAll(attusers);
                    updateTask(taskItemEntity, taskItemEntity.state, taskCheckbox);
                    if (usersAdapter != null) {
                        if (taskItemEntity.attendeeUsers.size() > 0)
                            Collections.reverse(taskItemEntity.attendeeUsers);
                        usersAdapter.bindData(true, taskItemEntity.attendeeUsers);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTaskNameEvent(TaskActionEvent event) {
        if (event == null) return;
        if (taskItemEntity == null) return;
        if (event.action == TaskActionEvent.TASK_UPDATE_NAME_ACTION) {//修改任务名称
            String desc = event.desc;
            if (!TextUtils.isEmpty(desc)) {
                taskItemEntity.name = desc;
                updateTask(taskItemEntity, taskItemEntity.state, null);
            }
        } else if (event.action == TaskActionEvent.TASK_UPDATE_PROJECT_ACTION) {//修改任务所属项目
            taskUsersLayout.setVisibility(View.GONE);
            taskUserLayout.setVisibility(View.VISIBLE);
            if (getLoginUserInfo() != null) {
                GlideUtils.loadUser(this, getLoginUserInfo().getPic(), taskUserPic);
                taskUserName.setText(getLoginUserInfo().getName());
                if (taskItemEntity.attendeeUsers != null) {
                    taskItemEntity.attendeeUsers.clear();
                    TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUserEntity = new TaskEntity.TaskItemEntity.AttendeeUserEntity();
                    attendeeUserEntity.pic = getLoginUserInfo().getPic();
                    attendeeUserEntity.userName = getLoginUserInfo().getName();
                    attendeeUserEntity.userId = getLoginUserInfo().getUserId();
                    taskItemEntity.attendeeUsers.add(attendeeUserEntity);
                }
            }
            if (taskItemEntity.matter != null) {
                taskItemEntity.matter.id = event.projectId;
            }
        } else if (event.action == TaskActionEvent.TASK_REFRESG_ACTION) {
            if (StringUtils.equalsIgnoreCase(getIntent().getStringExtra(KEY_TASK_ID), event.id, false)) {
                getData(true);
            }
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskUsersAdapter) {
            if (taskItemEntity != null) {
                if (!taskItemEntity.state)
                    if (taskItemEntity.matter != null) {
                        showTaskAllotSelectDialogFragment(taskItemEntity.matter.id);
                    } else {
                        showTopSnackBar("请优先选择项目");
                    }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == START_COMMENT_FORRESULT_CODE) {
                int commentCount = data.getIntExtra(KEY_ACTIVITY_RESULT, -1);
                commentTv.setText(commentCount + "条动态");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUpdateCheckItem(String checkItemCount) {
        getData(true);
    }

    @Override
    public void onUpdateDocument(String documentCount) {
        String attachTargetStr = documentCount;
        String attachOriginStr = "附件 " + attachTargetStr;
        SpannableString attachTextForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(attachOriginStr, attachTargetStr, 0xFFCACACA);
        tabTitles.put(2, attachTextForegroundColorSpan);

        baseFragmentAdapter.bindTitle(true, Arrays.asList(tabTitles.get(0, ""),
                tabTitles.get(1, ""),
                tabTitles.get(2, "")));
    }
}
