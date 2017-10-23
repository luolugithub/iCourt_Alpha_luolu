package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blog.www.guideview.Component;
import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.ItemsEntityImp;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.OverTimingRemindEvent;
import com.icourt.alpha.entity.event.ServerTimingEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.entity.event.UnReadEvent;
import com.icourt.alpha.fragment.TabCustomerFragment;
import com.icourt.alpha.fragment.TabDocumentsFragment;
import com.icourt.alpha.fragment.TabMineFragment;
import com.icourt.alpha.fragment.TabNewsFragment;
import com.icourt.alpha.fragment.TabProjectFragment;
import com.icourt.alpha.fragment.TabSearchFragment;
import com.icourt.alpha.fragment.TabTaskFragment;
import com.icourt.alpha.fragment.TabTimingFragment;
import com.icourt.alpha.fragment.dialogfragment.OverTimingRemindDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingNoticeDialogFragment;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.http.observer.BaseObserver;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.service.DaemonService;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.SimpleViewGestureListener;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.CheckableLayout;
import com.icourt.alpha.view.SimpleComponent;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.alpha.widget.nim.GlobalMessageObserver;
import com.icourt.alpha.widget.popupwindow.BaseListActionItemPop;
import com.icourt.alpha.widget.popupwindow.ListActionItemPop;
import com.icourt.lib.daemon.IntentWrapper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.leolin.shortcutbadger.ShortcutBadger;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.MSG_TYPE_ALPHA_SYNC;


