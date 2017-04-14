package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.R.id.contact_file_tv;

/**
 * Description  联系人详情界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/14
 * version 1.0.0
 */
public class ContactDetailActivity extends BaseActivity {


    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.contact_icon_iv)
    ImageView contactIconIv;
    @BindView(R.id.contact_name_tv)
    TextView contactNameTv;
    @BindView(R.id.contact_phone_tv)
    TextView contactPhoneTv;
    @BindView(R.id.contact_phone_ll)
    LinearLayout contactPhoneLl;
    @BindView(R.id.contact_email_tv)
    TextView contactEmailTv;
    @BindView(R.id.contact_email_ll)
    LinearLayout contactEmailLl;
    @BindView(contact_file_tv)
    TextView contactFileTv;
    @BindView(R.id.contact_setTop_switch)
    Switch contactSetTopSwitch;
    @BindView(R.id.contact_send_msg_tv)
    TextView contactSendMsgTv;
    GroupContactBean groupContactBean;

    public static void launch(@NonNull Context context,
                              @Nullable GroupContactBean contactBean) {
        if (context == null) return;
        if (contactBean == null) return;
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra("contact", contactBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ButterKnife.bind(this);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        groupContactBean = (GroupContactBean) getIntent().getSerializableExtra("contact");
        setTitle(groupContactBean.name);
        contactNameTv.setText(groupContactBean.name);
        contactPhoneTv.setText(groupContactBean.phone);
        contactEmailTv.setText(groupContactBean.email);
        GlideUtils.loadUser(getContext(), groupContactBean.pic, contactIconIv);
    }

    @OnClick({R.id.contact_send_msg_tv
            , R.id.contact_phone_ll
            , R.id.contact_email_ll
            , R.id.contact_file_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_send_msg_tv:
                showTopSnackBar("un finish");
                break;
            case R.id.contact_phone_ll:
                break;
            case R.id.contact_email_ll:
                break;
            case R.id.contact_file_tv:
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        contactSetTopSwitch.setOnCheckedChangeListener(null);
        getApi().isSetTop(groupContactBean.userId)
                .enqueue(new SimpleCallBack<Integer>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Integer>> call, Response<ResEntity<Integer>> response) {
                        dismissLoadingDialog();
                        contactSetTopSwitch.setChecked(response.body().result != null && response.body().result.intValue() == 1);
                        contactSetTopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                setTop();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Integer>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });


    }

    /**
     * 置顶
     */
    private void setTop() {
        getApi().setTop(groupContactBean.userId)
                .enqueue(new SimpleCallBack<Integer>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Integer>> call, Response<ResEntity<Integer>> response) {

                    }
                });
    }

}
