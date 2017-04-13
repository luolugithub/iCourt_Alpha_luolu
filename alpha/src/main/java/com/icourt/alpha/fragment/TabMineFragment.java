package com.icourt.alpha.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.AboutActivity;
import com.icourt.alpha.activity.LoginSelectActivity;
import com.icourt.alpha.activity.UpdatePhoneOrMailActivity;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.utils.GlideUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabMineFragment extends BaseFragment {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    Unbinder unbinder;
    @BindView(R.id.my_center_photo_view)
    ImageView myCenterPhotoView;
    @BindView(R.id.my_center_photo_layout)
    FrameLayout myCenterPhotoLayout;
    @BindView(R.id.my_center_name_view)
    TextView myCenterNameView;
    @BindView(R.id.my_center_lawplace_view)
    TextView myCenterLawplaceView;
    @BindView(R.id.my_center_phone_textview)
    TextView myCenterPhoneTextview;
    @BindView(R.id.my_center_phone_layout)
    LinearLayout myCenterPhoneLayout;
    @BindView(R.id.my_center_mail_textview)
    TextView myCenterMailTextview;
    @BindView(R.id.my_center_mail_layout)
    LinearLayout myCenterMailLayout;
    @BindView(R.id.my_center_collect_layout)
    LinearLayout myCenterCollectLayout;
    @BindView(R.id.my_center_at_layout)
    LinearLayout myCenterAtLayout;
    @BindView(R.id.my_center_file_layout)
    LinearLayout myCenterFileLayout;
    @BindView(R.id.my_center_clear_cache_layout)
    LinearLayout myCenterClearCacheLayout;
    @BindView(R.id.my_center_clear_about_layout)
    LinearLayout myCenterClearAboutLayout;
    @BindView(R.id.my_center_clear_loginout_layout)
    LinearLayout myCenterClearLoginoutLayout;

    @BindView(R.id.my_center_collect_textview)
    TextView myCenterCollectTextview;
    @BindView(R.id.my_center_at_textview)
    TextView myCenterAtTextview;
    @BindView(R.id.my_center_file_textview)
    TextView myCenterFileTextview;
    @BindView(R.id.my_center_clear_cache_textview)
    TextView myCenterClearCacheTextview;
    @BindView(R.id.my_center_about_count_view)
    TextView myCenterAboutCountView;
    @BindView(R.id.my_center_alpha_qiyu_msgcount_textview)
    TextView myCenterAlphaQiyuMsgcountTextview;
    @BindView(R.id.my_center_clear_help_layout)
    LinearLayout myCenterClearHelpLayout;

    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_mine, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        titleBack.setVisibility(View.INVISIBLE);
        titleContent.setText("个人中心");
        if (GlideUtils.canLoadImage(this)) {
            GlideUtils.loadUser(getContext(), "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491804322903&di=06004cfeb2a47c19dfcca762851687dd&imgtype=0&src=http%3A%2F%2Fimage.coolapk.com%2Fapk_logo%2F2015%2F0703%2F257251_1435907700_3237.png", myCenterPhotoView);
        }
        myCenterNameView.setText("Alpha");
        myCenterLawplaceView.setText("iCourt");
        myCenterPhoneTextview.setText("18888887777");
        myCenterMailTextview.setText("zhaolu@icourt.cc");
    }


    @OnClick({R.id.my_center_phone_layout, R.id.my_center_mail_layout, R.id.my_center_collect_layout, R.id.my_center_at_layout, R.id.my_center_file_layout, R.id.my_center_clear_cache_layout, R.id.my_center_clear_about_layout, R.id.my_center_clear_loginout_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.my_center_phone_layout://电话
                UpdatePhoneOrMailActivity.launch(getContext(), UpdatePhoneOrMailActivity.UPDATE_PHONE_TYPE, myCenterPhoneTextview.getText().toString().trim());
                break;
            case R.id.my_center_mail_layout://邮箱
                UpdatePhoneOrMailActivity.launch(getContext(), UpdatePhoneOrMailActivity.UPDATE_EMAIL_TYPE, myCenterMailTextview.getText().toString().trim());
                break;
            case R.id.my_center_collect_layout://收藏

                break;
            case R.id.my_center_at_layout://提及我的

                break;
            case R.id.my_center_file_layout://我的文件

                break;
            case R.id.my_center_clear_cache_layout://清除缓存

                break;
            case R.id.my_center_clear_about_layout://关于
                AboutActivity.launch(getContext());
                break;
            case R.id.my_center_clear_loginout_layout://退出
                showLoginOutConfirmDialog();
                break;
        }
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
        //  groupContactBeanDao.deleteAll();
        //  personContactBeanDao.deleteAll();
        NIMClient.getService(AuthService.class).logout();
        LoginSelectActivity.launch(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
