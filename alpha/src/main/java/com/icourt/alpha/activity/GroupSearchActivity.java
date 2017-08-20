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
import com.icourt.alpha.adapter.GroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.activity.GroupListActivity.GROUP_TYPE_MY_JOIN;
import static com.icourt.alpha.activity.GroupListActivity.GROUP_TYPE_TYPE_ALL;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/22
 * version 1.0.0
 */
public class GroupSearchActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String KEY_GROUP_QUERY_TYPE = "GroupQueryType";
    public static final String KEY_KEYWORD = "keyWord";
    GroupAdapter groupAdapter;

    @BindView(R.id.et_input_name)
    EditText etInputName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    public static void launch(@NonNull Context context,
                              View searchLayout,
                              @GroupListActivity.GroupQueryType int type,
                              @Nullable String keyWord) {
        if (context == null) return;
        Intent intent = new Intent(context, GroupSearchActivity.class);
        intent.putExtra(KEY_GROUP_QUERY_TYPE, type);
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

    @GroupListActivity.GroupQueryType
    private int getGroupQueryType() {
        switch (getIntent().getIntExtra(KEY_GROUP_QUERY_TYPE, 0)) {
            case 0:
                return GROUP_TYPE_MY_JOIN;
            case 1:
                return GROUP_TYPE_TYPE_ALL;
        }
        return GROUP_TYPE_TYPE_ALL;
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
        refreshLayout.setPullRefreshEnable(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupAdapter = new GroupAdapter());
        groupAdapter.setOnItemClickListener(this);
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
                    groupAdapter.setKeyWord(null);
                    groupAdapter.clearData();
                } else {
                    groupAdapter.setKeyWord(s.toString());
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


        groupAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                contentEmptyText.setVisibility(groupAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        String keyWord = etInputName.getText().toString();
        getChatApi().groupQueryByName(keyWord)
                .enqueue(new SimpleCallBack<List<GroupEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupEntity>>> call, Response<ResEntity<List<GroupEntity>>> response) {
                        groupAdapter.bindData(true, response.body().result);
                    }
                });
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

    /**
     * 是否是我加入的群组
     *
     * @param tid
     * @return
     */
    private boolean isMyJionedGroup(String tid) {
        try {
            Team team = NIMClient.getService(TeamService.class)
                    .queryTeamBlock(tid);
            return team != null && team.isMyTeam();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        GroupEntity item = groupAdapter.getItem(position);
        if (item == null) return;
        if (isMyJionedGroup(item.tid)) {
            ChatActivity.launchTEAM(getContext(),
                    item.tid,
                    item.name,
                    0, 0);
        } else {
            GroupDetailActivity.launchTEAM(getContext(), item.tid);
        }
    }
}
