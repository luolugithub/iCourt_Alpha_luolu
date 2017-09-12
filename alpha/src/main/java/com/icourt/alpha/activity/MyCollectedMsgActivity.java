package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ImUserMessageAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.FilterDropEntity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.event.MessageEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.popupwindow.TopMiddlePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
 * Description 消息分类检索:【我收藏的消息】【讨论组钉的消息】
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class MyCollectedMsgActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, TopMiddlePopup.OnItemClickListener {

    ImUserMessageAdapter imUserMessageAdapter;
    TopMiddlePopup topMiddlePopup;
    int select_position = 0;

    //本地同步的联系人
    protected final List<GroupContactBean> localContactList = new ArrayList<>();
    private final List<FilterDropEntity> dropEntities = Arrays.asList(
            new FilterDropEntity("所有", "0", 10),//待定
            new FilterDropEntity("消息", "0", Const.MSG_TYPE_TXT),
            new FilterDropEntity("图片", "0", Const.MSG_TYPE_IMAGE),
            new FilterDropEntity("文档", "0", Const.MSG_TYPE_FILE),
            new FilterDropEntity("网页", "0", Const.MSG_TYPE_LINK)
    );
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collected);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 我收藏的消息
     *
     * @param context
     */
    public static void launchMyCollected(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MyCollectedMsgActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        topMiddlePopup = new TopMiddlePopup(this, DensityUtil.getWidthInDp(getContext()), (int) (DensityUtil.getHeightInPx(getContext()) - DensityUtil.dip2px(getContext(), 75)), this);
        topMiddlePopup.setMyItems(dropEntities);
        setTitle("所有收藏");
        refreshLayout.setNoticeEmpty(R.mipmap.bg_no_task, R.string.my_center_null_collect_text);
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull10Divider(getContext(), false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imUserMessageAdapter = new ImUserMessageAdapter(localContactList));
        imUserMessageAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, imUserMessageAdapter));
        imUserMessageAdapter.setOnItemClickListener(this);
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
        topMiddlePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                titleContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.task_dropdown, 0);
            }
        });
    }

    @OnClick({R.id.titleBack,
            R.id.titleContent})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                finish();
                break;
            case R.id.titleContent:
                if (topMiddlePopup.isShowing()) {
                    topMiddlePopup.dismiss();
                } else {
                    topMiddlePopup.show(titleView, dropEntities, select_position);
                    titleContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.task_dropup, 0);
                    if (topMiddlePopup.isShowing()) {
                        // TODO: 17/9/12  获取各种消息数量
                    }
                }
                break;
        }
        super.onClick(v);
    }

    private long getEndlyId() {
        long msg_id = imUserMessageAdapter.getData().size() > 0
                ? imUserMessageAdapter.getItemId(imUserMessageAdapter.getData().size() - 1) : 0;
        return msg_id;
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        Call<ResEntity<List<IMMessageCustomBody>>> call = null;
        if (isRefresh) {
            call = getChatApi()
                    .getMyCollectedMessages();
        } else {
            call = getChatApi()
                    .getMyCollectedMessages(getEndlyId());
        }
        call.enqueue(new SimpleCallBack<List<IMMessageCustomBody>>() {
            @Override
            public void onSuccess
                    (Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                imUserMessageAdapter.bindData(isRefresh, response.body().result);
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
                    imUserMessageAdapter.notifyDataSetChanged();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event == null) return;
        switch (event.action) {
            case MessageEvent.ACTION_MSG_CANCEL_COLLECT:
                List<IMMessageCustomBody> data = imUserMessageAdapter.getData();
                IMMessageCustomBody targetBody = new IMMessageCustomBody();
                targetBody.id = event.msgId;
                if (data.contains(targetBody)) {
                    imUserMessageAdapter.removeItem(targetBody);
                }
                break;
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        IMMessageCustomBody item = imUserMessageAdapter.getItem(adapter.getRealPos(position));
        if (item == null) return;
        if (item.show_type == Const.MSG_TYPE_LINK) {
            if (item.ext == null) return;
            WebViewActivity.launch(this, item.ext.url);
        } else {
            FileDetailsActivity.launch(getContext(), item, ChatMsgClassfyActivity.MSG_CLASSFY_MY_COLLECTEED);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(TopMiddlePopup topMiddlePopup, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        topMiddlePopup.dismiss();
        if (select_position != position) {
            FilterDropEntity filterDropEntity = (FilterDropEntity) adapter.getItem(position);
            select_position = position;
            setTitle(filterDropEntity.name);
            topMiddlePopup.getAdapter().setSelectedPos(select_position);
            // TODO: 17/9/12 获取各种状态列表 
        }
    }
}
