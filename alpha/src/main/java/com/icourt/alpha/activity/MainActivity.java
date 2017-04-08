package com.icourt.alpha.activity;

import android.os.Bundle;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class MainActivity extends BaseAppUpdateActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("alpha");
        unregisterClick(R.id.titleBack);
        registerClick(R.id.bt_demo);
        registerClick(R.id.bt_login);
        registerClick(R.id.bt_db);
        registerClick(R.id.bt_bugs);
        registerClick(R.id.bt_about);
        registerClick(R.id.bt_phone);
        registerClick(R.id.bt_email);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        // checkAppUpdate(getContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_demo:
                DemoActivity.launch(getContext());
                break;
            case R.id.bt_login: {
                doOauth(SHARE_MEDIA.WEIXIN);
                /*if (isInstall(SHARE_MEDIA.WEIXIN)) {
                    doOauth(SHARE_MEDIA.WEIXIN);
                } else {
                    showTopSnackBar(R.string.umeng_wexin_uninstalled);
                }*/
            }
            break;
            case R.id.bt_db:
                DemoRealmActivity.launch(getActivity());
                break;
            case R.id.bt_bugs:
                BugtagsDemoActivity.launch(getContext());
                break;
            case R.id.bt_about:
                AboutActivity.launch(getContext());
                break;
            case R.id.bt_phone:
                UpdatePhoneOrMailActivity.launch(this, UpdatePhoneOrMailActivity.UPDATE_PHONE_TYPE, "18888887777");
                break;
            case R.id.bt_email:
                UpdatePhoneOrMailActivity.launch(this, UpdatePhoneOrMailActivity.UPDATE_EMAIL_TYPE, "zhaolu@icourt.cc");
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
