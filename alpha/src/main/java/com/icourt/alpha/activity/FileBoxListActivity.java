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
import com.icourt.alpha.adapter.ProjectFileBoxAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description  文档列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/15
 * version 2.0.0
 */

public class FileBoxListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    FileBoxBean fileBoxBean;
    String authToken, seaFileRepoId, rootName;
    ProjectFileBoxAdapter projectFileBoxAdapter;

    public static void launch(@NonNull Context context, @NonNull FileBoxBean fileBoxBean, @NonNull String authToken, @NonNull String seaFileRepoId, @NonNull String rootName) {
        if (context == null) return;
        Intent intent = new Intent(context, FileBoxListActivity.class);
        intent.putExtra("file", fileBoxBean);
        intent.putExtra("authToken", authToken);
        intent.putExtra("seaFileRepoId", seaFileRepoId);
        intent.putExtra("rootName", rootName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_box_list_layout);
        ButterKnife.bind(this);
        initView();

    }

    @Override
    protected void initView() {
        super.initView();
        seaFileRepoId = getIntent().getStringExtra("seaFileRepoId");
        rootName = getIntent().getStringExtra("rootName");
        authToken = getIntent().getStringExtra("authToken");
        fileBoxBean = (FileBoxBean) getIntent().getSerializableExtra("file");
        if (fileBoxBean != null) {
            setTitle(fileBoxBean.name);
        }
        titleAction.setImageResource(R.mipmap.header_icon_add);
        titleAction2.setImageResource(R.mipmap.header_icon_more);
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_project, R.string.null_project);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(projectFileBoxAdapter = new ProjectFileBoxAdapter());
        projectFileBoxAdapter.setOnItemClickListener(this);
        projectFileBoxAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, projectFileBoxAdapter));

        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().projectQueryFileBoxByDir("Token " + authToken, seaFileRepoId, rootName).enqueue(new Callback<List<FileBoxBean>>() {
            @Override
            public void onResponse(Call<List<FileBoxBean>> call, Response<List<FileBoxBean>> response) {
                stopRefresh();
                if (response.body() != null) {
                    projectFileBoxAdapter.bindData(isRefresh, response.body());
                }
            }

            @Override
            public void onFailure(Call<List<FileBoxBean>> call, Throwable t) {
                stopRefresh();
                showTopSnackBar("获取文档列表失败");
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
        FileBoxBean fileBoxBean = (FileBoxBean) adapter.getItem(position);
        if (!TextUtils.isEmpty(fileBoxBean.type)) {
            if (TextUtils.equals("file", fileBoxBean.type)) {

            }
            if (TextUtils.equals("dir", fileBoxBean.type)) {
                FileBoxListActivity.launch(this, fileBoxBean, authToken, seaFileRepoId, rootName + "/" + fileBoxBean.name);
            }
        }
    }
}
