package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatMsgClassfyActivity;
import com.icourt.alpha.activity.FileDetailsActivity;
import com.icourt.alpha.adapter.ImUserMessageAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
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
        extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    public static final int TYPE_ALL_FILE = 0;
    public static final int TYPE_MY_FILE = 2;
    private static final String KEY_FILE_TYPE = "key_file_type";


    @IntDef({TYPE_ALL_FILE,
            TYPE_MY_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QueryFileType {

    }

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    Unbinder unbinder;
    //本地同步的联系人
    protected final List<GroupContactBean> localContactList = new ArrayList<>();

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
        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.null_files);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fileAdapter = new ImUserMessageAdapter(localContactList));
        fileAdapter.setOnItemClickListener(this);
        fileAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, fileAdapter));
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
        refreshLayout.startRefresh();
        getLocalContacts();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        long msgid = 0;
        if (isRefresh) {
            msgid = Integer.MAX_VALUE;
        } else {
            if (!fileAdapter.getData().isEmpty()) {
                IMMessageCustomBody item = fileAdapter.getItem(fileAdapter.getData().size() - 1);
                if (item != null) {
                    msgid = item.id;
                }
            }
        }
        Call<ResEntity<List<IMMessageCustomBody>>> call;
        switch (getQueryFileType()) {
            case TYPE_ALL_FILE:
                call = getChatApi().getMyAllFiles(msgid);
                break;
            case TYPE_MY_FILE:
                call = getChatApi().getMyFiles(msgid);
                break;
            default:
                call = getChatApi().getMyFiles(msgid);
                break;
        }
        call.enqueue(new SimpleCallBack<List<IMMessageCustomBody>>() {
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
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 获取本地联系人
     */
    private void getLocalContacts() {
        queryAllContactFromDbAsync(new Consumer<List<GroupContactBean>>() {
            @Override
            public void accept(List<GroupContactBean> groupContactBeen) throws Exception {
                if (groupContactBeen != null && !groupContactBeen.isEmpty()) {
                    localContactList.clear();
                    localContactList.addAll(groupContactBeen);
                    fileAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 异步查询本地联系人
     */
    protected final void queryAllContactFromDbAsync(@NonNull Consumer<List<GroupContactBean>> consumer) {
        if (consumer == null) return;
        Observable.create(new ObservableOnSubscribe<List<GroupContactBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<GroupContactBean>> e) throws Exception {
                ContactDbService threadContactDbService = null;
                try {
                    if (!e.isDisposed()) {
                        threadContactDbService = new ContactDbService(getLoginUserId());
                        RealmResults<ContactDbModel> contactDbModels = threadContactDbService.queryAll();
                        if (contactDbModels != null) {
                            List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels));
                            e.onNext(contactBeen);
                        }
                        e.onComplete();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (threadContactDbService != null) {
                        threadContactDbService.releaseService();
                    }
                }
            }
        }).compose(this.<List<GroupContactBean>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        IMMessageCustomBody item = fileAdapter.getItem(adapter.getRealPos(position));
        if (item == null) return;
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
