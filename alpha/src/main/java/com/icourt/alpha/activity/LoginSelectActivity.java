package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseUmengActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.LoginIMToken;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UserPreferences;
import com.icourt.api.RequestUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class LoginSelectActivity extends BaseUmengActivity {

    @BindView(R.id.loginWeixinBtn)
    TextView loginWeixinBtn;
    @BindView(R.id.actionLoginWithPwd)
    TextView actionLoginWithPwd;
    Map<String, String> umengDataMap;

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, LoginSelectActivity.class);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selelct);
        ButterKnife.bind(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
    }

    /**
     * 微信返回数据如下:
     * /*{
     * unionid=o7RAwt26jDrJKHUlD8NYJGWVz8SE,
     * screen_name=alienware,
     * city=,
     * accessToken=6aWq7LXwYsbMHsHGcaK8SiYpTOy2cnmy_WR_LJNv6wmuTGtWghf3pVv552GOerDzHDXV8bws2JZZlyLFRKYD_gFMblcubo5EAU4SyMWfHN0,
     * refreshToken=ALS9PpnlXUpcQAqsV6MG4nJcwnMXheBDR9xcrCGV6nKMWDEEb7HZ9zZmzW-D_7-zvJpykHmO46CqRqjkqIpe_htDw9Jr3unx5IPYDiVpQZo,
     * gender=0,
     * province=,
     * openid=oqnF3wGpyJNLeQIGPBt3UQpBS7Q8,
     * profile_image_url=http: //wx.qlogo.cn/mmopen/PiajxSqBRaEKnA88aQHTjcyL3iamq60kE9fVEg8Jgyib7uPaeThHQj8mPggZoIZlNOdgiaSTViccW2BZXvXGMF9iaNXf9bYXeYl32BBg46zgGeqPU/0,
     * country=中国,
     * access_token=6aWq7LXwYsbMHsHGcaK8SiYpTOy2cnmy_WR_LJNv6wmuTGtWghf3pVv552GOerDzHDXV8bws2JZZlyLFRKYD_gFMblcubo5EAU4SyMWfHN0,
     * iconurl=http: //wx.qlogo.cn/mmopen/PiajxSqBRaEKnA88aQHTjcyL3iamq60kE9fVEg8Jgyib7uPaeThHQj8mPggZoIZlNOdgiaSTViccW2BZXvXGMF9iaNXf9bYXeYl32BBg46zgGeqPU/0,
     * name=alienware,
     * uid=o7RAwt26jDrJKHUlD8NYJGWVz8SE,
     * expiration=1488449184208,
     * language=zh_CN,
     * expires_in=1488449184208
     * }
     *
     * @param share_media
     * @param i
     * @param map
     */
    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        super.onComplete(share_media, i, map);
        umengDataMap = map;
        switch (share_media) {
            case WEIXIN:
                getData(true);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        JsonObject weixinLoginParam = getWeixinLoginParam();
        if (weixinLoginParam == null) return;
        showLoadingDialog(null);
        getApi().loginWithWeiXin(RequestUtils.createJsonBody(weixinLoginParam.toString()))
                .enqueue(new SimpleCallBack<AlphaUserInfo>() {
                    @Override
                    public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                        if (response.body().result != null) {
                            AlphaClient.getInstance().setOfficeId(response.body().result.getOfficeId());
                            AlphaClient.getInstance().setToken(response.body().result.getToken());

                            //下一步 im的token
                            getChatEaseAccount(response.body().result);
                        } else {
                            dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<AlphaUserInfo>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Nullable
    @CheckResult
    private JsonObject getWeixinLoginParam() {
        if (umengDataMap == null) return null;
        JsonObject jsonObject = new JsonObject();
        //注意 服务器api 是opneid 微信是openid
        jsonObject.addProperty("opneid", umengDataMap.get("openid"));
        jsonObject.addProperty("unionid", umengDataMap.get("unionid"));
        jsonObject.addProperty("uniqueDevice", "device");
        jsonObject.addProperty("deviceTyp", "android");
        return jsonObject;
    }


    /**
     * 获取云信登陆的token
     *
     * @param result
     */
    private void getChatEaseAccount(@NonNull final AlphaUserInfo result) {
        showLoadingDialog(null);
        getApi().getChatToken()
                .enqueue(new SimpleCallBack<LoginIMToken>() {
                    @Override
                    public void onSuccess(Call<ResEntity<LoginIMToken>> call, Response<ResEntity<LoginIMToken>> response) {
                        if (response.body().result == null) {
                            dismissLoadingDialog();
                        } else {
                            result.setOfficeId(response.body().result.thirdpartId);
                            result.setChatToken(response.body().result.chatToken);
                            result.setLoginTime(System.currentTimeMillis());

                            //保存登陆信息
                            saveLoginUserInfo(result);
                            //登陆云信im
                            loginChatEase(response.body().result);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<LoginIMToken>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 登陆云信im
     *
     * @param result
     */
    private void loginChatEase(final LoginIMToken result) {
        if (result == null) {
            dismissLoadingDialog();
        } else {
            String upperThirdpartId = TextUtils.isEmpty(result.thirdpartId) ? result.thirdpartId : result.thirdpartId.toUpperCase();
            NIMClient.getService(AuthService.class)
                    .login(new LoginInfo(upperThirdpartId, result.chatToken))
                    .setCallback(new RequestCallback<LoginInfo>() {

                        @Override
                        public void onSuccess(LoginInfo param) {
                            log("-------------->thread:" + Thread.currentThread().getName());
                            // 初始化消息提醒
                            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
                            NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                            // 进入主界面
                            MainActivity.launch(getContext());
                        }

                        @Override
                        public void onFailed(int code) {
                            log("------------>云信登陆失败：" + code);
                            if (code == 302 || code == 404) {

                            } else {

                            }
                            feedBackYunXinLog(result + " code:" + code);
                            // 进入主界面
                            MainActivity.launch(getContext());
                        }

                        @Override
                        public void onException(Throwable exception) {
                            feedBackYunXinLog(result + " ex:" + StringUtils.throwable2string(exception));
                            // 进入主界面
                            MainActivity.launch(getContext());
                        }
                    });
        }
    }

    public void feedBackYunXinLog(String errorlog) {
        LogUtils.feedToServer("云信登陆异常:" + errorlog);
    }

    @OnClick({R.id.loginWeixinBtn, R.id.actionLoginWithPwd})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.loginWeixinBtn:
                doOauth(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.actionLoginWithPwd:
                LoginWithPwdActivity.launch(getContext());
                break;
        }
    }
}
