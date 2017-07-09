package com.icourt.alpha.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.MultiSelectRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.api.RequestUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/7/8
 * version 1.0.0
 */

public class TaskSimpleAdapter extends MultiSelectRecyclerAdapter<TaskEntity.TaskItemEntity>
        implements BaseRecyclerAdapter.OnItemChildClickListener {
    public TaskSimpleAdapter() {
        this.setOnItemChildClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_simple_task;
    }


    @Override
    public void onBindSelectableHolder(ViewHolder holder, TaskEntity.TaskItemEntity taskItemEntity, boolean selected, int position) {
        ImageView task_item_checkbox = holder.obtainView(R.id.task_item_checkbox);
        TextView task_name_tv = holder.obtainView(R.id.task_name_tv);
        TextView task_desc_tv = holder.obtainView(R.id.task_desc_tv);
        task_name_tv.setText(taskItemEntity.name);
        if (taskItemEntity.state)//已完成
        {
            task_item_checkbox.setImageResource(R.mipmap.checkbox_selected);
            task_desc_tv.setText(String.format("%s %s",
                    DateUtils.get23Hour59MinFormat(taskItemEntity.updateTime)
                    , getProjectTaskGroupInfo(taskItemEntity)));
        } else {
            task_item_checkbox.setImageResource(R.mipmap.checkbox_unselect);
            task_desc_tv.setText(String.format("%s %s",
                    DateUtils.get23Hour59MinFormat(taskItemEntity.dueTime)
                    , getProjectTaskGroupInfo(taskItemEntity)));
        }
        holder.bindChildClick(task_item_checkbox);
    }

    /**
     * 获取 项目 任务组等信息的组合
     *
     * @param taskItemEntity
     * @return
     */
    private String getProjectTaskGroupInfo(TaskEntity.TaskItemEntity taskItemEntity) {
        if (taskItemEntity != null) {
            if (taskItemEntity.matter != null) {
                if (taskItemEntity.parentFlow != null) {
                    if (!TextUtils.isEmpty(taskItemEntity.parentFlow.name))
                        return taskItemEntity.matter.name + " - " + taskItemEntity.parentFlow.name;
                    else
                        return taskItemEntity.matter.name;
                } else {
                    if (!TextUtils.isEmpty(taskItemEntity.parentName))
                        return taskItemEntity.matter.name + " - " + taskItemEntity.parentName;
                    else
                        return taskItemEntity.matter.name;
                }
            }
        }
        return "未指定所属项目";
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, ViewHolder holder, final View view, int position) {
        final TaskEntity.TaskItemEntity itemEntity = getItem(adapter.getRealPos(position));
        if (itemEntity == null) return;
        switch (view.getId()) {
            case R.id.task_item_checkbox:
                if (itemEntity.attendeeUsers != null && itemEntity.attendeeUsers.size() > 1) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("提示")
                            .setMessage(itemEntity.state ? "该任务由多人负责,确定取消完成?" : "该任务由多人负责,确定完成?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateTask(view.getContext(), itemEntity, !itemEntity.state);
                                }
                            }).setNegativeButton("取消", null)
                            .show();
                } else {
                    updateTask(view.getContext(), itemEntity, !itemEntity.state);
                }
                break;
        }
    }

    /**
     * 修改任务状态
     *
     * @param itemEntity
     * @param state
     */
    private void updateTask(Context context,
                            final TaskEntity.TaskItemEntity itemEntity,
                            final boolean state) {
        showLoadingDialog(context, null);
        getApi().taskUpdate(RequestUtils.createJsonBody(getTaskJson(itemEntity, state)))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        itemEntity.state = state;
                        updateItem(itemEntity);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", itemEntity.id);
            jsonObject.addProperty("name", itemEntity.name);
            jsonObject.addProperty("state", state);
            jsonObject.addProperty("valid", true);
            jsonObject.addProperty("updateTime", DateUtils.millis());
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
