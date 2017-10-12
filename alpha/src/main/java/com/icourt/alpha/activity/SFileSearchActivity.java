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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SFileSearchAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.SFileSearchEntity;
import com.icourt.alpha.entity.bean.SFileSearchPage;
import com.icourt.alpha.fragment.dialogfragment.FileDetailDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/20
 * version 2.1.0
 */
public class SFileSearchActivity extends BaseActivity
        implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemChildClickListener {

    private static final String transitionName = "searchLayout1";
    SFileSearchAdapter sFileSearchAdapter;
    int pageIndex = 1;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.et_search_name)
    ClearEditText etSearchName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;

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
            etSearchName.setTransitionName(transitionName);
        }
        etSearchName.setHint(R.string.sfile_search_range);
        contentEmptyText.setText(R.string.sfile_searched_no_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sFileSearchAdapter = new SFileSearchAdapter());
        sFileSearchAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                refreshLayout.setEnableRefresh(sFileSearchAdapter.getItemCount() > 0);
                contentEmptyText.setVisibility(sFileSearchAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
            }
        });
        sFileSearchAdapter.setOnItemClickListener(this);
        sFileSearchAdapter.setOnItemChildClickListener(this);
        recyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    sFileSearchAdapter.clearData();
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
                        SystemUtils.hideSoftKeyBoard(getActivity(), etSearchName);
                        if (!TextUtils.isEmpty(etSearchName.getText())) {
                            getData(true);
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(com.scwang.smartrefresh.layout.api.RefreshLayout refreshlayout) {
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
        if (TextUtils.isEmpty(etSearchName.getText())) {
            sFileSearchAdapter.clearData();
            stopRefresh();
            return;
        }
        callEnqueue(
                getSFileApi().fileSearch(
                        pageIndex,
                        etSearchName.getText().toString(),
                        "custom",
                        "all"),
                new SFileCallBack<SFileSearchPage>() {
                    @Override
                    public void onSuccess(Call<SFileSearchPage> call, Response<SFileSearchPage> response) {
                        sFileSearchAdapter.bindData(isRefresh, response.body().results);
                        pageIndex += 1;
                        stopRefresh();
                        enableLoadMore(response.body().has_more);
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
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    private void enableLoadMore(boolean hasMore) {
        if (refreshLayout != null) {
            refreshLayout.setEnableLoadmore(hasMore);
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
            if (item.is_dir) {
                showTopSnackBar(R.string.sfile_searched_folder_un_click);
                return;
            }
            if (IMUtils.isPIC(item.name)) {
                ArrayList<SFileSearchEntity> imageDatas = new ArrayList<>();
                for (int i = 0; i < sFileSearchAdapter.getItemCount(); i++) {
                    SFileSearchEntity folderDocumentEntity = sFileSearchAdapter.getItem(i);
                    if (folderDocumentEntity == null) continue;
                    if (IMUtils.isPIC(folderDocumentEntity.name)) {
                        imageDatas.add(folderDocumentEntity);
                    }
                }
                int indexOf = imageDatas.indexOf(item);
                ImageViewerActivity.launch(
                        getContext(),
                        SFileConfig.FILE_FROM_REPO,
                        imageDatas,
                        indexOf);
            } else {
                FileDownloadActivity.launch(
                        getContext(),
                        item,
                        SFileConfig.FILE_FROM_REPO);
            }
        }
    }


    private String getDirPath(String fullPath) {
        if (!TextUtils.isEmpty(fullPath)) {
            int indexOf = fullPath.lastIndexOf("/");
            if (indexOf >= 0) {
                return fullPath.substring(0, indexOf + 1);
            }
        }
        return fullPath;
    }

    /**
     * 查看详情
     *
     * @param item
     */
    private void lookDetail(SFileSearchEntity item) {
        if (item == null) return;
        if (item.is_dir) {
            showTopSnackBar(R.string.sfile_searched_folder_un_click);
        } else {
            FileDetailDialogFragment.show(
                    SFileConfig.REPO_UNKNOW,
                    item,
                    0,
                    getSupportFragmentManager());
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        SFileSearchEntity item = sFileSearchAdapter.getItem(position);
        if (item != null) {
            switch (view.getId()) {
                case R.id.document_detail_iv:
                    lookDetail(item);
                    break;
            }
        }
    }
}
