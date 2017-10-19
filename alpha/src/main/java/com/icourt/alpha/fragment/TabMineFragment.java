package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.icourt.alpha.activity.MyTimingActivity;
import com.icourt.alpha.activity.SetingActivity;
import com.icourt.alpha.activity.TaskMonthFinishActivity;
import com.icourt.alpha.activity.UserInfoActivity;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.entity.bean.GroupBean;
import com.icourt.alpha.entity.bean.UserDataEntity;
import com.icourt.alpha.entity.event.ServerTimingEvent;
import com.icourt.alpha.entity.event.TimingEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.callback.AppUpdateCallBack;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.transformations.BlurTransformation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 2.0.0
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
    @BindView(R.id.setting_about_count_view)
    TextView settingAboutCountView;

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
        if (event == null) {
            return;
        }
        switch (event.action) {
            case TimingEvent.TIMING_ADD:

                break;
            case TimingEvent.TIMING_UPDATE_PROGRESS:
                break;
            case TimingEvent.TIMING_STOP:
                getMyDoneTask();
                break;
            default:
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
        if (event == null) {
            return;
        }
        if (TextUtils.equals(event.clientId, getlocalUniqueId())) {
            return;
        }
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
            R.id.menu_test,
            R.id.my_center_timer_layout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_image:
//                ImagePagerActivity.launch(getContext(), Arrays.asList(getLoginUserInfo().getPic()));//头像大图浏览
                UserInfoActivity.launch(getContext());
                break;
            case R.id.user_info_layout:
                UserInfoActivity.launch(getContext());
                break;
            //今日计时
            case R.id.today_duraction_layout:
                showTopSnackBar(R.string.mine_today_time);
                break;
            //本月计时
            case R.id.month_duraction_layout:
                showTopSnackBar(R.string.mine_month_time);
                break;
            //本月完成任务
            case R.id.done_task_layout:
                TaskMonthFinishActivity.launch(getContext());
                break;
            //设置
            case R.id.my_center_set_layout:
                SetingActivity.launch(getContext());
                break;
            //收藏
            case R.id.my_center_collect_layout:
                ChatMsgClassfyActivity.launchMyCollected(getContext());
                break;
            case R.id.menu_test:
//                test1();
                break;
            case R.id.my_center_timer_layout:
                MyTimingActivity.launch(getActivity());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        callEnqueue(
                getChatApi().userInfoQuery(),
                new SimpleCallBack<AlphaUserInfo>() {
                    @Override
                    public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                        AlphaUserInfo info = response.body().result;
                        AlphaUserInfo alphaUserInfo = getLoginUserInfo();
                        if (alphaUserInfo != null && info != null) {
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
        checkVersion();
        getMyDoneTask();
        getGroupList();
    }

    /**
     * 检测版本
     */
    private void checkVersion() {
        callEnqueue(
                getApi().getNewVersionAppInfo(BuildConfig.APK_UPDATE_URL), new AppUpdateCallBack() {
                    @Override
                    public void onSuccess(Call<AppVersionEntity> call, Response<AppVersionEntity> response) {
                        if (settingAboutCountView == null) {
                            return;
                        }
                        settingAboutCountView.setVisibility(shouldUpdate(response.body()) ? View.VISIBLE : View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<AppVersionEntity> call, Throwable t) {
                        if (t instanceof HttpException) {
                            HttpException hx = (HttpException) t;
                            if (hx.code() == 401) {
                                showTopSnackBar("fir token 更改");
                                return;
                            }
                        }
                        super.onFailure(call, t);
                    }
                });
    }

    public final boolean shouldUpdate(@NonNull AppVersionEntity appVersionEntity) {
        return appVersionEntity != null
                && !TextUtils.equals(appVersionEntity.versionShort, BuildConfig.VERSION_NAME);
    }

    /**
     * 获取我的今日计时、本月计时、本月完成任务
     */
    private void getMyDoneTask() {
        callEnqueue(
                getApi().getUserData(getLoginUserId()),
                new SimpleCallBack<UserDataEntity>() {
                    @Override
                    public void onSuccess(Call<ResEntity<UserDataEntity>> call, Response<ResEntity<UserDataEntity>> response) {
                        if (response.body().result != null) {
                            if (todayDuractionTv == null) return;
                            UserDataEntity userDataEntity = response.body().result;
                            todayDuractionTv.setText(getHm(userDataEntity.timingCountToday));
                            monthDuractionTv.setText(getHm(userDataEntity.timingCountMonth));
                            doneTaskTv.setText(String.valueOf(userDataEntity.taskMonthConutDone));

                            todayDuractionTv.setTextColor(getDoneTextColor(userDataEntity.timingCountToday));
                            todayDuractionLayout.setClickable(userDataEntity.timingCountToday > 0);

                            monthDuractionTv.setTextColor(getDoneTextColor(userDataEntity.timingCountMonth));
                            monthDuractionLayout.setClickable(userDataEntity.timingCountMonth > 0);

                            doneTaskTv.setTextColor(getDoneTextColor(userDataEntity.taskMonthConutDone));
                            doneTaskLayout.setClickable(userDataEntity.taskMonthConutDone > 0);
                        }
                    }
                });
    }

    /**
     * 获取完成数 字体颜色
     *
     * @param count
     * @return
     */
    private int getDoneTextColor(long count) {
        return count <= 0 ? SystemUtils.getColor(getContext(), R.color.alpha_font_color_gray) : SystemUtils.getColor(getContext(), R.color.alpha_font_color_orange);
    }

    /**
     * 获取负责团队列表
     */
    private void getGroupList() {
        callEnqueue(
                getApi().lawyerGroupListQuery(),
                new SimpleCallBack<List<GroupBean>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupBean>>> call, Response<ResEntity<List<GroupBean>>> response) {
                        List<GroupBean> myGroups = response.body().result;
                        StringBuilder stringBuilder = new StringBuilder();
                        if (myGroups != null) {
                            if (myGroups.size() > 0) {
                                for (GroupBean groupBean : myGroups) {
                                    stringBuilder.append(groupBean.getName()).append(",");
                                }
                                if (officeNameTv == null) {
                                    return;
                                }
                                officeNameTv.setText(stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1));
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
