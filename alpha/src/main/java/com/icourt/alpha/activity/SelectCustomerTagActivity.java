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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description  选择客户信息标签
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/18
 * version 2.0.0
 */

public class SelectCustomerTagActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    public static final String SP_CUSTOMER_TAG = "sp_customer_tag";

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.activity_select_contact_tag_fixation_recyclerview)
    RecyclerView activitySelectContactTagFixationRecyclerview;
    @BindView(R.id.activity_select_custom_contact_tag_view)
    EditText activitySelectCustomContactTagView;
    @BindView(R.id.activity_select_contact_tag_custom_recyclerview)
    RecyclerView activitySelectContactTagCustomRecyclerview;
    private String action, tagname;
    private int position;
    private List<String> fixationTagList, customTagList;
    private CustomerTagListAdapter contactTagListAdapter, customTagListAdapter;
    private String custTagStr;
    private StringBuffer buffer = new StringBuffer();

    public static void launchForResult(@NonNull Activity context, @NonNull String action, int position, @NonNull String tagname, int requestCode) {
        if (context == null) return;
        Intent intent = new Intent(context, SelectCustomerTagActivity.class);
        intent.setAction(action);
        intent.putExtra("position", position);
        intent.putExtra("tagname", tagname);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact_tag_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        action = getIntent().getAction();
        position = getIntent().getIntExtra("position", -1);
        tagname = getIntent().getStringExtra("tagname");
        custTagStr = SpUtils.getInstance().getStringData(SP_CUSTOMER_TAG, "");

        activitySelectContactTagFixationRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        activitySelectContactTagCustomRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        activitySelectContactTagFixationRecyclerview.setAdapter(contactTagListAdapter = new CustomerTagListAdapter(true));
        activitySelectContactTagCustomRecyclerview.setAdapter(customTagListAdapter = new CustomerTagListAdapter(true));

        if (action.equals(Const.SELECT_PHONE_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_phone_tag_array));
            setTitle("电话");
        } else if (action.equals(Const.SELECT_EMAIL_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_email_tag_array));
            setTitle("邮箱");
        } else if (action.equals(Const.SELECT_ADDRESS_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_address_tag_array));
            setTitle("地址");
        } else if (action.equals(Const.SELECT_PAPERS_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_papers_tag_array));
            setTitle("个人证件");
        } else if (action.equals(Const.SELECT_IM_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_im_account_tag_array));
            setTitle("即时通讯帐号");
        } else if (action.equals(Const.SELECT_DATE_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_date_tag_array));
            setTitle("重要日期");
        } else if (action.equals(Const.SELECT_ENTERPRISE_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.contact_enterprise_tag_array));
            setTitle("工作单位");
        } else if (action.equals(Const.SELECT_ENTERPRISE_ADDRESS_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.enterprise_contact_address_tag_array));
            setTitle("机构地址");
        } else if (action.equals(Const.SELECT_ENTERPRISE_EMAIL_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.enterprise_contact_email_tag_array));
            setTitle("机构邮箱");
        } else if (action.equals(Const.SELECT_ENTERPRISE_DATE_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.enterprise_contact_date_tag_array));
            setTitle("重要日期");
        } else if (action.equals(Const.SELECT_ENTERPRISE_PARPER_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.enterprise_contact_parper_tag_array));
            setTitle("机构证件");
        } else if (action.equals(Const.SELECT_RELATION_TAG_ACTION)) {
            fixationTagList = Arrays.asList(getResources().getStringArray(R.array.enterprise_contact_liaison_tag_array));
            setTitle("标签");
        }
        contactTagListAdapter.bindData(true, fixationTagList);
        contactTagListAdapter.setOnItemClickListener(this);
        customTagListAdapter.setOnItemClickListener(this);
        for (int i = 0; i < fixationTagList.size(); i++) {
            if (TextUtils.equals(fixationTagList.get(i), tagname)) {
                contactTagListAdapter.setSelectedPos(i);
                break;
            }
        }
        getTagList();
        activitySelectCustomContactTagView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“DONE”键*/
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setDataToAdapter(activitySelectCustomContactTagView.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
    }

    private void setDataToAdapter(String tag) {
        customTagList.add(tag);
        buffer.append(tag + ",");
        SpUtils.getInstance().putData(SP_CUSTOMER_TAG, buffer.toString());
        customTagListAdapter.addItem(tag);
        activitySelectCustomContactTagView.setText("");
        SystemUtils.hideSoftKeyBoard(this);
    }

    private void getTagList() {
        customTagList = new ArrayList<>();
        if (!TextUtils.isEmpty(custTagStr)) {
            buffer.append(custTagStr);
            String[] tagV = custTagStr.substring(0, custTagStr.length() - 1).split(",");
            customTagList.addAll(Arrays.asList(tagV));
            customTagListAdapter.bindData(true, customTagList);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int p) {
        String tagname = (String) adapter.getItem(p);
        CustomerPersonCreateActivity.launchSetResultFromTag(this, action, position, tagname);
        this.finish();
    }
}
