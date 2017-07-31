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
import android.util.SparseIntArray;
import android.util.SparseLongArray;
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
import com.icourt.alpha.entity.event.ServerTimingEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.entity.event.UnReadEvent;
import com.icourt.alpha.fragment.TabCustomerFragment;
import com.icourt.alpha.fragment.TabMineFragment;
import com.icourt.alpha.fragment.TabNewsFragment;
import com.icourt.alpha.fragment.TabProjectFragment;
import com.icourt.alpha.fragment.TabSearchFragment;
import com.icourt.alpha.fragment.TabTaskFragment;
import com.icourt.alpha.fragment.TabTimingFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingNoticeDialogFragment;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.service.DaemonService;
import com.icourt.alpha.utils.AppManager;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SimpleViewGestureListener;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.CheckableLayout;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Description  主页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class MainActivity extends BaseAppUpdateActivity
        implements OnFragmentCallBackListener {
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

    @IntDef({
            TYPE_FRAGMENT_NEWS,
            TYPE_FRAGMENT_TASK,
            TYPE_FRAGMENT_PROJECT,
            TYPE_FRAGMENT_MINE,
            TYPE_FRAGMENT_TIMING,
            TYPE_FRAGMENT_CUSTOMER,
            TYPE_FRAGMENT_SEARCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChildFragmentType {

    }


    private final List<ItemsEntity> tabData = Arrays.asList(
            new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.drawable.tab_project),
            new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
            new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.drawable.tab_timer),
            new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.drawable.tab_customer),
            new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search));

    //可改变的tab
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
            }
            return super.onDoubleTap(v, e);
        }
    };
    final ArrayMap<Long, Integer> serverTimingSyncTimesArray = new ArrayMap<>();

    class MyHandler extends Handler {
        public static final int TYPE_TOKEN_REFRESH = 101;//token刷新
        public static final int TYPE_CHECK_APP_UPDATE = 102;//检查更新
        public static final int TYPE_CHECK_TIMING_UPDATE = 103;//检查计时

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
            }
        }


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
    }

    /**
     * 点击通知栏跳转到对应的聊天页面
     */
    private void gotoChatByNotifaction() {
        IMMessage imMessage = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (imMessage != null) {
            int totalUnReadCount = NIMClient.getService(MsgService.class).getTotalUnreadCount();
            IMMessageCustomBody customBody = GlobalMessageObserver.getIMBody(imMessage);
            if (customBody == null) return;
            if (customBody.imMessage.getMsgType() == MsgTypeEnum.custom) {
                AlphaSpecialHelperActivity.launch(this, customBody.imMessage.getSessionId(), totalUnReadCount);
            } else {
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
                    new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.drawable.tab_timer),
                    new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search)));
        } else if (hasCustomerPermission()) {
            tabChangeableData.addAll(Arrays.asList(
                    new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.drawable.tab_customer),
                    new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
                    new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.drawable.tab_timer),
                    new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search)));
        } else {
            tabChangeableData.addAll(Arrays.asList(
                    new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.drawable.tab_timer),
                    new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search),
                    new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine)));
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
        } else {
            /*try {
                IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行");
            } catch (Throwable e) {
                e.printStackTrace();
            }*/
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
                return convert2ChildFragmentType(SpUtils.getInstance().getIntData(KEY_FIND_FRAGMENT, TYPE_FRAGMENT_TIMING));
            case R.id.tab_mine:
                return convert2ChildFragmentType(SpUtils.getInstance().getIntData(KEY_MINE_FRAGMENT, TYPE_FRAGMENT_MINE));
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
        if (itemsEntity == null) return;
        switch (tabId) {
            case R.id.tab_find:
                tabFindCtv.setText(itemsEntity.getItemTitle());
                tabFindCtv.setCompoundDrawablesWithIntrinsicBounds(0, itemsEntity.getItemIconRes(), 0, 0);
                break;
            case R.id.tab_mine:
                tabMineCtv.setText(itemsEntity.getItemTitle());
                tabMineCtv.setCompoundDrawablesWithIntrinsicBounds(0, itemsEntity.getItemIconRes(), 0, 0);
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
            if (itemsEntity == null) continue;
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
        }
        checkedFragment(type);
    }


    public void checkedFragment(@ChildFragmentType int type) {
        currentFragment = addOrShowFragment(getTabFragment(type), currentFragment, R.id.main_fl_content);
        currentFragment.setUserVisibleHint(true);
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
                checkedTab(R.id.tab_news, TYPE_FRAGMENT_NEWS);
                break;
            case R.id.tab_task:
                checkedTab(R.id.tab_task, TYPE_FRAGMENT_TASK);
                break;
            case R.id.tab_find:
                checkedTab(R.id.tab_find, getFragmentType(R.id.tab_find));
                break;
            case R.id.tab_mine:
                checkedTab(R.id.tab_mine, getFragmentType(R.id.tab_mine));
                break;
            case R.id.tab_timing:
                if (TimerManager.getInstance().hasTimer()) {
                    showTimingDialogFragment();
                } else {
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
        getApi().refreshToken(loginUserInfo.getRefreshToken())
                .enqueue(new SimpleCallBack<AlphaUserInfo>() {
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
        if (event == null) return;
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
    public void onServerTimingEvent(ServerTimingEvent event) {
        if (event == null) return;
        if (TextUtils.equals(event.clientId, getlocalUniqueId())) return;
        if (isInterceptServerTimingEvent()) return;

        if (event.isSyncObject() && event.isSyncTimingType()) {
            if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_START)) {
                TimerManager.getInstance().resumeTimer(event);
            } else if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_DELETE)) {
                if (TimerManager.getInstance().isTimer(event.pkId)) {
                    TimerManager.getInstance().clearTimer();
                }
            } else if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_EDIT)) {
                if (TimerManager.getInstance().isTimer(event.pkId)) {
                    if (event.state == 1) {//已经完成
                        TimerManager.getInstance().clearTimer();
                    } else {
                        TimerManager.getInstance().resumeTimer(event);
                    }
                } else {
                    if (event.state == 0) {//计时中...
                        TimerManager.getInstance().resumeTimer(event);
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
                dismissTimingDialogFragment();
                tabTimingTv.setText("开始计时");
                tabTimingIcon.setImageResource(R.mipmap.ic_time_start);
                tabTimingIcon.clearAnimation();
                timingAnim = null;
                break;
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
     * 获取权限
     */
    private void getPermission() {
        //联系人查看的权限
        getApi().permissionQuery(getLoginUserId(), "CON")
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null) {
                            //showToast("客户权限：" + response.body().result.booleanValue());
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
        getApi().permissionQuery(getLoginUserId(), "MAT")
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null) {
                            //showToast("项目权限：" + response.body().result.booleanValue());
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

       /* if (isUserLogin()) {
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
        }*/

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
        TimingNoticeDialogFragment.newInstance(timer)
                .show(mFragTransaction, tag);
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
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
