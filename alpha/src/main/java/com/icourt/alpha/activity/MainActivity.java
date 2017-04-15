package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.fragment.TabFindFragment;
import com.icourt.alpha.fragment.TabMineFragment;
import com.icourt.alpha.fragment.TabNewsFragment;
import com.icourt.alpha.fragment.TabTaskFragment;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SimpleViewGestureListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.msg.MsgService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
        implements RadioGroup.OnCheckedChangeListener
        , OnFragmentCallBackListener {

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    @BindView(R.id.tab_news)
    RadioButton tabNews;
    @BindView(R.id.tab_task)
    RadioButton tabTask;
    @BindView(R.id.tab_voice)
    ImageButton tabVoice;
    @BindView(R.id.tab_find)
    RadioButton tabFind;
    @BindView(R.id.tab_mine)
    RadioButton tabMine;
    @BindView(R.id.rg_main_tab)
    RadioGroup rgMainTab;
    Fragment currentFragment;
    final SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();
    GestureDetector.SimpleOnGestureListener tabGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Fragment currFragment = currentFragment;
            if (currFragment instanceof OnTabDoubleClickListener) {
                ((OnTabDoubleClickListener) currFragment).onTabDoubleClick(currFragment, null, null);
            }
            return super.onDoubleTap(e);
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
        loginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        rgMainTab.setOnCheckedChangeListener(this);
        new SimpleViewGestureListener(tabNews, tabGestureListener);
        currentFragment = addOrShowFragment(getTabFragment(rgMainTab.getCheckedRadioButtonId()), currentFragment, R.id.main_fl_content);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHandler.addTokenRefreshTask();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        currentFragment = addOrShowFragment(getTabFragment(checkedId), currentFragment, R.id.main_fl_content);
    }

    @OnClick({R.id.tab_voice})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_voice:
                showTopSnackBar("语音....");
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
                            AlphaClient.getInstance().setOfficeId(response.body().result.getOfficeId());
                            AlphaClient.getInstance().setToken(response.body().result.getToken());
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

    /**
     * 获取 本地未读消息
     *
     * @return
     */
    private int getLocalUnReadNum() {
        if (NIMClient.getStatus() == StatusCode.LOGINED) {
            return NIMClient.getService(MsgService.class).getTotalUnreadCount();
        }
        return 0;
    }

    @Override
    public void OnFragmentCallBack(Fragment fragment, Bundle params) {
        if (fragment == getTabFragment(R.id.tab_news)) {
            if (params != null) {
                updateBadge(getTabNewsBadge(), params.getInt("unReadNum"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }
}
