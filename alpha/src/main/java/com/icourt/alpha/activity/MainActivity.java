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
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        checkAppUpdate(this);
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
            default:
                super.onClick(v);
                break;
        }
    }
}
