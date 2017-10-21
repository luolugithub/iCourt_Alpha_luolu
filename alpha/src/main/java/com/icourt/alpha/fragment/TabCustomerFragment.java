package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.CustomerCompanyCreateActivity;
import com.icourt.alpha.activity.CustomerCompanyDetailActivity;
import com.icourt.alpha.activity.CustomerPersonCreateActivity;
import com.icourt.alpha.activity.CustomerPersonDetailActivity;
import com.icourt.alpha.activity.CustomerSearchActivity;
import com.icourt.alpha.activity.MyCollectedCustomersActivity;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.event.UpdateCustomerEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.widget.comparators.PinyinComparator;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  客户界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class TabCustomerFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String STRING_TOP = "↑︎";

    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    CustomerAdapter customerAdapter;
    HeaderFooterAdapter<CustomerAdapter> headerFooterAdapter;
    @BindView(R.id.recyclerIndexBar)
    WaveSideBar recyclerIndexBar;
    SuspensionDecoration mDecoration;
    CustomerDbService customerDbService;
    LinearLayoutManager linearLayoutManager;

    public static TabCustomerFragment newInstance() {
        return new TabCustomerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find_customer, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        recyclerView.setNoticeEmpty(R.mipmap.icon_placeholder_user, R.string.client_not_add);
        customerDbService = new CustomerDbService(getLoginUserId());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        headerFooterAdapter = new HeaderFooterAdapter<>(customerAdapter = new CustomerAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_customer_search, recyclerView.getRecyclerView());
        View header_customer_collected = headerView.findViewById(R.id.header_customer_collected);
        registerClick(header_customer_collected);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);
        customerAdapter.setOnItemClickListener(this);

        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
        mDecoration.setHeaderViewCount(headerFooterAdapter.getHeaderCount());
        recyclerView.addItemDecoration(mDecoration);
        recyclerIndexBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                if (TextUtils.equals(index, STRING_TOP)) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                    return;
                }
                for (int i = 0; i < customerAdapter.getItemCount(); i++) {
                    CustomerEntity item = customerAdapter.getItem(i);
                    if (item != null && TextUtils.equals(item.getSuspensionTag(), index)) {
                        linearLayoutManager
                                .scrollToPositionWithOffset(i + headerFooterAdapter.getHeaderCount(), 0);
                        return;
                    }
                }
            }
        });

        recyclerView.setAdapter(headerFooterAdapter);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }
        });
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.autoRefresh();

        getLocalCustomers();
    }

    /**
     * 获取本地存储的数据
     */
    private void getLocalCustomers() {
        if (customerDbService != null) {
            RealmResults<CustomerDbModel> customerDbModels = customerDbService.queryAll();
            if (customerDbModels != null) {
                ArrayList<IConvertModel<CustomerEntity>> iConvertModels = new ArrayList<IConvertModel<CustomerEntity>>(customerDbModels);
                List<CustomerEntity> customerEntities = ListConvertor.convertList(iConvertModels);
                IndexUtils.setSuspensions(getContext(), customerEntities);
                try {
                    if (customerEntities != null)
                        Collections.sort(customerEntities, new PinyinComparator<CustomerEntity>());
                } catch (Exception e) {
                    e.printStackTrace();
                    bugSync("排序异常", e);
                }
                customerAdapter.bindData(true, customerEntities);
                updateIndexBar(customerEntities);
            }
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        callEnqueue(
                getApi().getCustomers(100000),
                new SimpleCallBack<List<CustomerEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<CustomerEntity>>> call, Response<ResEntity<List<CustomerEntity>>> response) {
                        stopRefresh();
                        if (response.body().result != null) {
                            IndexUtils.setSuspensions(getContext(), response.body().result);
                            try {
                                Collections.sort(response.body().result, new PinyinComparator<CustomerEntity>());
                            } catch (Exception e) {
                                e.printStackTrace();
                                bugSync("排序异常", e);
                            }
                            customerAdapter.bindData(true, response.body().result);
                            customerAdapter.setShowCustomerNum(true);
                            updateIndexBar(response.body().result);
                            insert2Db(response.body().result);
                            recyclerView.enableEmptyView(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<CustomerEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                CustomerSearchActivity.launch(getContext(), v, CustomerSearchActivity.SEARCH_CUSTOMER_TYPE, -1);
                break;
            case R.id.header_customer_collected:
                MyCollectedCustomersActivity.launch(getContext());
                break;
            case R.id.titleAction:
                new BottomActionDialog(getContext(), getString(R.string.client_menu_add), Arrays.asList(getString(R.string.client_menu_person), getString(R.string.client_menu_company)), new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                CustomerPersonCreateActivity.launch(getContext(), null, CustomerPersonCreateActivity.CREATE_CUSTOMER_ACTION);
                                break;
                            case 1:
                                CustomerCompanyCreateActivity.launch(getContext(), null, CustomerCompanyCreateActivity.CREATE_CUSTOMER_ACTION);
                                break;
                        }
                    }
                }).show();
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    /**
     * 保存到数据库
     *
     * @param data
     */
    private void insert2Db(List<CustomerEntity> data) {
        if (data == null) return;
        if (customerDbService != null) {
            customerDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<CustomerDbModel>>(data));
        }
    }


    /**
     * 更新indextBar
     *
     * @param data
     */
    private void updateIndexBar(List<CustomerEntity> data) {
        try {
            ArrayList<String> suspensions = IndexUtils.getSuspensions(data);
            suspensions.add(0, STRING_TOP);
            recyclerIndexBar.setIndexItems(suspensions.toArray(new String[suspensions.size()]));
            mDecoration.setmDatas(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        if (customerDbService != null) {
            customerDbService.releaseService();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        CustomerEntity customerEntity = (CustomerEntity) adapter.getItem(adapter.getRealPos(position));
        if (!TextUtils.isEmpty(customerEntity.contactType)) {
            MobclickAgent.onEvent(getContext(), UMMobClickAgent.look_client_click_id);
            //公司
            if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "C")) {
                CustomerCompanyDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, true);
            } else if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "P")) {
                CustomerPersonDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCustEvent(UpdateCustomerEvent event) {
        if (event != null) {
            getData(true);
        }
    }
}
