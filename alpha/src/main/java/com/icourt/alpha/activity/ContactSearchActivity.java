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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.IMContactAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

/**
 * Description 联系人搜索
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ContactSearchActivity extends BaseActivity {
    IMContactAdapter imContactAdapter;
    @BindView(R.id.et_contact_name)
    EditText etContactName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static void launch(@NonNull Context context, View searchLayout) {
        if (context == null) return;
        Intent intent = new Intent(context, ContactSearchActivity.class);
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
        setContentView(R.layout.activity_contact_search);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(imContactAdapter = new IMContactAdapter());
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
                    imContactAdapter.clearData();
                } else {
                    getData(true);
                }
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        log("------------>user:" + loginUserInfo);
        ContactDbService contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        RealmResults<ContactDbModel> name = contactDbService.contains("name", etContactName.getText().toString());
        if (name == null) {
            imContactAdapter.clearData();
            return;
        }
        List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(name));
        imContactAdapter.bindData(true, contactBeen);
        contactDbService.releaseService();
    }

}
