package com.icourt.alpha.activity;

import android.os.Bundle;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;

public class MainActivity extends BaseActivity {

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
    }

}
