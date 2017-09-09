package com.icourt.alpha.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.umeng.socialize.utils.DeviceConfig.context;

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
    @BindView(R.id.about_new_version_view)
    TextView aboutNewVersionView;
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

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

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
        setTitle("关于");
        registerClick(aboutCheckIsUpdateView);
        aboutVersonTextview.setText(BuildConfig.VERSION_NAME);
        aboutVersonReleaseTime.setText("build in " + DateUtils.getyyyyMMddHHmm(Long.valueOf(BuildConfig.APK_RELEASE_TIME)));
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        checkAppUpdate(new SimpleCallBack<AppVersionEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<AppVersionEntity>> call, Response<ResEntity<AppVersionEntity>> response) {
                appVersionEntity = response.body().result;
                if (isUpdateApp(appVersionEntity)) {
                    aboutCheckIsUpdateView.setVisibility(View.VISIBLE);
                    aboutNewVersionView.setVisibility(View.GONE);
                } else {
                    aboutCheckIsUpdateView.setVisibility(View.GONE);
                    aboutNewVersionView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResEntity<AppVersionEntity>> call, Throwable t) {
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
                if (appVersionEntity == null) return;
                if (hasFilePermission(context)) {
                    MobclickAgent.onEvent(context, UMMobClickAgent.dialog_update_btn_click_id);
                    showAppDownloadingDialog(getActivity(), appVersionEntity.upgradeUrl);
                } else {
                    requestFilePermission(context, REQUEST_FILE_PERMISSION);
                }
                break;
            case R.id.about_new_version_view:
                showUpdateDescDialog();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    /**
     * 显示日志dialog
     */
    private void showUpdateDescDialog() {
        if (appVersionEntity == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("更新日志")
                .setMessage(TextUtils.isEmpty(appVersionEntity.versionDesc) ? "有一个新版本,请立即更新吧" : appVersionEntity.versionDesc); //设置内容
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
