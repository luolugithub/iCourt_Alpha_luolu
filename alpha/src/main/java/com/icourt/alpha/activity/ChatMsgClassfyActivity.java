package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ImUserMessageAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.RefreshViewEmptyObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
public class ChatMsgClassfyActivity extends BaseActivity {
    public static final int MSG_CLASSFY_MY_COLLECTEED = 0;  //我收藏的消息
    public static final int MSG_CLASSFY_CHAT_DING = 1;      //讨论组钉的消息
    private static final String KEY_CLASSFY_TYPE = "KEY_CLASSFY_TYPE";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_CHAT_TYPE = " KEY_CHAT_TYPE";

    @IntDef({
            MSG_CLASSFY_MY_COLLECTEED,
            MSG_CLASSFY_CHAT_DING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MsgClassfyType {

    }

    /**
     * 聊天钉的消息
     *
     * @param context
     * @param chatType 单聊 群聊
     * @param id       单聊为uid 群聊为群id
     */
    public static void launchDing(
            @NonNull Context context,
            @Const.CHAT_TYPE int chatType,
            String id) {
        if (context == null) return;
        Intent intent = new Intent(context, ChatMsgClassfyActivity.class);
        intent.putExtra(KEY_CLASSFY_TYPE, MSG_CLASSFY_CHAT_DING);
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
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    private int pageIndex = 1;


    @MsgClassfyType
    public int getMsgClassfyType() {
        switch (getIntent().getIntExtra(KEY_CLASSFY_TYPE, 0)) {
            case MSG_CLASSFY_MY_COLLECTEED:
                return MSG_CLASSFY_MY_COLLECTEED;
            case MSG_CLASSFY_CHAT_DING:
                return MSG_CLASSFY_CHAT_DING;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ated);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        switch (getMsgClassfyType()) {
            case MSG_CLASSFY_CHAT_DING:
                setTitle("钉的消息");
                refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, "暂无钉的消息");
                break;
            case MSG_CLASSFY_MY_COLLECTEED:
                setTitle("我收藏的消息");
                refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.my_center_null_collect_text);
                break;
            default:
                setTitle("我收藏的消息");
                refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.my_center_null_collect_text);
                break;
        }
        refreshLayout.setMoveForHorizontal(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imUserMessageAdapter = new ImUserMessageAdapter(getUserToken()));
        imUserMessageAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, imUserMessageAdapter));
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
        refreshLayout.setAutoRefresh(true);
        refreshLayout.startRefresh();
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 0;
        }
        Call<ResEntity<List<IMMessageCustomBody>>> call = null;
        switch (getMsgClassfyType()) {
            case MSG_CLASSFY_CHAT_DING:
                call = getApi().getDingMessages(getMsgChatType(), getIntent().getStringExtra(KEY_ID));
                break;
            case MSG_CLASSFY_MY_COLLECTEED:
                call = getApi().getMyCollectedMessages(pageIndex, ActionConstants.DEFAULT_PAGE_SIZE);
                break;
            default:
                call = getApi().getMyCollectedMessages(pageIndex, ActionConstants.DEFAULT_PAGE_SIZE);
                break;
        }
        call.enqueue(new SimpleCallBack<List<IMMessageCustomBody>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                imUserMessageAdapter.bindData(isRefresh, response.body().result);
                stopRefresh();
                pageIndex += 1;
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
}
