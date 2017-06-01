package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.event.SetTopEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;

/**
 * Description alpha小助手设置页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/6/1
 * version 2.0.0
 */

public class AlphaSpeciaSetActivity extends BaseActivity {
    private static final String KEY_UID = "key_uid";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.set_top_switch)
    Switch setTopSwitch;

    public static void launch(@NonNull Context context, String accid) {
        if (context == null) return;
        Intent intent = new Intent(context, AlphaSpeciaSetActivity.class);
        intent.putExtra(KEY_UID, accid);
        context.startActivity(intent);
    }

    protected String getIMChatId() {
        return getIntent().getStringExtra(KEY_UID);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_alpha_special_setting_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("设置");
        getSetTopSessions();
        getIsSetGroupNoDisturbing();
    }

    /**
     * 云信状态码  http://dev.netease.im/docs?doc=nim_status_code
     * 获取讨论组 是否免打扰
     */
    private void getIsSetGroupNoDisturbing() {
        //先拿网络 保持三端一致
        getChatApi().sessionQueryAllNoDisturbingIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            setTopSwitch.setChecked(response.body().result.contains(getIMChatId()));
                        } else {
                            setTopSwitch.setChecked(false);
                        }
                    }
                });
    }

    /**
     * 获取所有置顶的会话ids
     */
    private void getSetTopSessions() {
        getChatApi().sessionQueryAllsetTopIds()
                .enqueue(new SimpleCallBack<List<String>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                        if (response.body().result != null) {
                            boolean isTop = response.body()
                                    .result.contains(getIMChatId());
                            setTopSwitch.setChecked(isTop);
                            EventBus.getDefault().post(new SetTopEvent(isTop, getIMChatId()));
                        } else {
                            setTopSwitch.setChecked(false);
                            EventBus.getDefault().post(new SetTopEvent(false, getIMChatId()));
                        }
                    }
                });
    }

    @OnClick({R.id.set_top_switch})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.set_top_switch:
                if (!setTopSwitch.isChecked()) {
                    setGroupTopCancel();
                } else {
                    setGroupTop();
                }
                break;
        }
    }

    /**
     * 广播通知其它页面更新置顶
     */
    private void broadSetTopEvent() {
        if (setTopSwitch == null) return;
        EventBus.getDefault().post(new SetTopEvent(setTopSwitch.isChecked(), getIMChatId()));
    }

    /**
     * 取消置顶
     */
    private void setGroupTopCancel() {
        showLoadingDialog(null);
        getChatApi().sessionSetTopCancel(CHAT_TYPE_P2P, getIMChatId())
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            setTopSwitch.setChecked(false);
                            broadSetTopEvent();
                        } else {
                            setTopSwitch.setChecked(true);
                            broadSetTopEvent();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 设置置顶
     */
    private void setGroupTop() {
        showLoadingDialog(null);
        getChatApi().sessionSetTop(CHAT_TYPE_P2P, getIMChatId())
                .enqueue(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Call<ResEntity<Boolean>> call, Response<ResEntity<Boolean>> response) {
                        dismissLoadingDialog();
                        if (response.body().result != null && response.body().result.booleanValue()) {
                            setTopSwitch.setChecked(true);
                            broadSetTopEvent();
                        } else {
                            setTopSwitch.setChecked(false);
                            broadSetTopEvent();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<Boolean>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }
}
