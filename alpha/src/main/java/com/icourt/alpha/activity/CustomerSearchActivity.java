package com.icourt.alpha.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asange.recyclerviewadapter.BaseRecyclerAdapter;
import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/21
 * version 1.0.0
 */
public class CustomerSearchActivity extends BaseActivity implements OnItemClickListener {

    public static final int SEARCH_LIAISON_TYPE = 0;//搜索联络人
    public static final int SEARCH_CUSTOMER_TYPE = 1;//搜索联系人

    public static final int CUSTOMER_PERSON_TYPE = 1;//个人
    public static final int CUSTOMER_COMPANY_TYPE = 2;//企业

    CustomerAdapter customerAdapter;
    @BindView(R.id.et_search_name)
    ClearEditText etSearchName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.contentEmptyImage)
    ImageView contentEmptyImage;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.search_pb)
    ProgressBar searchPb;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    CustomerDbService customerDbService;


    @IntDef({SEARCH_LIAISON_TYPE,
            SEARCH_CUSTOMER_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SEARCH_ACTION {

    }

    int type, customer_type;

    public static void launch(@NonNull Context context, View searchLayout, @SEARCH_ACTION int type, int customer_type) {
        if (context == null) return;
        Intent intent = new Intent(context, CustomerSearchActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("customer_type", customer_type);
        if (context instanceof Activity
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && searchLayout != null) {
            ViewCompat.setTransitionName(searchLayout, "searchLayout");
            context.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation((Activity) context, searchLayout, "searchLayout").toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    public static void launchResult(@NonNull Activity context, View searchLayout, @SEARCH_ACTION int type, int customer_type, int requestCode) {
        if (context == null) return;
        Intent intent = new Intent(context, CustomerSearchActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("customer_type", customer_type);
        if (context instanceof Activity
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && searchLayout != null) {
            ViewCompat.setTransitionName(searchLayout, "searchLayout");
            context.startActivityForResult(intent, requestCode,
                    ActivityOptions.makeSceneTransitionAnimation(context, searchLayout, "searchLayout").toBundle());
        } else {
            context.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search_reyclerview);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        contentEmptyText.setText(R.string.empty_list_customer_search);
        type = getIntent().getIntExtra("type", -1);
        customer_type = getIntent().getIntExtra("customer_type", -1);
        customerDbService = new CustomerDbService(getLoginUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(customerAdapter = new CustomerAdapter());
        customerAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (emptyLayout != null) {
                    emptyLayout.setVisibility(customerAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
        customerAdapter.setOnItemClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (softKeyboardSizeWatchLayout != null
                                && softKeyboardSizeWatchLayout.isSoftKeyboardPop()) {
                            SystemUtils.hideSoftKeyBoard(getActivity(), etSearchName, true);
                        }
                    }
                    break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    cancelAllCall();
                    customerAdapter.clearData();
                    setViewVisible(emptyLayout, false);
                } else {
                    getData(true);
                }
            }
        });
        etSearchName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), v);
                        if (!TextUtils.isEmpty(v.getText())) {
                            getData(true);
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        try {
            RealmResults<CustomerDbModel> name = customerDbService.contains("name", etSearchName.getText().toString());
            if (name == null) {
                customerAdapter.clearData();
                return;
            }
            List<CustomerEntity> customerEntities = ListConvertor.convertList(new ArrayList<IConvertModel<CustomerEntity>>(name));
            customerAdapter.bindData(true, customerEntities);
        } catch (Throwable e) {
            e.printStackTrace();
            bugSync("加载本地联系人失败", e);
        }
    }


    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                SystemUtils.hideSoftKeyBoard(getActivity(), etSearchName, true);
                finish();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customerDbService != null) {
            customerDbService.releaseService();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        CustomerEntity customerEntity = customerAdapter.getItem(i);
        if (type == SEARCH_CUSTOMER_TYPE) {
            if (!TextUtils.isEmpty(customerEntity.contactType)) {
                //公司
                if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "C")) {
                    CustomerCompanyDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, true);
                } else if (TextUtils.equals(customerEntity.contactType.toUpperCase(), "P")) {
                    CustomerPersonDetailActivity.launch(getContext(), customerEntity.pkid, customerEntity.name, true);
                }
            }
        } else if (type == SEARCH_LIAISON_TYPE) {
            if (customer_type == CUSTOMER_COMPANY_TYPE) {
                SelectLiaisonActivity.launchSetResultFromLiaison(this,
                        Const.SELECT_ENTERPRISE_LIAISONS_TAG_ACTION,
                        customerEntity);
            } else if (customer_type == CUSTOMER_PERSON_TYPE) {
                SelectLiaisonActivity.launchSetResultFromLiaison(this,
                        Const.SELECT_LIAISONS_TAG_ACTION, customerEntity);
            }
            finish();
        }
    }
}
