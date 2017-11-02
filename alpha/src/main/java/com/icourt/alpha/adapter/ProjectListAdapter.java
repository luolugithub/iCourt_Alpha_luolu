package com.icourt.alpha.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.ProjectDetailActivity;
import com.icourt.alpha.adapter.baseadapter.BaseArrayRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.utils.LoginInfoUtils.getLoginUserId;

/**
 * Description
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/2
 * version 2.0.0
 */

public class ProjectListAdapter extends BaseArrayRecyclerAdapter<ProjectEntity> implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {


    public ProjectListAdapter() {
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.adapter_item_project;
    }

    @Override
    public void onBindHoder(ViewHolder holder, ProjectEntity projectEntity, int position) {
        if (projectEntity == null) return;
        ImageView headerIcon = holder.obtainView(R.id.header_imageview);
        TextView nameView = holder.obtainView(R.id.project_name);
        TextView typeView = holder.obtainView(R.id.project_type);
        TextView taskView = holder.obtainView(R.id.project_task);
        TextView timeView = holder.obtainView(R.id.project_time_tv);

        nameView.setText(projectEntity.name);
        taskView.setText((projectEntity.allTask - projectEntity.unfinishTask) + "/" + projectEntity.allTask);
        if (projectEntity.sumTime > 0) {
            timeView.setText(DateUtils.getHHmm(projectEntity.sumTime / 1000));
        } else {
            timeView.setText("00:00");
        }
        if (!TextUtils.isEmpty(projectEntity.matterType)) {
            switch (Integer.valueOf(projectEntity.matterType)) {
                case 0:
                    if (!TextUtils.isEmpty(projectEntity.caseProcessName))
                        typeView.setText(projectEntity.caseProcessName);
                    else if (!TextUtils.isEmpty(projectEntity.matterTypeName))
                        typeView.setText(projectEntity.matterTypeName);
                    else
                        typeView.setText("争议解决");
                    headerIcon.setImageResource(R.mipmap.project_type_dis);
                    break;
                case 1:
                    if (!TextUtils.isEmpty(projectEntity.matterTypeName))
                        typeView.setText(projectEntity.matterTypeName);
                    else
                        typeView.setText("非诉专项");
                    headerIcon.setImageResource(R.mipmap.project_type_noju);
                    break;
                case 2:
                    if (!TextUtils.isEmpty(projectEntity.matterTypeName))
                        typeView.setText(projectEntity.matterTypeName);
                    else
                        typeView.setText("常年顾问");
                    headerIcon.setImageResource(R.mipmap.project_type_coun);
                    break;
                case 3:
                    if (!TextUtils.isEmpty(projectEntity.matterTypeName))
                        typeView.setText(projectEntity.matterTypeName);
                    else
                        typeView.setText("内部事务");
                    headerIcon.setImageResource(R.mipmap.project_type_aff);
                    break;
            }
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {
        ProjectEntity projectEntity = (ProjectEntity) adapter.getItem(getRealPos(position));
        if (projectEntity != null) {
            checkAddTaskAndDocumentPms(view.getContext(), projectEntity);
        }
    }

    /**
     * 获取项目权限
     */
    private void checkAddTaskAndDocumentPms(final Context context, final ProjectEntity projectEntity) {
        showLoadingDialog(context, null);
        getApi().permissionQuery(getLoginUserId(), "MAT", projectEntity.pkId).enqueue(new SimpleCallBack<List<String>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                dismissLoadingDialog();
                if (response.body().result != null) {
                    if (response.body().result.contains("MAT:matter.matterSelf:enter")) {
                        ProjectDetailActivity.launch(context, projectEntity.pkId, projectEntity.name);
                    } else {
                        showToast("您没有进入此项目的权限");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, ViewHolder holder, View view, int position) {

        return true;
    }
}
