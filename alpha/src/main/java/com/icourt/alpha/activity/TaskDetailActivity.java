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
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/11
 * version 2.0.0
 */

public class TaskDetailActivity extends BaseActivity implements OnFragmentCallBackListener {

    private static final String KEY_TASK_ID = "key_task_id";
    private static final int SHOW_DELETE_DIALOG = 0;//删除提示对话框
    private static final int SHOW_FINISH_DIALOG = 1;//完成任务提示对话框

    String taskId;
    BaseFragmentAdapter baseFragmentAdapter;
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
    @BindView(R.id.task_user_recyclerview)
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
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.task_user_layout)
    LinearLayout taskUserLayout;
    @BindView(R.id.task_users_layout)
    LinearLayout taskUsersLayout;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.task_time_parent_layout)
    RelativeLayout taskTimeParentLayout;
    @BindView(R.id.comment_tv)
    TextView commentTv;

    int myStar = -1;
    boolean isStrat = false;
    TaskEntity.TaskItemEntity taskItemEntity;
    TaskUsersAdapter usersAdapter;
    @BindView(R.id.comment_layout)
    LinearLayout commentLayout;

    public static void launch(@NonNull Context context, @NonNull String taskId) {
        if (context == null) return;
        if (TextUtils.isEmpty(taskId)) return;
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(KEY_TASK_ID, taskId);
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
    protected void initView() {
        super.initView();
        setTitle("");
        EventBus.getDefault().register(this);
        taskId = getIntent().getStringExtra(KEY_TASK_ID);
        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(baseFragmentAdapter);
        taskTablayout.setupWithViewPager(viewpager);
        titleAction2.setImageResource(R.mipmap.header_icon_more);
        getData(false);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.titleAction, R.id.titleAction2, R.id.comment_layout, R.id.task_checkbox, R.id.task_user_layout, R.id.task_users_layout, R.id.task_start_iamge})
    @Override
    public void onClick(View v) {
        super.onClick(v);
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
            case R.id.task_user_layout:
            case R.id.task_users_layout://选择负责人
                if (taskItemEntity.matter != null) {
                    showTaskAllotSelectDialogFragment(taskItemEntity.matter.id);
                } else {
                    showTopSnackBar("请优先选择项目");
                }
                break;
            case R.id.task_start_iamge://开始计时
                if (isStrat)
                    TimerManager.getInstance().stopTimer();
                else
                    TimerManager.getInstance().addTimer(getTimer());
                break;
            case R.id.task_checkbox://  完成／取消完成
                if (taskItemEntity.state) {
                    if (taskItemEntity.attendeeUsers != null) {
                        if (taskItemEntity.attendeeUsers.size() > 1) {
                            showDeleteDialog("该任务为多人任务，确定要取消完成吗?", SHOW_FINISH_DIALOG);
                        } else {
                            updateTask(taskItemEntity, false, taskCheckbox);
                        }
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
                break;
            case R.id.comment_layout://更多评论动态
                CommentListActivity.launch(this, taskId);
                break;
        }
    }

    /**
     * 获取添加计时实体
     *
     * @return
     */
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

    public String toTime(long times) {
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
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
                TimeEntity.ItemEntity addItem = TimerManager.getInstance().getTimer();
                if (addItem != null) {
                    isStrat = true;
                    if (TextUtils.equals(addItem.taskPkId, taskItemEntity.id)) {
                        taskStartIamge.setImageResource(R.drawable.orange_side_dot_bg);
                    }
                }
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                TimeEntity.ItemEntity updateItem = TimerManager.getInstance().getTimer();
                if (updateItem != null) {
                    isStrat = true;
                    if (TextUtils.equals(updateItem.taskPkId, taskItemEntity.id)) {
                        taskStartIamge.setImageResource(R.drawable.orange_side_dot_bg);
                        taskTime.setText(toTime(event.timingSecond));
                    }
                }
                break;
            case TimingEvent.TIMING_STOP:
                isStrat = false;
                taskStartIamge.setImageResource(R.mipmap.icon_start_20);
                taskTime.setText(DateUtils.getTimeDurationDate(taskItemEntity.timingSum + event.timingSecond));
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
                            if (taskItemEntity.state) {
                                updateTask(taskItemEntity, false, taskCheckbox);
                            } else {
                                updateTask(taskItemEntity, true, taskCheckbox);
                            }
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
        builder.create().show();
    }

    @Override
    protected void getData(boolean isRefresh) {
        showLoadingDialog(null);
        getApi().taskQueryDetail(taskId).enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
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
     * 展示选择负责人对话框
     */
    public void showTaskAllotSelectDialogFragment(String projectId) {
        String tag = "TaskAllotSelectDialogFragment";
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }

        TaskAllotSelectDialogFragment.newInstance(projectId, taskItemEntity.attendeeUsers)
                .show(mFragTransaction, tag);
    }

    /**
     * 设置数据到view
     *
     * @param taskItemEntity
     */
    private void setDataToView(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
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
            taskTime.setText(DateUtils.getTimeDurationDate(taskItemEntity.timingSum));
            baseFragmentAdapter.bindTitle(true, Arrays.asList(
                    "任务详情", "检查项 " + taskItemEntity.doneItemCount + "/" + taskItemEntity.itemCount, "附件 " + taskItemEntity.attachmentCount
            ));
            baseFragmentAdapter.bindData(false, Arrays.asList(
                    TaskDetailFragment.newInstance(taskItemEntity),
                    TaskCheckItemFragment.newInstance(taskItemEntity.id),
                    TaskAttachmentFragment.newInstance(taskItemEntity.id)
            ));
            if (taskItemEntity.attendeeUsers != null) {
                if (taskItemEntity.attendeeUsers.size() > 0) {
                    if (taskItemEntity.attendeeUsers.size() > 1) {
                        taskUsersLayout.setVisibility(View.VISIBLE);
                        taskUserLayout.setVisibility(View.GONE);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        layoutManager.setReverseLayout(true);
                        taskUserRecyclerview.setLayoutManager(layoutManager);
                        taskUserRecyclerview.setAdapter(usersAdapter = new TaskUsersAdapter());
                        usersAdapter.bindData(false, taskItemEntity.attendeeUsers);
                    } else if (taskItemEntity.attendeeUsers.size() == 1) {
                        taskUsersLayout.setVisibility(View.GONE);
                        taskUserLayout.setVisibility(View.VISIBLE);
                        GlideUtils.loadUser(this, taskItemEntity.attendeeUsers.get(0).pic, taskUserPic);
                        taskUserName.setText(taskItemEntity.attendeeUsers.get(0).userName);
                    }
                } else {
                    taskTimeParentLayout.setVisibility(View.GONE);
                }
            }
        }
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
        if (state) {
            showLoadingDialog("完成任务...");
        } else {
            showLoadingDialog("取消完成任务...");
        }
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state))).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
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
            JsonArray jsonarr = new JsonArray();
            if (itemEntity.attendeeUsers != null) {
                for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUser : itemEntity.attendeeUsers) {
                    jsonarr.add(attendeeUser.userId);
                }
            }
            jsonObject.add("attendees", jsonarr);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

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
        if (fragment instanceof TaskAllotSelectDialogFragment) {
            if (params != null) {
                List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = (List<TaskEntity.TaskItemEntity.AttendeeUserEntity>) params.getSerializable("list");
                usersAdapter.bindData(true, attusers);
                if (taskItemEntity.attendeeUsers != null) {
                    taskItemEntity.attendeeUsers.clear();
                    taskItemEntity.attendeeUsers.addAll(attusers);
                    updateTask(taskItemEntity, taskItemEntity.state, taskCheckbox);
                }
            }
        }
    }
}