/**
 * Description  主页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class MainActivity extends BaseAppUpdateActivity implements OnFragmentCallBackListener {
    public static String KEY_FIND_FRAGMENT = "type_TabFindFragment_fragment";
    public static String KEY_MINE_FRAGMENT = "type_TabMimeFragment_fragment";
    public static String KEY_PROJECT_PERMISSION = "cache_project_permission";
    public static String KEY_CUSTOMER_PERMISSION = "cache_customer_permission";

    public static final String KEY_FROM_LOGIN = "FromLogin";

    public static final int TYPE_FRAGMENT_NEWS = 0;
    public static final int TYPE_FRAGMENT_TASK = 1;
    public static final int TYPE_FRAGMENT_PROJECT = 2;
    public static final int TYPE_FRAGMENT_MINE = 3;
    public static final int TYPE_FRAGMENT_TIMING = 4;
    public static final int TYPE_FRAGMENT_CUSTOMER = 5;
    public static final int TYPE_FRAGMENT_SEARCH = 6;
    public static final int TYPE_FRAGMENT_DOCUMENTS = 7;

    @IntDef({
            TYPE_FRAGMENT_NEWS,
            TYPE_FRAGMENT_TASK,
            TYPE_FRAGMENT_PROJECT,
            TYPE_FRAGMENT_MINE,
            TYPE_FRAGMENT_TIMING,
            TYPE_FRAGMENT_CUSTOMER,
            TYPE_FRAGMENT_SEARCH,
            TYPE_FRAGMENT_DOCUMENTS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChildFragmentType {

    }

    private final List<ItemsEntity> tabData = Arrays.asList(
            new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.drawable.tab_project),
            new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
            new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.drawable.tab_customer),
            new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search),
            new ItemsEntity("文档", TYPE_FRAGMENT_DOCUMENTS, R.drawable.tab_document));

    /**
     * 可改变的tab
     */
    private final List<ItemsEntity> tabChangeableData = new ArrayList<>();

    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    @BindView(R.id.tab_news)
    CheckableLayout tabNews;
    @BindView(R.id.tab_task)
    CheckableLayout tabTask;
    @BindView(R.id.tab_find_ctv)
    CheckedTextView tabFindCtv;
    @BindView(R.id.tab_find)
    CheckableLayout tabFind;
    @BindView(R.id.tab_mine_ctv)
    CheckedTextView tabMineCtv;
    @BindView(R.id.tab_mine)
    CheckableLayout tabMine;
    @BindView(R.id.rg_main_tab)
    LinearLayout rgMainTab;
    @BindView(R.id.tab_timing_icon)
    ImageView tabTimingIcon;
    @BindView(R.id.tab_timing_tv)
    TextView tabTimingTv;
    @BindView(R.id.tab_timing)
    LinearLayout tabTiming;
    RotateAnimation timingAnim;

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void launchFromLogin(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_FROM_LOGIN, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void launchByNotifaction(Context context, IMMessage imMessage) {
        if (context == null) return;
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, imMessage);
        context.startActivity(intent);
    }

    Fragment currentFragment;
    final SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();
    SimpleViewGestureListener.OnSimpleViewGestureListener onSimpleViewGestureListener = new SimpleViewGestureListener.OnSimpleViewGestureListener() {
        @Override
        public boolean onDoubleTap(View v, MotionEvent e) {
            if (v == null) super.onDoubleTap(v, e);
            switch (v.getId()) {
                case R.id.tab_news: {
                    Fragment currFragment = currentFragment;
                    if (currFragment instanceof OnTabDoubleClickListener) {
                        ((OnTabDoubleClickListener) currFragment).onTabDoubleClick(currFragment, null, null);
                    }
                }
                break;
                default:
                    break;
            }
            return super.onDoubleTap(v, e);
        }
    };
    final ArrayMap<Long, Integer> serverTimingSyncTimesArray = new ArrayMap<>();

    class MyHandler extends Handler {
        public static final int TYPE_TOKEN_REFRESH = 101;//token刷新
        public static final int TYPE_CHECK_APP_UPDATE = 102;//检查更新
        public static final int TYPE_CHECK_TIMING_UPDATE = 103;//检查计时
        public static final int TYPE_OVER_TIMING_REMIND_AUTO_CLOSE = 104;//持续计时过久时的提醒覆层关闭
        public static final int TYPE_OVER_TIMING_REMIND = 105;//计时超时提醒
        private static final int TYPE_CHECK_SD_SPACE = 106;//检查内置存储空间

        /**
         * 刷新登陆token
         */
        public void addTokenRefreshTask() {
            this.removeMessages(TYPE_TOKEN_REFRESH);
            this.sendEmptyMessageDelayed(TYPE_TOKEN_REFRESH, 2_000);
        }

        /**
         * 检查更新
         */
        public void addCheckAppUpdateTask() {
            this.removeMessages(TYPE_CHECK_APP_UPDATE);
            this.sendEmptyMessageDelayed(TYPE_CHECK_APP_UPDATE, 3_000);
        }


        /**
         * 检查计时
         */
        public void addCheckTimingTask() {
            this.removeMessages(TYPE_CHECK_TIMING_UPDATE);
            this.sendEmptyMessageDelayed(TYPE_CHECK_TIMING_UPDATE, 1_000);
        }

        /**
         * 添加超时提醒
         */
        public void addOverTimingRemind(String remindContent) {
            this.removeMessages(TYPE_OVER_TIMING_REMIND);
            Message obtain = Message.obtain();
            obtain.what = TYPE_OVER_TIMING_REMIND;
            obtain.obj = remindContent;
            this.sendMessageDelayed(obtain, 100);
        }

        /**
         * 检查sd卡存储空间
         */
        public void addCheckSdSpace() {
            this.removeMessages(TYPE_CHECK_SD_SPACE);
            this.sendEmptyMessageDelayed(TYPE_CHECK_SD_SPACE, 1_000);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TYPE_TOKEN_REFRESH:
                    refreshToken();
                    break;
                case TYPE_CHECK_APP_UPDATE:
                    checkAppUpdate(getContext());
                    break;
                case TYPE_CHECK_TIMING_UPDATE:
                    TimerManager.getInstance().timerQuerySync();
                    break;
                case TYPE_OVER_TIMING_REMIND_AUTO_CLOSE:
                    dismissOverTimingRemindDialogFragment(true);
                    break;
                case TYPE_OVER_TIMING_REMIND:
                    String remindContent = (String) msg.obj;
                    showOverTimingRemindDialogFragment(remindContent);
                    break;
                case TYPE_CHECK_SD_SPACE:
                    checkSdSpace();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 检查sd卡可用空间
     */
    private void checkSdSpace() {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> e) throws Exception {
                try {
                    e.onNext(FileUtils.getAvaiableSpaceMB());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    e.onNext(Long.valueOf(-1));
                } finally {
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long aLong) {
                        if (aLong > 0 && aLong < 100) {
                            new AlertDialog.Builder(getContext())
                                    .setMessage("存储空间小于100M啦,去清理?")
                                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            SystemUtils.launchInterStorageSettings(getContext());
                                        }
                                    })
                                    .setNegativeButton(R.string.str_cancel, null)
                                    .show();
                        }
                    }
                });
    }

    MyHandler mHandler = new MyHandler();
    ContactDbService contactDbService;
    AlphaUserInfo loginUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gotoChatByNotifaction();
        initView();
        SFileTokenUtils.syncServerSFileToken();
    }

    /**
     * 点击通知栏跳转到对应的聊天页面
     */
    private void gotoChatByNotifaction() {
        IMMessage imMessage = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (imMessage != null) {
            int totalUnReadCount = NIMClient.getService(MsgService.class).getTotalUnreadCount();
            if (imMessage.getMsgType() == MsgTypeEnum.custom) {
                if (imMessage.getAttachment() != null) {
                    try {
                        String s = imMessage.getAttachment().toJson(false);
                        JSONObject jsonObject = JsonUtils.getJSONObject(s);
                        if (jsonObject.getInt("showType") == MSG_TYPE_ALPHA_SYNC && "BUBBLE_SYNC".equalsIgnoreCase(jsonObject.getString("type")) && "TIMING_TOO_LONG".equalsIgnoreCase(jsonObject.getString("scene"))) {
                            TimerTimingActivity.launch(this, TimerManager.getInstance().getTimer());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    AlphaSpecialHelperActivity.launch(this, imMessage.getSessionId(), totalUnReadCount);
                }
            } else {
                IMMessageCustomBody customBody = GlobalMessageObserver.getIMBody(imMessage);
                if (customBody == null) return;
                switch (customBody.ope) {
                    case Const.CHAT_TYPE_P2P:
                        ChatActivity.launchP2P(this, customBody.from, customBody.name, 0, totalUnReadCount, true);
                        break;
                    case Const.CHAT_TYPE_TEAM:
                        Team team = NIMClient.getService(TeamService.class)
                                .queryTeamBlock(customBody.imMessage.getSessionId());
                        if (team != null)
                            ChatActivity.launchTEAM(this, customBody.imMessage.getSessionId(), team.getName(), 0, totalUnReadCount, true);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();
        initChangedTab();
        EventBus.getDefault().register(this);
        loginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        new SimpleViewGestureListener(tabNews, onSimpleViewGestureListener);
        initChangedTab();
        checkedTab(R.id.tab_news, TYPE_FRAGMENT_NEWS);
        mHandler.addCheckSdSpace();
        if (BuildConfig.BUILD_TYPE_INT > 0) {
            mHandler.addCheckAppUpdateTask();
        }
        mHandler.addTokenRefreshTask();
        mHandler.addCheckTimingTask();
    }

    private void initTabChangeableData() {
        tabChangeableData.clear();
        if (hasProjectPermission() && hasCustomerPermission()) {
            tabChangeableData.addAll(tabData);
        } else if (hasProjectPermission()) {
            tabChangeableData.addAll(Arrays.asList(
                    new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.drawable.tab_project),
                    new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
                    new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search),
                    new ItemsEntity("文档", TYPE_FRAGMENT_DOCUMENTS, R.drawable.tab_document))
            );
        } else if (hasCustomerPermission()) {
            tabChangeableData.addAll(Arrays.asList(
                    new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.drawable.tab_customer),
                    new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
                    new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search),
                    new ItemsEntity("文档", TYPE_FRAGMENT_DOCUMENTS, R.drawable.tab_document))
            );
        } else {
            tabChangeableData.addAll(Arrays.asList(
                    new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search),
                    new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
                    new ItemsEntity("文档", TYPE_FRAGMENT_DOCUMENTS, R.drawable.tab_document))
            );
        }
    }


    private boolean hasProjectPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_PROJECT_PERMISSION, false);
    }

    private void setProjectPermission(boolean hasPermission) {
        SpUtils.getInstance().putData(KEY_PROJECT_PERMISSION, hasPermission);
    }

    private boolean hasCustomerPermission() {
        return SpUtils.getInstance().getBooleanData(KEY_CUSTOMER_PERMISSION, false);
    }

    private void setCustomerPermission(boolean hasPermission) {
        SpUtils.getInstance().putData(KEY_CUSTOMER_PERMISSION, hasPermission);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationisEnable();
        getPermission();
        timerQuerySync();
    }

    /**
     * 检查通知是否打开
     */
    private void checkNotificationisEnable() {
        if (!SystemUtils.isEnableNotification(getContext())
                || !NotificationManagerCompat.from(getContext()).areNotificationsEnabled()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("为了您能收到消息提醒,请打开通知设置开关!")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bugSync("通知开关设置", "未打开");
                            SystemUtils.launchPhoneSettings(getContext());
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    /**
     * 保存切换的tab
     *
     * @param id
     * @param type
     */
    private void saveChangedTab(@IdRes int id, @ChildFragmentType int type) {
        switch (id) {
            case R.id.tab_find:
                SpUtils.getInstance().putData(KEY_FIND_FRAGMENT, type);
                break;
            case R.id.tab_mine:
                SpUtils.getInstance().putData(KEY_MINE_FRAGMENT, type);
                break;
            default:
                break;
        }
    }

    /**
     * 类型转化
     *
     * @param type
     * @return
     */
    @ChildFragmentType
    public static final int convert2ChildFragmentType(int type) {
        switch (type) {
            case TYPE_FRAGMENT_NEWS:
                return TYPE_FRAGMENT_NEWS;
            case TYPE_FRAGMENT_TASK:
                return TYPE_FRAGMENT_TASK;
            case TYPE_FRAGMENT_PROJECT:
                return TYPE_FRAGMENT_PROJECT;
            case TYPE_FRAGMENT_CUSTOMER:
                return TYPE_FRAGMENT_CUSTOMER;
            case TYPE_FRAGMENT_SEARCH:
                return TYPE_FRAGMENT_SEARCH;
            case TYPE_FRAGMENT_TIMING:
                return TYPE_FRAGMENT_TIMING;
            case TYPE_FRAGMENT_MINE:
                return TYPE_FRAGMENT_MINE;
            case TYPE_FRAGMENT_DOCUMENTS:
                return TYPE_FRAGMENT_DOCUMENTS;
            default:
                break;
        }
        return TYPE_FRAGMENT_PROJECT;
    }


    /**
     * 获取页面对应的tab
     *
     * @return
     */
    private int getFragmentType(@IdRes int id) {
        switch (id) {
            case R.id.tab_find:
                return convert2ChildFragmentType(SpUtils.getInstance().getIntData(KEY_FIND_FRAGMENT, TYPE_FRAGMENT_PROJECT));
            case R.id.tab_mine:
                return convert2ChildFragmentType(SpUtils.getInstance().getIntData(KEY_MINE_FRAGMENT, TYPE_FRAGMENT_MINE));
            default:
                break;
        }
        return TYPE_FRAGMENT_PROJECT;
    }

    /**
     * 初始化可改变的tab
     */
    private void initChangedTab() {
        setTabInfo(R.id.tab_find, getFragmentType(R.id.tab_find));
        setTabInfo(R.id.tab_mine, getFragmentType(R.id.tab_mine));
    }

    /**
     * 设置tab对应的图标
     *
     * @param type
     */
    private void setTabInfo(@IdRes int tabId, @ChildFragmentType int type) {
        ItemsEntity itemsEntity = null;
        for (ItemsEntity item : tabData) {
            if (item != null & item.itemType == type) {
                itemsEntity = item;
                break;
            }
        }
        if (itemsEntity == null) {
            return;
        }
        switch (tabId) {
            case R.id.tab_find:
                tabFindCtv.setText(itemsEntity.getItemTitle());
                tabFindCtv.setCompoundDrawablesWithIntrinsicBounds(0, itemsEntity.getItemIconRes(), 0, 0);
                break;
            case R.id.tab_mine:
                tabMineCtv.setText(itemsEntity.getItemTitle());
                tabMineCtv.setCompoundDrawablesWithIntrinsicBounds(0, itemsEntity.getItemIconRes(), 0, 0);
                break;
            default:
                break;
        }
    }

    @OnLongClick({
            R.id.tab_find,
            R.id.tab_mine})
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tab_find:
            case R.id.tab_mine:
                showTabMenu(v);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 获取展示的动态菜单数据
     *
     * @return
     */
    private List<ItemsEntity> getShowTabMenuData() {
        int mineFragmentType = getFragmentType(R.id.tab_mine);
        int findFragmentType = getFragmentType(R.id.tab_find);
        List<ItemsEntity> menus = new ArrayList<>();
        for (ItemsEntity itemsEntity : tabChangeableData) {
            if (itemsEntity == null) {
                continue;
            }
            if (itemsEntity.itemType != mineFragmentType
                    && itemsEntity.itemType != findFragmentType) {
                menus.add(itemsEntity);
            }
        }
        return menus;
    }

    /**
     * 展示发现页面切换菜单
     *
     * @param target
     */
    private void showTabMenu(final View target) {
        if (target == null) return;
        List<ItemsEntity> showTabMenuData = getShowTabMenuData();
        if (showTabMenuData.isEmpty()) return;
        new ListActionItemPop(getContext(), showTabMenuData)
                .withOnItemClick(new BaseListActionItemPop.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseListActionItemPop listActionItemPop, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        listActionItemPop.dismiss();
                        Object item = adapter.getItem(position);
                        if (item instanceof ItemsEntityImp) {
                            ItemsEntityImp itemsEntityImp = (ItemsEntityImp) item;
                            int type = convert2ChildFragmentType(itemsEntityImp.getItemType());
                            //保存
                            saveChangedTab(target.getId(), type);
                            //设置tab信息
                            setTabInfo(target.getId(), type);
                            //选中该tab
                            checkedTab(target.getId(), type);
                        }
                    }
                }).showUpCenter(target, DensityUtil.dip2px(getContext(), 5));
    }

    /**
     * 选择tab
     *
     * @param id
     * @param type
     */
    private void checkedTab(@IdRes int id, @ChildFragmentType int type) {
        switch (id) {
            case R.id.tab_news:
                tabNews.setChecked(true);
                tabTask.setChecked(false);
                tabFind.setChecked(false);
                tabMine.setChecked(false);
                break;
            case R.id.tab_task:
                tabNews.setChecked(false);
                tabTask.setChecked(true);
                tabFind.setChecked(false);
                tabMine.setChecked(false);
                break;
            case R.id.tab_find:
                tabNews.setChecked(false);
                tabTask.setChecked(false);
                tabFind.setChecked(true);
                tabMine.setChecked(false);
                break;
            case R.id.tab_mine:
                tabNews.setChecked(false);
                tabTask.setChecked(false);
                tabFind.setChecked(false);
                tabMine.setChecked(true);
                break;
            default:
                break;
        }
        checkedFragment(type);
    }


    public void checkedFragment(@ChildFragmentType int type) {
        currentFragment = addOrShowFragment(getTabFragment(type), currentFragment, R.id.main_fl_content);
        currentFragment.setUserVisibleHint(true);
    }

    /**
     * tab设置自定义事件
     */
    private void mobClickAgent() {
        if (currentFragment instanceof TabProjectFragment) {
            MobclickAgent.onEvent(this, UMMobClickAgent.main_project_tab_click_id);
        } else if (currentFragment instanceof TabTimingFragment) {
            MobclickAgent.onEvent(this, UMMobClickAgent.main_timer_tab_click_id);
        } else if (currentFragment instanceof TabCustomerFragment) {
            MobclickAgent.onEvent(this, UMMobClickAgent.main_client_tab_click_id);
        } else if (currentFragment instanceof TabSearchFragment) {
            MobclickAgent.onEvent(this, UMMobClickAgent.main_search_tab_click_id);
        } else if (currentFragment instanceof TabMineFragment) {
            MobclickAgent.onEvent(this, UMMobClickAgent.main_mine_tab_click_id);
        }
    }

    @OnClick({R.id.tab_timing,
            R.id.tab_news,
            R.id.tab_task,
            R.id.tab_find,
            R.id.tab_mine})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_news:
                MobclickAgent.onEvent(this, UMMobClickAgent.main_chat_tab_click_id);
                checkedTab(R.id.tab_news, TYPE_FRAGMENT_NEWS);
                break;
            case R.id.tab_task:
                MobclickAgent.onEvent(this, UMMobClickAgent.main_task_tab_click_id);
                checkedTab(R.id.tab_task, TYPE_FRAGMENT_TASK);
                break;
            case R.id.tab_find:
                checkedTab(R.id.tab_find, getFragmentType(R.id.tab_find));
                mobClickAgent();
                break;
            case R.id.tab_mine:
                showGuideView();
