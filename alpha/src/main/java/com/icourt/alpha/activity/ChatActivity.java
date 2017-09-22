package com.icourt.alpha.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ChatAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.ChatFileInfoEntity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.event.GroupActionEvent;
import com.icourt.alpha.entity.event.MemberEvent;
import com.icourt.alpha.entity.event.NoDisturbingEvent;
import com.icourt.alpha.entity.event.UnReadEvent;
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.callback.SimpleTextWatcher;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.ImageUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.bgabadgeview.BGABadgeTextView;
import com.icourt.alpha.view.emoji.MySelectPhotoLayout;
import com.icourt.alpha.view.emoji.MyXhsEmoticonsKeyBoard;
import com.icourt.alpha.view.recyclerviewDivider.ChatItemDecoration;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;
import com.icourt.alpha.widget.comparators.ORDER;
import com.icourt.alpha.widget.nim.GlobalMessageObserver;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.team.model.Team;
import com.sj.emoji.DefEmoticons;
import com.sj.emoji.EmojiBean;
import com.sj.emoji.EmojiDisplay;
import com.sj.emoji.EmojiSpan;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.functions.Consumer;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
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
import sj.keyboard.widget.FuncLayout;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.netease.nimlib.sdk.msg.constant.MsgStatusEnum.unread;
import static com.netease.nimlib.sdk.msg.constant.SessionTypeEnum.P2P;
import static com.netease.nimlib.sdk.msg.model.QueryDirectionEnum.QUERY_OLD;

