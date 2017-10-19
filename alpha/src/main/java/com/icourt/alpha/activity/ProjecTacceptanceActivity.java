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
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.ProjectProcessesEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;

/**
 * Description  项目程序信息二级列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class ProjecTacceptanceActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private ProjectProcessesEntity.ExtraBean extraBean;
    ProjectJudgeAdapter projectJudgeAdapter;
    private CustomerDbService customerDbService = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_acceptance_layout);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    public static void launch(@NonNull Context context, ProjectProcessesEntity.ExtraBean extraBean) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjecTacceptanceActivity.class);
        intent.putExtra("extraBean", (Serializable) extraBean);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        customerDbService = new CustomerDbService(LoginInfoUtils.getLoginUserId());
        extraBean = (ProjectProcessesEntity.ExtraBean) getIntent().getSerializableExtra("extraBean");
        if (extraBean != null) {
            setTitle(extraBean.name);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerView.setAdapter(projectJudgeAdapter = new ProjectJudgeAdapter());

        projectJudgeAdapter.setOnItemClickListener(this);
        projectJudgeAdapter.setOnItemChildClickListener(this);

        bindData();
    }

    private void bindData() {
        if (extraBean != null) {
            projectJudgeAdapter.bindData(true, extraBean.values);
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

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof ProjectJudgeAdapter) {
            ProjectProcessesEntity.ExtraBean.ValuesBean valuesBean = (ProjectProcessesEntity.ExtraBean.ValuesBean) adapter.getItem(position);
            if (valuesBean == null) return;
            if (TextUtils.isEmpty(valuesBean.id)) return;
            //type ＝ L0 ：自定义当事人
            if (!TextUtils.isEmpty(valuesBean.type) && !TextUtils.isEmpty(valuesBean.id) && !TextUtils.equals("L0", valuesBean.type)) {
                gotoCustiomer(valuesBean);
            }

        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof ProjectJudgeAdapter) {
            ProjectProcessesEntity.ExtraBean.ValuesBean valuesBean = (ProjectProcessesEntity.ExtraBean.ValuesBean) adapter.getItem(position);
            if (valuesBean == null) return;
            callPhone(valuesBean.phone);
        }
    }

    /**
     * 是否有查看联系人权限
     *
     * @return true:有权限 false：无权限
     */
    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }

    /**
     * 跳转到客户详情
     *
     * @param valuesBean
     */
    private void gotoCustiomer(ProjectProcessesEntity.ExtraBean.ValuesBean valuesBean) {
        if (!hasCustomerPermission()) return;
        if (customerDbService == null) return;
        CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", valuesBean.id);
        if (customerDbModel == null) {
            showTopSnackBar(R.string.project_not_look_info_premission);
            return;
        }
        CustomerEntity customerEntity = customerDbModel.convert2Model();
        if (customerEntity == null) {
            showTopSnackBar(R.string.project_not_look_info_premission);
            return;
        }
        if (!TextUtils.isEmpty(customerEntity.contactType)) {
            MobclickAgent.onEvent(getContext(), UMMobClickAgent.look_client_click_id);
            //公司
            if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "C")) {
                CustomerCompanyDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, false);
            } else if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "P")) {
                CustomerPersonDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, false);
            }
        }
    }
}
