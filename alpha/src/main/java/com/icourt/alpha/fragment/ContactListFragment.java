package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ContactDetailActivity;
import com.icourt.alpha.activity.ContactSearchActivity;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.ItemActionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.CustomIndexBarDataHelper;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  联系人列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class ContactListFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerIndexBar)
    IndexBar recyclerIndexBar;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    AlphaUserInfo loginUserInfo;
    RecyclerView headerRecyclerView;
    ItemActionAdapter<ItemsEntity> itemsEntityItemActionAdapter;
    HeaderFooterAdapter<IMContactAdapter> headerFooterAdapter;
    IMContactAdapter imContactAdapter;
    SuspensionDecoration mDecoration;
    ContactDbService contactDbService;

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_contact_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    protected void initView() {
        loginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFf4f4f4);
        mDecoration.setColorTitleFont(0xFF4a4a4a);
        mDecoration.setTitleFontSize(DensityUtil.sp2px(getContext(), 16));
        recyclerView.addItemDecoration(mDecoration);

        headerFooterAdapter = new HeaderFooterAdapter<IMContactAdapter>(imContactAdapter = new IMContactAdapter());
        imContactAdapter.setOnItemClickListener(this);
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_contact_search, recyclerView);
        headerRecyclerView = (RecyclerView) headerView.findViewById(R.id.headerRecyclerView);
        headerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerRecyclerView.setAdapter(itemsEntityItemActionAdapter = new ItemActionAdapter<ItemsEntity>());
        itemsEntityItemActionAdapter.bindData(true, Arrays.asList(new ItemsEntity("我加入的讨论组", R.mipmap.tab_message),
                new ItemsEntity("所有讨论组", R.mipmap.tab_message),
                new ItemsEntity("已归档讨论组", R.mipmap.tab_message)));


        headerView.findViewById(R.id.rl_comm_search).setOnClickListener(this);
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
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContactsFromDb();
        if (refreshLayout != null) {
            refreshLayout.startRefresh();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_comm_search:
                ContactSearchActivity.launch(getActivity(), v);
                break;
        }
    }

    /**
     * 从数据库获取所有联系人
     */
    private void getContactsFromDb() {
        try {
            RealmResults<ContactDbModel> contactDbModels = contactDbService.queryAll();
            if (contactDbModels != null) {
                List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels));
                filterRobot(contactBeen);
                imContactAdapter.bindData(true, contactBeen);
                updateIndexBar(contactBeen);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取联系人
     *
     * @param isRefresh 是否刷新
     */
    @Override
    protected void getData(boolean isRefresh) {
        getApi().getGroupContacts(loginUserInfo == null ? "" : loginUserInfo.getOfficeId())
                .enqueue(new SimpleCallBack<List<GroupContactBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupContactBean>>> call, Response<ResEntity<List<GroupContactBean>>> response) {
                        if (response.body().result != null) {
                            List<GroupContactBean> data = response.body().result;
                            //插入数据库
                            insertAsynContact(data);
                            getRobos();
                            filterRobot(data);
                            imContactAdapter.bindData(true, data);
                            updateIndexBar(data);
                        }
                        refreshLayout.stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<GroupContactBean>>> call, Throwable t) {
                        super.onFailure(call, t);
                        refreshLayout.stopRefresh();
                    }
                });
    }

    /**
     * 过滤掉 机器人（robot == 1）
     *
     * @param data
     * @return
     */
    private List<GroupContactBean> filterRobot(List<GroupContactBean> data) {
        if (data != null) {
            //过滤
            for (int i = data.size() - 1; i >= 0; i--) {
                GroupContactBean groupContactBean = data.get(i);
                if (groupContactBean != null) {
                    if (groupContactBean.robot == 1) {
                        data.remove(i);
                    }
                }
            }
        }
        return data;
    }

    /**
     * 更新indextBar
     *
     * @param data
     */
    private void updateIndexBar(List<GroupContactBean> data) {
        List<GroupContactBean> wrapDatas = new ArrayList<GroupContactBean>(data);
        GroupContactBean headerContactBean = new GroupContactBean();
        headerContactBean.isNotNeedToPinyin = true;
        headerContactBean.setBaseIndexTag("↑︎");
        wrapDatas.add(0, headerContactBean);
        try {
            recyclerIndexBar.setDataHelper(new CustomIndexBarDataHelper()).setmSourceDatas(wrapDatas).invalidate();
            mDecoration.setmDatas(wrapDatas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步插入联系人
     * 先删除所有联系人
     *
     * @param data
     */
    private void insertAsynContact(List<GroupContactBean> data) {
        if (data == null) return;
        try {
            contactDbService.deleteAll();
            contactDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<ContactDbModel>>(data));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取机器人
     */
    private void getRobos() {
        getApi().getRobos()
                .enqueue(new SimpleCallBack<List<GroupContactBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupContactBean>>> call, Response<ResEntity<List<GroupContactBean>>> response) {
                        if (response.body().result != null
                                && contactDbService != null) {
                            contactDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<ContactDbModel>>(response.body().result));
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        GroupContactBean data = imContactAdapter.getData(position -
                (imContactAdapter.getParentHeaderFooterAdapter() == null
                        ? 0 : imContactAdapter.getParentHeaderFooterAdapter().getHeaderCount()));
        ContactDetailActivity.launch(getContext(), data, false, false);
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, Bundle bundle) {

    }
}
