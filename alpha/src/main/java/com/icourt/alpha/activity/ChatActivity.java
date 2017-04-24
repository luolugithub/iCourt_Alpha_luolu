package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAdapter;
import com.icourt.alpha.entity.bean.IMBodyEntity;
import com.icourt.alpha.entity.bean.IMCustomerMessageEntity;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.view.emoji.MySelectPhotoLayout;
import com.icourt.alpha.view.emoji.MyXhsEmoticonsKeyBoard;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.sj.emoji.DefEmoticons;
import com.sj.emoji.EmojiBean;
import com.sj.emoji.EmojiDisplay;
import com.sj.emoji.EmojiSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import sj.keyboard.adpater.EmoticonsAdapter;
import sj.keyboard.adpater.PageSetAdapter;
import sj.keyboard.data.EmoticonPageEntity;
import sj.keyboard.data.EmoticonPageSetEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.interfaces.EmoticonDisplayListener;
import sj.keyboard.interfaces.EmoticonFilter;
import sj.keyboard.interfaces.PageViewInstantiateListener;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.EmoticonPageView;

import static com.netease.nimlib.sdk.msg.model.QueryDirectionEnum.QUERY_OLD;

/**
 * Description  聊天[单聊 群聊]
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class ChatActivity extends ChatBaseActivity {

    private static final String KEY_UID = "key_uid";
    private static final String KEY_TID = "key_tid";
    private static final String KEY_GROUP_ID = "key_group_id";

    private static final String KEY_TITLE = "key_title";

    private static final String KEY_CHAT_TYPE = "key_chat_type";

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.ek_bar)
    MyXhsEmoticonsKeyBoard ekBar;

    ChatAdapter chatAdapter;
    LinearLayoutManager linearLayoutManager;
    int pageIndex;
    int pageSize = 20;


    /**
     * 启动 单聊
     *
     * @param context
     * @param uid
     * @param title
     */
    public static void launchP2P(@NonNull Context context, @NonNull String uid, String title) {
        if (context == null) return;
        if (TextUtils.isEmpty(uid)) return;
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_UID, uid);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_CHAT_TYPE, CHAT_TYPE_P2P);
        context.startActivity(intent);
    }

    /**
     * 启动 群聊
     *
     * @param context
     * @param tid
     * @param groupId
     */
    public static void launchTEAM(@NonNull Context context, String tid, String groupId, String title) {
        if (context == null) return;
        if (TextUtils.isEmpty(tid)) return;
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_TID, tid);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_CHAT_TYPE, CHAT_TYPE_TEAM);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
        initEmoticonsKeyBoardBar();
        getData(true);
    }

    private void initEmoticonsKeyBoardBar() {
        // source data
        ArrayList<EmojiBean> emojiArray = new ArrayList<>();
        Collections.addAll(emojiArray, DefEmoticons.getDefEmojiArray());

        // emoticon click
        final EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    int action = KeyEvent.ACTION_DOWN;
                    int code = KeyEvent.KEYCODE_DEL;
                    KeyEvent event = new KeyEvent(action, code);
                    ekBar.getEtChat().onKeyDown(KeyEvent.KEYCODE_DEL, event);
                } else {
                    if (o == null) {
                        return;
                    }
                    String content = null;
                    if (o instanceof EmojiBean) {
                        content = ((EmojiBean) o).emoji;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        };

        // emoticon instantiate
        final EmoticonDisplayListener emoticonDisplayListener = new EmoticonDisplayListener() {
            @Override
            public void onBindView(int i, ViewGroup viewGroup, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
                final EmojiBean emojiBean = (EmojiBean) object;
                if (emojiBean == null && !isDelBtn) {
                    return;
                }

                viewHolder.ly_root.setBackgroundResource(com.keyboard.view.R.drawable.bg_emoticon);

                if (isDelBtn) {
                    viewHolder.iv_emoticon.setImageResource(R.mipmap.icon_del);
                } else {
                    viewHolder.iv_emoticon.setImageResource(emojiBean.icon);
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emoticonClickListener.onEmoticonClick(emojiBean, 0, isDelBtn);
                    }
                });
            }
        };

        //  page instantiate
        PageViewInstantiateListener pageViewInstantiateListener = new PageViewInstantiateListener<EmoticonPageEntity>() {
            @Override
            public View instantiateItem(ViewGroup viewGroup, int i, EmoticonPageEntity pageEntity) {
                if (pageEntity.getRootView() == null) {
                    EmoticonPageView pageView = new EmoticonPageView(viewGroup.getContext());
                    pageView.setNumColumns(pageEntity.getRow());
                    pageEntity.setRootView(pageView);
                    try {
                        EmoticonsAdapter adapter = new EmoticonsAdapter(viewGroup.getContext(), pageEntity, null);
                        // emoticon instantiate
                        adapter.setOnDisPlayListener(emoticonDisplayListener);
                        pageView.getEmoticonsGridView().setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return pageEntity.getRootView();
            }
        };

        // build
        EmoticonPageSetEntity xhsPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(3)
                .setRow(7)
                .setEmoticonList(emojiArray)
                .setIPageViewInstantiateItem(pageViewInstantiateListener)
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .setIconUri(R.mipmap.ic_launcher)
                .build();

        PageSetAdapter pageSetAdapter = new PageSetAdapter();
        pageSetAdapter.add(xhsPageSetEntity);
        ekBar.setAdapter(pageSetAdapter);
        ekBar.addFuncView(new MySelectPhotoLayout(getContext()));


        class EmojiFilter extends EmoticonFilter {

            private int emojiSize = -1;

            @Override
            public void filter(EditText editText, CharSequence text, int start, int lengthBefore, int lengthAfter) {
                emojiSize = emojiSize == -1 ? EmoticonsKeyboardUtils.getFontHeight(editText) : emojiSize;
                clearSpan(editText.getText(), start, text.toString().length());
                Matcher m = EmojiDisplay.getMatcher(text.toString().substring(start, text.toString().length()));
                if (m != null) {
                    while (m.find()) {
                        String emojiHex = Integer.toHexString(Character.codePointAt(m.group(), 0));
                        Drawable drawable = getDrawable(editText.getContext(), EmojiDisplay.HEAD_NAME + emojiHex);
                        if (drawable != null) {
                            int itemHeight;
                            int itemWidth;
                            if (emojiSize == EmojiDisplay.WRAP_DRAWABLE) {
                                itemHeight = drawable.getIntrinsicHeight();
                                itemWidth = drawable.getIntrinsicWidth();
                            } else {
                                itemHeight = emojiSize;
                                itemWidth = emojiSize;
                            }

                            drawable.setBounds(0, 0, itemHeight, itemWidth);
                            EmojiSpan imageSpan = new EmojiSpan(drawable);
                            editText.getText().setSpan(imageSpan, start + m.start(), start + m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }

            private void clearSpan(Spannable spannable, int start, int end) {
                if (start == end) {
                    return;
                }
                EmojiSpan[] oldSpans = spannable.getSpans(start, end, EmojiSpan.class);
                for (int i = 0; i < oldSpans.length; i++) {
                    spannable.removeSpan(oldSpans[i]);
                }
            }
        }
        // add a filter
        ekBar.getEtChat().addEmoticonFilter(new EmojiFilter());
    }

    private void scrollToBottom() {
    }

    private void OnSendBtnClick(String txt) {

    }


    @Override
    protected void initView() {
        super.initView();
        setTitle(getIntent().getStringExtra(KEY_TITLE));
        ImageView titleActionImage2 = getTitleActionImage2();
        if (titleActionImage2 != null) {
            titleActionImage2.setImageResource(R.mipmap.header_icon_more);
        }
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.icon_mute);
        }

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(chatAdapter = new ChatAdapter(getUserToken()));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
    }


    /**
     * 获取历史消息
     *
     * @param isRefresh 是否刷新
     */
    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(getLastMessage(), QUERY_OLD, pageSize, false)
                .setCallback(new RequestCallback<List<IMMessage>>() {
                    @Override
                    public void onSuccess(List<IMMessage> param) {
                        chatAdapter.addItems(convert2CustomerMessages(param));
                        if (param != null && param.size() > 0) {
                            pageIndex += 1;
                        }
                        stopRefresh();
                    }

                    @Override
                    public void onFailed(int code) {
                        log("--------->load fail:" + code);
                        stopRefresh();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        log("--------->load  exe:" + exception);
                        stopRefresh();
                    }
                });
    }

    /**
     * 获取最后一条消息
     *
     * @return
     */
    private IMMessage getLastMessage() {
        switch (getIntent().getIntExtra(KEY_CHAT_TYPE, 0)) {
            case CHAT_TYPE_P2P:
                if (chatAdapter.getItemCount() > 0) {
                    return chatAdapter.getItem(chatAdapter.getData().size() - 1).imMessage;
                }
                return MessageBuilder.createEmptyMessage(getAccount(), SessionTypeEnum.P2P, 0);
            case CHAT_TYPE_TEAM:
                if (chatAdapter.getItemCount() > 0) {
                    return chatAdapter.getItem(chatAdapter.getData().size() - 1).imMessage;
                }
                return MessageBuilder.createEmptyMessage(getAccount(), SessionTypeEnum.Team, 0);
            default: {
                return null;
            }
        }
    }


    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 转化成自定义的消息体
     *
     * @param param
     * @return
     */
    private List<IMCustomerMessageEntity> convert2CustomerMessages(List<IMMessage> param) {
        List<IMCustomerMessageEntity> customerMessageEntities = new ArrayList<>();
        if (param != null) {
            for (IMMessage message : param) {
                if (message != null) {
                    IMCustomerMessageEntity customerMessageEntity = new IMCustomerMessageEntity();
                    customerMessageEntity.imMessage = message;
                    customerMessageEntity.customIMBody = getIMBody(message);
                    customerMessageEntities.add(customerMessageEntity);
                }
            }
        }
        return customerMessageEntities;
    }

    /**
     * 是否是当前聊天组对话
     *
     * @param sessionId
     * @return
     */
    private boolean isCurrentRoomSession(String sessionId) {
        return TextUtils.equals(sessionId, getAccount());
    }

    /**
     * 获取账号 与云信挂钩
     *
     * @return
     */
    private String getAccount() {
        switch (getIntent().getIntExtra(KEY_CHAT_TYPE, 0)) {
            case CHAT_TYPE_P2P:
                return getIntent().getStringExtra(KEY_UID);
            case CHAT_TYPE_TEAM:
                return getIntent().getStringExtra(KEY_TID);
        }
        return "";
    }

    @Override
    public void onMessageReceived(List<IMMessage> list) {
        log("----------------->onMessageReceived:" + list);
        List<IMCustomerMessageEntity> customerMessageEntities = new ArrayList<>();
        for (IMMessage message : list) {
            IMUtils.logIMMessage("--------->message:", message);
            if (message != null
                    && isCurrentRoomSession(message.getSessionId())) {
                IMCustomerMessageEntity customerMessageEntity = new IMCustomerMessageEntity();
                customerMessageEntity.imMessage = message;
                customerMessageEntity.customIMBody = getIMBody(message);
                customerMessageEntities.add(customerMessageEntity);
            }
        }
        chatAdapter.addItems(0, customerMessageEntities);
    }

    private IMBodyEntity getIMBody(IMMessage message) {
        IMBodyEntity imBodyEntity = null;
        try {
            log("--------------->customBody:" + message.getContent());
            imBodyEntity = JsonUtils.Gson2Bean(message.getContent(), IMBodyEntity.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return imBodyEntity;
    }

    @Override
    public void onMessageReadAckReceived(List<MessageReceipt> list) {
        log("----------------->onMessageReadAckReceived:" + list);
    }

    @Override
    public void onMessageChanged(IMMessage message) {
        log("----------------->onMessageChanged:" + message);
    }

    @Override
    public void onMessageRevoke(IMMessage message) {
        log("----------------->onMessageRevoke:" + message);
        deleteFromDb(message);
        if (isCurrentRoomSession(message.getSessionId())) {
            removeFromAdapter(message.getSessionId());
        }
    }

    /**
     * 是列表适配器中移除
     *
     * @param sessionId
     */
    private synchronized void removeFromAdapter(String sessionId) {
        for (int i = chatAdapter.getData().size() - 1; i >= 0; i++) {
            IMCustomerMessageEntity item = chatAdapter.getItem(i);
            if (item != null && item.imMessage != null) {
                if (TextUtils.equals(sessionId, item.imMessage.getSessionId())) {
                    chatAdapter.removeItem(item);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction2:
                switch (getIntent().getIntExtra(KEY_CHAT_TYPE, 0)) {
                    case CHAT_TYPE_P2P:
                        ContactDetailActivity.launch(
                                getContext(),
                                null,
                                false,
                                false
                        );
                    case CHAT_TYPE_TEAM:
                        GroupDetailActivity.launch(getContext(),
                                getIntent().getStringExtra(KEY_GROUP_ID),
                                getIntent().getStringExtra(KEY_TID));
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
