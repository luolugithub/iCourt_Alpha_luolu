package com.icourt.alpha.activity;

import android.app.Activity;
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
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.PinyinComparator;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 选择联络人
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class SelectLiaisonActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String STRING_TOP = "↑︎";
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
    @BindView(R.id.recyclerIndexBar)
    WaveSideBar recyclerIndexBar;
    CustomerAdapter customerAdapter;
    SuspensionDecoration mDecoration;
    CustomerDbService customerDbService;
    LinearLayoutManager linearLayoutManager;

    List<CustomerEntity> liaisonsList;
    String pkid, action;

    public static void launchForResult(@NonNull Activity context, @NonNull String action, @NonNull String pkid, @NonNull List<CustomerEntity> liaisonsList, int requestCode) {
        if (context == null) return;
        Intent intent = new Intent(context, SelectLiaisonActivity.class);
        intent.setAction(action);
        intent.putExtra("liaisonsList", (Serializable) liaisonsList);
        intent.putExtra("pkid", pkid);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_liaison_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("选择联络人");
        liaisonsList = (List<CustomerEntity>) getIntent().getSerializableExtra("liaisonsList");
        pkid = getIntent().getStringExtra("pkid");
        action = getIntent().getAction();
        customerDbService = new CustomerDbService(getLoginUserId());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(customerAdapter = new CustomerAdapter());
        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
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
                                .scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });




        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        customerAdapter.setOnItemClickListener(this);
        refreshLayout.setPullRefreshEnable(true);
        refreshLayout.setAutoRefresh(true);

        getLocalCustomers();

        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().getCustomers(100000)
                .enqueue(new SimpleCallBack<List<CustomerEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<CustomerEntity>>> call, Response<ResEntity<List<CustomerEntity>>> response) {
                        stopRefresh();
                        if (response.body().result != null) {
                            removeSelected(response.body().result);
                            IndexUtils.setSuspensions(getContext(), response.body().result);
//                            Collections.sort(response.body().result, new PinyinComparator<CustomerEntity>());
                          Arrays.sort(response.body().result.toArray(new CustomerEntity[response.body().result.size()]),new PinyinComparator<CustomerEntity>());
                            updateIndexBar(response.body().result);
                            customerAdapter.bindData(true, response.body().result);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<CustomerEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void removeSelected(List<CustomerEntity> list) {
        if (list != null && liaisonsList != null) {
            list.removeAll(liaisonsList);
        }
        if (!TextUtils.isEmpty(pkid)) {
            Iterator<CustomerEntity> it = list.iterator();
            while (it.hasNext()) {
                if (TextUtils.equals(pkid, it.next().pkid)) {
                    it.remove();
                }
            }
        }
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
                removeSelected(customerEntities);
                customerAdapter.bindData(true, customerEntities);
                updateIndexBar(customerEntities);
            }
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
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        CustomerEntity entity = (CustomerEntity) adapter.getItem(adapter.getRealPos(position));
        CustomerPersonCreateActivity.launchSetResultFromLiaison(this, action, entity);
        finish();
    }
}
