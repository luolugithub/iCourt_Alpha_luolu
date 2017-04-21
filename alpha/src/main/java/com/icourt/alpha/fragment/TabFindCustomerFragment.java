package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.CustomerSearchActivity;
import com.icourt.alpha.activity.MyCollectedCustomersActivity;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.CustomIndexBarDataHelper;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
public class TabFindCustomerFragment extends BaseFragment {

    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    CustomerAdapter customerAdapter;
    HeaderFooterAdapter<CustomerAdapter> headerFooterAdapter;
    @BindView(R.id.recyclerIndexBar)
    IndexBar recyclerIndexBar;
    SuspensionDecoration mDecoration;
    CustomerDbService customerDbService;

    public static TabFindCustomerFragment newInstance() {
        return new TabFindCustomerFragment();
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
        customerDbService = new CustomerDbService(getLoginUserId());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
        recyclerView.addItemDecoration(mDecoration);

        headerFooterAdapter = new HeaderFooterAdapter<>(customerAdapter = new CustomerAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_customer_search, recyclerView);
        View header_customer_collected = headerView.findViewById(R.id.header_customer_collected);
        registerClick(header_customer_collected);
        View rl_comm_search = headerView.findViewById(R.id.rl_comm_search);
        registerClick(rl_comm_search);
        headerFooterAdapter.addHeader(headerView);
        recyclerView.setAdapter(headerFooterAdapter);

        recyclerIndexBar
                //.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)
                .setmLayoutManager(linearLayoutManager);

        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.setPullRefreshEnable(true);
        refreshLayout.setAutoRefresh(true);

        getLocalCustomers();

        refreshLayout.startRefresh();
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
                customerAdapter.bindData(true, customerEntities);
                updateIndexBar(customerEntities);
            }
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().getCustomers(0, 100000)
                .enqueue(new SimpleCallBack<List<CustomerEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<CustomerEntity>>> call, Response<ResEntity<List<CustomerEntity>>> response) {
                        stopRefresh();
                        customerAdapter.bindData(isRefresh, response.body().result);
                        updateIndexBar(response.body().result);
                        insert2Db(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<CustomerEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                CustomerSearchActivity.launch(getContext(),v);
                break;
            case R.id.header_customer_collected:
                MyCollectedCustomersActivity.launch(getContext());
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
        List<CustomerEntity> wrapDatas = new ArrayList<CustomerEntity>(data);
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.isNotNeedToPinyin = true;
        customerEntity.setBaseIndexTag("↑︎");
        wrapDatas.add(0, customerEntity);
        try {
            recyclerIndexBar.setDataHelper(new CustomIndexBarDataHelper()).setmSourceDatas(wrapDatas).invalidate();
            mDecoration.setmDatas(wrapDatas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (customerDbService != null) {
            customerDbService.releaseService();
        }
    }
}
