package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.GroupListActivity;
import com.icourt.alpha.activity.SearchPolymerizationActivity;
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
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.icourt.alpha.widget.comparators.PinyinComparator;
import com.icourt.alpha.widget.filter.ListFilter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private static final String STRING_TOP = "↑︎";
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.recyclerIndexBar)
    WaveSideBar recyclerIndexBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;
    AlphaUserInfo loginUserInfo;
    RecyclerView headerRecyclerView;
    ItemActionAdapter<ItemsEntity> itemsEntityItemActionAdapter;
    HeaderFooterAdapter<IMContactAdapter> headerFooterAdapter;
    IMContactAdapter imContactAdapter;
    SuspensionDecoration mDecoration;
    ContactDbService contactDbService;
    LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_contact_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getContactsFromDb();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }


    @Override
    protected void initView() {
        loginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshLayout.setEnableLoadmore(false);

        headerFooterAdapter = new HeaderFooterAdapter<IMContactAdapter>(imContactAdapter = new IMContactAdapter());
        imContactAdapter.setOnItemClickListener(this);
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_contact_search, recyclerView.getRecyclerView());
        headerRecyclerView = (RecyclerView) headerView.findViewById(R.id.headerRecyclerView);
        headerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerRecyclerView.setAdapter(itemsEntityItemActionAdapter = new ItemActionAdapter<ItemsEntity>());
        itemsEntityItemActionAdapter.setOnItemClickListener(this);
        itemsEntityItemActionAdapter.bindData(true, Arrays.asList(new ItemsEntity("我加入的讨论组", R.mipmap.ic_members),
                new ItemsEntity("所有讨论组", R.mipmap.ic_all_group)));


        headerView.findViewById(R.id.rl_comm_search).setOnClickListener(this);
        headerFooterAdapter.addHeader(headerView);


        mDecoration = new SuspensionDecoration(getActivity(), null);
        mDecoration.setColorTitleBg(0xFFF3F3F3);
        mDecoration.setColorTitleFont(0xFFa6a6a6);
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
                for (int i = 0; i < imContactAdapter.getItemCount(); i++) {
                    GroupContactBean item = imContactAdapter.getItem(i);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comm_search:
                SearchPolymerizationActivity.launch(getContext(),
                        SearchPolymerizationActivity.SEARCH_PRIORITY_CONTACT);
                break;
            default:
                super.onClick(v);
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
                filterMySelf(contactBeen);
                IndexUtils.setSuspensions(getContext(), contactBeen);
                try {
                    Collections.sort(contactBeen, new PinyinComparator<GroupContactBean>());
                } catch (Exception e) {
                    e.printStackTrace();
                    bugSync("排序异常", e);
                }
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
        callEnqueue(
                getChatApi().usersQuery(),
                new SimpleCallBack<List<GroupContactBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupContactBean>>> call, Response<ResEntity<List<GroupContactBean>>> response) {
                        if (response.body().result != null) {
                            List<GroupContactBean> data = response.body().result;
                            //插入数据库
                            insertAsynContact(data);
                            filterRobot(data);
                            filterMySelf(data);
                            IndexUtils.setSuspensions(getContext(), data);
                            try {
                                Collections.sort(data, new PinyinComparator<GroupContactBean>());
                            } catch (Exception e) {
                                e.printStackTrace();
                                bugSync("排序异常", e);
                            }
                            imContactAdapter.bindData(true, data);
                            updateIndexBar(data);
                        }
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<GroupContactBean>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    /**
     * 过滤掉 机器人（robot == 100）
     *
     * @param data
     * @return
     */
    private List<GroupContactBean> filterRobot(List<GroupContactBean> data) {
        return new ListFilter<GroupContactBean>().filter(data, GroupContactBean.TYPE_ROBOT);
    }

    /**
     * 过滤调自己
     *
     * @param data
     * @return
     */
    private List<GroupContactBean> filterMySelf(List<GroupContactBean> data) {
        GroupContactBean groupContactBean = new GroupContactBean();
        groupContactBean.accid = StringUtils.toLowerCase(getLoginUserId());
        new ListFilter<GroupContactBean>().filter(data, groupContactBean);
        return data;
    }

    /**
     * 更新indextBar
     *
     * @param data
     */
    private void updateIndexBar(List<GroupContactBean> data) {
        try {
            ArrayList<String> suspensions = IndexUtils.getSuspensions(data);
            suspensions.add(0, STRING_TOP);
            recyclerIndexBar.setIndexItems(suspensions.toArray(new String[suspensions.size()]));
            mDecoration.setmDatas(data);
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
        if (data == null || data.isEmpty()) return;
        try {
            contactDbService.deleteAll();
            contactDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<ContactDbModel>>(data));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter == imContactAdapter) {
            GroupContactBean data = imContactAdapter.getData(adapter.getRealPos(position));
            showContactDialogFragment(data);
        } else if (adapter == itemsEntityItemActionAdapter) {
            ItemsEntity item = itemsEntityItemActionAdapter.getItem(position);
            if (item != null) {
                switch (position) {
                    case 0:
                        GroupListActivity.launch(getContext(),
                                GroupListActivity.GROUP_TYPE_MY_JOIN);
                        break;
                    case 1:
                        GroupListActivity.launch(getContext(),
                                GroupListActivity.GROUP_TYPE_TYPE_ALL);
                        break;
                }
            }
        }
    }

    /**
     * 展示联系人对话框
     *
     * @param data
     */
    public void showContactDialogFragment(GroupContactBean data) {
        if (data == null) return;
        String tag = ContactDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(data.accid, "联系人资料", StringUtils.equalsIgnoreCase(data.accid, getLoginUserId(), false))
                .show(mFragTransaction, tag);
    }

}
