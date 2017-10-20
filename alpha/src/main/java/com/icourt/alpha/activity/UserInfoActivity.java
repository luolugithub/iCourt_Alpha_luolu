package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icourt.alpha.activity.UpdateUserInfoActivity.UPDATE_EMAIL_TYPE;
import static com.icourt.alpha.activity.UpdateUserInfoActivity.UPDATE_NAME_TYPE;
import static com.icourt.alpha.activity.UpdateUserInfoActivity.UPDATE_PHONE_TYPE;

/**
 * Description 设置
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class UserInfoActivity extends BaseActivity {
    //TODO 这些常量没有引用
    private static final int REQUEST_CODE_CAMERA = 1000;//拍照 request code
    private static final int REQUEST_CODE_GALLERY = 1001;//相册 request code
    private static final int REQUEST_CODE_CROP = 1002;//裁剪 request code
    private static final int REQ_CODE_PERMISSION_CAMERA = 1100; //相机权限
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;//本地文件读写权限
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.photo_image)
    ImageView photoImage;
    @BindView(R.id.photo_layout)
    LinearLayout photoLayout;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.name_layout)
    LinearLayout nameLayout;
    @BindView(R.id.phone_tv)
    TextView phoneTv;
    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.email_tv)
    TextView emailTv;
    @BindView(R.id.email_layout)
    LinearLayout emailLayout;

    private AlphaUserInfo alphaUserInfo;
    private String path;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.mine_personage_set);
        alphaUserInfo = getLoginUserInfo();
        if (alphaUserInfo != null) {
            GlideUtils.loadUser(this, alphaUserInfo.getPic(), photoImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataToView();
    }

    private void setDataToView() {
        alphaUserInfo = getLoginUserInfo();
        nameTv.setText(alphaUserInfo.getName());
        phoneTv.setText(alphaUserInfo.getPhone());
        emailTv.setText(alphaUserInfo.getMail());

    }

    @OnClick({R.id.name_layout,
            R.id.phone_layout,
            R.id.email_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.name_layout:
                //TODO 空指针危险
                UpdateUserInfoActivity.launch(this, UPDATE_NAME_TYPE, alphaUserInfo.getName());
                break;
            case R.id.phone_layout:
                UpdateUserInfoActivity.launch(this, UPDATE_PHONE_TYPE, alphaUserInfo.getPhone());
                break;
            case R.id.email_layout:
                UpdateUserInfoActivity.launch(this, UPDATE_EMAIL_TYPE, alphaUserInfo.getMail());
                break;
        }
    }
}
