package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ImUserMessageAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.event.MessageEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.zhaol.refreshlayout.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description 消息分类检索:【我收藏的消息】【讨论组钉的消息】
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/19
 * version 1.0.0
 */
public class ChatMsgClassfyActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    public static final int MSG_CLASSFY_MY_COLLECTEED = 0;  //我收藏的消息
    public static final int MSG_CLASSFY_CHAT_DING = 1;      //讨论组钉的消息
    public static final int MSG_CLASSFY_CHAT_FILE = 2;      //讨论组的文件消息

    private static final String KEY_CLASSFY_TYPE = "KEY_CLASSFY_TYPE";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_CHAT_TYPE = " KEY_CHAT_TYPE";


    @IntDef({
            MSG_CLASSFY_MY_COLLECTEED,
            MSG_CLASSFY_CHAT_DING,
            MSG_CLASSFY_CHAT_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgClassfyType {

    }

    //本地同步的联系人
    protected final List<GroupContactBean> localContactList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ated);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 聊天钉的消息
     *
     * @param context
     * @param chatType 单聊 群聊
     * @param id       单聊为uid 群聊为群id
     */
    public static void launch(
            @NonNull Context context,
            @MsgClassfyType int msgClassfyType,
            @Const.CHAT_TYPE int chatType,
            String id) {
        if (context == null) return;
        Intent intent = new Intent(context, ChatMsgClassfyActivity.class);
        intent.putExtra(KEY_CLASSFY_TYPE, msgClassfyType);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_CHAT_TYPE, chatType);
        context.startActivity(intent);
    }

    /**
     * 我收藏的消息
     *
     * @param context
     */
    public static void launchMyCollected(
            @NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, ChatMsgClassfyActivity.class);
        intent.putExtra(KEY_CLASSFY_TYPE, MSG_CLASSFY_MY_COLLECTEED);
        context.startActivity(intent);
    }


    ImUserMessageAdapter imUserMessageAdapter;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    @MsgClassfyType
    public int getMsgClassfyType() {
        switch (getIntent().getIntExtra(KEY_CLASSFY_TYPE, 0)) {
            case MSG_CLASSFY_MY_COLLECTEED:
                return MSG_CLASSFY_MY_COLLECTEED;
            case MSG_CLASSFY_CHAT_DING:
                return MSG_CLASSFY_CHAT_DING;
            case MSG_CLASSFY_CHAT_FILE:
                return MSG_CLASSFY_CHAT_FILE;
            default:
                return MSG_CLASSFY_MY_COLLECTEED;
        }
    }


    @Const.CHAT_TYPE
    public int getMsgChatType() {
        switch (getIntent().getIntExtra(KEY_CHAT_TYPE, 0)) {
            case CHAT_TYPE_P2P:
                return CHAT_TYPE_P2P;
            case CHAT_TYPE_TEAM:
                return Const.CHAT_TYPE_TEAM;
            default:
                return CHAT_TYPE_P2P;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        switch (getMsgClassfyType()) {
            case MSG_CLASSFY_CHAT_DING:
                setTitle("钉的消息");
                recyclerView.setNoticeEmpty(R.mipmap.ic_empty_data, R.string.empty_list_im_ding_msg);
                break;
            case MSG_CLASSFY_CHAT_FILE:
                setTitle("文件");
                recyclerView.setNoticeEmpty(R.mipmap.ic_empty_data, R.string.empty_list_im_file_msg);
                break;
            case MSG_CLASSFY_MY_COLLECTEED:
                setTitle("我收藏的消息");
                recyclerView.setNoticeEmpty(R.mipmap.ic_empty_data, R.string.empty_list_im_collected_msg);
                break;
            default:
                setTitle("我收藏的消息");
                recyclerView.setNoticeEmpty(R.mipmap.ic_empty_data, R.string.empty_list_im_collected_msg);
                break;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFull10Divider(getContext(), false));
        recyclerView.setAdapter(imUserMessageAdapter = new ImUserMessageAdapter(localContactList));
        imUserMessageAdapter.setOnItemClickListener(this);
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
        getLocalContacts();
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
        switch (getMsgClassfyType()) {
            case MSG_CLASSFY_CHAT_DING:
                if (isRefresh) {
                    call = getChatApi()
                            .getDingMessages(getMsgChatType(),
                                    getIntent().getStringExtra(KEY_ID));
                } else {
                    call = getChatApi()
                            .getDingMessages(getMsgChatType(),
                                    getIntent().getStringExtra(KEY_ID),
                                    getEndlyId());
                }
                break;
            case MSG_CLASSFY_CHAT_FILE:
                if (isRefresh) {
                    call = getChatApi()
                            .msgQueryFiles(getMsgChatType(),
                                    getIntent().getStringExtra(KEY_ID));
                } else {
                    call = getChatApi()
                            .msgQueryFiles(getMsgChatType(),
                                    getIntent().getStringExtra(KEY_ID),
                                    getEndlyId());
                }
                break;
            case MSG_CLASSFY_MY_COLLECTEED:
                if (isRefresh) {
                    call = getChatApi()
                            .getMyCollectedMessages();
                } else {
                    call = getChatApi()
                            .getMyCollectedMessages(getEndlyId());
                }
                break;
            default:
                if (isRefresh) {
                    call = getChatApi()
                            .getMyCollectedMessages();
                } else {
                    call = getChatApi()
                            .getMyCollectedMessages(getEndlyId());
                }
                break;
        }
        callEnqueue(call, new SimpleCallBack<List<IMMessageCustomBody>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event == null) {
            return;
        }
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
        if (item == null) {
            return;
        }
        FileDetailsActivity.launch(getContext(), item, getMsgClassfyType());
    }

}
