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
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.widget.filter.ListFilter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

/**
 * Description 联系人搜索【本地数据库】【屏蔽机器人 本地数据库缓存了机器人】
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ContactSearchActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    public static final String KEY_KEYWORD = "keyWord";
    IMContactAdapter imContactAdapter;
    ContactDbService contactDbService;
    @BindView(R.id.et_input_name)
    EditText etInputName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;

    public static void launch(@NonNull Context context,
                              @Nullable View searchLayout,
                              @Nullable String keyWord) {
        if (context == null) return;
        Intent intent = new Intent(context, ContactSearchActivity.class);
        intent.putExtra(KEY_KEYWORD, keyWord);
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
        setContentView(R.layout.activity_base_search_reyclerview);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        contactDbService = new ContactDbService(getLoginUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(imContactAdapter = new IMContactAdapter());
        imContactAdapter.setOnItemClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (softKeyboardSizeWatchLayout != null
                                && softKeyboardSizeWatchLayout.isSoftKeyboardPop()) {
                            SystemUtils.hideSoftKeyBoard(getActivity(), etInputName, true);
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
        etInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    imContactAdapter.setKeyWord(null);
                    imContactAdapter.clearData();
                } else {
                    imContactAdapter.setKeyWord(s.toString());
                    getData(true);
                }
            }
        });
        etInputName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), etInputName);
                        if (!TextUtils.isEmpty(etInputName.getText())) {
                            getData(true);
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        etInputName.setText(getIntent().getStringExtra(KEY_KEYWORD));
        etInputName.setSelection(etInputName.getText().length());

        imContactAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                contentEmptyText.setVisibility(imContactAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        try {
            RealmResults<ContactDbModel> name = contactDbService.contains("name", etInputName.getText().toString());
            if (name == null) {
                imContactAdapter.clearData();
                return;
            }
            List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(name));
            fiterRobots(contactBeen);
            filterMySelf(contactBeen);
            imContactAdapter.bindData(true, contactBeen);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 过滤掉机器人
     *
     * @param contactBeen
     */
    private List<GroupContactBean> fiterRobots(List<GroupContactBean> contactBeen) {
        return new ListFilter<GroupContactBean>().filter(contactBeen, GroupContactBean.TYPE_ROBOT);
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

    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                SystemUtils.hideSoftKeyBoard(getActivity(), etInputName, true);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        GroupContactBean data = imContactAdapter.getData(adapter.getRealPos(position));
        if (data == null) return;
        ContactDetailActivity.launch(getContext(), data.accid, false, false);
    }
}
