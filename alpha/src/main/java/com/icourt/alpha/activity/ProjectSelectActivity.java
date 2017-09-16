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

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
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
    private static final String CLOSE_ACTION = "close_action";//关闭当前页面
    private static final String KEY_SEAFILEREPOID = "key_seaFileRepoId";
    private static final String KEY_AUTHTOKEN = "key_authToken";
    private static final String KEY_FILEPATH= "key_filePath";
    String seaFileRepoId, filePath;
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

    public static void launch(@NonNull Context context,
                              @NonNull String authToken,
                              @NonNull String seaFileRepoId,
                              @NonNull String filePath) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectSelectActivity.class);
        intent.putExtra(KEY_AUTHTOKEN, authToken);
        intent.putExtra(KEY_SEAFILEREPOID, seaFileRepoId);
        intent.putExtra(KEY_FILEPATH, filePath);
        context.startActivity(intent);
    }

    public static void lauchClose(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(CLOSE_ACTION);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.equals(action, CLOSE_ACTION)) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            if (TextUtils.equals(getIntent().getAction(), CLOSE_ACTION)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_project_select_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(getString(R.string.project_my_participation));
        seaFileRepoId = getIntent().getStringExtra(KEY_SEAFILEREPOID);
        filePath = getIntent().getStringExtra(KEY_FILEPATH);

        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, getString(R.string.project_no));
        refreshLayout.setMoveForHorizontal(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(projectAdapter = new ProjectAdapter(false));
        projectAdapter.setOnItemClickListener(this);
        projectAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, projectAdapter));
        //token 分享和保存到项目 token替换了
       /* if (TextUtils.isEmpty(authToken)) {
            getFileBoxToken();
        } else {
            getData(true);
        }*/
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);

            }
        });
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(
                getApi().projectPmsSelectListQuery("MAT:matter.document:readwrite"),
                new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        stopRefresh();
                        projectAdapter.bindData(true, response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<ProjectEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }


    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        ProjectEntity projectEntity = (ProjectEntity) adapter.getItem(position);
        if (projectEntity != null) {
            FolderboxSelectActivity.launch(this,
                    projectEntity.pkId,
                    null,
                    filePath,
                    null);
        }
    }
}
