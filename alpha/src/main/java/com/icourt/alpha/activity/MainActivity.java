package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.Gravity;
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
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.entity.bean.ItemsEntityImp;
import com.icourt.alpha.entity.bean.TimeEntity;
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
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SimpleViewGestureListener;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.view.CheckableLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.alpha.widget.popupwindow.BaseListActionItemPop;
import com.icourt.alpha.widget.popupwindow.ListActionItemPop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class MainActivity extends BaseAppUpdateActivity
        implements OnFragmentCallBackListener {
    public static String KEY_FIND_FRAGMENT = "type_TabFindFragment_fragment";
    public static String KEY_MINE_FRAGMENT = "type_TabMimeFragment_fragment";

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

    //可改变的tab
    private final List<ItemsEntity> tabChangeableData = Arrays.asList(
            new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.drawable.tab_project),
            new ItemsEntity("我的", TYPE_FRAGMENT_MINE, R.drawable.tab_mine),
            new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.drawable.tab_timer),
            new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.drawable.tab_customer),
            new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.drawable.tab_search));

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

    class MyHandler extends Handler {
        public static final int TYPE_TOKEN_REFRESH = 101;//token刷新
        public static final int TYPE_CHECK_APP_UPDATE = 102;//检查更新

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
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTimering();
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
     * 获取发现页面
     *
     * @return
     */
    private int getFragmentType(@IdRes int id) {
        switch (id) {
            case R.id.tab_find:
                return convert2ChildFragmentType(SpUtils.getInstance().getIntData(KEY_FIND_FRAGMENT, TYPE_FRAGMENT_PROJECT));
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
        for (ItemsEntity item : tabChangeableData) {
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
        new ListActionItemPop(getContext(), getShowTabMenuData())
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
                    TimerManager.getInstance().addTimer(new TimeEntity.ItemEntity());
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
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        if (loginUserInfo == null) return;
        getApi().refreshToken(loginUserInfo.getRefreshToken())
                .enqueue(new SimpleCallBack<AlphaUserInfo>() {
                    @Override
                    public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                        if (response.body().result != null) {
                            AlphaClient.setOfficeId(response.body().result.getOfficeId());
                            AlphaClient.setToken(response.body().result.getToken());
                            saveLoginUserInfo(response.body().result);
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnReadEvent(UnReadEvent event) {
        if (event == null) return;
        int unReadNum = event.unReadCount;
        updateBadge(getTabNewsBadge(), unReadNum);
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
                badge.setBadgeText("...");
            } else {
                badge.setBadgeNumber(num);
            }
        }
    }

    private void getTimering() {
        TimerManager.getInstance().timerQuerySync();
    }


    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState); //解决bug 崩溃后出现重影
    }

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

    public String toTime(long times) {
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        long second = times % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    private void showTimingDialogFragment() {
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
}
