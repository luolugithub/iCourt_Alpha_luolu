package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.fragment.ContactActionFragment;
import com.icourt.alpha.fragment.GroupActionFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.DensityUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/10
 * version 1.0.0
 */
public class ContactShareDialogFragment extends BaseDialogFragment {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;

    public static ContactShareDialogFragment newInstance(@NonNull String msgId) {
        ContactShareDialogFragment contactDialogFragment = new ContactShareDialogFragment();
        Bundle args = new Bundle();
        args.putString("msgId", msgId);
        contactDialogFragment.setArguments(args);
        return contactDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_share, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        baseFragmentAdapter.bindData(true, Arrays.asList(ContactActionFragment.newInstance()
                , GroupActionFragment.newInstance()));
    }

    @OnClick({R.id.titleBack, R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                dismiss();
                break;
            case R.id.titleAction:
                shareMsg();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 分享消息
     */
    private void shareMsg() {
        showLoadingDialog(null);
        getChatApi().msgTrans(Const.CHAT_TYPE_P2P, "x", "x")
                .enqueue(new SimpleCallBack<JsonElement>() {

                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        dismissLoadingDialog();
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
