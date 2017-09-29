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
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icourt.alpha.activity.MainActivity.KEY_CUSTOMER_PERMISSION;

/**
 * Description  项目当事人列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class ProjectJudgeActivity extends BaseActivity {
    private static final String KEY_LIST = "key_list";
    private static final String KEY_TYPE = "key_type";
    private static final String KEY_MKEY = "key_mkey";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List list = new ArrayList<>();
    int type;
    String key;
    ProjectJudgeAdapter projectJudgeAdapter;
    private CustomerDbService customerDbService = null;

    public static void launch(@NonNull Context context, String key, List list, int type) {
        if (context == null) return;
        Intent intent = new Intent(context, ProjectJudgeActivity.class);
        intent.putExtra(KEY_LIST, (Serializable) list);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_MKEY, key);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_acceptance_layout);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        list = (List) getIntent().getSerializableExtra(KEY_LIST);
        type = getIntent().getIntExtra(KEY_TYPE, -1);
        key = getIntent().getStringExtra(KEY_MKEY);
        customerDbService = new CustomerDbService(LoginInfoUtils.getLoginUserId());
        setTitle(key);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerView.setAdapter(projectJudgeAdapter = new ProjectJudgeAdapter());
        projectJudgeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                if (adapter instanceof ProjectJudgeAdapter) {
                    if (type == Const.PROJECT_JUDGE_TYPE ||//法官
                            type == Const.PROJECT_CLERK_TYPE ||//书记员
                            type == Const.PROJECT_ARBITRATORS_TYPE ||//仲裁员
                            type == Const.PROJECT_SECRETARIES_TYPE) {//仲裁秘书
                        TextView phoneview = holder.obtainView(R.id.judge_phone_tv);
                        callPhone(phoneview.getText());
                    } else if (type == Const.PROJECT_PERSON_TYPE) {//当事人
                        ProjectDetailEntity.LitigantsBean litigantsBean = (ProjectDetailEntity.LitigantsBean) projectJudgeAdapter.getItem(position);
                        if (!TextUtils.isEmpty(litigantsBean.type) && !TextUtils.isEmpty(litigantsBean.contactPkid) && !TextUtils.equals("L1", litigantsBean.type)) {
                            gotoContactActivity(litigantsBean);
                        }
                    }
                }
            }
        });
        bindData();
    }

    private void bindData() {
        if (list != null) {
            projectJudgeAdapter.bindData(true, list);
        }
    }

    /**
     * 跳转到联系人详情
     *
     * @param litigantsBean
     */
    private void gotoContactActivity(ProjectDetailEntity.LitigantsBean litigantsBean) {
        if (!hasCustomerPermission()) return;
        if (customerDbService == null) return;

        CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", litigantsBean.contactPkid);
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

    /**
     * 搜索
     *
     * @param name
     */
    private void searchUserByName(String name) {
        if (TextUtils.isEmpty(name)) return;
        if (list != null) {
            List entities = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof ProjectDetailEntity.JudgeBean) {
                    ProjectDetailEntity.JudgeBean judge = (ProjectDetailEntity.JudgeBean) list.get(i);
                    if (judge.name.contains(name)) {
                        entities.add(judge);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.ClerkBean) {
                    ProjectDetailEntity.ClerkBean clerk = (ProjectDetailEntity.ClerkBean) list.get(i);
                    if (clerk.name.contains(name)) {
                        entities.add(clerk);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.ArbitratorBean) {
                    ProjectDetailEntity.ArbitratorBean arbitrator = (ProjectDetailEntity.ArbitratorBean) list.get(i);
                    if (arbitrator.name.contains(name)) {
                        entities.add(arbitrator);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.SecretarieBean) {
                    ProjectDetailEntity.SecretarieBean secretar = (ProjectDetailEntity.SecretarieBean) list.get(i);
                    if (secretar.name.contains(name)) {
                        entities.add(secretar);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.GroupsBean) {
                    ProjectDetailEntity.GroupsBean groupsBean = (ProjectDetailEntity.GroupsBean) list.get(i);
                    if (groupsBean.name.contains(name)) {
                        entities.add(groupsBean);
                    }
                } else if (list.get(i) instanceof ProjectDetailEntity.LitigantsBean) {
                    ProjectDetailEntity.LitigantsBean litigantsBean = (ProjectDetailEntity.LitigantsBean) list.get(i);
                    if (litigantsBean.contactName.contains(name)) {
                        entities.add(litigantsBean);
                    }
                }
            }
            projectJudgeAdapter.bindData(true, entities);
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

    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }
}
