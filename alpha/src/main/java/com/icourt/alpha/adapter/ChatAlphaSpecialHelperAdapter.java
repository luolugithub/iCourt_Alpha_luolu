package com.icourt.alpha.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.icourt.alpha.view.recyclerviewDivider.ITimeDividerInterface;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
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
        if (alphaSecialHeplerMsgEntity == null) return;
        ImageView msg_user_icon_iv = holder.obtainView(R.id.msg_user_icon_iv);
        TextView msg_title_tv = holder.obtainView(R.id.msg_title_tv);
        TextView msg_content_tv = holder.obtainView(R.id.msg_content_tv);
        TextView msg_time_tv = holder.obtainView(R.id.msg_time_tv);
        TextView msg_from_tv = holder.obtainView(R.id.msg_from_tv);


        if (TextUtils.isEmpty(alphaSecialHeplerMsgEntity.pic)) {
            msg_user_icon_iv.setImageResource(R.mipmap.alpha_assistant_20);
        } else {
            GlideUtils.loadUser(msg_user_icon_iv.getContext(), alphaSecialHeplerMsgEntity.pic, msg_user_icon_iv);
        }
        msg_title_tv.setText(alphaSecialHeplerMsgEntity.content);
        if (TextUtils.equals(alphaSecialHeplerMsgEntity.object, "TASK")) {
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
                    msg_time_tv.setText(DateUtils.getyyyy_YEAR_MM_MONTH_dd_DAY_HHmm(alphaSecialHeplerMsgEntity.dueTime));
                    msg_time_tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_message_due_14, 0, 0, 0);
                }
            }
            msg_from_tv.setText(alphaSecialHeplerMsgEntity.matterName);
        } else if (TextUtils.equals(alphaSecialHeplerMsgEntity.object, "MATTER")) {
            msg_content_tv.setVisibility(View.VISIBLE);
            msg_time_tv.setVisibility(View.VISIBLE);
            msg_from_tv.setVisibility(View.VISIBLE);
            msg_content_tv.setText(alphaSecialHeplerMsgEntity.matterName);
            String secondeContent = null;
            if (!TextUtils.isEmpty(alphaSecialHeplerMsgEntity.caseProcess)) {
                secondeContent = alphaSecialHeplerMsgEntity.caseProcess;
            } else if (!TextUtils.isEmpty(alphaSecialHeplerMsgEntity.serveContent)) {
                secondeContent = alphaSecialHeplerMsgEntity.serveContent;
            } else if (alphaSecialHeplerMsgEntity.startDate > 0 && alphaSecialHeplerMsgEntity.endDate > 0) {
                //显示时间
                StringBuilder timeStringBuilder = new StringBuilder();
                if (alphaSecialHeplerMsgEntity.startDate > 0 && alphaSecialHeplerMsgEntity.endDate > 0) {
                    timeStringBuilder.append(DateUtils.getyyyyMMdd(alphaSecialHeplerMsgEntity.startDate));
                    timeStringBuilder.append(" - ");
                    timeStringBuilder.append(DateUtils.getyyyyMMdd(alphaSecialHeplerMsgEntity.endDate));
                } else {
                    long showTime = (alphaSecialHeplerMsgEntity.startDate > 0 ? alphaSecialHeplerMsgEntity.startDate : alphaSecialHeplerMsgEntity.endDate);
                    timeStringBuilder.append(DateUtils.getyyyyMMdd(showTime));
                }
            } else if (!TextUtils.isEmpty(alphaSecialHeplerMsgEntity.status)) {
                secondeContent = alphaSecialHeplerMsgEntity.status;
            }
            msg_time_tv.setText(secondeContent);

            //处理信息来源
            msg_from_tv.setText(alphaSecialHeplerMsgEntity.clientName);
            msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            boolean showArrow = TextUtils.equals(AlphaSecialHeplerMsgEntity.MATTER_MEMBER_REMOVEU, alphaSecialHeplerMsgEntity.scene);
            msg_title_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, showArrow ? 0 : R.mipmap.message_arrow_14, 0);
        } else {
            msg_content_tv.setVisibility(View.GONE);
            msg_time_tv.setVisibility(View.GONE);
            msg_from_tv.setVisibility(View.GONE);
        }
        addTimeDividerArray(alphaSecialHeplerMsgEntity, position);
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
        if (msgEntity == null) return;
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
            if (!TextUtils.equals(msgEntity.scene, AlphaSecialHeplerMsgEntity.MATTER_MEMBER_REMOVEU)) {
                ProjectDetailActivity.launch(view.getContext(), msgEntity.id, msgEntity.matterName);
            }
        }
    }
}
