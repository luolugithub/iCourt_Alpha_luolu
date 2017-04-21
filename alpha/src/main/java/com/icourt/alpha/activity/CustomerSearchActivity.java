package com.icourt.alpha.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.CustomerAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;

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
public class CustomerSearchActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    CustomerAdapter customerAdapter;
    @BindView(R.id.et_contact_name)
    EditText etContactName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    CustomerDbService customerDbService;

    public static void launch(@NonNull Context context, View searchLayout) {
        if (context == null) return;
        Intent intent = new Intent(context, CustomerSearchActivity.class);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        customerDbService = new CustomerDbService(getLoginUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(customerAdapter = new CustomerAdapter());
        customerAdapter.setOnItemClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (softKeyboardSizeWatchLayout != null
                                && softKeyboardSizeWatchLayout.isSoftKeyboardPop()) {
                            SystemUtils.hideSoftKeyBoard(getActivity(), etContactName, true);
                        }
                    }
                    break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        etContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    customerAdapter.clearData();
                } else {
                    getData(true);
                }
            }
        });
        etContactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), etContactName);
                        if (!TextUtils.isEmpty(etContactName.getText())) {
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
            RealmResults<CustomerDbModel> name = customerDbService.contains("name", etContactName.getText().toString());
            if (name == null) {
                customerAdapter.clearData();
                return;
            }
            List<CustomerEntity> customerEntities = ListConvertor.convertList(new ArrayList<IConvertModel<CustomerEntity>>(name));
            customerAdapter.bindData(true, customerEntities);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                SystemUtils.hideSoftKeyBoard(getActivity(), etContactName, true);
                finish();
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
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        customerAdapter.getData(customerAdapter.getRealPos(position));
    }
}
