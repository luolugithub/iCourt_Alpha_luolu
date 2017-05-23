package com.icourt.alpha.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.AboutActivity;
import com.icourt.alpha.activity.ChatMsgClassfyActivity;
import com.icourt.alpha.activity.LoginSelectActivity;
import com.icourt.alpha.activity.MyAtedActivity;
import com.icourt.alpha.activity.MyFileTabActivity;
import com.icourt.alpha.activity.SettingActivity;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.UserDataEntity;
import com.icourt.alpha.fragment.dialogfragment.DateSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.transformations.BlurTransformation;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Locale;
import java.util.Map;

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
    @BindView(R.id.set_image)
    ImageView setImage;
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
    @BindView(R.id.my_center_collect_textview)
    TextView myCenterCollectTextview;
    @BindView(R.id.my_center_collect_layout)
    LinearLayout myCenterCollectLayout;
    @BindView(R.id.my_center_at_textview)
    TextView myCenterAtTextview;
    @BindView(R.id.my_center_at_layout)
    LinearLayout myCenterAtLayout;
    @BindView(R.id.my_center_file_textview)
    TextView myCenterFileTextview;
    @BindView(R.id.my_center_file_layout)
    LinearLayout myCenterFileLayout;
    @BindView(R.id.my_center_clear_cache_textview)
    TextView myCenterClearCacheTextview;
    @BindView(R.id.my_center_clear_cache_layout)
    LinearLayout myCenterClearCacheLayout;
    @BindView(R.id.my_center_about_count_view)
    TextView myCenterAboutCountView;
    @BindView(R.id.my_center_clear_about_layout)
    LinearLayout myCenterClearAboutLayout;
    @BindView(R.id.my_center_clear_loginout_layout)
    LinearLayout myCenterClearLoginoutLayout;
    @BindView(R.id.menu_test)
    TextView menuTest;

    private UMShareAPI mShareAPI;

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
        getData(false);
//        setDataToView(getLoginUserInfo());
        mShareAPI = UMShareAPI.get(getContext());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getData(false);
        }
    }

    /**
     * 设置数据
     *
     * @param alphaUserInfo
     */
    private void setDataToView(AlphaUserInfo alphaUserInfo) {
        if (alphaUserInfo != null) {
            GlideUtils.loadUser(getContext(), alphaUserInfo.getPic(), photoImage);
            if (GlideUtils.canLoadImage(getContext())) {
                Glide.with(getContext())
                        .load(alphaUserInfo.getPic())
                        .bitmapTransform(new BlurTransformation(getContext(),50))
                        .crossFade()
                        .into(photoBigImage);
            }
            userNameTv.setText(alphaUserInfo.getName());
            officeNameTv.setText(alphaUserInfo.getOfficename());
        }
    }

    @OnClick({R.id.set_image,
            R.id.my_center_collect_layout,
            R.id.my_center_at_layout,
            R.id.my_center_file_layout,
            R.id.my_center_clear_cache_layout,
            R.id.my_center_clear_about_layout,
            R.id.my_center_clear_loginout_layout,
            R.id.menu_test})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.set_image://设置
                SettingActivity.launch(getContext());
                break;
            case R.id.my_center_collect_layout://收藏
                ChatMsgClassfyActivity.launchMyCollected(getContext());
                break;
            case R.id.my_center_at_layout://提及我的
                MyAtedActivity.launch(getContext());
                break;
            case R.id.my_center_file_layout://我的文件
                MyFileTabActivity.launch(getContext());
                break;
            case R.id.my_center_clear_cache_layout://清除缓存
                break;
            case R.id.my_center_clear_about_layout://关于
                AboutActivity.launch(getContext());
                break;
            case R.id.my_center_clear_loginout_layout://退出
                showLoginOutConfirmDialog();
                break;
            case R.id.menu_test:
                test1();
                break;
        }
    }

    private void test1() {
        String tag = DateSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        DateSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    private void showLoginOutConfirmDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(getResources().getStringArray(R.array.my_center_isloginout_text_arr)[Math.random() > 0.5 ? 1 : 0].replace("|", "\n"))
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginOut();
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    /**
     * 退出登录
     */
    private void loginOut() {
        //撤销微信授权
        if (!mShareAPI.isAuthorize(getActivity(), SHARE_MEDIA.WEIXIN)) {
            dismissLoadingDialog();
            LoginSelectActivity.launch(getContext());
        } else {
            mShareAPI.deleteOauth(getActivity(), SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                @Override
                public void onStart(SHARE_MEDIA share_media) {
                    showLoadingDialog(null);
                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    exit();
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    exit();
                }


                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    exit();
                }

                private void exit() {
                    dismissLoadingDialog();
                    LoginSelectActivity.launch(getContext());
                }

            });
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        getChatApi().userInfoQuery().enqueue(new SimpleCallBack<AlphaUserInfo>() {
            @Override
            public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                AlphaUserInfo info = response.body().result;
                AlphaUserInfo alphaUserInfo = getLoginUserInfo();
                if (alphaUserInfo != null) {
                    info.setOfficename(alphaUserInfo.getOfficename());
                }
                setDataToView(info);
            }

            @Override
            public void onFailure(Call<ResEntity<AlphaUserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                setDataToView(getLoginUserInfo());
            }
        });

        getApi().getUserData(getLoginUserId()).enqueue(new SimpleCallBack<UserDataEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<UserDataEntity>> call, Response<ResEntity<UserDataEntity>> response) {
                if (response.body().result != null) {
                    todayDuractionTv.setText(getHm(response.body().result.timingCountToday));
                    monthDuractionTv.setText(getHm(response.body().result.timingCountMonth));
                    doneTaskTv.setText(String.valueOf(response.body().result.taskMonthConutDone));
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mShareAPI != null) {
            mShareAPI.release();
        }
    }
}
