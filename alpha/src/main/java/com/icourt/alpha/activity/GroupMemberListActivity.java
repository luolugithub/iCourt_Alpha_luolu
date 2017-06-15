package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjiazhe.wavesidebar.WaveSideBar;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.PinyinComparator;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.view.recyclerviewDivider.SuspensionDecoration;
import com.icourt.alpha.widget.filter.ListFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHOICE_TYPE_MULTIPLE;
import static com.icourt.alpha.constants.Const.CHOICE_TYPE_SINGLE;

/**
 * Description  讨论组成员列表
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/6
 * version 1.0.0
 */
public class GroupMemberListActivity
        extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String STRING_TOP = "↑︎";
    private static final String KEY_SELCTED_TYPE = "key_selcted_type";
    private static final String KEY_ADD_AT_ALL = "key_add_at_all";
    private static final String KEY_SELECTED_DATA = "key_selected_data";
    private static final String KEY_TID = "key_tid";
    private static final String KEY_IS_FILTER_ME = "key_is_filter_me";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
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
    final List<GroupContactBean> groupMembers = new ArrayList<>();
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;


    /**
     * 选择
     *
     * @param context
     * @param tid
     * @param type
     * @param reqCode
     */
    public static void launchSelect(
            @NonNull Activity context,
            @NonNull String tid,
            @Const.ChoiceType int type,
            int reqCode, boolean addAtAll,
            @Nullable ArrayList<String> selectedData,
            boolean isFilterMySelf) {
        if (context == null) return;
        if (TextUtils.isEmpty(tid)) return;
        Intent intent = new Intent(context, GroupMemberListActivity.class);
        intent.putExtra(KEY_TID, tid);
        intent.putExtra(KEY_SELCTED_TYPE, type);
        intent.putExtra(KEY_ADD_AT_ALL, addAtAll);
        intent.putExtra(KEY_IS_FILTER_ME, isFilterMySelf);
        intent.putExtra(KEY_SELECTED_DATA, selectedData);
        context.startActivityForResult(intent, reqCode);
    }


    /**
     * 浏览模式
     *
     * @param context
     * @param tid
     */
    public static void launch(
            @NonNull Activity context,
            @NonNull String tid,
            boolean isFilterMySelf) {
        if (context == null) return;
        if (TextUtils.isEmpty(tid)) return;
        Intent intent = new Intent(context, GroupMemberListActivity.class);
        intent.putExtra(KEY_TID, tid);
        intent.putExtra(KEY_IS_FILTER_ME, isFilterMySelf);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contact_list);
        ButterKnife.bind(this);
        initView();
        getData(true);
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
        imContactAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (contentEmptyText != null) {
                    contentEmptyText.setVisibility(imContactAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
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
                setViewInVisible(titleActionTextView, false);
                break;
            case CHOICE_TYPE_MULTIPLE:
                imContactAdapter.setSelectable(true);
                setTitle("选择成员");
                if (titleActionTextView != null) {
                    titleActionTextView.setText("确定");
                }
                break;
            default:
                setTitle("成员");
                setViewInVisible(titleActionTextView, false);
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

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        getChatApi().groupQueryAllMemberIds(getIntent().getStringExtra(KEY_TID))
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        dismissLoadingDialog();
                        queryFromDbByIds(response.body().result);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });

    }

    /**
     * 从本地查询联系人
     *
     * @param ids
     */
    private void queryFromDbByIds(final List<String> ids) {
        if (ids == null || ids.isEmpty()) return;
        Observable.create(new ObservableOnSubscribe<List<GroupContactBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<GroupContactBean>> e) throws Exception {
                if (e.isDisposed()) return;
                ContactDbService threadContactDbService = new ContactDbService(getLoginUserId());
                List<GroupContactBean> groupContactBeans = new ArrayList<>();
                for (String id : ids) {
                    if (!TextUtils.isEmpty(id)) {
                        ContactDbModel accid = threadContactDbService.queryFirst("accid", id);
                        if (accid != null) {
                            try {
                                GroupContactBean contactBean = accid.convert2Model();
                                if (contactBean != null) {
                                    groupContactBeans.add(contactBean);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                threadContactDbService.releaseService();
                e.onNext(groupContactBeans);
                e.onComplete();
            }
        }).compose(this.<List<GroupContactBean>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GroupContactBean>>() {
                    @Override
                    public void accept(List<GroupContactBean> contactBeanList) throws Exception {
                        try {
                            filterRobot(contactBeanList);
                            //过滤自己
                            if (getIntent().getBooleanExtra(KEY_IS_FILTER_ME, false)) {
                                filterMySelf(contactBeanList);
                            }
                            IndexUtils.setSuspensions(getContext(), contactBeanList);
                            if (contactBeanList != null) {
                                groupMembers.clear();
                                groupMembers.addAll(contactBeanList);
                            }
                            Collections.sort(contactBeanList, new PinyinComparator<GroupContactBean>());
                            //添加@所有人
                            if (getIntent().getBooleanExtra(KEY_ADD_AT_ALL, false)) {
                                GroupContactBean atall = new GroupContactBean() {
                                    @Override
                                    public boolean isShowSuspension() {
                                        return false;
                                    }
                                };
                                atall.type = GroupContactBean.TYPE_ALL;
                                atall.name = "所有人";
                                contactBeanList.add(0, atall);
                            }
                            imContactAdapter.bindData(true, contactBeanList);
                            updateIndexBar(contactBeanList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
     * 搜索用户 当前讨论组
     *
     * @param name
     */
    private void serachGroupMember(String name) {
        try {
            List<GroupContactBean> contactBeen = new ArrayList<>();
            for (GroupContactBean contactBean : groupMembers) {
                if (contactBean == null) continue;
                if (StringUtils.equalsIgnoreCase(contactBean.name, name, false)
                        || StringUtils.containsIgnoreCase(contactBean.name, name)) {
                    contactBeen.add(contactBean);
                }
            }
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


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        switch (getIntent().getIntExtra(KEY_SELCTED_TYPE, 0)) {
            case CHOICE_TYPE_SINGLE:
                Parcelable item1 = imContactAdapter.getItem(imContactAdapter.getRealPos(position));
                if (item1 == null) return;
                Intent intent = getIntent();
                intent.putExtra(KEY_ACTIVITY_RESULT, item1);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case CHOICE_TYPE_MULTIPLE:
                imContactAdapter.toggleSelected(position);
                break;
            default:
                GroupContactBean item = imContactAdapter.getItem(imContactAdapter.getRealPos(position));
                if (item == null) return;
                showContactDialogFragment(item.accid, StringUtils.equalsIgnoreCase(item.accid, getLoginUserId(), false));
                break;
        }
    }

    /**
     * 展示联系人对话框
     *
     * @param accid
     * @param hiddenChatBtn
     */
    public void showContactDialogFragment(String accid, boolean hiddenChatBtn) {
        String tag = "ContactDialogFragment";
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(accid, "成员资料", hiddenChatBtn)
                .show(mFragTransaction, tag);
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


    @Override
    protected void onDestroy() {
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
        super.onDestroy();
    }
}
