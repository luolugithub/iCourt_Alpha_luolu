package com.icourt.alpha.fragment.dialogfragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.ChatActivity;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.LoginInfoUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.transformations.BlurTransformation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/5
 * version 1.0.0
 */
public class ContactDialogFragment extends BaseDialogFragment {

    @BindView(R.id.contact_title_tv)
    TextView contactTitleTv;
    @BindView(R.id.contact_bg_iv)
    ImageView contactBgIv;
    @BindView(R.id.contact_icon_iv)
    ImageView contactIconIv;
    @BindView(R.id.contact_phone_tv)
    TextView contactPhoneTv;
    @BindView(R.id.contact_phone_ll)
    LinearLayout contactPhoneLl;
    @BindView(R.id.contact_email_tv)
    TextView contactEmailTv;
    @BindView(R.id.contact_email_ll)
    LinearLayout contactEmailLl;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    Unbinder unbinder;
    ContactDbService contactDbService;
    GroupContactBean contactBean;
    @BindView(R.id.contact_name_tv)
    TextView contactNameTv;
    @BindView(R.id.contact_desc_tv)
    TextView contactDescTv;
    @BindView(R.id.bt_chat)
    TextView btChat;

    public static ContactDialogFragment newInstance(@NonNull String accId,
                                                    String title,
                                                    boolean hiddenChatBtn) {
        ContactDialogFragment contactDialogFragment = new ContactDialogFragment();
        Bundle args = new Bundle();
        args.putString("accId", accId);
        args.putString("title", title);
        args.putBoolean("hiddenChatBtn", hiddenChatBtn);
        contactDialogFragment.setArguments(args);
        return contactDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_contact, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setPadding(0, 0, 0, DensityUtil.dip2px(getContext(), 20));
                }
            }
        }
        contactDbService = new ContactDbService(LoginInfoUtils.getLoginUserId());
        ContactDbModel contactDbModel = contactDbService.queryFirst("accid", getArguments().getString("accId", ""));
        if (contactDbModel != null) {
            contactBean = contactDbModel.convert2Model();
            if (contactBean != null) {
                contactNameTv.setText(contactBean.name);
                contactDescTv.setText(contactBean.title);
                contactPhoneTv.setText(contactBean.phone);
                contactEmailTv.setText(contactBean.email);
                GlideUtils.loadUser(getContext(), contactBean.pic, contactIconIv);
                if (GlideUtils.canLoadImage(getContext())) {
                    Glide.with(getContext())
                            .load(contactBean.pic)
                            .crossFade()
                            .bitmapTransform(new BlurTransformation(getContext()))
                            .into(contactBgIv);
                }
            }
        }
        String title = getArguments().getString("title", null);
        if (!TextUtils.isEmpty(title)) {
            contactTitleTv.setText(title);
        }
        btChat.setVisibility(getArguments().getBoolean("hiddenChatBtn") ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.bt_cancel,
            R.id.bt_chat,
            R.id.contact_phone_ll,
            R.id.contact_email_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_chat:
                if (contactBean == null) return;
                ChatActivity.launchP2P(getActivity(),
                        getArguments().getString("accId", ""),
                        contactBean.name);
                dismiss();
                break;
            case R.id.contact_phone_ll:
                if (!TextUtils.isEmpty(contactPhoneTv.getText())) {
                    if (!SystemUtils.checkPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE})) {
                        SystemUtils.reqPermission(getActivity(), new String[]{Manifest.permission.CALL_PHONE,}, 12345);
                    } else {
                        SystemUtils.callPhone(getContext(), contactPhoneTv.getText().toString());
                        dismiss();
                    }
                }
                break;
            case R.id.contact_email_ll:
                if (!TextUtils.isEmpty(contactEmailTv.getText())) {
                    try {
                        SystemUtils.sendEmail(getContext(), contactEmailTv.getText().toString());
                        dismiss();
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        showTopSnackBar("未找到邮件发送的app!");
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }
}
