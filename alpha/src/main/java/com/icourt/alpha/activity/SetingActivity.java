package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.interfaces.callback.AppUpdateCallBack;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.widget.manager.DataCleanManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description 设置页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/9/6
 * version 2.0.0
 */

public class SetingActivity extends BaseAppUpdateActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.setting_clear_cache_textview)
    TextView settingClearCacheTextview;
    @BindView(R.id.setting_clear_cache_layout)
    LinearLayout settingClearCacheLayout;
    @BindView(R.id.setting_helper_layout)
    LinearLayout settingHelperLayout;
    @BindView(R.id.setting_feedback_layout)
    LinearLayout settingFeedbackLayout;
    @BindView(R.id.setting_about_count_view)
    TextView settingAboutCountView;
    @BindView(R.id.setting_about_layout)
    LinearLayout settingAboutLayout;
    @BindView(R.id.setting_loginout_layout)
    LinearLayout settingLoginoutLayout;

    private UMShareAPI mShareAPI;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, SetingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("设置");
        mShareAPI = UMShareAPI.get(getContext());
        try {
            settingClearCacheTextview.setText(DataCleanManager.getTotalCacheSize(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        checkAppUpdate(new AppUpdateCallBack() {
            @Override
            public void onSuccess(Call<AppVersionEntity> call, Response<AppVersionEntity> response) {
                if (settingAboutCountView == null) return;
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

    @OnClick({R.id.setting_clear_cache_layout,
            R.id.setting_feedback_layout,
            R.id.setting_about_layout,
            R.id.setting_helper_layout,
            R.id.setting_loginout_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.setting_clear_cache_layout:
                new AlertDialog.Builder(getContext())
                        .setMessage("确认清除?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataCleanManager.clearAllCache(getActivity());
                                settingClearCacheTextview.setText("0K");
                            }
                        }).setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.setting_helper_layout:
               WebViewActivity.launch(this,"帮助中心","https://mp.weixin.qq.com/s/CghSah5E7Kj_IMZ65Shp-A");
                break;
            case R.id.setting_feedback_layout:
                FeedBackActivity.launch(this);
                break;
            case R.id.setting_about_layout:
                AboutActivity.launch(getContext());
                break;
            case R.id.setting_loginout_layout:
                showLoginOutConfirmDialog();
                break;
        }
    }

    /**
     * 显示退出登录对话框
     */
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
        //神策退出
       /* SensorsDataAPI.sharedInstance(getContext())
                .logout();*/
        MobclickAgent.onEvent(getContext(), UMMobClickAgent.login_out_click_id);
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
}
