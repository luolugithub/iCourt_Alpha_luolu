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
import android.widget.CheckedTextView;
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
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.TaskAttachmentFragment;
import com.icourt.alpha.fragment.TaskCheckItemFragment;
import com.icourt.alpha.fragment.TaskDetailFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskAllotSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskTimersDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnUpdateTaskListener;
import com.icourt.alpha.utils.BeanUtils;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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

    /**
     * 初始化，传递进来的任务id的key
     */
    private static final String KEY_TASK_ID = "key_task_id";
    /**
     * 用来判断是否要默认切换到检查项，并且弹出键盘的key
     */
    public static final String KEY_IS_CHECK_ITEM = "key_is_check_item";
    /**
     * 用来判断任务是否完成的key
     */
    public static final String KEY_ISFINISH = "isFinish";
    public static final String KEY_VALID = "valid";
    public static final String KEY_TASKITEMENTITY = "taskItemEntity";

    /**
     * 更新每个子Fragment的type标签
     */
    public static final int TYPE_UPDATE_CHILD_FRAGMENT = 100;
    /**
     * 切换到检查项，并且弹出键盘的type标签
     */
    public static final int TYPE_CHECK_ITEM_SHOW_KEYBOARD = 101;

    /**
     * 删除提示对话框
     */
    private static final int SHOW_DELETE_DIALOG = 0;
    /**
     * 完成任务提示对话框
     */
    private static final int SHOW_FINISH_DIALOG = 1;
    /**
     * 恢复任务提示对话框
     */
    private static final int SHOW_RENEW_DIALOG = 2;

    /**
     * 跳转评论code
     */
    private static final int START_COMMENT_FORRESULT_CODE = 0;

    /**
     * 删除二次确认
     */
    private static final int SHOW_DELETE_BUTTOM_SHEET = 0;
    /**
     * 清空二次确认
     */
    private static final int SHOW_CLEAR_BUTTOM_SHEET = 1;
    /**
     * 恢复二次确认
     */
    private static final int SHOW_RENEW_BUTTOM_SHEET = 2;

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.task_checkbox)
    CheckedTextView taskCheckbox;
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
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R.id.task_tieming_image)
    ImageView taskTiemingImage;
    @BindView(R.id.task_users_arrow_iv)
    ImageView taskUsersArrowIv;
    @BindView(R.id.task_user_arrow_iv)
    ImageView taskUserArrowIv;
    @BindView(R.id.comment_edit_tv)
    TextView commentEditTv;
    @BindView(R.id.task_time_sum_layout)
    LinearLayout taskTimeSumLayout;

    String taskId;
    String timmingTaskId;
    BaseFragmentAdapter baseFragmentAdapter;
    int myStar = -1;
    boolean isStart = false;
    /**
     * 是否默认选中检查项tab（创建完任务成功后，跳转到任务详情，会默认选中检查项，选中之后置为false）。
     */
    boolean isSelectedCheckItem = false;
    TaskEntity.TaskItemEntity taskItemEntity, cloneItemEntity;
    TaskUsersAdapter usersAdapter;
    TaskDetailFragment taskDetailFragment;
    TaskCheckItemFragment taskCheckItemFragment;
    TaskAttachmentFragment taskAttachmentFragment;
    TabLayout.OnTabSelectedListener onTabSelectedListener;

    ViewPager.SimpleOnPageChangeListener onPageChangeListener;


    final SparseArray<CharSequence> tabTitles = new SparseArray<>();

    public static void launch(@NonNull Context context, @NonNull String taskId) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(KEY_TASK_ID, taskId);
        context.startActivity(intent);
    }

    /**
     * 新建任务之后跳转到详情并默认选中检查项tab
     *
     * @param context
     * @param taskId
     * @param isSelectedCheckItem
     */
    public static void launchTabSelectCheckItem(@NonNull Context context, @NonNull String taskId, boolean isSelectedCheckItem) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(KEY_TASK_ID, taskId);
        intent.putExtra(KEY_IS_CHECK_ITEM, isSelectedCheckItem);
        context.startActivity(intent);
    }

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

    @Override
    protected void initView() {
        super.initView();
        setTitle("");
        EventBus.getDefault().register(this);
        MobclickAgent.onEvent(this, UMMobClickAgent.look_task_click_id);
        taskId = getIntent().getStringExtra(KEY_TASK_ID);
        isSelectedCheckItem = getIntent().getBooleanExtra(KEY_IS_CHECK_ITEM, false);
        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(baseFragmentAdapter);
        taskTablayout.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (isSelectedCheckItem && position == 1) {
                    return;
                }
                SystemUtils.hideSoftKeyBoard(TaskDetailActivity.this);
                taskTablayout.setFocusable(true);
                taskTablayout.setFocusableInTouchMode(true);
                taskTablayout.requestFocus();
                taskTablayout.findFocus();
                updateTabItemFragment();
            }
        });

        taskTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) {
                    return;
                }
                tab.setText(tabTitles.get(tab.getPosition(), ""));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        titleAction2.setImageResource(R.mipmap.header_icon_more);
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
            R.id.task_start_iamge,
            R.id.task_time_sum_layout,
            R.id.task_time})
    @Override
    public void onClick(View v) {
        SystemUtils.hideSoftKeyBoard(this);
        mainContent.setFocusable(true);
        mainContent.setFocusableInTouchMode(true);
        mainContent.requestFocus();
        mainContent.findFocus();
        switch (v.getId()) {
            //关注
            case R.id.titleAction:
                if (myStar == TaskEntity.UNATTENTIONED) {
                    addStar();
                } else {
                    deleteStar();
                }
                break;
            //更多
            case R.id.titleAction2:
                showBottomMenu();
                break;
            case R.id.task_name:
                if (taskItemEntity == null) {
                    return;
                }
                if (!taskItemEntity.state) {
                    if (hasTaskEditPermission()) {
                        TaskRenameActivity.launch(
                                getContext(),
                                taskItemEntity);
                    } else {
                        showTopSnackBar(getString(R.string.task_not_permission_edit_task));
                    }
                }
                break;
            case R.id.task_user_layout:
            case R.id.task_users_layout:
                if (taskItemEntity == null) {
                    return;
                }
                if (!taskItemEntity.state) {
                    if (hasTaskEditPermission() && taskItemEntity.valid) {
                        if (taskItemEntity.matter != null) {
                            showTaskAllotSelectDialogFragment(taskItemEntity.matter.id);
                        } else {
                            showTopSnackBar(getString(R.string.task_please_check_project));
                        }
                    } else {
                        showTopSnackBar(getString(R.string.task_not_permission_edit_task));
                    }
                }
                break;
            //开始计时
            case R.id.task_start_iamge:
                if (taskItemEntity == null) {
                    return;
                }
                if (isStart) {
                    startTimeMethod();
                } else {
                    stopTimeMethod();
                }
                break;
            //  完成／取消完成
            case R.id.task_checkbox:
                if (taskItemEntity == null) {
                    return;
                }
                if (hasTaskEditPermission()) {
                    if (taskItemEntity.valid) {
                        if (taskItemEntity.state) {
                            if (taskItemEntity.attendeeUsers != null) {
                                updateTask(taskItemEntity, false, taskCheckbox);
                            } else {
                                updateTask(taskItemEntity, false, taskCheckbox);
                            }
                        } else {
                            if (taskItemEntity.attendeeUsers != null) {
                                if (taskItemEntity.attendeeUsers.size() > 1) {
                                    showDeleteDialog(getString(R.string.task_is_confirm_complete_task), SHOW_FINISH_DIALOG);
                                } else {
                                    updateTask(taskItemEntity, true, taskCheckbox);
                                }
                            } else {
                                updateTask(taskItemEntity, true, taskCheckbox);
                            }
                        }
                    } else {
                        if (taskItemEntity.attendeeUsers != null) {
                            if (taskItemEntity.attendeeUsers.size() > 1) {
                                showDeleteDialog(getString(R.string.task_is_confirm_revert_task), SHOW_RENEW_DIALOG);
                            } else {
                                showTwiceSureDialog(getString(R.string.task_is_revert), SHOW_RENEW_BUTTOM_SHEET);
                            }
                        } else {
                            showTwiceSureDialog(getString(R.string.task_is_revert_task), SHOW_RENEW_BUTTOM_SHEET);
                        }
                    }
                } else {
                    taskCheckbox.setChecked(!taskCheckbox.isChecked());
                    showTopSnackBar(getString(R.string.task_not_permission_edit_task));
                }
                break;
            case R.id.comment_tv:
                if (taskItemEntity == null) {
                    return;
                }
                CommentListActivity.launchForResult(this,
                        taskItemEntity,
                        START_COMMENT_FORRESULT_CODE,
                        false);
                break;
            //更多评论动态
            case R.id.comment_layout:
                if (taskItemEntity == null) {
                    return;
                }
                CommentListActivity.launchForResult(this,
                        taskItemEntity,
                        START_COMMENT_FORRESULT_CODE,
                        taskItemEntity.valid);
                break;
            case R.id.task_time_sum_layout:
                if (taskItemEntity == null) {
                    return;
                }
                if (taskItemEntity.timingSum > 0) {
                    showTimersDialogFragment();
                }
                break;
            case R.id.task_time:
                if (taskItemEntity == null) {
                    return;
                }
                if (taskItemEntity.timingSum > 0) {
                    showTimersDialogFragment();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 开始计时
     */
    private void startTimeMethod() {
        MobclickAgent.onEvent(getContext(), UMMobClickAgent.stop_timer_click_id);
        TimerManager.getInstance().stopTimer(new SimpleCallBack<TimeEntity.ItemEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<TimeEntity.ItemEntity>> call, Response<ResEntity<TimeEntity.ItemEntity>> response) {
                TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
                TimerDetailActivity.launch(getContext(), timer);
            }

            @Override
            public void onFailure(Call<ResEntity<TimeEntity.ItemEntity>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 停止计时
     */
    private void stopTimeMethod() {
        showLoadingDialog(null);
        final TimeEntity.ItemEntity itemEntity = getTimer(taskItemEntity);
        MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
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

    public TaskEntity.TaskItemEntity getTaskItemEntity() {
        return taskItemEntity;
    }

    /**
     * 计时事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) {
            return;
        }

        switch (event.action) {
            case TimingEvent.TIMING_ADD:
                TimeEntity.ItemEntity itemEntity = TimerManager.getInstance().getTimer();
                if (taskItemEntity != null && itemEntity != null) {
                    if (TextUtils.equals(itemEntity.taskPkId, taskItemEntity.id)) {
                        isStart = true;
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
                        isStart = true;
                        taskStartIamge.setImageResource(R.drawable.orange_side_dot_bg);
                        taskTiemingImage.setImageResource(R.mipmap.task_detail_timing);
                        taskTime.setText(DateUtils.getTimingStr(event.timingSecond));
                    }
                }
                break;
            case TimingEvent.TIMING_STOP:
                if (taskItemEntity != null) {
                    if (TextUtils.equals(timmingTaskId, taskItemEntity.id)) {
                        isStart = false;
                        taskStartIamge.setImageResource(R.mipmap.time_start_orange);
                        taskTiemingImage.setImageResource(R.mipmap.ic_task_time);
                        long mis = event.timingSecond * 1000;
                        if (mis > 0 && mis / 1000 / 60 <= 0) {
                            mis = 60000;
                        }
                        if (taskItemEntity != null) {
                            taskTime.setText(getHm(taskItemEntity.timingSum + mis));
                            taskItemEntity.timingSum += mis;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示底部菜单
     */
    private void showBottomMenu() {
        if (taskItemEntity == null) {
            return;
        }
        List<String> titles = null;
        if (taskItemEntity.valid) {
            if (taskItemEntity.state) {
                titles = Arrays.asList(getString(R.string.task_update_state_to_unfinish), getString(R.string.task_delete));
            } else {
                titles = Arrays.asList(getString(R.string.task_update_state_to_finish), getString(R.string.task_delete));
            }
        } else {
            titles = Arrays.asList(getString(R.string.task_revert), getString(R.string.task_downright_delete));
        }
        new BottomActionDialog(getContext(),
                null,
                titles,
                1,
                0xFFFF0000,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                if (taskItemEntity == null) {
                                    return;
                                }
                                if (taskItemEntity.valid) {
                                    if (!taskItemEntity.state) {
                                        if (taskItemEntity.attendeeUsers != null) {
                                            if (taskItemEntity.attendeeUsers.size() > 1) {
                                                showDeleteDialog(getString(R.string.task_is_confirm_complete_task), SHOW_FINISH_DIALOG);
                                            } else {
                                                updateTask(taskItemEntity, true, taskCheckbox);
                                            }
                                        } else {
                                            updateTask(taskItemEntity, true, taskCheckbox);
                                        }
                                    } else {
                                        updateTask(taskItemEntity, false, taskCheckbox);
                                    }
                                } else {
                                    if (taskItemEntity.attendeeUsers != null) {
                                        if (taskItemEntity.attendeeUsers.size() > 1) {
                                            showDeleteDialog(getString(R.string.task_is_confirm_revert_task), SHOW_RENEW_DIALOG);
                                        } else {
                                            showTwiceSureDialog(getString(R.string.task_is_revert), SHOW_RENEW_BUTTOM_SHEET);
                                        }
                                    } else {
                                        showTwiceSureDialog(getString(R.string.task_is_revert_task), SHOW_RENEW_BUTTOM_SHEET);
                                    }
                                }
                                break;
                            case 1:
                                if (taskItemEntity.valid) {
                                    if (taskItemEntity != null) {
                                        if (taskItemEntity.attendeeUsers != null) {
                                            if (taskItemEntity.attendeeUsers.size() > 1) {
                                                showDeleteDialog(getString(R.string.task_is_confirm_delete_task), SHOW_DELETE_DIALOG);
                                            } else {
                                                showTwiceSureDialog(getString(R.string.task_is_delete), SHOW_DELETE_BUTTOM_SHEET);
                                            }
                                        } else {
                                            showTwiceSureDialog(getString(R.string.task_is_delete), SHOW_DELETE_BUTTOM_SHEET);
                                        }
                                    }
                                } else {
                                    showTwiceSureDialog(getString(R.string.task_delete_not_revert), SHOW_CLEAR_BUTTOM_SHEET);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 显示二次确认对话框
     */
    private void showTwiceSureDialog(String title, final int type) {
        new BottomActionDialog(getContext(),
                title,
                Arrays.asList(getString(R.string.str_ok)),
                0,
                0xFFFF0000,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                if (type == SHOW_DELETE_BUTTOM_SHEET) {
                                    deleteTask();
                                } else if (type == SHOW_CLEAR_BUTTOM_SHEET) {
                                    clearAllDeletedTask();
                                } else if (type == SHOW_RENEW_BUTTOM_SHEET) {
                                    recoverTaskById(taskId);
                                }
                                break;
                            default:
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
                        } else if (type == SHOW_RENEW_DIALOG) {
                            recoverTaskById(taskId);
                        }
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        if (type == SHOW_FINISH_DIALOG) {
                            if (taskCheckbox != null) {
                                taskCheckbox.setChecked(taskItemEntity.state);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.task_remind));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.task_confirm), dialogOnclicListener);
        builder.setNegativeButton(getString(R.string.task_cancel), dialogOnclicListener);
        builder.create().show();
    }

    @Override
    protected void getData(boolean isRefresh) {
        //有返回权限
        callEnqueue(
                getApi().taskQueryDetailWithRight(taskId),
                new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        dismissLoadingDialog();
                        taskItemEntity = response.body().result;
                        try {
                            cloneItemEntity = (TaskEntity.TaskItemEntity) BeanUtils.cloneTo(taskItemEntity);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
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
            return taskItemEntity.right.contains("MAT:matter.task:edit") || taskItemEntity.right.contains("MAT:matter.task:view");
        }
        return false;
    }

    /**
     * 是否有上传附件权限
     *
     * @return
     */
    private boolean hasDocumentAddPermission() {
        if (taskItemEntity != null && taskItemEntity.right != null) {
            return !taskItemEntity.state && taskItemEntity.valid
                    && taskItemEntity.right.contains("MAT:matter.document:readwrite");
        }
        return false;
    }

    /**
     * 是否有文件浏览权限
     * 可读 可读写
     *
     * @return
     */
    private boolean hasDocumentLookPermission() {
        if (taskItemEntity != null
                && taskItemEntity.right != null) {
            return taskItemEntity.right.contains("MAT:matter.document:readwrite")
                    || taskItemEntity.right.contains("MAT:matter.document:read");
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
        if (taskItemEntity != null) {
            TaskAllotSelectDialogFragment.newInstance(projectId, taskItemEntity.attendeeUsers)
                    .show(mFragTransaction, tag);
        }
    }

    /**
     * 展示计时列表对话框
     */
    public void showTimersDialogFragment() {
        String tag = TaskTimersDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        if (taskItemEntity != null) {
            TaskTimersDialogFragment.newInstance(taskItemEntity)
                    .show(mFragTransaction, tag);
        }
    }

    public String getHm(long times) {
        times /= 1000;
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        if (second > 0) {
            minute += 1;
        }
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    /**
     * 设置数据到view
     *
     * @param taskItemEntity
     */
    private void setDataToView(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
            if (taskName == null) {
                return;
            }
            if (titleAction2 != null) {
                titleAction2.setVisibility(hasTaskDeletePermission() ? View.VISIBLE : View.GONE);
            }
            taskStartIamge.setVisibility(hasAddTimerPermission() ? View.VISIBLE : View.GONE);
            taskName.setText(taskItemEntity.name);
            myStar = taskItemEntity.attentioned;
            commentTv.setText(String.format("%s条动态", taskItemEntity.commentCount));
            if (taskItemEntity.state) {
                taskCheckbox.setChecked(true);
            } else {
                taskCheckbox.setChecked(false);
            }
            if (!taskItemEntity.valid) {
                taskCheckbox.setBackgroundResource(R.mipmap.restore);
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

            SpannableString checkTextForegroundColorSpan = null;
            if (taskItemEntity.itemCount > 0) {
                String checkTargetStr = String.format("%s/%s", taskItemEntity.doneItemCount, taskItemEntity.itemCount);
                String checkOriginStr = getString(R.string.task_check) + " " + checkTargetStr;
                checkTextForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(checkOriginStr, checkTargetStr, 0xFFCACACA);
            } else {
                checkTextForegroundColorSpan = new SpannableString(getString(R.string.task_check));
            }

            SpannableString attachTextForegroundColorSpan = null;
            if (taskItemEntity.attachmentCount > 0) {
                String attachTargetStr = String.valueOf(taskItemEntity.attachmentCount);
                String attachOriginStr = getString(R.string.task_attachment) + " " + attachTargetStr;
                attachTextForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(attachOriginStr, attachTargetStr, 0xFFCACACA);
            } else {
                attachTextForegroundColorSpan = new SpannableString(getString(R.string.task_attachment));
            }

            tabTitles.put(0, getString(R.string.task_detail));
            tabTitles.put(1, checkTextForegroundColorSpan);
            tabTitles.put(2, attachTextForegroundColorSpan);
            baseFragmentAdapter.bindTitle(true, Arrays.asList(tabTitles.get(0, ""),
                    tabTitles.get(1, ""),
                    tabTitles.get(2, "")));
            if (baseFragmentAdapter.getCount() <= 0) {
                baseFragmentAdapter.bindData(true, Arrays.asList(
                        taskDetailFragment == null ? taskDetailFragment = TaskDetailFragment.newInstance(taskItemEntity) : taskDetailFragment,
                        taskCheckItemFragment == null ? taskCheckItemFragment = TaskCheckItemFragment.newInstance(
                                taskItemEntity,
                                hasTaskEditPermission()) : taskCheckItemFragment,
                        taskAttachmentFragment == null ? taskAttachmentFragment = TaskAttachmentFragment.newInstance(
                                taskItemEntity.id,
                                taskItemEntity.matterId,
                                taskItemEntity.matter != null ? taskItemEntity.matter.name : "",
                                hasDocumentLookPermission(),
                                hasDocumentAddPermission(),
                                hasTaskEditPermission()) : taskAttachmentFragment
                ));
            }

            updateTabItemFragment();

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
                        if (taskItemEntity.valid) {
                            usersAdapter.setOnItemClickListener(this);
                        }
                        Collections.reverse(taskItemEntity.attendeeUsers);
                        usersAdapter.bindData(true, taskItemEntity.attendeeUsers);
                    } else if (taskItemEntity.attendeeUsers.size() == 1) {
                        taskUsersLayout.setVisibility(View.GONE);
                        taskUserLayout.setVisibility(View.VISIBLE);
                        taskUserPic.setVisibility(View.VISIBLE);
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
            taskUsersArrowIv.setVisibility(taskItemEntity.valid && !taskItemEntity.state ? View.VISIBLE : View.GONE);
            taskUserArrowIv.setVisibility(taskItemEntity.valid && !taskItemEntity.state ? View.VISIBLE : View.GONE);
            taskStartIamge.setVisibility(taskItemEntity.valid ? View.VISIBLE : View.GONE);
            commentEditTv.setVisibility(taskItemEntity.valid ? View.VISIBLE : View.GONE);
            if (isSelectedCheckItem) {
                viewpager.removeOnPageChangeListener(onPageChangeListener);
                viewpager.setCurrentItem(1);
                updateCheckItemFragment();
                isSelectedCheckItem = false;
                viewpager.addOnPageChangeListener(onPageChangeListener);
            }
        }
    }

    /**
     * 更新tab的每个fragment
     */
    private void updateTabItemFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_ISFINISH, taskItemEntity.state);
        bundle.putBoolean(KEY_VALID, taskItemEntity.valid);
        bundle.putSerializable(KEY_TASKITEMENTITY, taskItemEntity);
        if (taskDetailFragment != null) {
            taskDetailFragment.notifyFragmentUpdate(taskDetailFragment, TYPE_UPDATE_CHILD_FRAGMENT, bundle);
        }
        if (taskCheckItemFragment != null) {
            taskCheckItemFragment.notifyFragmentUpdate(taskCheckItemFragment, TYPE_UPDATE_CHILD_FRAGMENT, bundle);
        }
        if (taskAttachmentFragment != null) {
            taskAttachmentFragment.notifyFragmentUpdate(taskAttachmentFragment, TYPE_UPDATE_CHILD_FRAGMENT, bundle);
        }
    }

    /**
     * 更新检查项fragment
     */
    private void updateCheckItemFragment() {
        if (taskCheckItemFragment == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_CHECK_ITEM, isSelectedCheckItem);
        taskCheckItemFragment.notifyFragmentUpdate(taskCheckItemFragment, TYPE_CHECK_ITEM_SHOW_KEYBOARD, bundle);
    }

    /**
     * 设置未分配
     */
    private void setNoAllocation() {
        taskUsersLayout.setVisibility(View.GONE);
        taskUserLayout.setVisibility(View.VISIBLE);
        taskUserPic.setVisibility(View.GONE);
        taskUserName.setText(getString(R.string.task_not_allot));
    }

    /**
     * 恢复已删除任务
     *
     * @param taskId
     */
    private void recoverTaskById(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskRecoverById(taskId),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        taskCheckbox.setBackgroundResource(R.drawable.sl_checkbox);
                        if (taskItemEntity.state) {
                            taskCheckbox.setChecked(true);
                        } else {
                            taskCheckbox.setChecked(false);
                        }
                        taskUserArrowIv.setVisibility(View.VISIBLE);
                        taskStartIamge.setVisibility(View.VISIBLE);
                        taskItemEntity.valid = true;
                        updateTabItemFragment();
                        EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_DELETE_ACTION, taskItemEntity));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        taskCheckbox.setBackgroundResource(R.mipmap.restore);
                    }
                });
    }

    /**
     * 清空所有已删除的任务
     */
    public void clearAllDeletedTask() {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        List<String> ids = new ArrayList<>();
        ids.add(taskId);
        if (ids.size() > 0) {
            showLoadingDialog(null);
            callEnqueue(
                    getApi().clearDeletedTask(ids),
                    new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            dismissLoadingDialog();
                            EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
        }
    }

    /**
     * 添加关注
     */
    private void addStar() {
        showLoadingDialog(null);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("taskId", taskId);
        callEnqueue(
                getApi().taskAddStar(RequestUtils.createJsonBody(jsonObject.toString())),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        myStar = TaskEntity.ATTENTIONED;
                        taskItemEntity.attentioned = TaskEntity.ATTENTIONED;
                        titleAction.setImageResource(R.mipmap.header_icon_star_solid);
                        EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION, taskItemEntity));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        titleAction.setImageResource(R.mipmap.header_icon_star_line);
                    }
                });
    }

    /**
     * 取消关注
     */
    private void deleteStar() {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskDeleteStar(taskId),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        myStar = TaskEntity.UNATTENTIONED;
                        titleAction.setImageResource(R.mipmap.header_icon_star_line);
                        taskItemEntity.attentioned = TaskEntity.ATTENTIONED;
                        EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION, taskItemEntity));
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        titleAction.setImageResource(R.mipmap.header_icon_star_solid);
                    }
                });
    }

    /**
     * 删除任务
     */
    private void deleteTask() {
        showLoadingDialog(null);
        MobclickAgent.onEvent(this, UMMobClickAgent.delete_task_click_id);
        callEnqueue(
                getApi().taskDelete(taskId),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION, taskItemEntity));
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
    private void updateTask(TaskEntity.TaskItemEntity itemEntity, final boolean state, final CheckedTextView checkbox) {
        showLoadingDialog(null);
        callEnqueue(
                getApi().taskUpdateNew(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))),
                new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null) {
                            EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION, response.body().result));
                        }
                        if (checkbox != null) {
                            checkbox.setChecked(state);
                        }
                        taskItemEntity = response.body().result;
                        try {
                            cloneItemEntity = (TaskEntity.TaskItemEntity) BeanUtils.cloneTo(taskItemEntity);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                        setDataToView(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                        setDataToView(cloneItemEntity);
                        if (checkbox != null) {
                            checkbox.setChecked(cloneItemEntity.state);
                        }
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
            jsonObject.addProperty("parentId", itemEntity.parentId);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            JsonArray jsonarr = new JsonArray();
            if (itemEntity.attendeeUsers != null) {
                if (itemEntity.attendeeUsers.size() > 0) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                        jsonarr.add(attendeeUser.userId);
                    }
                }
                jsonObject.add("attendees", jsonarr);
            }
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
                    if (e1 == null || e2 == null) {
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                    boolean canFastScroll = e1.getRawX() > appbar.getBottom() && e2.getRawX() > appbar.getBottom();
                    if (!canFastScroll) {
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
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

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        //选择负责人回调
        if (fragment instanceof TaskAllotSelectDialogFragment) {
            if (params != null) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                if (taskItemEntity.attendeeUsers != null) {
                    taskItemEntity.attendeeUsers.clear();
                    taskItemEntity.attendeeUsers.addAll(attusers);
                    updateTask(taskItemEntity, taskItemEntity.state, taskCheckbox);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTaskNameEvent(TaskActionEvent event) {
        if (event == null) {
            return;
        }
        if (taskItemEntity == null) {
            return;
        }
        //修改任务名称
        if (event.action == TaskActionEvent.TASK_UPDATE_NAME_ACTION) {
            String desc = event.desc;
            if (!TextUtils.isEmpty(desc)) {
                taskItemEntity.name = desc;
                updateTask(taskItemEntity, taskItemEntity.state, null);
            }
        }//修改任务所属项目
        else if (event.action == TaskActionEvent.TASK_UPDATE_PROJECT_ACTION) {
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
        } else if (event.action == TaskActionEvent.TASK_UPDATE_ITEM) {
            if (event.entity != null) {
                taskItemEntity = event.entity;
                setDataToView(taskItemEntity);
            }
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof TaskUsersAdapter) {
            if (taskItemEntity != null && !taskItemEntity.state) {
                if (taskItemEntity.matter != null) {
                    showTaskAllotSelectDialogFragment(taskItemEntity.matter.id);
                } else {
                    showTopSnackBar(getString(R.string.task_please_check_project));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == START_COMMENT_FORRESULT_CODE) {
                int commentCount = data.getIntExtra(KEY_ACTIVITY_RESULT, -1);
                commentTv.setText(String.format("%s条动态", commentCount));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUpdateCheckItem(String checkItemCount) {
        getData(true);
    }

    @Override
    public void onUpdateDocument(int documentCount) {
        if (documentCount > 0) {
            String attachTargetStr = String.valueOf(documentCount);
            String attachOriginStr = getString(R.string.task_attachment) + " " + attachTargetStr;
            SpannableString attachTextForegroundColorSpan = SpannableUtils.getTextForegroundColorSpan(attachOriginStr, attachTargetStr, 0xFFCACACA);
            tabTitles.put(2, attachTextForegroundColorSpan);
        } else {
            tabTitles.put(2, getString(R.string.task_attachment));
        }
        baseFragmentAdapter.bindTitle(true, Arrays.asList(tabTitles.get(0, ""),
                tabTitles.get(1, ""),
                tabTitles.get(2, "")));
    }
}
