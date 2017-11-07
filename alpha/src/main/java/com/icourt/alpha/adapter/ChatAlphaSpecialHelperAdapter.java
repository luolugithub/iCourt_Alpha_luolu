package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectDetailActivity;
import com.icourt.alpha.activity.TaskDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.AlphaSecialHeplerMsgEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.view.recyclerviewDivider.ITimeDividerInterface;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  这个消息结构太乱,前后端需要重构
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/13
 * version 1.0.0
 */
public class ChatAlphaSpecialHelperAdapter
        extends BaseArrayRecyclerAdapter<AlphaSecialHeplerMsgEntity>
        implements ITimeDividerInterface, BaseRecyclerAdapter.OnItemClickListener {

    public ChatAlphaSpecialHelperAdapter() {
        setOnItemClickListener(this);
    }

    private Set<Long> timeShowArray = new HashSet<>();//时间分割线消息
    private final int TIME_DIVIDER = 5 * 60 * 1_000;
    private Comparator<Long> longComparator = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            if (o1 != null && o2 != null) {
                return o1.compareTo(o2);
            }
            return 0;
        }
    };

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_alpha_special_helper_msg;
    }

    @Override
    public void onBindHoder(ViewHolder holder, AlphaSecialHeplerMsgEntity alphaSecialHeplerMsgEntity, int position) {
        if (alphaSecialHeplerMsgEntity == null) {
            return;
        }
        TextView msg_title_tv = holder.obtainView(R.id.msg_title_tv);

        //处理用户信息:头像
        setUserInfo(holder, alphaSecialHeplerMsgEntity, position);

        //分类型处理
        dispatchSetItemInfo(holder, alphaSecialHeplerMsgEntity, position);

        //标题
        msg_title_tv.setText(alphaSecialHeplerMsgEntity.content);

        //分割线
        addTimeDividerArray(alphaSecialHeplerMsgEntity, position);
    }

    /**
     * 分发处理
     *
     * @param holder
     * @param alphaSecialHeplerMsgEntity
     * @param position
     */
    private void dispatchSetItemInfo(ViewHolder holder, AlphaSecialHeplerMsgEntity alphaSecialHeplerMsgEntity, int position) {
        if (TextUtils.equals(alphaSecialHeplerMsgEntity.object, AlphaSecialHeplerMsgEntity.OBJECT_TYPE_TASK)) {
            setTaskInfo(holder, alphaSecialHeplerMsgEntity, position);
        } else if (TextUtils.equals(alphaSecialHeplerMsgEntity.object, AlphaSecialHeplerMsgEntity.OBJECT_TYPE_PROJECT)) {
            setProjectInfo(holder, alphaSecialHeplerMsgEntity, position);
        } else {
            TextView msg_title_tv = holder.obtainView(R.id.msg_title_tv);
            msg_title_tv.setText("此消息手机端不支持!");
        }
    }


    /**
     * 设置任务的信息
     *
     * @param holder
     * @param alphaSecialHeplerMsgEntity
     * @param position
     */
    private void setTaskInfo(ViewHolder holder, AlphaSecialHeplerMsgEntity alphaSecialHeplerMsgEntity, int position) {
        TextView msg_title_tv = holder.obtainView(R.id.msg_title_tv);
        TextView msg_content_tv = holder.obtainView(R.id.msg_content_tv);
        TextView msg_time_tv = holder.obtainView(R.id.msg_time_tv);
        TextView msg_from_tv = holder.obtainView(R.id.msg_from_tv);
        //任务模块
        msg_content_tv.setVisibility(View.VISIBLE);
        msg_time_tv.setVisibility(View.VISIBLE);
        msg_from_tv.setVisibility(View.VISIBLE);
        //任务
        msg_content_tv.setText(alphaSecialHeplerMsgEntity.taskName);
        //箭头的显示
        String taskType = alphaSecialHeplerMsgEntity.type;
        if (!TextUtils.isEmpty(taskType)) {
            taskType = taskType.trim();
        }
        if (TextUtils.equals(taskType, AlphaSecialHeplerMsgEntity.TASK_REPLY)) {
            msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.message_arrow_14, 0);
            msg_time_tv.setText(alphaSecialHeplerMsgEntity.reply);
            msg_time_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            if (TextUtils.equals(alphaSecialHeplerMsgEntity.scene, AlphaSecialHeplerMsgEntity.TASK_STATUS_DELETE) || TextUtils.equals(alphaSecialHeplerMsgEntity.scene, AlphaSecialHeplerMsgEntity.TASK_PRINCIPAL_REMOVEU)) {
                msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.message_arrow_14, 0);
            }
            if (alphaSecialHeplerMsgEntity.dueTime <= 0) {
                msg_time_tv.setText("");
                msg_time_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                if (isAllDayTask(alphaSecialHeplerMsgEntity.dueTime)) {
                    //全天任务 eg. yyyy年MM月dd日
                    msg_time_tv.setText(DateUtils.getFormatDate(alphaSecialHeplerMsgEntity.dueTime, DateUtils.DATE_YYYYMMDD_STYLE2));
                } else {
                    msg_time_tv.setText(DateUtils.getFormatDate(alphaSecialHeplerMsgEntity.dueTime, DateUtils.DATE_YYYYMMDD_HHMM_STYLE2));
                }
                msg_time_tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_message_due_14, 0, 0, 0);
            }
        }
        msg_from_tv.setText(alphaSecialHeplerMsgEntity.matterName);
    }

    /**
     * 设置项目的信息
     *
     * @param holder
     * @param alphaSecialHeplerMsgEntity
     * @param position
     */
    private void setProjectInfo(ViewHolder holder, AlphaSecialHeplerMsgEntity alphaSecialHeplerMsgEntity, int position) {
        TextView msg_title_tv = holder.obtainView(R.id.msg_title_tv);
        TextView msg_content_tv = holder.obtainView(R.id.msg_content_tv);
        TextView msg_time_tv = holder.obtainView(R.id.msg_time_tv);

        LinearLayout msg_time_ll = holder.obtainView(R.id.msg_from_ll);
        TextView msg_from_tv = holder.obtainView(R.id.msg_from_tv);
        //项目模块
        msg_content_tv.setVisibility(View.VISIBLE);
        msg_time_tv.setVisibility(View.VISIBLE);
        msg_from_tv.setVisibility(View.VISIBLE);
        msg_from_tv.setVisibility(View.VISIBLE);
        msg_content_tv.setText(alphaSecialHeplerMsgEntity.matterName);
        String secondeContent = null;

        if (!TextUtils.isEmpty(alphaSecialHeplerMsgEntity.caseProcess)) {
            //1. 优先案由
            secondeContent = alphaSecialHeplerMsgEntity.caseProcess;
        } else if (!TextUtils.isEmpty(alphaSecialHeplerMsgEntity.serveContent)) {
            //1. 服务内容? 项目的一个字符串字段
            secondeContent = alphaSecialHeplerMsgEntity.serveContent;
        } else if (alphaSecialHeplerMsgEntity.startDate > 0 && alphaSecialHeplerMsgEntity.endDate > 0) {
            //3. 显示时间
            StringBuilder timeStringBuilder = new StringBuilder();
            if (alphaSecialHeplerMsgEntity.startDate > 0 && alphaSecialHeplerMsgEntity.endDate > 0) {
                timeStringBuilder.append(DateUtils.getFormatDate(alphaSecialHeplerMsgEntity.startDate, DateUtils.DATE_YYYYMMDD_STYLE2));
                timeStringBuilder.append(" - ");
                timeStringBuilder.append(DateUtils.getFormatDate(alphaSecialHeplerMsgEntity.endDate, DateUtils.DATE_YYYYMMDD_STYLE2));
            } else {
                long showTime = (alphaSecialHeplerMsgEntity.startDate > 0 ? alphaSecialHeplerMsgEntity.startDate : alphaSecialHeplerMsgEntity.endDate);
                timeStringBuilder.append(DateUtils.getFormatDate(showTime, DateUtils.DATE_YYYYMMDD_STYLE2));
            }
            secondeContent = timeStringBuilder.toString();
        } else if (!TextUtils.isEmpty(alphaSecialHeplerMsgEntity.status)) {
            // 4. 项目状态
            secondeContent = alphaSecialHeplerMsgEntity.status;
        } else {
            // 5.
        }
        msg_time_tv.setText(secondeContent);
        //处理信息来源
        msg_from_tv.setText(alphaSecialHeplerMsgEntity.clientName);
        msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        boolean showArrow = TextUtils.equals(AlphaSecialHeplerMsgEntity.MATTER_MEMBER_REMOVEU, alphaSecialHeplerMsgEntity.scene);
        msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, showArrow ? 0 : R.mipmap.message_arrow_14, 0);


        //新的处理方式
        if (TextUtils.equals(alphaSecialHeplerMsgEntity.scene, AlphaSecialHeplerMsgEntity.SCENE_PROJECT_MEMBER_ADDED)) {
            //项目成员添加
            msg_content_tv.setText(TextUtils.isEmpty(alphaSecialHeplerMsgEntity.matterName) ? "服务器返回项目名称null" : alphaSecialHeplerMsgEntity.matterName);
            msg_time_tv.setVisibility(View.GONE);
            msg_time_ll.setVisibility(View.GONE);
        } else if (TextUtils.equals(alphaSecialHeplerMsgEntity.scene, AlphaSecialHeplerMsgEntity.SCENE_PROJECT_DELETE)) {
            //项目删除
            msg_content_tv.setText(TextUtils.isEmpty(alphaSecialHeplerMsgEntity.matterName) ? "服务器返回项目名称null" : alphaSecialHeplerMsgEntity.matterName);
            msg_time_tv.setVisibility(View.GONE);
            msg_time_ll.setVisibility(View.GONE);
            msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, showArrow ? 0 : 0, 0);
        }
    }

    /**
     * 设置用户数据
     *
     * @param holder
     * @param alphaSecialHeplerMsgEntity
     * @param position
     */
    private void setUserInfo(ViewHolder holder, AlphaSecialHeplerMsgEntity alphaSecialHeplerMsgEntity, int position) {
        ImageView msg_user_icon_iv = holder.obtainView(R.id.msg_user_icon_iv);
        GlideUtils.loadAlphaSpecialUser(alphaSecialHeplerMsgEntity.pic, msg_user_icon_iv);
    }

    /**
     * 是否是全天任务
     *
     * @param milliseconds
     * @return
     */
    private boolean isAllDayTask(long milliseconds) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            return (hour == 23 && minute == 59 && second == 59);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 处理时间分割线
     *
     * @param imMessageCustomBody
     * @param position
     */
    private void addTimeDividerArray(AlphaSecialHeplerMsgEntity imMessageCustomBody, int position) {
        if (imMessageCustomBody == null) return;
        if (imMessageCustomBody.imMessage == null) return;

        //消息时间本身已经有序

        if (timeShowArray.isEmpty()) {
            timeShowArray.add(imMessageCustomBody.imMessage.getTime());
        } else {
            if (!timeShowArray.contains(imMessageCustomBody.imMessage.getTime())) {
                if (imMessageCustomBody.imMessage.getTime() - Collections.max(timeShowArray, longComparator).longValue() >= TIME_DIVIDER) {
                    timeShowArray.add(imMessageCustomBody.imMessage.getTime());
                } else if (Collections.min(timeShowArray, longComparator).longValue() - imMessageCustomBody.imMessage.getTime() >= TIME_DIVIDER) {
                    timeShowArray.add(imMessageCustomBody.imMessage.getTime());
                }
            }
        }
    }


    @Override
    public boolean isShowTimeDivider(int pos) {
        AlphaSecialHeplerMsgEntity item = getItem(pos);
        return item != null && item.imMessage != null && timeShowArray.contains(item.imMessage.getTime());
    }

    @NonNull
    @Override
    public String getShowTime(int pos) {
        AlphaSecialHeplerMsgEntity item = getItem(pos);
        return item != null && item.imMessage != null ?
                DateUtils.getFormatChatTime(item.imMessage.getTime()) : "null";
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        AlphaSecialHeplerMsgEntity msgEntity = getItem(position);
        if (msgEntity == null) {
            return;
        }
        //项目已删除
        if (TextUtils.equals(msgEntity.scene, AlphaSecialHeplerMsgEntity.SCENE_PROJECT_DELETE)) {
            return;
        }
        if (TextUtils.equals(msgEntity.scene, AlphaSecialHeplerMsgEntity.MATTER_MEMBER_REMOVEU)) {
            return;
        }
        if (!TextUtils.isEmpty(msgEntity.route) && msgEntity.route.startsWith("alpha://")) {
            notifacionMsgJump(view, msgEntity);
        }
    }

    /**
     * 通知消息跳转
     */
    private void notifacionMsgJump(final View view, final AlphaSecialHeplerMsgEntity msgEntity) {
        if (view == null) return;
        if (msgEntity == null) return;
        if (TextUtils.equals(msgEntity.object, "TASK")) {
            if (!TextUtils.equals(msgEntity.scene, AlphaSecialHeplerMsgEntity.TASK_STATUS_DELETE)
                    && !TextUtils.equals(msgEntity.scene, AlphaSecialHeplerMsgEntity.TASK_PRINCIPAL_REMOVEU)) {
                showLoadingDialog(view.getContext(), null);
                //有返回权限
                getApi().taskQueryDetailWithRight(msgEntity.id)
                        .enqueue(new SimpleCallBack<TaskEntity.TaskItemEntity>() {
                            @Override
                            public void onSuccess(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Response<ResEntity<TaskEntity.TaskItemEntity>> response) {
                                dismissLoadingDialog();
                                if (response.body().result != null) {
                                    if (response.body().result.valid) {
                                        TaskDetailActivity.launch(view.getContext(), msgEntity.id);
                                    } else {
                                        showTopSnackBar(view, "该任务已删除");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResEntity<TaskEntity.TaskItemEntity>> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });

            }
        } else if (TextUtils.equals(msgEntity.object, "MATTER")) {
            showLoadingDialog(view.getContext(), null);
            getApi().permissionQuery(LoginInfoUtils.getLoginUserId(), "MAT", msgEntity.id)
                    .enqueue(new SimpleCallBack<List<String>>() {
                        @Override
                        public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                            dismissLoadingDialog();
                            if (response.body().result != null
                                    && (response.body().result.contains("MAT:matter.document:readwrite")
                                    || response.body().result.contains("MAT:matter.document:read"))) {
                                ProjectDetailActivity.launch(
                                        view.getContext(),
                                        msgEntity.id,
                                        msgEntity.matterName);
                            } else {
                                showTopSnackBar(view, "无权查看");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
                            dismissLoadingDialog();
                            super.onFailure(call, t);
                        }
                    });
        }
    }
}
