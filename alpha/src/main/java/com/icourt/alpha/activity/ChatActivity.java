package com.icourt.alpha.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMCustomerMessageEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.callback.SimpleTextWatcher;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.emoji.MySelectPhotoLayout;
import com.icourt.alpha.view.emoji.MyXhsEmoticonsKeyBoard;
import com.icourt.alpha.view.recyclerviewDivider.ChatItemDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.api.RequestUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
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
import sj.keyboard.widget.EmoticonsEditText;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.netease.nimlib.sdk.msg.model.QueryDirectionEnum.QUERY_OLD;

/**
 * Description  聊天[单聊 群聊]
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class ChatActivity extends ChatBaseActivity {
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_AT_MEMBER = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;

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
    MySelectPhotoLayout mySelectPhotoLayout;
    ChatAdapter chatAdapter;
    LinearLayoutManager linearLayoutManager;
    int pageIndex;
    int pageSize = 20;
    final List<GroupContactBean> atContactList = new ArrayList<>();
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                for (PhotoInfo photoInfo : resultList) {
                    if (photoInfo != null) {
                        uploadFile2Sfile(new File(photoInfo.getPhotoPath()));
                    }
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };


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


    @Const.CHAT_TYPE
    @Override
    protected int getIMChatType() {
        switch (getIntent().getIntExtra(KEY_CHAT_TYPE, 0)) {
            case CHAT_TYPE_P2P:
                return CHAT_TYPE_P2P;
            case CHAT_TYPE_TEAM:
                return CHAT_TYPE_TEAM;
            default:
                return CHAT_TYPE_TEAM;
        }
    }

    @Override
    protected String getIMChatId() {
        switch (getIMChatType()) {
            case CHAT_TYPE_P2P:
                return getIntent().getStringExtra(KEY_UID);
            case CHAT_TYPE_TEAM:
                return getIntent().getStringExtra(KEY_TID);
            default:
                return getIntent().getStringExtra(KEY_TID);
        }
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


    /**
     * 初始化表情
     */
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
        ekBar.setRequestActionListener(new MyXhsEmoticonsKeyBoard.OnRequestActionListener() {
            @Override
            public void onRequestSendText(EmoticonsEditText inputText) {
                dispatchEditTextSend(inputText);
            }

            @Override
            public void onRequestOpenCamera() {
                checkAndOpenCamera();
            }

            @Override
            public void onRequestAtMemeber() {
                openAtMember();
            }
        });
        mySelectPhotoLayout = new MySelectPhotoLayout(getContext());
        mySelectPhotoLayout.setOnImageSendListener(new MySelectPhotoLayout.OnBottomChatPanelListener() {
            @Override
            public boolean requestImageSend(List<String> pics) {
                log("---------->onImageSend:" + pics);
                if (pics != null && pics.size() > 0) {
                    for (int i = 0; i < pics.size(); i++) {
                        String path = pics.get(i);
                        if (!TextUtils.isEmpty(path)) {
                            uploadFile2Sfile(new File(path));
                        }
                    }
                }
                return true;
            }

            @Override
            public void requestOpenPhotos() {
                checkAndOpenPhotos();
            }

            @Override
            public void requestFilePermission() {
                reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        "我们需要文件读写权限!",
                        REQ_CODE_PERMISSION_ACCESS_FILE);
            }
        });
        ekBar.addFuncView(mySelectPhotoLayout);


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
        ekBar.getEtChat().addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().substring(start).trim().equals("@")) {
                    openAtMember();
                }
            }
        });
    }

    /**
     * 分发是发送文本消息 还是@消息
     *
     * @param inputText
     */
    private void dispatchEditTextSend(EmoticonsEditText inputText) {
        if (TextUtils.isEmpty(inputText.getText())) {
            return;
        }
        String txt = inputText.getText().toString();
        if (txt.contains("@")) {
            if (txt.contains("@所有人")) {
                sendAtMsg(txt, true, null);
            } else {
                List<String> accIds = new ArrayList<String>();
                for (GroupContactBean atBean : atContactList) {
                    if (atBean != null && txt.contains(String.format("@%s", atBean.name))) {
                        accIds.add(atBean.accid);
                    }
                }
                atContactList.clear();
                if (!accIds.isEmpty()) {
                    sendAtMsg(txt, false, accIds);
                } else {
                    sendTextMsg(txt);
                }
            }
        } else {
            sendTextMsg(txt);
        }
        inputText.setText("");
    }

    /**
     * 发送文本吧消息
     *
     * @param text
     */
    private void sendTextMsg(String text) {
        if (TextUtils.isEmpty(text)) return;
        super.sendIMTextMsg(text);
    }


    private void uploadFile2Sfile(File file) {
        if (file != null && file.exists()) {
            Map<String, RequestBody> photos = new HashMap<>();
            photos.put("file\"; filename=\"icon.png", RequestUtils.createImgBody(file));
            getApi().groupUploadFile(getIntent().getStringExtra(KEY_GROUP_ID), photos)
                    .enqueue(new SimpleCallBack<JsonElement>() {
                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            sendFileMsg("");
                        }
                    });
        } else {
            showTopSnackBar("文件已不存在!");
        }
    }

    /**
     * 发送文件消息
     *
     * @param url
     */
    private void sendFileMsg(String url) {
        super.sendIMFileMsg(url);
    }


    /**
     * 发送@消息
     *
     * @param text
     * @param isAtAll 是否是at所有人;@所有人 accid可空; 否则不可空
     * @param accIds
     */
    private void sendAtMsg(@NonNull String text, boolean isAtAll, @Nullable List<String> accIds) {
        if (TextUtils.isEmpty(text)) return;
        super.sendIMAtMsg(text, isAtAll, accIds);
    }

    /**
     * 打开@某人的界面
     */
    private void openAtMember() {
        if (getIMChatType() == CHAT_TYPE_TEAM) {
            ContactListActivity.launchSelect(
                    getActivity(),
                    Const.CHOICE_TYPE_SINGLE,
                    REQUEST_CODE_AT_MEMBER);
        }
    }

    /**
     * 打开相机
     */
    private void checkAndOpenCamera() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            Uri picUri = Uri.fromFile(new File(SystemUtils.getFileDiskCache(getContext()) + File.separator
                    + System.currentTimeMillis() + ".png"));
            SystemUtils.doTakePhotoAction(getContext(), picUri, REQUEST_CODE_CAMERA);
        } else {
            reqPermission(Manifest.permission.CAMERA, "我们需要拍照权限!", REQ_CODE_PERMISSION_CAMERA);
        }
    }

    /**
     * 打开相册
     */
    private void checkAndOpenPhotos() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FunctionConfig config = new FunctionConfig.Builder()
                    .setMutiSelectMaxSize(9)
                    .build();
            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, mOnHanlderResultCallback);
        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", REQ_CODE_PERMISSION_ACCESS_FILE);
        }
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
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(chatAdapter = new ChatAdapter(getLoadedLoginToken()));
        chatAdapter.setOnItemLongClickListener(this);
        recyclerView.addItemDecoration(new ChatItemDecoration(getContext(), chatAdapter));
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(false);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
                log("------------->11:" + isSilence);
            }

            @Override
            public void onRelease(float direction) {
                super.onRelease(direction);
                log("------------->12:" + direction);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        ekBar.reset();
                        /*    SystemUtils.hideSoftKeyBoard(getActivity(), etContactName, true);*/
                    }
                    break;
                }
            }
        });
    }


    /**
     * 是否应该滚动(最后一条可见);非是否可以滚动
     *
     * @return
     */
    private boolean shouldScrollToBottom() {
        if (linearLayoutManager != null) {
            return (linearLayoutManager.findLastVisibleItemPosition() + 1)
                    >= linearLayoutManager.getItemCount();
        }
        return false;
    }

    /**
     * 滚动到底部
     */
    private void scrollToBottom() {
        if (linearLayoutManager != null) {
            linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.getItemCount() - 1, 0);
        }
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
                .queryMessageListEx(getLastMessage(), QUERY_OLD, pageSize, true)
                .setCallback(new RequestCallback<List<IMMessage>>() {
                    @Override
                    public void onSuccess(List<IMMessage> param) {
                        chatAdapter.addItems(0, convert2CustomerMessages(param));
                        if (param != null && param.size() > 0) {
                            pageIndex += 1;
                        }
                        stopRefresh();
                        if (isRefresh) {
                            scrollToBottom();
                        }
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
        switch (getIMChatType()) {
            case CHAT_TYPE_P2P:
                if (chatAdapter.getItemCount() > 0) {
                    return chatAdapter.getItem(0).imMessage;
                }
                return MessageBuilder.createEmptyMessage(getIMChatId(), SessionTypeEnum.P2P, 0);
            case CHAT_TYPE_TEAM:
                if (chatAdapter.getItemCount() > 0) {
                    return chatAdapter.getItem(0).imMessage;
                }
                return MessageBuilder.createEmptyMessage(getIMChatId(), SessionTypeEnum.Team, 0);
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
        if (!customerMessageEntities.isEmpty()) {
            if (shouldScrollToBottom()) {
                chatAdapter.addItems(customerMessageEntities);
                scrollToBottom();
            } else {
                chatAdapter.addItems(customerMessageEntities);
            }
        }
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
                switch (getIMChatType()) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String imageRealPathFromURI = SystemUtils.getImageRealPathFromURI(data.getData(), getContentResolver());
                    log("------------>uriL:" + imageRealPathFromURI);
                }
                break;
            case REQUEST_CODE_AT_MEMBER:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    GroupContactBean result = (GroupContactBean) data.getSerializableExtra(KEY_ACTIVITY_RESULT);
                    if (result != null) {
                        appendAtMember(result);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    /**
     * 追加@某人
     *
     * @param contactBean
     */
    private void appendAtMember(GroupContactBean contactBean) {
        if (contactBean == null) return;
        atContactList.add(contactBean);
        EmoticonsEditText etChat = ekBar.getEtChat();
        Editable text = etChat.getText();
        if (TextUtils.isEmpty(text)) {
            text.append(String.format("@%s ", contactBean.name));
        } else {
            text.append(String.format(" @%s ", contactBean.name));
        }
        etChat.setText(text);
        etChat.setSelection(etChat.getText().length());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showTopSnackBar("相机权限被拒绝!");
                }
                break;
            case REQ_CODE_PERMISSION_ACCESS_FILE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showTopSnackBar("文件读写权限被拒绝!");
                } else {
                    mySelectPhotoLayout.refreshFile();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }



}
