package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.PinyinComparator;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHOICE_TYPE_MULTIPLE;
import static com.icourt.alpha.constants.Const.CHOICE_TYPE_SINGLE;

/**
 * Description  我的联系人
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/27
 * version 1.0.0
 */
public class ContactListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String STRING_TOP = "↑︎";
    private static final String KEY_SELCTED_TYPE = "key_selcted_type";
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
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;

    SuspensionDecoration mDecoration;
    ContactDbService contactDbService;
    IMContactAdapter imContactAdapter;
    LinearLayoutManager linearLayoutManager;
    HeaderFooterAdapter<IMContactAdapter> headerFooterAdapter;
    EditText header_input_et;

    /**
     * 浏览
     *
     * @param context
     */
    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, ContactListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 选择
     *
     * @param context
     * @param type
     * @param reqCode
     */
    public static void launchSelect(
            @NonNull Activity context,
            @Const.ChoiceType int type,
            int reqCode) {
        if (context == null) return;
        Intent intent = new Intent(context, ContactListActivity.class);
        intent.putExtra(KEY_SELCTED_TYPE, type);
        context.startActivityForResult(intent, reqCode);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contact_list);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();

        contactDbService = new ContactDbService(getLoginUserId());
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (softKeyboardSizeWatchLayout != null
                                && softKeyboardSizeWatchLayout.isSoftKeyboardPop()) {
                            SystemUtils.hideSoftKeyBoard(getActivity(), header_input_et, true);
                        }
                    }
                    break;
                }
            }
        });
        headerFooterAdapter = new HeaderFooterAdapter<>(imContactAdapter = new IMContactAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_input_text, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        header_input_et = (EditText) headerView.findViewById(R.id.header_input_et);
        header_input_et.clearFocus();
        header_input_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    getData(true);
                } else {
                    serachGroupMember(s.toString());
                }
            }
        });


        TextView titleActionTextView = getTitleActionTextView();
        recyclerView.setAdapter(headerFooterAdapter);
        switch (getIntent().getIntExtra(KEY_SELCTED_TYPE, 0)) {
            case CHOICE_TYPE_SINGLE:
                setTitle("选择成员");
                setViewVisible(titleActionTextView, false);
                break;
            case CHOICE_TYPE_MULTIPLE:
                imContactAdapter.setSelectable(true);
                setTitle("选择成员");
                if (titleActionTextView != null) {
                    titleActionTextView.setText("确定");
                }
                break;
            default:
                setTitle("通讯录");
                setViewVisible(titleActionTextView, false);
                break;
        }

        imContactAdapter.setOnItemClickListener(this);
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

        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.setPullRefreshEnable(true);
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();

        getContactsFromDb();
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
                IndexUtils.setSuspensions(getContext(), contactBeen);
                Collections.sort(contactBeen, new PinyinComparator<GroupContactBean>());
                imContactAdapter.bindData(true, contactBeen);
                updateIndexBar(contactBeen);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
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

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().usersQuery()
                .enqueue(new SimpleCallBack<List<GroupContactBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupContactBean>>> call, Response<ResEntity<List<GroupContactBean>>> response) {
                        if (response.body().result != null) {
                            List<GroupContactBean> data = response.body().result;
                            //插入数据库
                            insertAsynContact(data);
                            filterRobot(data);
                            IndexUtils.setSuspensions(getContext(), data);
                            Collections.sort(data, new PinyinComparator<GroupContactBean>());
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
     * 搜索用户
     *
     * @param name
     */
    private void serachGroupMember(String name) {
        try {
            RealmResults<ContactDbModel> result = contactDbService.contains("name", name);
            if (result == null) {
                imContactAdapter.clearData();
                return;
            }
            List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(result));
            filterRobot(contactBeen);
            imContactAdapter.bindData(true, contactBeen);
            updateIndexBar(contactBeen);
        } catch (Throwable e) {
            e.printStackTrace();
        }
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

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
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
        switch (getIntent().getIntExtra(KEY_SELCTED_TYPE, 0)) {
            case CHOICE_TYPE_SINGLE:
                Intent intent = getIntent();
                intent.putExtra(KEY_ACTIVITY_RESULT, imContactAdapter.getItem(imContactAdapter.getRealPos(position)));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case CHOICE_TYPE_MULTIPLE:
                imContactAdapter.toggleSelected(position);
                break;
            default:
                //TODO 进入详情
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                Intent intent = getIntent();
                intent.putExtra(KEY_ACTIVITY_RESULT, imContactAdapter.getSelectedData());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}