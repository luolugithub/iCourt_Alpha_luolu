package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.interfaces.callback.AppUpdateCallBack;
import com.icourt.alpha.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Description  关于界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/3/31
 * version 1.0.0
 */
public class AboutActivity extends BaseAppUpdateActivity {

    @BindView(R.id.about_verson_release_time)
    TextView aboutVersonReleaseTime;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.about_verson_textview)
    TextView aboutVersonTextview;
    @BindView(R.id.about_check_is_update_view)
    TextView aboutCheckIsUpdateView;
    AppVersionEntity appVersionEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_activity);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.mine_about);
        registerClick(aboutCheckIsUpdateView);
        aboutVersonTextview.setText(BuildConfig.VERSION_NAME);
        aboutVersonReleaseTime.setText("build in " + DateUtils.getyyyyMMddHHmm(Long.valueOf(BuildConfig.APK_RELEASE_TIME)));
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        checkAppUpdate(new AppUpdateCallBack() {
            @Override
            public void onSuccess(Call<AppVersionEntity> call, Response<AppVersionEntity> response) {
                appVersionEntity = response.body();
                if (shouldUpdate(appVersionEntity)) {
                    aboutCheckIsUpdateView.setText(getString(R.string.my_center_have_new_version_text));
                } else {
                    aboutCheckIsUpdateView.setText(getString(R.string.my_center_check_update_text));
                }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.about_check_is_update_view:
                if (shouldUpdate(appVersionEntity)) {
                    showAppUpdateDialog(getActivity(), appVersionEntity);
                } else {
                    showTopSnackBar("已是最新版,无需更新");
                }
                break;
            default:
                super.onClick(view);
                break;
        }
    }
}
