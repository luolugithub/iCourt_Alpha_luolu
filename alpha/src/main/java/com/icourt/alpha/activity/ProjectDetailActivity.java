package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.ProjectDetailFragment;
import com.icourt.alpha.fragment.ProjectFileBoxFragment;
import com.icourt.alpha.fragment.ProjectTaskFragment;
import com.icourt.alpha.fragment.ProjectTimeFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description   项目详情页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/2
 * version 2.0.0
 */

public class ProjectDetailActivity extends BaseActivity implements OnFragmentCallBackListener {

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
    @BindView(R.id.detail_tablayout)
    TabLayout detailTablayout;
    @BindView(R.id.detail_viewpager)
    ViewPager detailViewpager;

    String projectId, projectName;
    int myStar;
    BaseFragmentAdapter baseFragmentAdapter;
    ProjectFileBoxFragment projectFileBoxFragment;
    private boolean nameIsUp = false, timeIsUp = false, sizeIsUp = false;

    public static void launch(@NonNull Context context, @NonNull String projectId, @NonNull String proectName) {
        if (context == null) return;
        if (TextUtils.isEmpty(projectId)) return;
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra("projectId", projectId);
        intent.putExtra("projectName", proectName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        projectId = getIntent().getStringExtra("projectId");
        projectName = getIntent().getStringExtra("projectName");
        myStar = getIntent().getIntExtra("myStar", -1);
        if (!TextUtils.isEmpty(projectName)) {
            setTitle(projectName);
        }
        if (myStar != 1) {
            titleAction2.setImageResource(R.mipmap.header_icon_star_line);
        } else {
            titleAction2.setImageResource(R.mipmap.header_icon_star_solid);
        }

        //第一次打开默认概览：隐藏更多菜单入口
        titleAction.setVisibility(View.GONE);

        baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager());
        detailViewpager.setAdapter(baseFragmentAdapter);
        detailTablayout.setupWithViewPager(detailViewpager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList(
                "概览", "任务", "计时", "文档"
        ));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        ProjectDetailFragment.newInstance(projectId),
                        ProjectTaskFragment.newInstance(projectId),
                        ProjectTimeFragment.newInstance(projectId),
                        projectFileBoxFragment = ProjectFileBoxFragment.newInstance(projectId)
                ));
        detailTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isShowTitleAction(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 是否显示更多菜单入口
     *
     * @param position
     */
    private void isShowTitleAction(int position) {
        switch (position) {
            case 0:
                if (myStar != 1) {
                    titleAction2.setImageResource(R.mipmap.header_icon_star_line);
                } else {
                    titleAction2.setImageResource(R.mipmap.header_icon_star_solid);
                }
                titleAction.setVisibility(View.INVISIBLE);
                titleAction2.setVisibility(View.VISIBLE);
                break;
            case 1:
                titleAction.setImageResource(R.mipmap.header_icon_add);
                titleAction2.setImageResource(R.mipmap.header_icon_more);
                titleAction.setVisibility(View.VISIBLE);
                titleAction2.setVisibility(View.VISIBLE);
                break;
            case 2:
                titleAction.setVisibility(View.INVISIBLE);
                titleAction2.setVisibility(View.INVISIBLE);
                break;
            case 3:
                titleAction.setImageResource(R.mipmap.header_icon_add);
                titleAction2.setImageResource(R.mipmap.header_icon_more);
                titleAction.setVisibility(View.VISIBLE);
                titleAction2.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick({R.id.titleAction, R.id.titleAction2})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.titleAction:
                titleActionClick();
                break;
            case R.id.titleAction2:
                showBottomMeau();
                break;
        }
    }

    private void titleActionClick() {
        switch (detailTablayout.getSelectedTabPosition()) {
            case 0:     //概览
            case 2:     //计时
                break;
            case 1:     //任务
                TaskCreateActivity.launchFomProject(this, projectId, projectName);
                break;
            case 3:     //文档
                if (projectFileBoxFragment != null) {
                    projectFileBoxFragment.showBottomMeau();
                }
                break;
        }
    }

    /**
     * 显示底部更多菜单
     */
    private void showBottomMeau() {
        switch (detailTablayout.getSelectedTabPosition()) {
            case 0:     //概览
                if (myStar != 1) {
                    addStar();
                } else {
                    deleteStar();
                }
                break;
            case 1:     //任务
                showTaskMeau();
                break;
            case 2:     //计时
                break;
            case 3:     //文档
                showDocumentMeau();
                break;
        }
    }

    /**
     * 显示任务更多菜单
     */
    private void showTaskMeau() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("已完成任务", "管理任务组"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0://"已完成任务"
                                ProjectEndTaskActivity.launch(ProjectDetailActivity.this, projectId);
                                break;
                            case 1://"管理任务组"
                                ProjectTaskGroupActivity.launch(ProjectDetailActivity.this, projectId);
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 显示文档更多菜单
     */
    private void showDocumentMeau() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("按文件名升序排序", "按文件大小升序排序"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                if (projectFileBoxFragment != null) {
                                    projectFileBoxFragment.sortFileByNameList(nameIsUp);
                                    nameIsUp = !nameIsUp;
                                    timeIsUp = false;
                                    sizeIsUp = false;
                                }
                                break;
                            case 1:
                                if (projectFileBoxFragment != null) {
                                    projectFileBoxFragment.sortFileBySizeList(sizeIsUp);
                                    sizeIsUp = !sizeIsUp;
                                    nameIsUp = false;
                                    timeIsUp = false;
                                }
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 添加关注
     */
    private void addStar() {
        showLoadingDialog("正在关注...");
        getApi().projectAddStar(projectId).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                myStar = 1;
                titleAction2.setImageResource(R.mipmap.header_icon_star_solid);
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
        showLoadingDialog("正在取消关注...");
        getApi().projectDeleteStar(projectId).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                myStar = 0;
                titleAction2.setImageResource(R.mipmap.header_icon_star_line);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof ProjectDetailFragment) {
            myStar = params.getInt("myStar");
            if (myStar != 1) {
                titleAction2.setImageResource(R.mipmap.header_icon_star_line);
            } else {
                titleAction2.setImageResource(R.mipmap.header_icon_star_solid);
            }
        }
    }
}
