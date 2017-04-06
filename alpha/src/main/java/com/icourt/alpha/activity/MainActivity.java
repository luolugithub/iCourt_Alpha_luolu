package com.icourt.alpha.activity;

import android.os.Bundle;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.base.BaseUmengActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class MainActivity extends BaseAppUpdateActivity {

    @Override
    protected void onResume() {
        super.onResume();
       // checkAppUpdate(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoActivity.launch(getContext());
            }
        });
        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOauth(SHARE_MEDIA.WEIXIN);
                /*if (isInstall(SHARE_MEDIA.WEIXIN)) {
                    doOauth(SHARE_MEDIA.WEIXIN);
                } else {
                    showTopSnackBar(R.string.umeng_wexin_uninstalled);
                }*/
            }
        });
        findViewById(R.id.bt_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoRealmActivity.launch(getActivity());
            }
        });
    }


}
