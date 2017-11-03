package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asange.recyclerviewadapter.BaseViewHolder;
import com.asange.recyclerviewadapter.OnItemClickListener;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatMsgClassfyActivity;
import com.icourt.alpha.activity.FileDetailsActivity;
import com.icourt.alpha.adapter.ImUserMessageAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class FileListFragment
        extends BaseFragment implements OnItemClickListener {
    public static final int TYPE_ALL_FILE = 0;
    public static final int TYPE_MY_FILE = 2;
    private static final String KEY_FILE_TYPE = "key_file_type";




    @IntDef({TYPE_ALL_FILE,
            TYPE_MY_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QueryFileType {

    }

    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    Unbinder unbinder;

    ImUserMessageAdapter fileAdapter;

    public static FileListFragment newInstance(@QueryFileType int fileType) {
        FileListFragment fileListFragment = new FileListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FILE_TYPE, fileType);
        fileListFragment.setArguments(bundle);
        return fileListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_file_list, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @QueryFileType
    private int getQueryFileType() {
        switch (getArguments().getInt(KEY_FILE_TYPE)) {
            case TYPE_ALL_FILE:
                return TYPE_ALL_FILE;
            case TYPE_MY_FILE:
                return TYPE_MY_FILE;
            default:
                return TYPE_MY_FILE;
        }
    }


    @Override
    protected void initView() {
        recyclerView.setNoticeEmpty(R.mipmap.bg_no_task, R.string.null_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), false));
        recyclerView.setAdapter(fileAdapter = new ImUserMessageAdapter());
        fileAdapter.setOnItemClickListener(this);
        fileAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(recyclerView, fileAdapter));
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);
            }
        });
        refreshLayout.autoRefresh();
    }

    private long getEndlyId() {
        long msg_id = 0;
        if (!fileAdapter.getData().isEmpty()) {
            IMMessageCustomBody imMessageCustomBody = fileAdapter.getData().get(fileAdapter.getData().size() - 1);
            if (imMessageCustomBody != null) {
                msg_id = imMessageCustomBody.id;
            }
        }
        return msg_id;
    }

    @Override
    protected void getData(final boolean isRefresh) {
        Call<ResEntity<List<IMMessageCustomBody>>> call;
        switch (getQueryFileType()) {
            case TYPE_ALL_FILE:
                if (isRefresh) {
                    call = getChatApi().getMyAllFiles();
                } else {
                    call = getChatApi().getMyAllFiles(getEndlyId());
                }
                break;
            case TYPE_MY_FILE:
                if (isRefresh) {
                    call = getChatApi().getMyFiles();
                } else {
                    call = getChatApi().getMyFiles(getEndlyId());
                }
                break;
            default:
                if (isRefresh) {
                    call = getChatApi().getMyFiles();
                } else {
                    call = getChatApi().getMyFiles(getEndlyId());
                }
                break;
        }
        callEnqueue(call,new SimpleCallBack<List<IMMessageCustomBody>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                fileAdapter.bindData(isRefresh, response.body().result);
                stopRefresh();
                enableLoadMore(response.body().result);
            }

            @Override
            public void onFailure(Call<ResEntity<List<IMMessageCustomBody>>> call, Throwable t) {
                super.onFailure(call, t);
                stopRefresh();
            }
        });
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setEnableLoadmore(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }



    @Override
    public void onItemClick(com.asange.recyclerviewadapter.BaseRecyclerAdapter baseRecyclerAdapter, BaseViewHolder baseViewHolder, View view, int i) {
        IMMessageCustomBody item = fileAdapter.getItem(i);
        if (item == null){ return;}
        FileDetailsActivity.launch(getContext(),
                item,
                ChatMsgClassfyActivity.MSG_CLASSFY_CHAT_FILE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
