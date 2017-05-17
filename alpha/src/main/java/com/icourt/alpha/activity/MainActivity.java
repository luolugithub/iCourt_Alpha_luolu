package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.ItemsEntityImp;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.fragment.TabFindFragment;
import com.icourt.alpha.fragment.TabMineFragment;
import com.icourt.alpha.fragment.TabNewsFragment;
import com.icourt.alpha.fragment.TabTaskFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingNoticeDialogFragment;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SimpleViewGestureListener;
import com.icourt.alpha.view.CheckableLayout;
import com.icourt.alpha.widget.manager.TimerManager;
import com.icourt.alpha.widget.popupwindow.BaseListActionItemPop;
import com.icourt.alpha.widget.popupwindow.ListActionItemPop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
public class MainActivity extends BaseActivity
        implements OnFragmentCallBackListener {


    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    @BindView(R.id.tab_news)
    CheckableLayout tabNews;
    @BindView(R.id.tab_task)
    CheckableLayout tabTask;
    @BindView(R.id.tab_voice)
    TextView tabVoice;
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

        /**
         * 刷新登陆token
         */
        public void addTokenRefreshTask() {
            this.removeMessages(TYPE_TOKEN_REFRESH);
            this.sendEmptyMessageDelayed(TYPE_TOKEN_REFRESH, 2_000);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TYPE_TOKEN_REFRESH:
                    refreshToken();
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
        getRobos();
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        loginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        new SimpleViewGestureListener(tabNews, onSimpleViewGestureListener);
        initTabFind();
        tabNews.setChecked(true);
        currentFragment = addOrShowFragment(getTabFragment(R.id.tab_news), currentFragment, R.id.main_fl_content);
        resumeTimer();
    }


    /**
     * 初始化发现tab
     */
    private void initTabFind() {
        switch (TabFindFragment.getLastChildFragmentType()) {
            case TabFindFragment.TYPE_FRAGMENT_PROJECT:
                tabFindCtv.setText("项目");
                break;
            case TabFindFragment.TYPE_FRAGMENT_TIMING:
                tabFindCtv.setText("计时");
                break;
            case TabFindFragment.TYPE_FRAGMENT_CUSTOMER:
                tabFindCtv.setText("客户");
                break;
            case TabFindFragment.TYPE_FRAGMENT_SEARCH:
                tabFindCtv.setText("搜索");
                break;
        }
    }

    @OnLongClick({R.id.tab_find})
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tab_find: {
                if (!tabFind.isChecked()) return false;
                showTabFindMenu(v);
            }
            break;
        }
        return true;
    }

    /**
     * 展示发现页面切换菜单
     *
     * @param v
     */
    private void showTabFindMenu(View v) {
        if (currentFragment instanceof INotifyFragment && currentFragment instanceof TabFindFragment) {
            TabFindFragment tabFindFragment = (TabFindFragment) currentFragment;
            new ListActionItemPop(getContext(), TabFindFragment.generateMenuData(tabFindFragment)).withOnItemClick(new BaseListActionItemPop.OnItemClickListener() {
                @Override
                public void onItemClick(BaseListActionItemPop listActionItemPop, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                    listActionItemPop.dismiss();
                    Object item = adapter.getItem(position);
                    if (item instanceof ItemsEntityImp) {
                        ItemsEntityImp itemsEntityImp = (ItemsEntityImp) item;
                        Bundle bundle = new Bundle();
                        switch (itemsEntityImp.getItemType()) {
                            case TabFindFragment.TYPE_FRAGMENT_PROJECT:
                                bundle.putInt(TabFindFragment.KEY_TYPE_FRAGMENT, TabFindFragment.TYPE_FRAGMENT_PROJECT);
                                break;
                            case TabFindFragment.TYPE_FRAGMENT_CUSTOMER:
                                bundle.putInt(TabFindFragment.KEY_TYPE_FRAGMENT, TabFindFragment.TYPE_FRAGMENT_CUSTOMER);
                                break;
                            case TabFindFragment.TYPE_FRAGMENT_SEARCH:
                                bundle.putInt(TabFindFragment.KEY_TYPE_FRAGMENT, TabFindFragment.TYPE_FRAGMENT_SEARCH);
                                break;
                            case TabFindFragment.TYPE_FRAGMENT_TIMING:
                                bundle.putInt(TabFindFragment.KEY_TYPE_FRAGMENT, TabFindFragment.TYPE_FRAGMENT_TIMING);
                                break;
                        }
                        ((INotifyFragment) currentFragment).notifyFragmentUpdate(currentFragment, 0, bundle);
                        tabFindCtv.setText(itemsEntityImp.getItemTitle());
                    }
                }
            }).showUpCenter(v, DensityUtil.dip2px(getContext(), 5));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.addTokenRefreshTask();
    }


    public void checkedFragment(@IdRes int checkedId) {
        currentFragment = addOrShowFragment(getTabFragment(checkedId), currentFragment, R.id.main_fl_content);
    }


    private void resumeTimer() {
        TimerManager.getInstance().resumeTimer();
    }

    @OnClick({R.id.tab_voice,
            R.id.tab_news,
            R.id.tab_task,
            R.id.tab_find,
            R.id.tab_mine})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_news:
                tabNews.setChecked(true);
                tabTask.setChecked(false);
                tabFind.setChecked(false);
                tabMine.setChecked(false);
                checkedFragment(v.getId());
                break;
            case R.id.tab_task:
                tabNews.setChecked(false);
                tabTask.setChecked(true);
                tabFind.setChecked(false);
                tabMine.setChecked(false);
                checkedFragment(v.getId());
                break;
            case R.id.tab_find:
                tabNews.setChecked(false);
                tabTask.setChecked(false);
                tabFind.setChecked(true);
                tabMine.setChecked(false);
                checkedFragment(v.getId());
                break;
            case R.id.tab_mine:
                tabNews.setChecked(false);
                tabTask.setChecked(false);
                tabFind.setChecked(false);
                tabMine.setChecked(true);
                checkedFragment(v.getId());
                break;
            case R.id.tab_voice:
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
     * @param checkedId
     * @return
     */
    private Fragment getTabFragment(@IdRes int checkedId) {
        Fragment fragment = fragmentSparseArray.get(checkedId);
        if (fragment == null) {
            switch (checkedId) {
                case R.id.tab_news:
                    putTabFragment(checkedId, TabNewsFragment.newInstance());
                    break;
                case R.id.tab_task:
                    putTabFragment(checkedId, TabTaskFragment.newInstance());
                    break;
                case R.id.tab_find:
                    putTabFragment(checkedId, TabFindFragment.newInstance());
                    break;
                case R.id.tab_mine:
                    putTabFragment(checkedId, TabMineFragment.newInstance());
                    break;
            }
            return fragmentSparseArray.get(checkedId);
        } else {
            return fragment;
        }
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

    /**
     * 获取机器人
     */
    private void getRobos() {
        getApi().getRobos()
                .enqueue(new SimpleCallBack<List<GroupContactBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupContactBean>>> call, Response<ResEntity<List<GroupContactBean>>> response) {
                        if (response.body().result != null
                                && contactDbService != null) {
                            contactDbService.insertOrUpdateAsyn(new ArrayList<IConvertModel<ContactDbModel>>(response.body().result));
                        }
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        //super.defNotify(noticeStr);
                    }
                });
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


    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment == getTabFragment(R.id.tab_news)) {
            if (params != null) {
                updateBadge(getTabNewsBadge(), params.getInt("unReadNum"));
            }
        }
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
               /* RotateAnimation operatingAnim = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_nim);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);*/
                tabVoice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.rotate_timing, 0, 0);
                // tabVoice.startAnimation(operatingAnim);
                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                tabVoice.setText(toTime(event.timingSecond));
                break;
            case TimingEvent.TIMING_STOP:
                tabVoice.setText("开始计时");
                //tabVoice.clearAnimation();
                tabVoice.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_time_start, 0, 0);
                break;
        }
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
