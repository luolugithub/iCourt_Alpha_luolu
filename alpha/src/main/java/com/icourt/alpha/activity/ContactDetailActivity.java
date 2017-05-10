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
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.event.SetTopEvent;
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

    private static final String KEY_ACCID = "key_accid";
    private static final String KEY_HIDEN_FILE_ITEM = "hidenFileItem";
    private static final String KEY_HIDEN_SEND_MSG_BTN = "hidenSendMsgBtn";
    private static final int CODE_REQUEST_PERMISSION_CALL_PHONE = 101;
    ContactDbService contactDbService;

    public static void launch(@NonNull Context context,
                              @Nullable String accId,
                              boolean hidenFileItem,
                              boolean hidenSendMsgBtn) {
        if (context == null) return;
        if (TextUtils.isEmpty(accId)) return;
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra(KEY_ACCID, accId);
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
        contactDbService = new ContactDbService(getLoginUserId());
        ContactDbModel contactDbModel = contactDbService.queryFirst("accid", getIntent().getStringExtra(KEY_ACCID));
        if (contactDbModel != null) {
            groupContactBean = contactDbModel.convert2Model();
            if (groupContactBean != null) {
                setTitle(groupContactBean.name);
                contactNameTv.setText(groupContactBean.name);
                contactPhoneTv.setText(groupContactBean.phone);
                contactEmailTv.setText(groupContactBean.email);
                GlideUtils.loadUser(getContext(), groupContactBean.pic, contactIconIv);
            }
        }
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
                if (!contactSetTopSwitch.isChecked()) {
                    setTopCancel();
                } else {
                    setTop();
                }
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
        getChatApi().sessionQueryAllsetTopIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null
                                && groupContactBean != null) {
                            contactSetTopSwitch.setChecked(response.body().result.contains(groupContactBean.accid));
                            broadSetTopEvent();
                        } else {
                            contactSetTopSwitch.setChecked(false);
                            broadSetTopEvent();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
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
        if (groupContactBean == null) return;
        getChatApi().sessionSetTop(Const.CHAT_TYPE_P2P, groupContactBean.accid)
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            contactSetTopSwitch.setChecked(true);
                            broadSetTopEvent();
                        } else {
                            contactSetTopSwitch.setChecked(false);
                            broadSetTopEvent();
                        }
                    }
                });
    }


    /**
     * 广播通知其它页面更新置顶
     */
    private void broadSetTopEvent() {
        if (contactSetTopSwitch == null) return;
        if (groupContactBean == null) return;
        EventBus.getDefault().post(new SetTopEvent(contactSetTopSwitch.isChecked(), groupContactBean.accid));
    }


    /**
     * 取消置顶
     */
    private void setTopCancel() {
        if (groupContactBean == null) return;
        getChatApi().sessionSetTopCancel(Const.CHAT_TYPE_P2P, groupContactBean.accid)
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            contactSetTopSwitch.setChecked(false);
                            broadSetTopEvent();
                        } else {
                            contactSetTopSwitch.setChecked(true);
                            broadSetTopEvent();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
        super.onDestroy();
    }
}