//                checkedTab(R.id.tab_mine, getFragmentType(R.id.tab_mine));
//                mobClickAgent();
                break;
            case R.id.tab_timing:
                dismissOverTimingRemindDialogFragment(true);
                if (TimerManager.getInstance().hasTimer()) {
                    showTimingDialogFragment();
                } else {
                    MobclickAgent.onEvent(getContext(), UMMobClickAgent.start_timer_click_id);
                    TimerManager.getInstance().addTimer(new TimeEntity.ItemEntity(),
                            new Callback<TimeEntity.ItemEntity>() {
                                @Override
                                public void onResponse(Call<TimeEntity.ItemEntity> call, Response<TimeEntity.ItemEntity> response) {
                                    if (TimerManager.getInstance().hasTimer()) {
                                        showTimingDialogFragment();
                                    }
                                }

                                @Override
                                public void onFailure(Call<TimeEntity.ItemEntity> call, Throwable throwable) {

                                }
                            });
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    /**
     * 获取对应fragment
     *
     * @param type
     * @return
     */
    private Fragment getTabFragment(@ChildFragmentType int type) {
        Fragment fragment = fragmentSparseArray.get(type);
        if (fragment == null) {
            switch (type) {
                case TYPE_FRAGMENT_NEWS:
                    fragment = TabNewsFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_TASK:
                    fragment = TabTaskFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_PROJECT:
                    fragment = TabProjectFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_TIMING:
                    fragment = TabTimingFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_CUSTOMER:
                    fragment = TabCustomerFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_SEARCH:
                    fragment = TabSearchFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_MINE:
                    fragment = TabMineFragment.newInstance();
                    break;
                case TYPE_FRAGMENT_DOCUMENTS:
                    fragment = TabDocumentsFragment.newInstance();
                    break;
                default:
                    break;
            }
        }
        putTabFragment(type, fragment);
        return fragment;
    }

    /**
     * 存放对应fragment
     *
     * @param checkedId
     * @param fragment
     */
    private void putTabFragment(@IdRes int checkedId, Fragment fragment) {
        fragmentSparseArray.put(checkedId, fragment);
    }

    /**
     * 刷新登陆的token
     */
    protected final void refreshToken() {
        final AlphaUserInfo loginUserInfo = getLoginUserInfo();
        if (loginUserInfo == null) return;
        callEnqueue(
                getApi().refreshToken(loginUserInfo.getRefreshToken()),
                new SimpleCallBack<AlphaUserInfo>() {
                    @Override
                    public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                        if (response.body().result != null) {
                            AlphaClient.setToken(response.body().result.getToken());

                            //重新附值两个最新的token
                            loginUserInfo.setToken(response.body().result.getToken());
                            loginUserInfo.setRefreshToken(response.body().result.getRefreshToken());

                            //保存
                            saveLoginUserInfo(loginUserInfo);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
    }


    /**
     * 享聊未读消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnReadEvent(UnReadEvent event) {
        if (event == null) {
            return;
        }
        int unReadNum = event.unReadCount;
        updateBadge(getTabNewsBadge(), unReadNum);
    }


    /**
     * 获取本地唯一id
     *
     * @return
     */
    private String getlocalUniqueId() {
        AlphaUserInfo loginUserInfo = LoginInfoUtils.getLoginUserInfo();
        if (loginUserInfo != null) {
            return loginUserInfo.localUniqueId;
        }
        return null;
    }

    /**
     * //每分钟5次有效 避免死循环
     *
     * @return
     */
    private boolean isInterceptServerTimingEvent() {
        long currSeconde = DateUtils.millis() / TimeUnit.MINUTES.toMillis(1);
        Integer eventTimes = serverTimingSyncTimesArray.get(currSeconde);
        if (eventTimes != null && eventTimes.intValue() > 5) {
            return true;
        }
        serverTimingSyncTimesArray.put(currSeconde, eventTimes != null ? eventTimes.intValue() + 1 : 1);
        return false;
    }

    /**
     * 网络计时同步更新通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerTimingEvent(final ServerTimingEvent event) {
        if (event == null) {
            return;
        }
        if (TextUtils.equals(event.clientId, getlocalUniqueId())) {
            return;
        }
        if (isInterceptServerTimingEvent()) {
            return;
        }

        if (event.isSyncObject()) {
            if (event.isSyncTimingType()) {
                if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_START)) {
                    TimerManager.getInstance().resumeTimer(event);
                } else if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_DELETE)) {
                    if (TimerManager.getInstance().isTimer(event.pkId)) {
                        TimerManager.getInstance().clearTimer();
                    }
                } else if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_EDIT)) {
                    if (TimerManager.getInstance().isTimer(event.pkId)) {
                        //已经完成
                        if (event.state == 1) {
                            TimerManager.getInstance().clearTimer();
                        } else {
                            TimerManager.getInstance().resumeTimer(event);
                        }
                    } else {
                        //计时中...
                        if (event.state == 0) {
                            TimerManager.getInstance().resumeTimer(event);
                        }
                    }
                }
            } else if (event.isBubbleSync()) {
                if (event.scene != null) {
                    switch (event.scene) {
                        //计时超长的通知（这个通知每2小时通知一次）
                        case ServerTimingEvent.TIMING_SYNC_TOO_LONG:
                            //使用handler来发送超时提醒，为了防止一次性收到多个超时提醒，导致弹出多个提示窗。
                            mHandler.addOverTimingRemind(event.content);
                            break;
                        //关闭该计时任务超长提醒泡泡的通知
                        case ServerTimingEvent.TIMING_SYNC_CLOSE_BUBBLE:
                            dismissOverTimingRemindDialogFragment(true);
                            TimerManager.getInstance().setOverBubbleRemind(false);
                            break;
                        //该计时任务不再提醒泡泡的通知
                        case ServerTimingEvent.TIMING_SYNC_NO_REMIND:
                            dismissOverTimingRemindDialogFragment(true);
                            TimerManager.getInstance().setOverTimingRemind(false);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }


    public Badge tabNewsBadge;

    private Badge getTabNewsBadge() {
        if (tabNewsBadge == null) {
            tabNewsBadge = new QBadgeView(getContext())
                    .bindTarget(rgMainTab);
            tabNewsBadge.setBadgeGravity(Gravity.START | Gravity.TOP);
            tabNewsBadge.setGravityOffset(DensityUtil.px2dip(getContext(), 0.5f * tabNews.getWidth()), 0, true);
            tabNewsBadge.setBadgeTextSize(10, true);
            tabNewsBadge.stroke(Color.WHITE, 1, true);
            tabNewsBadge.setBadgePadding(3, true);
        }
        return tabNewsBadge;
    }

    /**
     * 更新消息提醒
     *
     * @param badge
     * @param num
     */
    private void updateBadge(Badge badge, int num) {
        if (badge != null && num >= 0) {
            ShortcutBadger.applyCount(getBaseContext(), num);
            if (num > 99) {
                //显示99+
                badge.setBadgeText("99+");
            } else {
                badge.setBadgeNumber(num);
            }
        }
    }

    /**
     * 接口方式同步计时
     */
    private void timerQuerySync() {
        TimerManager.getInstance().timerQuerySync();
    }


    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    /**
     * 显示或隐藏 持续计时过久时的提醒覆层
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOverTimingRemindEvent(OverTimingRemindEvent event) {
        if (event == null) return;

        switch (event.action) {
            case OverTimingRemindEvent.ACTION_TIMING_REMIND_NO_REMIND:
                dismissOverTimingRemindDialogFragment(true);
                break;
            case OverTimingRemindEvent.ACTION_SYNC_BUBBLE_CLOSE_TO_SERVER:
                dismissOverTimingRemindDialogFragment(false);
                TimerManager.getInstance().setOverTimingRemindClose(TimerManager.OVER_TIME_REMIND_BUBBLE_OFF);
                break;
            default:
                break;
        }
    }

    /**
     * 本地计时状态通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_ADD:
                tabTimingIcon.setImageResource(R.mipmap.ic_tab_timing);
                tabTimingIcon.clearAnimation();
                timingAnim = getTimingAnimation(0f, 359f);
                tabTimingIcon.startAnimation(timingAnim);
                //如果添加的计时大于2个小时，弹出提示
                outTwoHourShowRemind();
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                if (timingAnim == null) {
                    tabTimingIcon.setImageResource(R.mipmap.ic_tab_timing);
                    tabTimingIcon.clearAnimation();
                    float fromDegrees = event.timingSecond % 60 * 6;
                    float toDegrees = 359f + fromDegrees;
                    timingAnim = getTimingAnimation(fromDegrees, toDegrees);
                    tabTimingIcon.startAnimation(timingAnim);
                }
                tabTimingTv.setText(toTime(event.timingSecond));
                break;
            case TimingEvent.TIMING_STOP:
                dismissOverTimingRemindDialogFragment(true);
                dismissTimingDialogFragment();
                tabTimingTv.setText("开始计时");
                tabTimingIcon.setImageResource(R.mipmap.ic_time_start);
                tabTimingIcon.clearAnimation();
                timingAnim = null;
                break;
            //同步计时，如果计时大于两个小时，弹出提示
            case TimingEvent.TIMING_SYNC_SUCCESS:
                outTwoHourShowRemind();
                break;
            default:
                break;
        }
    }

    /**
     * 判断是否超过2小时，是否提醒。
     */
    private void outTwoHourShowRemind() {
        if (TimeUnit.SECONDS.toHours(TimerManager.getInstance().getTimingSeconds()) >= 2) {
            if (TimerManager.getInstance().isBubbleRemind() && TimerManager.getInstance().isOverTimingRemind()) {
                mHandler.addOverTimingRemind(getOverTimingRemindContent(TimerManager.getInstance().getTimingSeconds()));
            }
        } else {
            dismissTimingDialogFragment();
        }
    }

    /**
     * 获取旋转动画
     *
     * @param fromDegrees
     * @param toDegrees
     * @return
     */
    private RotateAnimation getTimingAnimation(float fromDegrees, float toDegrees) {
        RotateAnimation anim = new RotateAnimation(fromDegrees,
                toDegrees,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(60 * 1000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(-1);
        return anim;
    }

    /**
     * 时间格式化 秒--->小时分钟
     *
     * @param times
     * @return
     */
    public String toTime(long times) {
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 判断>=2的奇数小时整点
     *
     * @param times 单位Second
     * @return
     */
    private boolean judgeEvenTime(long times) {
        long hour = TimeUnit.SECONDS.toHours(times);
        if ((hour >= 2) && (times % 3600 == 0) && (hour % 2 == 1)) {
            //如果可以弹出泡泡，并且没有点击不再提醒。
            if (TimerManager.getInstance().isOverTimingRemind() && TimerManager.getInstance().isBubbleRemind()) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 获取权限
     */
    private void getPermission() {
        //联系人查看的权限
        callEnqueue(
                getApi().permissionQuery(getLoginUserId(), "CON"),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null) {
                            setCustomerPermission(response.body().result.booleanValue());
                            int findFragmentType = getFragmentType(R.id.tab_find);
                            int mineFragmentType = getFragmentType(R.id.tab_mine);
                            if (findFragmentType == TYPE_FRAGMENT_CUSTOMER
                                    && !response.body().result.booleanValue()) {
                                //没有权限
                                //设置tab信息
                                if (mineFragmentType != TYPE_FRAGMENT_TIMING) {
                                    saveChangedTab(R.id.tab_find, TYPE_FRAGMENT_TIMING);
                                    setTabInfo(R.id.tab_find, TYPE_FRAGMENT_TIMING);
                                } else if (mineFragmentType != TYPE_FRAGMENT_SEARCH) {
                                    saveChangedTab(R.id.tab_find, TYPE_FRAGMENT_SEARCH);
                                    setTabInfo(R.id.tab_find, TYPE_FRAGMENT_SEARCH);
                                } else {
                                    saveChangedTab(R.id.tab_find, TYPE_FRAGMENT_MINE);
                                    setTabInfo(R.id.tab_find, TYPE_FRAGMENT_MINE);
                                }
                                onClick(tabFind);
                            }

                            if (mineFragmentType == TYPE_FRAGMENT_CUSTOMER
                                    && !response.body().result.booleanValue()) {
                                //没有权限
                                //设置tab信息
                                if (findFragmentType != TYPE_FRAGMENT_MINE) {
                                    saveChangedTab(R.id.tab_mine, TYPE_FRAGMENT_MINE);
                                    setTabInfo(R.id.tab_mine, TYPE_FRAGMENT_MINE);
                                } else if (findFragmentType != TYPE_FRAGMENT_TIMING) {
                                    saveChangedTab(R.id.tab_mine, TYPE_FRAGMENT_TIMING);
                                    setTabInfo(R.id.tab_mine, TYPE_FRAGMENT_TIMING);
                                } else {
                                    saveChangedTab(R.id.tab_mine, TYPE_FRAGMENT_SEARCH);
                                    setTabInfo(R.id.tab_mine, TYPE_FRAGMENT_SEARCH);
                                }
                                onClick(tabMine);
                            }
                            initTabChangeableData();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
                        super.onFailure(call, t);
                        initTabChangeableData();
                    }
                });

        //项目查看的权限
        callEnqueue(
                getApi().permissionQuery(getLoginUserId(), "MAT"),
                new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null) {
                            setProjectPermission(response.body().result.booleanValue());
                            int findFragmentType = getFragmentType(R.id.tab_find);
                            int mineFragmentType = getFragmentType(R.id.tab_mine);
                            if (findFragmentType == TYPE_FRAGMENT_PROJECT
                                    && !response.body().result.booleanValue()) {
                                //没有权限
                                //设置tab信息
                                if (mineFragmentType != TYPE_FRAGMENT_TIMING) {
                                    saveChangedTab(R.id.tab_find, TYPE_FRAGMENT_TIMING);
                                    setTabInfo(R.id.tab_find, TYPE_FRAGMENT_TIMING);
                                } else if (mineFragmentType != TYPE_FRAGMENT_SEARCH) {
                                    saveChangedTab(R.id.tab_find, TYPE_FRAGMENT_SEARCH);
                                    setTabInfo(R.id.tab_find, TYPE_FRAGMENT_SEARCH);
                                } else {
                                    saveChangedTab(R.id.tab_find, TYPE_FRAGMENT_MINE);
                                    setTabInfo(R.id.tab_find, TYPE_FRAGMENT_MINE);
                                }

                                onClick(tabFind);
                            }

                            if (mineFragmentType == TYPE_FRAGMENT_PROJECT
                                    && !response.body().result.booleanValue()) {
                                //没有权限
                                //设置tab信息
                                if (findFragmentType != TYPE_FRAGMENT_MINE) {
                                    saveChangedTab(R.id.tab_mine, TYPE_FRAGMENT_MINE);
                                    setTabInfo(R.id.tab_mine, TYPE_FRAGMENT_MINE);
                                } else if (findFragmentType != TYPE_FRAGMENT_TIMING) {
                                    saveChangedTab(R.id.tab_mine, TYPE_FRAGMENT_TIMING);
                                    setTabInfo(R.id.tab_mine, TYPE_FRAGMENT_TIMING);
                                } else {
                                    saveChangedTab(R.id.tab_mine, TYPE_FRAGMENT_SEARCH);
                                    setTabInfo(R.id.tab_mine, TYPE_FRAGMENT_SEARCH);
                                }

                                onClick(tabMine);
                            }
                            initTabChangeableData();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
                        super.onFailure(call, t);
                        initTabChangeableData();
                    }
                });
    }


    @Override
    protected void onDestroy() {
        DaemonService.start(this);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    /**
     * 显示 计时的覆层
     */
    private void showTimingDialogFragment() {
        if (isDestroyOrFinishing()) return;
        TimeEntity.ItemEntity timer = TimerManager.getInstance().getTimer();
        if (timer == null) return;
        String tag = TimingNoticeDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        //show方法源码是commit提交，会产生：Can not perform this action after onSaveInstanceState 异常
//      TimingNoticeDialogFragment.newInstance(timer).show(mFragTransaction, tag);
        mFragTransaction.add(TimingNoticeDialogFragment.newInstance(timer), tag);
        mFragTransaction.commitAllowingStateLoss();
    }

    /**
     * 获取提醒文本的文字描述
     *
     * @param seconds
     */
    private String getOverTimingRemindContent(long seconds) {
        return getString(R.string.timer_over_timing_remind_text, TimeUnit.SECONDS.toHours(seconds));
    }

    /**
     * 显示 持续计时过久时的提醒覆层
     */
    public void showOverTimingRemindDialogFragment(String content) {
        if (isDestroyOrFinishing()) {//如果界面即将销毁，那么就不执行下去
            return;
        }
        //如果没有显示任务列表新任务的下一步按钮
        if (currentFragment instanceof TabTaskFragment) {
            TabTaskFragment tabTaskFragment = (TabTaskFragment) currentFragment;
            if (tabTaskFragment.isShowingNextTaskView()) {
                return;
            }
        }
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        String tag = OverTimingRemindDialogFragment.class.getSimpleName();
        OverTimingRemindDialogFragment fragment = (OverTimingRemindDialogFragment)
                getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            fragment.updateTimeText(content);
        } else {
            fragment = OverTimingRemindDialogFragment.newInstance(content);
            fragment.show(mFragTransaction, tag);
        }
        dismissTimingDialogFragment();
        tabTimingIcon.setImageResource(R.mipmap.timing_fill);
    }

    /**
     * 界面移除 持续计时过久时的提醒覆层
     */
    public void dismissOverTimingRemindDialogFragment(boolean isSyncServer) {
        if (isDestroyOrFinishing()) {
            return;
        }
        String tag = OverTimingRemindDialogFragment.class.getSimpleName();
        OverTimingRemindDialogFragment fragment = (OverTimingRemindDialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            fragment.dismiss(isSyncServer);
        }
        if (TimerManager.getInstance().hasTimer()) {
            tabTimingIcon.setImageResource(R.mipmap.ic_tab_timing);
        } else {
            tabTimingIcon.setImageResource(R.mipmap.ic_time_start);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 关闭 计时的覆层
     */
    private void dismissTimingDialogFragment() {
        String tag = TimingNoticeDialogFragment.class.getSimpleName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).dismissAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IntentWrapper.onBackPressed(this);
            try {
                moveTaskToBack(false);
            } catch (Exception e) {
                bugSync("返回键模拟HOME出错", e);
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Guide guide;

    private void showGuideView() {
        if (StringUtils.equalsIgnoreCase(BuildConfig.VERSION_NAME, Const.GUIDE_SHOW_TIME_VERSION_NAME, false)) {

        }
        final GuideBuilder builder = new GuideBuilder();
        builder.setTargetViewId(R.id.tab_mine)
                .setAlpha(150)
                .setHighTargetGraphStyle(Component.CIRCLE)
                .setFullingColorId(R.color.darkGray)
                .setOverlayTarget(false)
                .setHighTargetPadding(-10)
                .setOutsideTouchable(false);
        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {

            }

            @Override
            public void onDismiss() {

            }
        });
        SimpleComponent component = new SimpleComponent();
        component.setOnViewClick(new SimpleComponent.OnViewClick() {
            @Override
            public void onClick(View view) {
                if (guide != null) {
                    guide.dismiss();
                }
            }
        });
        builder.addComponent(component);
        guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(this);

    }
}
