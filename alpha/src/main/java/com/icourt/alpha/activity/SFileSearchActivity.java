package com.icourt.alpha.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SFileSearchAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.SFileSearchEntity;
import com.icourt.alpha.entity.bean.SFileSearchPage;
import com.icourt.alpha.fragment.dialogfragment.FileDetailDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FolderDetailDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.PERMISSION_R;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/20
 * version 2.1.0
 */
public class SFileSearchActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.et_input_name)
    ClearEditText etInputName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    SFileSearchAdapter sFileSearchAdapter;
    int pageIndex = 1;
    private static final String transitionName = "searchLayout1";
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    public static void launch(Activity context,
                              @Nullable View transitionView) {
        if (context == null) return;
        Intent intent = new Intent(context, SFileSearchActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && transitionView != null) {
            ViewCompat.setTransitionName(transitionView, transitionName);
            context.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(context, transitionView, transitionName).toBundle());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            etInputName.setTransitionName(transitionName);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sFileSearchAdapter = new SFileSearchAdapter());
        sFileSearchAdapter.setOnItemClickListener(this);
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
                    sFileSearchAdapter.clearData();
                } else {
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
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                getData(false);
            }
        });
    }


    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 1;
        }
        getSFileApi().fileSearch(
                pageIndex,
                etInputName.getText().toString(),
                "custom",
                "all")
                .enqueue(new SFileCallBack<SFileSearchPage>() {
                    @Override
                    public void onSuccess(Call<SFileSearchPage> call, Response<SFileSearchPage> response) {
                        sFileSearchAdapter.bindData(isRefresh, response.body().results);
                        pageIndex += 1;
                        stopRefresh();
                        enableLoadMore(response.body().has_more);
                        if (contentEmptyText != null) {
                            contentEmptyText.setVisibility(isRefresh && sFileSearchAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<SFileSearchPage> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    private void enableLoadMore(boolean hasMore) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(hasMore);
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                supportFinishAfterTransition();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        SFileSearchEntity item = sFileSearchAdapter.getItem(position);
        if (item != null) {
            //搜索的内容
            if (item.isSearchContent()) {

            }
            if (item.is_dir) {
                FolderDetailDialogFragment.show(
                        item.repo_id,
                        item.fullpath,
                        item.name,
                        item.size,
                        0,
                        PERMISSION_R,
                        getSupportFragmentManager());
            } else {
                FileDetailDialogFragment.show(
                        item.repo_id,
                        item.fullpath,
                        item.name,
                        item.size,
                        0,
                        PERMISSION_R,
                        getSupportFragmentManager());
            }
        }
    }
}
