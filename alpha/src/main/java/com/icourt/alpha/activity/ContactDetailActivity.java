package com.icourt.alpha.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.SetTopEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SystemUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
    @BindView(R.id.contact_file_android_top_ll)
    LinearLayout contactFileAndroidTopLl;
    GroupContactBean groupContactBean;

    private static final String KEY_CONTACT = "contact";
    private static final String KEY_HIDEN_FILE_ITEM = "hidenFileItem";
    private static final String KEY_HIDEN_SEND_MSG_BTN = "hidenSendMsgBtn";
    private static final int CODE_REQUEST_PERMISSION_CALL_PHONE = 101;

    public static void launch(@NonNull Context context,
                              @Nullable GroupContactBean contactBean,
                              boolean hidenFileItem,
                              boolean hidenSendMsgBtn) {
        if (context == null) return;
        if (contactBean == null) return;
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra(KEY_CONTACT, contactBean);
        intent.putExtra(KEY_HIDEN_FILE_ITEM, hidenFileItem);
        intent.putExtra(KEY_HIDEN_SEND_MSG_BTN, hidenSendMsgBtn);
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
        groupContactBean = (GroupContactBean) getIntent().getSerializableExtra(KEY_CONTACT);
        setTitle(groupContactBean.name);
        contactNameTv.setText(groupContactBean.name);
        contactPhoneTv.setText(groupContactBean.phone);
        contactEmailTv.setText(groupContactBean.email);
        GlideUtils.loadUser(getContext(), groupContactBean.pic, contactIconIv);
        setViewVisible(contactFileAndroidTopLl, !getIntent().getBooleanExtra(KEY_HIDEN_FILE_ITEM, false));
        setViewVisible(contactSendMsgTv, !getIntent().getBooleanExtra(KEY_HIDEN_SEND_MSG_BTN, false));
    }

    @OnClick({R.id.contact_send_msg_tv
            , R.id.contact_phone_ll
            , R.id.contact_email_ll
            , R.id.contact_file_tv
            , R.id.contact_setTop_switch})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_send_msg_tv:
                showTopSnackBar("un finish");
                break;
            case R.id.contact_phone_ll:
                if (!TextUtils.isEmpty(contactPhoneTv.getText())) {
                    if (!checkPermission(Manifest.permission.CALL_PHONE)) {
                        reqPermission(Manifest.permission.CALL_PHONE, "我们需要拨号权限", CODE_REQUEST_PERMISSION_CALL_PHONE);
                    } else {
                        SystemUtils.callPhone(getContext(), contactPhoneTv.getText().toString());
                    }
                }
                break;
            case R.id.contact_email_ll:
                if (!TextUtils.isEmpty(contactEmailTv.getText())) {
                    try {
                        SystemUtils.sendEmail(getContext(), contactEmailTv.getText().toString());
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        showTopSnackBar("未找到邮件发送的app!");
                    }
                }
                break;
            case R.id.contact_file_tv:
                IMFileListActivity.launch(getContext());
                break;
            case R.id.contact_setTop_switch:
                setTop();
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
        getApi().isSetTop(groupContactBean.userId)
                .enqueue(new SimpleCallBack<Integer>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Integer>> call, Response<ResEntity<Integer>> response) {
                        dismissLoadingDialog();
                        contactSetTopSwitch.setChecked(response.body().result != null && response.body().result.intValue() == 1);
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Integer>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_REQUEST_PERMISSION_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SystemUtils.callPhone(getContext(), contactPhoneTv.getText().toString());
                } else {
                    showTopSnackBar("拨号权限被拒绝!");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * 置顶
     */
    private void setTop() {
        getApi().setTop(groupContactBean.userId)
                .enqueue(new SimpleCallBack<List<SetTopEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<SetTopEntity>>> call, Response<ResEntity<List<SetTopEntity>>> response) {
                        if (response.body().result == null) return;
                        EventBus.getDefault().post(response.body().result);
                    }
                });
    }

}