/**
 * Description  聊天[单聊 群聊]
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class ChatActivity extends ChatBaseActivity implements BaseRecyclerAdapter.OnItemChildClickListener, BaseRecyclerAdapter.OnItemChildLongClickListener, BaseRecyclerAdapter.OnItemClickListener {
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_AT_MEMBER = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;

    private static final String KEY_UID = "key_uid";
    private static final String KEY_TID = "key_tid";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_TOTAL_UNREAD_NUM = "key_total_unread_num";
    private static final String KEY_HIDDEN_MORE_BTN = "key_hidden_more_btn";
    private static final String KEY_LOAD_SERVER_MSG = "key_load_server_msg";
    private static final String KEY_LOCATION_MSG_ID = "key_location_msg_id";
    private static final String KEY_CHAT_TYPE = "key_chat_type";

    //本地同步的联系人
    protected final List<GroupContactBean> localContactList = new ArrayList<>();
    ContactDbService contactDbService;
    RealmChangeListener<RealmResults<ContactDbModel>> realmResultsRealmChangeListener = new RealmChangeListener<RealmResults<ContactDbModel>>() {
        @Override
        public void onChange(RealmResults<ContactDbModel> contactDbModels) {
            if (contactDbModels != null) {
                localContactList.clear();
                localContactList.addAll(ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels)));
                if (recyclerView != null
                        && chatAdapter != null) {
                    chatAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    MySelectPhotoLayout mySelectPhotoLayout;
    ChatAdapter chatAdapter;
    LinearLayoutManager linearLayoutManager;
    int pageSize = 20;
    final List<GroupContactBean> atContactList = new ArrayList<>();
    @BindView(R.id.title_Badge_tv)
    BGABadgeTextView titleBadgeTv;
    @BindView(R.id.badgeTitleLayout)
    FrameLayout badgeTitleLayout;
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
    @BindView(R.id.chat_unread_num_tv)
    TextView chatUnreadNumTv;
    @BindView(R.id.ek_bar)
    MyXhsEmoticonsKeyBoard ekBar;

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                for (PhotoInfo photoInfo : resultList) {
                    if (photoInfo != null && !TextUtils.isEmpty(photoInfo.getPhotoPath())) {
                        sendIMPicMsg(photoInfo.getPhotoPath());
                    }
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };
    private RequestCallback<Team> teamCallBack = new RequestCallback<Team>() {
        @Override
        public void onSuccess(Team param) {
            if (param == null) return;
            if (!getIntent().getBooleanExtra(KEY_HIDDEN_MORE_BTN, false)) {
                setViewInVisible(getTitleActionImage(), param.mute());
            }
            if (chatAdapter != null) {
                chatAdapter.setShowMemberUserName(param.getMemberCount() >= 5);
            }
        }

        @Override
        public void onFailed(int code) {

        }

        @Override
        public void onException(Throwable exception) {

        }
    };
    private Runnable scrollToBottomTask = new Runnable() {
        @Override
        public void run() {
            linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.getItemCount() - 1, 0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
        initEmoticonsKeyBoardBar();
        if (getLocationMsgId() > 0) {
            getLocationCenterMsgId();
        } else {
            getData(true);
        }
    }

    public static final void launchP2P(@NonNull Context context,
                                       @NonNull String uid,
                                       String title,
                                       long locationMsgTime,
                                       int totalUnreadCount,
                                       boolean hiddenMoreBtn) {
        if (context == null) return;
        if (TextUtils.isEmpty(uid)) return;
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_UID, uid);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_CHAT_TYPE, CHAT_TYPE_P2P);
        intent.putExtra(KEY_LOCATION_MSG_ID, locationMsgTime);
        intent.putExtra(KEY_TOTAL_UNREAD_NUM, totalUnreadCount);
        intent.putExtra(KEY_HIDDEN_MORE_BTN, hiddenMoreBtn);
        intent.putExtra(KEY_LOAD_SERVER_MSG, true);
        context.startActivity(intent);
    }


    /**
     * 启动 单聊
     *
     * @param context
     * @param uid             对方id 不可变
     * @param title
     * @param locationMsgTime 定位消息的时间 小于=0 不定位消息
     */
    public static final void launchP2P(@NonNull Context context,
                                       @NonNull String uid,
                                       String title,
                                       long locationMsgTime,
                                       int totalUnreadCount) {
        launchP2P(context, uid, title, locationMsgTime, totalUnreadCount, false);
    }


    /**
     * 启动 群聊
     *
     * @param context
     * @param tid           云信tid
     * @param locationMsgId 定位消息的id 小于0=不定位消息
     */
    public static void launchTEAM(@NonNull Context context,
                                  String tid,
                                  String title,
                                  long locationMsgId,
                                  int totalUnreadCount) {
        launchTEAM(context, tid, title, locationMsgId, totalUnreadCount, false);
    }

    /**
     * 启动 群聊
     *
     * @param context
     * @param tid           云信tid
     * @param locationMsgId 定位消息的id 小于0=不定位消息
     */
    public static void launchTEAM(@NonNull Context context,
                                  String tid,
                                  String title,
                                  long locationMsgId,
                                  int totalUnreadCount,
                                  boolean hiddenMoreBtn) {
        launchTEAM(context, tid, title, locationMsgId, totalUnreadCount, hiddenMoreBtn, true);
    }

    /**
     * @param context
     * @param tid
     * @param title
     * @param locationMsgId
     * @param totalUnreadCount
     * @param hiddenMoreBtn
     * @param loadNetMsg       是否加载网络消息
     */
    public static void launchTEAM(@NonNull Context context,
                                  String tid,
                                  String title,
                                  long locationMsgId,
                                  int totalUnreadCount,
                                  boolean hiddenMoreBtn,
                                  boolean loadNetMsg) {
        if (context == null) return;
        if (TextUtils.isEmpty(tid)) return;
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_TID, tid);
        intent.putExtra(KEY_CHAT_TYPE, CHAT_TYPE_TEAM);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_LOCATION_MSG_ID, locationMsgId);
        intent.putExtra(KEY_TOTAL_UNREAD_NUM, totalUnreadCount);
        intent.putExtra(KEY_HIDDEN_MORE_BTN, hiddenMoreBtn);
        intent.putExtra(KEY_LOAD_SERVER_MSG, loadNetMsg);
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
     * 获取消息定位的时间
     *
     * @return
     */
    private long getLocationMsgId() {
        return getIntent().getLongExtra(KEY_LOCATION_MSG_ID, 0);
    }

    @Override
    protected void teamUpdates(@NonNull List<Team> teams) {
        if (teams != null & getIMChatType() == CHAT_TYPE_TEAM) {
            for (Team t : teams) {
                IMUtils.logIMTeam("------------>chat teamUpdates:", t);
                if (StringUtils.equalsIgnoreCase(t.getId(), getIMChatId(), false)) {
                    setTitle(String.valueOf(t.getName()));
                    break;
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoDisturbingEvent noDisturbingEvent) {
        if (noDisturbingEvent == null) return;
        setViewVisible(getTitleActionImage(), noDisturbingEvent.isNoDisturbing);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupEvent(final GroupActionEvent event) {
        if (event == null) return;
        //已经退出群组 关闭当前聊天窗口
        //华为荣耀7崩溃
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.equalsIgnoreCase(event.tid, getIMChatId(), false)) {
                    finish();
                }
            }
        }, 50);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMemberEvent(MemberEvent memberEvent) {
        if (memberEvent == null) return;
        switch (memberEvent.notificationType) {
            case KickMember:
                if (StringUtils.equalsIgnoreCase(memberEvent.sessionId, getIMChatId(), false)
                        && StringUtils.containsIgnoreCase(memberEvent.targets, getLoginUserId())) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("提示")
                            .setMessage("您已经被踢出讨论组啦")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //    removeSession(memberEvent.sessionId);
                                    finish();
                                }
                            }).show();

                }
                break;
        }
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
                if (StringUtils.isEmpty(inputText.getText())) {
                    inputText.setText("");
                    new AlertDialog.Builder(getContext())
                            .setMessage("不能发送空白消息")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
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
                        if (ImageUtils.getBitmapDegree(path) > 0) {
                            ImageUtils.degreeImage(path);
                        }
                        if (!TextUtils.isEmpty(path)) {
                            sendIMPicMsg(path);
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
        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                if (h != oldh && linearLayoutManager.getChildCount() > 0) {
                    linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.getItemCount() - 1, 0);
                }
            }
        });
        ekBar.addOnFuncKeyBoardListener(new FuncLayout.OnFuncKeyBoardListener() {
            @Override
            public void OnFuncPop(int i) {
                log("----------> OnFuncPop i:" + i);
                scrollToBottom();
            }

            @Override
            public void OnFuncClose() {
                log("---------->OnFuncClose ");
            }
        });

        ImageView chat_bottom_at_btn = ekBar.getChat_bottom_at_btn();
        if (chat_bottom_at_btn != null) {
            chat_bottom_at_btn.setVisibility(getIMChatType() == CHAT_TYPE_TEAM ? View.VISIBLE : View.GONE);
        }
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
        if (isIMLinkText(txt)) {
            sendIMLinkMsg(txt);
        } else {
            if (getIMChatType() == Const.CHAT_TYPE_TEAM
                    && txt.contains("@")) {
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
            GroupMemberListActivity.launchSelect(
                    getActivity(),
                    getIMChatId(),
                    Const.CHOICE_TYPE_SINGLE,
                    REQUEST_CODE_AT_MEMBER,
                    true,
                    null, true);
        }
    }

    String path;

    /**
     * 打开相机
     */
    private void checkAndOpenCamera() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            path = SystemUtils.getFileDiskCache(getContext()) + File.separator
                    + System.currentTimeMillis() + ".png";
            Uri picUri = Uri.fromFile(new File(path));
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
        //初始化数据
        contactDbService = new ContactDbService(getLoginUserId());
        RealmResults<ContactDbModel> contactDbModels = contactDbService.queryAll();
        if (contactDbModels != null) {
            localContactList.clear();
            localContactList.addAll(ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(contactDbModels)));
            contactDbModels.removeChangeListener(realmResultsRealmChangeListener);
            contactDbModels.addChangeListener(realmResultsRealmChangeListener);
        }


        setTitle(getIntent().getStringExtra(KEY_TITLE));
        ImageView titleActionImage2 = getTitleActionImage2();
        if (titleActionImage2 != null) {
            titleActionImage2.setImageResource(R.mipmap.header_icon_more);
            titleActionImage2.setVisibility(getIntent().getBooleanExtra(KEY_HIDDEN_MORE_BTN, false) ? View.INVISIBLE : View.VISIBLE);
        }
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.icon_mute);
            titleActionImage.setVisibility(View.INVISIBLE);
        }

        //显示未读数量状态
        setUnreadNum(0);
        getUnreadNum(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer == null) return;
                setUnreadNum(integer.intValue());
            }
        });
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(chatAdapter = new ChatAdapter(localContactList));
        chatAdapter.setOnItemClickListener(this);
        chatAdapter.setOnItemLongClickListener(this);
        chatAdapter.setOnItemChildClickListener(this);
        chatAdapter.setOnItemChildLongClickListener(this);
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
            }

        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (ekBar.isSoftKeyboardPop() || ekBar.isShowFunc()) {
                            ekBar.reset();
                        }
                    }
                    break;
                }
            }
        });
        getTeamINFO(teamCallBack);
        updateTotalUnRead(getIntent().getIntExtra(KEY_TOTAL_UNREAD_NUM, 0));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnReadEvent(UnReadEvent event) {
        if (event == null) return;
        updateTotalUnRead(event.unReadCount);
    }


    /**
     * 更新总数
     *
     * @param num
     */
    private void updateTotalUnRead(int num) {
        int unReadNum = num;
        if (unReadNum > 99) {
            //显示99+
            titleBadgeTv.showTextBadge("99+");
        } else if (unReadNum > 0) {
            titleBadgeTv.showTextBadge(String.valueOf(unReadNum));
        } else {
            titleBadgeTv.hiddenBadge();
        }
    }

    private int unReadCount = 0;

    /**
     * 0自动隐藏
     *
     * @param num
     */
    private void setUnreadNum(int num) {
        unReadCount = num;
        setViewVisible(chatUnreadNumTv, num > 20);
        chatUnreadNumTv.setText(String.format("%s条未读", num));
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
        if (linearLayoutManager != null && linearLayoutManager.getItemCount() > 0) {
            linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.getItemCount() - 1, 0);
            if (recyclerView != null) {
                recyclerView.postDelayed(scrollToBottomTask, 300);
            }
        }
    }

    /**
     * 滚动到中间
     */
    private void scrollToCenter() {
        if (linearLayoutManager != null && linearLayoutManager.getItemCount() > 1) {
            final int marginTop = refreshLayout.getHeight() / 2;
            long locationMsgId = getIntent().getLongExtra(KEY_LOCATION_MSG_ID, 0);
            IMMessageCustomBody targetImMessageCustomBody = new IMMessageCustomBody();
            targetImMessageCustomBody.id = locationMsgId;
            final int indexOf = chatAdapter.getData().indexOf(targetImMessageCustomBody);
            if (indexOf > 0) {
                linearLayoutManager.scrollToPositionWithOffset(indexOf, 0);
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutManager.scrollToPositionWithOffset(indexOf, marginTop);
                    }
                }, 100);
            }
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
                        if (param != null) {
                            for (IMMessage imMessage : param) {
                                IMUtils.logIMMessage("----------->query result:", imMessage);
                            }
                        }


                        //1 恢复草稿 文本消息
                        if (isRefresh) {
                            String newlyDraftTxtMsg = getNewlyDraftTxtMsg(param);
                            if (!TextUtils.isEmpty(newlyDraftTxtMsg) && ekBar != null) {
                                ekBar.getEtChat().setText(newlyDraftTxtMsg);
                                ekBar.getEtChat().setSelection(newlyDraftTxtMsg.length());
                            }
                        }


                        //2 过滤数据
                        param = filterMsgs(param);
                        if (param == null || param.isEmpty()
                                && getIntent().getBooleanExtra(KEY_LOAD_SERVER_MSG, true)) {
                            //本地为空从网络获取
                            getMsgFromServer(isRefresh);
                        } else {
                            chatAdapter.addItems(0, convert2CustomerMessages(param));

                            if (isRefresh) {
                                scrollToBottom();
                            }
                            if (param.size() < 20) {
                                getMsgFromServer(isRefresh);
                            } else {
                                stopRefresh();
                            }
                            if (chatAdapter.getItemCount() < 20) {
                                setUnreadNum(0);
                                clearUnReadNum();
                            }
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

    @Override
    protected List<IMMessageCustomBody> convert2CustomerMessages(List<IMMessage> param) {
        return filterCustomerMessagesFromAdapter(super.convert2CustomerMessages(param));
    }

    /**
     * 过滤适配器中已经存在的东西
     *
     * @param param
     * @return
     */
    protected List<IMMessageCustomBody> filterCustomerMessagesFromAdapter(List<IMMessageCustomBody> param) {
        List<IMMessageCustomBody> imMessageCustomBodies = param;
        //过滤已经存在的消息
        List<IMMessageCustomBody> data = chatAdapter.getData();
        if (imMessageCustomBodies != null && !imMessageCustomBodies.isEmpty()) {
            for (int i = imMessageCustomBodies.size() - 1; i >= 0; i--) {
                IMMessageCustomBody body = imMessageCustomBodies.get(i);
                if (data.contains(body)) {
                    imMessageCustomBodies.remove(i);
                }
            }
        }
        return imMessageCustomBodies;
    }

    private IMMessage getLocationMessage() {
        switch (getIMChatType()) {
            case CHAT_TYPE_P2P:
                return MessageBuilder.createEmptyMessage(getIMChatId(), P2P, getLocationMsgId());
            case CHAT_TYPE_TEAM:
                return MessageBuilder.createEmptyMessage(getIMChatId(), SessionTypeEnum.Team, getLocationMsgId());
            default: {
                return null;
            }
        }
    }

    /**
     * 获取 msgid前后的20条
     */
    private void getLocationCenterMsgId() {
        long locationMsgId = getIntent().getLongExtra(KEY_LOCATION_MSG_ID, 0);
        getMsgFromServer(locationMsgId);
    }


    /**
     * 过滤消息
     *
     * @param imMessages
     */
    private List<IMMessage> filterMsgs(List<IMMessage> imMessages) {
        List<IMMessage> msgs = new ArrayList<>();
        List<IMMessage> draftMsgs = new ArrayList<>();
        if (imMessages != null) {
            for (IMMessage imMessage : imMessages) {
                if (imMessage == null) continue;
                if (!GlobalMessageObserver.isFilterMsg(imMessage.getTime())
                        ) {
                    if (!GlobalMessageObserver.isDraftMsg(imMessage)) {
                        msgs.add(imMessage);
                    } else {
                        draftMsgs.add(imMessage);
                    }
                }
            }
        }
        deleteDraftMsgs(draftMsgs);
        return msgs;
    }


    /**
     * 获取最近的文本草稿消息
     *
     * @param imMessages
     * @return
     */
    private String getNewlyDraftTxtMsg(List<IMMessage> imMessages) {
        if (imMessages == null || imMessages.isEmpty()) return null;
        for (int i = imMessages.size() - 1; i >= 0; i--) {
            IMMessage imMessage = imMessages.get(i);
            if (imMessage == null) continue;
            if (GlobalMessageObserver.isDraftMsg(imMessage)) {
                IMMessageCustomBody imBody = getIMBody(imMessage);
                if (imBody != null) {
                    return imBody.content;
                }
            }
        }
        return null;
    }

    private void deleteDraftMsgs(List<IMMessage> draftMsgs) {
        if (draftMsgs == null || draftMsgs.isEmpty()) return;
        deleteMsgFromDb(draftMsgs);
    }


    /**
     * 获取定位前后的20条
     *
     * @param msgId
     */
    private void getMsgFromServer(long msgId) {
        String type = "around";
        callEnqueue(getChatApi().msgQueryAll(
                type,
                20,
                msgId,
                getIMChatType(),
                getIMChatId()),
                new SimpleCallBack<List<IMMessageCustomBody>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                        if (response.body().result != null) {
                            Collections.sort(response.body().result, new LongFieldEntityComparator<IMMessageCustomBody>(ORDER.ASC));
                            chatAdapter.addItems(0, response.body().result);
                            scrollToCenter();
                        }
                        setUnreadNum(0);
                        clearUnReadNum();
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<IMMessageCustomBody>>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });

    }

    /**
     * 获取服务器消息
     */
    private void getMsgFromServer(final boolean isRefresh) {
        String type = "latest";
        long msg_id = Integer.MAX_VALUE;
        if (chatAdapter.getItemCount() <= 0) {
            type = "latest";
        } else {
            type = "pre";
            IMMessageCustomBody item = chatAdapter.getItem(0);
            if (item != null) {
                msg_id = item.id;
            }
        }
        callEnqueue(getChatApi().msgQueryAll(
                type,
                20,
                msg_id,
                getIMChatType(),
                getIMChatId()),
                new SimpleCallBack<List<IMMessageCustomBody>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<IMMessageCustomBody>>> call, Response<ResEntity<List<IMMessageCustomBody>>> response) {
                        if (response.body().result != null) {
                            filterCustomerMessagesFromAdapter(response.body().result);
                            Collections.sort(response.body().result, new LongFieldEntityComparator<IMMessageCustomBody>(ORDER.ASC));
                            chatAdapter.addItems(0, response.body().result);
                            if (isRefresh) {
                                scrollToBottom();
                            }
                        }
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<IMMessageCustomBody>>> call, Throwable t) {
                        super.onFailure(call, t);
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
                    IMMessageCustomBody item = chatAdapter.getItem(0);
                    if (item != null) {
                        return MessageBuilder.createEmptyMessage(getIMChatId(), P2P, item.send_time);
                    }
                }
                return MessageBuilder.createEmptyMessage(getIMChatId(), P2P, 0);
            case CHAT_TYPE_TEAM:
                if (chatAdapter.getItemCount() > 0) {
                    IMMessageCustomBody item = chatAdapter.getItem(0);
                    if (item != null) {
                        return MessageBuilder.createEmptyMessage(getIMChatId(), SessionTypeEnum.Team, item.send_time);
                    }
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


    /**
     * 接受到自定义消息
     *
     * @param customBody
     */
    @Override
    public void onMessageReceived(IMMessageCustomBody customBody) {
        if (customBody == null) return;
        log("----------------->chat onMessageReceived:" + customBody);
        if (!isCurrentRoomSession(customBody)) return;
        //自己发送的消息推送回来了
        if (chatAdapter.getData().contains(customBody)) {
            customBody.msg_statu = Const.MSG_STATU_SUCCESS;
            chatAdapter.updateItem(customBody);
            scrollToBottom();
        } else {//别人发送的消息收到
            if (shouldScrollToBottom()) {
                chatAdapter.addItem(customBody);
                scrollToBottom();
            } else {
                chatAdapter.addItem(customBody);
            }
        }
    }

    @Override
    public void onMessageReadAckReceived(List<MessageReceipt> list) {
        log("----------------->chat onMessageReadAckReceived:" + list);
    }

    @Override
    public void onMessageChanged(IMMessageCustomBody customBody) {
        log("----------------->chat  onMessageChanged:" + customBody);
        if (customBody == null) return;
        if (!isCurrentRoomSession(customBody)) return;
        chatAdapter.updateItem(customBody);
    }

    @Override
    public void onMessageRevoke(IMMessage message) {
        log("----------------->chat onMessageRevoke:" + message);
        //注意 本身原消息 和撤回的那一条消息 macgic_id是一样的;
    }

    @Override
    public void onMessageRevoke(long msgId) {
        removeFromAdapter(msgId);
    }


    /**
     * 是列表适配器中移除
     *
     * @param msgId
     */
    private synchronized void removeFromAdapter(long msgId) {
        log("----------------->chat onMessageRevoke:msgId" + msgId);
        List<IMMessageCustomBody> data = chatAdapter.getData();
        for (int i = data.size() - 1; i >= 0; i--) {
            IMMessageCustomBody item = data.get(i);
            if (item != null) {
                if (msgId == item.id) {
                    chatAdapter.removeItem(item);
                    break;
                }
            }
        }
    }

    @OnClick({R.id.chat_unread_num_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_unread_num_tv:
                scrollToUnreadMsg();
                break;
            case R.id.titleAction2:
                switch (getIMChatType()) {
                    case CHAT_TYPE_P2P:
                        ContactDetailActivity.launch(
                                getContext(),
                                getIMChatId(),
                                false,
                                true
                        );
                        break;
                    case CHAT_TYPE_TEAM:
                        GroupDetailActivity.launchTEAM(getContext(),
                                getIntent().getStringExtra(KEY_TID));
                        break;
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 滚动到第一条未读消息
     */
    private void scrollToUnreadMsg() {
        if (unReadCount > chatAdapter.getItemCount()) {
            final int loadCount = unReadCount - chatAdapter.getItemCount();
            NIMClient.getService(MsgService.class)
                    .queryMessageListEx(getLastMessage(), QUERY_OLD, loadCount, true)
                    .setCallback(new RequestCallback<List<IMMessage>>() {
                        @Override
                        public void onSuccess(List<IMMessage> param) {
                            LogUtils.d("----------->query result3:" + param);
                            param = filterMsgs(param);
                            chatAdapter.addItems(0, convert2CustomerMessages(param));

                            //滚动到最近未读的一条
                            int unreadMsgIndex = getUnReadMsgIndex();
                           /* if (unreadMsgIndex >= 0) {
                                linearLayoutManager.scrollToPositionWithOffset(unreadMsgIndex, 0);
                                clearUnReadNum();
                                setUnreadNum(0);
                            }*/


                            linearLayoutManager.scrollToPositionWithOffset(0, 0);
                            clearUnReadNum();
                            setUnreadNum(0);
                        }

                        @Override
                        public void onFailed(int code) {
                            LogUtils.d("----------->query result3:Failed code" + code);
                        }

                        @Override
                        public void onException(Throwable exception) {
                            LogUtils.d("----------->query result3:Exception" + exception);
                        }
                    });
        } else {
            clearUnReadNum();
            setUnreadNum(0);
        }
    }

    /**
     * 获取列表中未读消息index
     * bug 所有消息都是success
     *
     * @return
     */
    @Deprecated
    private int getUnReadMsgIndex() {
        List<IMMessageCustomBody> data = chatAdapter.getData();
        if (data == null || data.isEmpty()) return -1;
        for (int i = 0; i < data.size(); i++) {
            IMMessageCustomBody imMessageCustomBody = data.get(i);
            if (imMessageCustomBody != null
                    && imMessageCustomBody.imMessage != null) {
                IMUtils.logIMMessage("-------->unReadMsgIndex:" + i + " :", imMessageCustomBody.imMessage);
                if (imMessageCustomBody.imMessage.getStatus() == unread) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (!TextUtils.isEmpty(path) && ImageUtils.getBitmapDegree(path) > 0) {
                        ImageUtils.degreeImage(path);
                    }
                    sendIMPicMsg(path);
                }
                break;
            case REQUEST_CODE_AT_MEMBER:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    GroupContactBean result = (GroupContactBean) data.getParcelableExtra(KEY_ACTIVITY_RESULT);
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
            if (text.toString().endsWith("@")) {
                text.append(String.format("%s ", contactBean.name));
            } else {
                text.append(String.format(" @%s ", contactBean.name));
            }
        }
        etChat.setText(text);
        etChat.setSelection(etChat.getText().length());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults == null) return;
        if (grantResults.length <= 0) return;
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


    @Override
    public void onItemChildClick(BaseRecyclerAdapter
                                         adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        IMMessageCustomBody item = chatAdapter.getItem(position);
        if (item == null) return;
        switch (view.getId()) {
            case R.id.chat_user_icon_iv:
                switch (getIMChatType()) {
                    case CHAT_TYPE_P2P:
                        showContactDialogFragment(item.from, true);
                        break;
                    case CHAT_TYPE_TEAM:
                        showContactDialogFragment(item.from,
                                StringUtils.equalsIgnoreCase(item.from, getLoadedLoginUserId(), false));
                        break;
                }
                break;
            case R.id.chat_image_iv:
                if (item.ext != null) {
                    ArrayList<String> mediumImageUrls = new ArrayList<>();
                    ArrayList<ChatFileInfoEntity> sFileImageInfoEntities = new ArrayList<>();
                    for (int i = 0; i < chatAdapter.getData().size(); i++) {
                        IMMessageCustomBody imMessageCustomBody = chatAdapter.getData().get(i);
                        if (imMessageCustomBody != null
                                && imMessageCustomBody.ext != null) {
                            if (imMessageCustomBody.show_type == Const.MSG_TYPE_IMAGE
                                    && imMessageCustomBody.id > 0) {
                                ChatFileInfoEntity sFileImageInfoEntity = imMessageCustomBody.ext.convert2Model();
                                if (sFileImageInfoEntity != null) {
                                    mediumImageUrls.add(sFileImageInfoEntity.getChatMiddlePic());
                                    sFileImageInfoEntity.setChatMsgId(imMessageCustomBody.id);
                                    sFileImageInfoEntities.add(sFileImageInfoEntity);
                                }
                            }
                        }
                    }
                    int pos = mediumImageUrls.indexOf(item.ext.getChatMediumImageUrl());
                    if (mediumImageUrls.isEmpty()) return;
                    View chat_image_iv = holder.obtainView(R.id.chat_image_iv);
                    ImagePagerActivity.launch(
                            view.getContext(),
                            sFileImageInfoEntities,
                            pos,
                            getIMChatType(),
                            getIMChatId(),
                            chat_image_iv);
                }
                break;
            case R.id.chat_send_fail_iv:
                if (item.show_type == Const.MSG_TYPE_IMAGE) {
                    retrySendIMPicMsg(item);
                } else {
                    retrySendCustomBody(item);
                }
                break;
            case R.id.chat_link_ll:
                if (item.ext != null) {
                    if (item.ext.ext != null) {
                        WebViewActivity.launch(getContext(), item.ext.ext.url);
                    } else {
                        WebViewActivity.launch(getContext(), item.ext.url);
                    }
                }
                break;
            case R.id.chat_ding_content_iamge_iv:
                if (item.ext == null) return;
                if (item.ext.ext == null) return;
                ChatFileInfoEntity chatFileInfoEntity = item.ext.ext.convert2Model();
                chatFileInfoEntity.setChatMsgId(item.ext.id);

                ArrayList<ChatFileInfoEntity> sFileImageInfoEntities = new ArrayList<>();

                sFileImageInfoEntities.add(chatFileInfoEntity);
                ImagePagerActivity.launch(getContext(),
                        sFileImageInfoEntities,
                        0,
                        getIMChatType(),
                        getIMChatId(),
                        view);
                break;
            case R.id.chat_ll_file:
                switch (item.show_type) {
                    case Const.MSG_TYPE_FILE:
                        //文件
                        if (item.ext != null) {
                            /**
                             *  "name":"不可描述.avi",
                             "size":123123,
                             "repo_id":"xasxas",
                             "path":"xasxax"
                             */
                            //item.ext.name;
                            FileDownloadActivity.launch(
                                    getContext(),
                                    item.ext,
                                    SFileConfig.FILE_FROM_IM
                            );
                        }
                        break;
                    case Const.MSG_TYPE_DING:
                        if (item.ext != null) {
                            //钉的文件
                            switch (item.ext.show_type) {
                                case Const.MSG_TYPE_FILE:
                                    if (item.ext.ext != null) {
                                        /**
                                         *  "name":"不可描述.avi",
                                         "size":123123,
                                         "repo_id":"xasxas",
                                         "path":"xasxax"
                                         */
                                        //item.ext.ext.name;
                                        FileDownloadActivity.launch(
                                                getContext(),
                                                item.ext.ext,
                                                SFileConfig.FILE_FROM_IM
                                        );
                                    }
                                    break;
                            }
                        }
                        break;
                }
                break;
        }
    }


    @Override
    public boolean onItemChildLongClick(BaseRecyclerAdapter
                                                adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        switch (view.getId()) {
            case R.id.chat_image_iv:
                return super.onItemLongClick(adapter, holder, view, position);
            case R.id.chat_link_ll:
                return super.onItemLongClick(adapter, holder, view, position);
            case R.id.chat_txt_tv:
                return super.onItemLongClick(adapter, holder, view, position);
            case R.id.chat_ll_file:
                return super.onItemLongClick(adapter, holder, view, position);
            case R.id.chat_ding_content_iamge_iv:
                return super.onItemLongClick(adapter, holder, view, position);
        }
        return false;
    }

    /**
     * 展示联系人对话框
     *
     * @param accid
     * @param hiddenChatBtn
     */
    public void showContactDialogFragment(String accid, boolean hiddenChatBtn) {
        String tag = ContactDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(accid, "成员资料", hiddenChatBtn)
                .show(mFragTransaction, tag);
    }


    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder
            holder, View view, int position) {
        if (ekBar != null) {
            ekBar.reset();
        }
    }


    /**
     * 获取输入未发送的文本
     *
     * @return
     */
    private String getInputText() {
        String textStr = null;
        if (ekBar != null && ekBar.getEtChat() != null) {
            Editable text = ekBar.getEtChat().getText();
            if (!TextUtils.isEmpty(text)) {
                textStr = text.toString();
            }
        }
        return textStr;
    }

    /**
     * 保存草稿
     */
    private void saveTextDraft() {
        String inputText = getInputText();
        if (!TextUtils.isEmpty(inputText)) {
            final IMMessageCustomBody textHistoryEntity = IMMessageCustomBody.createTextMsg(getIMChatType(),
                    getLoadedLoginName(),
                    getLoadedLoginUserId(),
                    getIMChatId(),
                    inputText);
            textHistoryEntity.msg_statu = Const.MSG_STATU_DRAFT;
            String jsonBody = null;
            try {
                jsonBody = JsonUtils.Gson2String(textHistoryEntity);
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(jsonBody)) {
                saveSendNimMsg(jsonBody, MsgStatusEnum.draft, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
        saveTextDraft();
        super.onDestroy();
    }
}
