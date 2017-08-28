package com.icourt.alpha.activity;

import android.Manifest;
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
import com.icourt.alpha.adapter.ProjectJudgeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.ProjectProcessesEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.SystemUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description  项目程序信息二级列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class ProjecTacceptanceActivity extends BaseActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private ProjectProcessesEntity.AcceptanceBean acceptanceBean;
    ProjectJudgeAdapter projectJudgeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_acceptance_layout);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    public static void launch(@NonNull Context context, ProjectProcessesEntity.AcceptanceBean acceptanceBean) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjecTacceptanceActivity.class);
        intent.putExtra("acceptanceBean", acceptanceBean);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        acceptanceBean = (ProjectProcessesEntity.AcceptanceBean) getIntent().getSerializableExtra("acceptanceBean");
        if (acceptanceBean != null) {
            setTitle(acceptanceBean.name);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerView.setAdapter(projectJudgeAdapter = new ProjectJudgeAdapter());

        projectJudgeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                if (adapter instanceof ProjectJudgeAdapter) {
                    TextView phoneview = holder.obtainView(R.id.judge_phone_tv);
                    callPhone(phoneview.getText());
                }
            }
        });
        bindData();
    }

    private void bindData() {
        if (acceptanceBean != null) {
            projectJudgeAdapter.bindData(true, acceptanceBean.values);
        }
    }

    /**
     * 打电话
     *
     * @param phone
     */
    private void callPhone(CharSequence phone) {
        if (!TextUtils.isEmpty(phone)) {
            if (!SystemUtils.checkPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE})) {
                SystemUtils.reqPermission(getActivity(), new String[]{Manifest.permission.CALL_PHONE,}, 12345);
            } else {
                SystemUtils.callPhone(getContext(), phone.toString());
            }
        }
    }
}
