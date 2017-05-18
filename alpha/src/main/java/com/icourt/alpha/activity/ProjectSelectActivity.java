package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择项目
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/16
 * version 2.0.0
 */

public class ProjectSelectActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    String authToken, seaFileRepoId, filePath;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    ProjectAdapter projectAdapter;

    public static void launch(@NonNull Context context, @NonNull String authToken, @NonNull String seaFileRepoId, @NonNull String filePath) {
        if (context == null) return;
        if (TextUtils.isEmpty(authToken)) return;
        if (TextUtils.isEmpty(seaFileRepoId)) return;
        Intent intent = new Intent(context, ProjectSelectActivity.class);
        intent.putExtra("authToken", authToken);
        intent.putExtra("seaFileRepoId", seaFileRepoId);
        intent.putExtra("filePath", filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_select_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("我参与的项目");
        authToken = getIntent().getStringExtra("authToken");
        seaFileRepoId = getIntent().getStringExtra("seaFileRepoId");
        filePath = getIntent().getStringExtra("filePath");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(projectAdapter = new ProjectAdapter(false));
        projectAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().projectSelectListQuery()
                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        dismissLoadingDialog();
                        projectAdapter.bindData(true, response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<ProjectEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        ProjectEntity projectEntity = (ProjectEntity) adapter.getItem(position);
        if(projectEntity!=null){
            FolderboxSelectActivity.launch(this,authToken,seaFileRepoId,filePath,null);
        }
    }
}
