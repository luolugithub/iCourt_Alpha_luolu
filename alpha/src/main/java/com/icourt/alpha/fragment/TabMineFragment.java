package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatMsgClassfyActivity;
import com.icourt.alpha.activity.ImagePagerActivity;
import com.icourt.alpha.activity.MyFinishTaskActivity;
import com.icourt.alpha.activity.SetingActivity;
import com.icourt.alpha.activity.UserInfoActivity;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupBean;
import com.icourt.alpha.entity.bean.UserDataEntity;
import com.icourt.alpha.entity.event.ServerTimingEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.transformations.BlurTransformation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabMineFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.photo_big_image)
    ImageView photoBigImage;
    @BindView(R.id.photo_image)
    ImageView photoImage;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.office_name_tv)
    TextView officeNameTv;
    @BindView(R.id.today_duraction_tv)
    TextView todayDuractionTv;
    @BindView(R.id.month_duraction_tv)
    TextView monthDuractionTv;
    @BindView(R.id.done_task_tv)
    TextView doneTaskTv;
    @BindView(R.id.my_center_timer_layout)
    LinearLayout myCenterTimerLayout;
    @BindView(R.id.my_center_collect_textview)
    TextView myCenterCollectTextview;
    @BindView(R.id.my_center_collect_layout)
    LinearLayout myCenterCollectLayout;
    @BindView(R.id.my_center_set_layout)
    LinearLayout myCenterSetLayout;
    @BindView(R.id.menu_test)
    TextView menuTest;
    @BindView(R.id.user_info_layout)
    LinearLayout userInfoLayout;
    @BindView(R.id.today_duraction_layout)
    LinearLayout todayDuractionLayout;
    @BindView(R.id.month_duraction_layout)
    LinearLayout monthDuractionLayout;
    @BindView(R.id.done_task_layout)
    LinearLayout doneTaskLayout;

    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_mine_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        getData(false);
        menuTest.setVisibility(BuildConfig.IS_DEBUG ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getData(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataToView(getLoginUserInfo());
    }

    /**
     * 设置数据
     *
     * @param alphaUserInfo
     */
    private void setDataToView(AlphaUserInfo alphaUserInfo) {
        if (userNameTv != null) {
            if (alphaUserInfo != null) {
                GlideUtils.loadUser(getContext(), alphaUserInfo.getPic(), photoImage);
                if (GlideUtils.canLoadImage(getContext())) {
                    Glide.with(getContext())
                            .load(alphaUserInfo.getPic())
                            .thumbnail(0.1f)
                            .bitmapTransform(new BlurTransformation(getContext(), 50))
                            .crossFade()
                            .into(photoBigImage);
                    userNameTv.setText(alphaUserInfo.getName());
                }
            }
        }
    }

    /**
     * 计时事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerEvent(TimingEvent event) {
        if (event == null) return;
        switch (event.action) {
            case TimingEvent.TIMING_ADD:

                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                break;
            case TimingEvent.TIMING_STOP:
                getMyDoneTask();
                break;
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerTimingEvent(ServerTimingEvent event) {
        if (event == null) return;
        if (TextUtils.equals(event.clientId, getlocalUniqueId())) return;
        if (event.isSyncObject() && event.isSyncTimingType()) {
            if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_START)) {

            } else if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_DELETE)) {
                getMyDoneTask();
            } else if (TextUtils.equals(event.scene, ServerTimingEvent.TIMING_SYNC_EDIT)) {
                getMyDoneTask();
            }
        }
    }


    @OnClick({
            R.id.photo_image,
            R.id.user_info_layout,
            R.id.today_duraction_layout,
            R.id.month_duraction_layout,
            R.id.done_task_layout,
            R.id.my_center_collect_layout,
            R.id.my_center_set_layout,
            R.id.menu_test})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.photo_image:
                ImagePagerActivity.launch(getContext(), Arrays.asList(getLoginUserInfo().getPic()));
                break;
            case R.id.user_info_layout:
                UserInfoActivity.launch(getContext());
                break;
            case R.id.today_duraction_layout://今日计时
                showTopSnackBar("今日计时");
                break;
            case R.id.month_duraction_layout://本月计时
                showTopSnackBar("本月计时");
                break;
            case R.id.done_task_layout://本月完成任务
                MyFinishTaskActivity.launch(getContext());
                break;
            case R.id.my_center_set_layout://设置
                SetingActivity.launch(getContext());
                break;
            case R.id.my_center_collect_layout://收藏
                ChatMsgClassfyActivity.launchMyCollected(getContext());
                break;
            case R.id.menu_test:
//                test1();
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        getChatApi().userInfoQuery().enqueue(new SimpleCallBack<AlphaUserInfo>() {
            @Override
            public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                AlphaUserInfo info = response.body().result;
                AlphaUserInfo alphaUserInfo = getLoginUserInfo();
                if (alphaUserInfo != null && info != null) {
//                    info.setGroups(alphaUserInfo.getGroups());
                    alphaUserInfo.setMail(info.getMail());
                    alphaUserInfo.setPhone(info.getPhone());
                    alphaUserInfo.setName(info.getName());
                    alphaUserInfo.setPic(info.getPic());
                    saveLoginUserInfo(alphaUserInfo);
                }
                setDataToView(info);
            }

            @Override
            public void onFailure(Call<ResEntity<AlphaUserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                setDataToView(getLoginUserInfo());
            }
        });
        getMyDoneTask();
        getGroupList();
    }

    /**
     * 获取我的今日计时、本月计时、本月完成任务
     */
    private void getMyDoneTask() {

        getApi().getUserData(getLoginUserId()).enqueue(new SimpleCallBack<UserDataEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<UserDataEntity>> call, Response<ResEntity<UserDataEntity>> response) {
                if (response.body().result != null) {
                    if (todayDuractionTv == null) return;
                    todayDuractionTv.setText(getHm(response.body().result.timingCountToday));
                    monthDuractionTv.setText(getHm(response.body().result.timingCountMonth));
                    doneTaskTv.setText(response.body().result.taskMonthConutDone + "");
                }
            }
        });
    }

    /**
     * 获取负责团队列表
     */
    private void getGroupList() {
        getApi().lawyerGroupListQuery().enqueue(new SimpleCallBack<List<GroupBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<GroupBean>>> call, Response<ResEntity<List<GroupBean>>> response) {
                List<GroupBean> myGroups = response.body().result;
                StringBuffer stringBuffer = new StringBuffer();
                if (myGroups != null) {
                    if (myGroups.size() > 0) {
                        for (GroupBean groupBean : myGroups) {
                            stringBuffer.append(groupBean.getName() + ",");
                        }
                        officeNameTv.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
                    }
                }
            }
        });
    }

    public String getHm(long times) {
        times /= 1000;
        long hour = times / 3600;
        long minute = times % 3600 / 60;
        return String.format(Locale.CHINA, "%02d:%02d", hour, minute);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}
