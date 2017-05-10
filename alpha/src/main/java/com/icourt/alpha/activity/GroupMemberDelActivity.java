package com.icourt.alpha.activity;

import android.app.Activity;
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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupMemberActionAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.api.RequestUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  讨论组成员删除
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/2
 * version 1.0.0
 */
public class GroupMemberDelActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String KEY_TID = "key_tid";
    private static final String KEY_CONTACTS = "key_contacts";
    private static final String KEY_DEL_FROM_NET = "key_del_from_net";
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    /**
     * 选择删除  返回剩余的成员
     *
     * @param context
     * @param tid                  delFromNet:true 不能为null
     * @param groupContactBeenList
     * @param delFromNet           是否从网络上删除
     * @param reqCode
     */
    public static void launchForResult(@NonNull Activity context,
                                       String tid,
                                       @NonNull ArrayList<GroupContactBean> groupContactBeenList,
                                       boolean delFromNet,
                                       int reqCode) {
        if (context == null) return;
        if (groupContactBeenList == null) return;
        Intent intent = new Intent(context, GroupMemberDelActivity.class);
        intent.putExtra(KEY_TID, tid);
        intent.putExtra(KEY_CONTACTS, groupContactBeenList);
        intent.putExtra(KEY_DEL_FROM_NET, delFromNet);
        context.startActivityForResult(intent, reqCode);
    }

    LinearLayoutManager linearLayoutManager;
    GroupMemberActionAdapter imContactAdapter;
    HeaderFooterAdapter<GroupMemberActionAdapter> headerFooterAdapter;
    ArrayList<GroupContactBean> groupContactBeenList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member_del);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("讨论组成员");
        TextView titleActionTextView = getTitleActionTextView();
        if (titleActionTextView != null) {
            titleActionTextView.setText("删除");
        }
        groupContactBeenList = (ArrayList<GroupContactBean>) getIntent().getSerializableExtra(KEY_CONTACTS);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<>(imContactAdapter = new GroupMemberActionAdapter());
        imContactAdapter.setSelectable(true);
        imContactAdapter.setOnItemClickListener(this);
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_input_text, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        EditText header_input_et = (EditText) headerView.findViewById(R.id.header_input_et);
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

        recyclerView.setAdapter(headerFooterAdapter);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        imContactAdapter.bindData(true, groupContactBeenList);
    }

    /**
     * 名字匹配
     *
     * @param s
     */
    private void serachGroupMember(String s) {
        if (groupContactBeenList != null) {
            List<GroupContactBean> filterList = new ArrayList<>();
            for (GroupContactBean bean : groupContactBeenList) {
                if (bean != null && !TextUtils.isEmpty(bean.name) && bean.name.contains(s)) {
                    filterList.add(bean);
                }
            }
            imContactAdapter.clearSelected();
            imContactAdapter.bindData(true, filterList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                if (getIntent().getBooleanExtra(KEY_DEL_FROM_NET, false)) {
                    deleteMembers();
                } else {
                    groupContactBeenList.removeAll(imContactAdapter.getSelectedData());
                    Intent intent = getIntent();
                    intent.putExtra(KEY_ACTIVITY_RESULT, groupContactBeenList);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void deleteMembers() {
        if (imContactAdapter.getSelectedPositions().isEmpty()) {
            return;
        }
        JsonArray jsonArray = new JsonArray();
        for (GroupContactBean bean : imContactAdapter.getSelectedData()) {
            if (bean != null) {
                jsonArray.add(bean.accid);
            }
        }
        showLoadingDialog(null);
        getChatApi().groupMemberRemoves(getIntent().getStringExtra(KEY_TID), RequestUtils.createJsonBody(jsonArray.toString()))
                .enqueue(new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        Intent intent = getIntent();
                        groupContactBeenList.removeAll(imContactAdapter.getSelectedData());
                        intent.putExtra(KEY_ACTIVITY_RESULT, groupContactBeenList);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });

    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        imContactAdapter.toggleSelected(position);
    }
}
