package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.IndexUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.comparators.PinyinComparator;
import com.icourt.alpha.widget.filter.ListFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/19
 * version 1.0.0
 */
public class ContactSelectDialogFragment extends BaseDialogFragment {

    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    Unbinder unbinder;
    IMContactAdapter imContactAdapter;
    HeaderFooterAdapter<IMContactAdapter> headerFooterAdapter;
    @BindView(R.id.header_comm_search_input_et)
    EditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.share_permission_rw_rb)
    RadioButton sharePermissionRwRb;
    @BindView(R.id.title_share_permission)
    LinearLayout titleSharePermission;
    @BindView(R.id.share_permission_r_rb)
    RadioButton sharePermissionRRb;

    public static ContactSelectDialogFragment newInstance(
            @Nullable ArrayList<GroupContactBean> selectedList,
            boolean isSelectPermission) {
        ContactSelectDialogFragment contactSelectDialogFragment = new ContactSelectDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", selectedList);
        args.putSerializable("isSelectPermission", isSelectPermission);
        contactSelectDialogFragment.setArguments(args);
        return contactSelectDialogFragment;
    }

    public static ContactSelectDialogFragment newInstance(
            @Nullable ArrayList<GroupContactBean> selectedList) {
        return newInstance(selectedList, false);
    }

    OnFragmentCallBackListener onFragmentCallBackListener;
    final List<GroupContactBean> currSelectedList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_contact_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private ArrayList<GroupContactBean> getSelectedData() {
        return (ArrayList<GroupContactBean>) getArguments().getSerializable("data");
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<>(imContactAdapter = new IMContactAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);
        imContactAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout == null) return;
                emptyLayout.setVisibility(imContactAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
            }
        });
        imContactAdapter.setSelectable(true);
        imContactAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                imContactAdapter.toggleSelected(position);
                GroupContactBean item = imContactAdapter.getItem(adapter.getRealPos(position));
                if (item == null) return;
                if (imContactAdapter.isSelected(adapter.getRealPos(position))) {
                    if (!currSelectedList.contains(item)) {
                        currSelectedList.add(item);
                    }
                } else {
                    currSelectedList.remove(item);
                }
                int selectedSize = currSelectedList.size();
                titleContent.setText(selectedSize > 0 ? String.format("选择成员(%s)", selectedSize) : "选择成员");
            }
        });
        headerCommSearchInputEt.addTextChangedListener(new TextWatcher() {
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
                    searchUserByName(s.toString());
                }
            }
        });
        headerCommSearchInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                        if (!TextUtils.isEmpty(headerCommSearchInputEt.getText())) {
                            searchUserByName(headerCommSearchInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        headerCommSearchInputLl.setVisibility(View.GONE);
        titleSharePermission.setVisibility(getArguments().getBoolean("isSelectPermission", false) ? View.VISIBLE : View.GONE);
        getData(true);
    }

    /**
     * 按名称搜索
     *
     * @param name
     */
    private void searchUserByName(final String name) {
        if (TextUtils.isEmpty(name)) return;
        try {
            ContactDbService contactDbService = new ContactDbService(getLoginUserId());
            RealmResults<ContactDbModel> result = contactDbService.contains("name", name);
            if (result != null) {
                List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(result));
                filterRobot(contactBeen);
                filterMySelf(contactBeen);
                ArrayList<GroupContactBean> selectedData = getSelectedData();
                if (selectedData != null) {
                    contactBeen.removeAll(selectedData);
                }
                try {
                    if (contactBeen != null) {
                        IndexUtils.setSuspensions(getContext(), contactBeen);
                        Collections.sort(contactBeen, new PinyinComparator<GroupContactBean>());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    bugSync("排序异常", e);
                }

                imContactAdapter.clearSelected();
                imContactAdapter.bindData(true, contactBeen);
                setLastSelected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLastSelected() {
        imContactAdapter.clearSelected();
        List<GroupContactBean> contactBeen = imContactAdapter.getData();
        //设置上次选中的
        for (int i = 0; i < contactBeen.size(); i++) {
            GroupContactBean groupContactBean = contactBeen.get(i);
            if (currSelectedList.contains(groupContactBean)) {
                imContactAdapter.setSelected(i, true);
            }
        }
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

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        Observable.create(new ObservableOnSubscribe<ArrayList<GroupContactBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<GroupContactBean>> e) throws Exception {
                if (e.isDisposed()) return;
                ArrayList<GroupContactBean> contactBeen = new ArrayList<GroupContactBean>();
                try {
                    ContactDbService contactDbService = new ContactDbService(getLoginUserId());
                    RealmResults<ContactDbModel> contactDbModels = contactDbService.queryAll();
                    if (contactDbModels != null) {
                        contactBeen.addAll(ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels)));
                        ArrayList<GroupContactBean> selectedData = getSelectedData();
                        if (selectedData != null) {
                            contactBeen.removeAll(selectedData);
                        }
                        filterRobot(contactBeen);
                        if (contactBeen != null) {
                            IndexUtils.setSuspensions(getContext(), contactBeen);
                            try {
                                Collections.sort(contactBeen, new PinyinComparator<GroupContactBean>());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                bugSync("排序异常", ex);
                            }
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
                e.onNext(contactBeen);
                e.onComplete();
            }
        }).compose(this.<ArrayList<GroupContactBean>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<GroupContactBean>>() {
                    @Override
                    public void accept(ArrayList<GroupContactBean> groupContactBeen) throws Exception {
                        imContactAdapter.bindData(true, groupContactBeen);
                        setLastSelected();
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

    @OnClick({R.id.bt_cancel,
            R.id.bt_ok,
            R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle params = new Bundle();
                    params.putString("permission", sharePermissionRwRb.isChecked() ? "rw" : "r");
                    params.putSerializable(KEY_FRAGMENT_RESULT, imContactAdapter.getSelectedData());
                    onFragmentCallBackListener.onFragmentCallBack(this, 0, params);
                }
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
