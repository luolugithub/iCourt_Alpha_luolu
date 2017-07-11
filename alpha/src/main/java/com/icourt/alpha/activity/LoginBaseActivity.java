package com.icourt.alpha.activity;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.bugtags.library.Bugtags;
import com.google.gson.JsonObject;
import com.icourt.alpha.base.BaseUmengActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.LoginIMToken;
import com.icourt.alpha.http.AlphaClient;
import com.icourt.alpha.http.callback.BaseCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.service.SyncDataService;
import com.icourt.alpha.utils.BugUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.UserPreferences;
import com.icourt.api.RequestUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  登陆基类封装
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/10
 * version 1.0.0
 */
public class LoginBaseActivity extends BaseUmengActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        super.onCreate(savedInstanceState);
        NIMClient.getService(AuthService.class).logout();
        deleteOauth(SHARE_MEDIA.WEIXIN);
        clearLoginUserInfo();
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
        if (map == null) return;
        switch (share_media) {
            case WEIXIN:
                loginWithWeiXin(map.get("openid"), map.get("unionid"));
                break;
        }
    }

    @Nullable
    @CheckResult
    private JsonObject getWeixinLoginParam(String openid, String unionid) {
        JsonObject jsonObject = new JsonObject();
        //注意 服务器api 是opneid 微信是openid
        jsonObject.addProperty("opneid", openid);
        jsonObject.addProperty("unionid", unionid);
        jsonObject.addProperty("uniqueDevice", "device");
        jsonObject.addProperty("deviceTyp", "android");
        return jsonObject;
    }

    private void loginWithWeiXin(String openid, String unionid) {
        JsonObject weixinLoginParam = getWeixinLoginParam(openid, unionid);
        if (weixinLoginParam == null) return;
        showLoadingDialog(null);
        getApi().loginWithWeiXin(RequestUtils.createJsonBody(weixinLoginParam.toString()))
                .enqueue(new SimpleCallBack<AlphaUserInfo>() {
                    @Override
                    public void onSuccess(Call<ResEntity<AlphaUserInfo>> call, Response<ResEntity<AlphaUserInfo>> response) {
                        if (response.body().result != null) {
                            AlphaClient.setOfficeId(response.body().result.getOfficeId());
                            AlphaClient.setToken(response.body().result.getToken());

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

    /* 获取云信登陆的token
    *
            * @param result
    */
    private void getChatEaseAccount(@NonNull final AlphaUserInfo result) {
        log("-------->token:" + result != null ? result.getToken() : "");
        showLoadingDialog(null);
        getChatApi().getChatToken()
                .enqueue(new SimpleCallBack<LoginIMToken>() {
                    @Override
                    public void onSuccess(Call<ResEntity<LoginIMToken>> call, Response<ResEntity<LoginIMToken>> response) {
                        if (response.body().result == null) {
                            dismissLoadingDialog();
                        } else {
                            result.setThirdpartId(response.body().result.accid);
                            result.setChatToken(response.body().result.imToken);
                            result.setLoginTime(System.currentTimeMillis());

                            //设置bugtags用户数据
                            try {
                                Bugtags.setUserData("AlphaUserInfo", JsonUtils.Gson2String(result));
                            } catch (Throwable e) {
                            }
                            //保存登陆信息
                            saveLoginUserInfo(result);

                            //神策统计
                           /* SensorsDataAPI.sharedInstance(getContext())
                                    .login(result.getUserId());*/
                            SyncDataService.startSysnContact(getContext());

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
            //模拟用户
       /*     result.accid = "D6A26515644911E7855190E2BACDCE28";
            result.imToken = "d781a5ee1ccff209b403cc6cf924d6c3";*/
            NIMClient.getService(AuthService.class)
                    .login(new LoginInfo(result.accid, result.imToken))
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
                            showToast("云信登陆失败:" + code);
                            if (code == 302 || code == 404) {

                            } else {

                            }
                            bugSync("云信登陆失败", result + " code:" + code);
                            // 进入主界面
                            MainActivity.launch(getContext());
                        }

                        @Override
                        public void onException(Throwable exception) {
                            showToast("云信登陆异常:" + exception);
                            bugSync("云信登陆异常", exception);
                            // 进入主界面
                            MainActivity.launch(getContext());
                        }
                    });
        }
    }


    @Nullable
    @CheckResult
    private JsonObject getPwdLoginParam(String user, String pwd) {
        JsonObject jsonObject = new JsonObject();
        //注意 服务器api 是opneid 微信是openid
        jsonObject.addProperty("user", user);
        jsonObject.addProperty("password", pwd);
        jsonObject.addProperty("uniqueDevice", "device");
        jsonObject.addProperty("deviceTyp", "android");
        return jsonObject;
    }


    /**
     * 密码登陆
     *
     * @param user 账号
     * @param pwd  密码
     */
    protected final void loginWithPwd(@NonNull String user, @NonNull String pwd) {
        JsonObject pwdLoginParam = getPwdLoginParam(user, pwd);
        if (pwdLoginParam == null) return;
        showLoadingDialog(null);
        getApi()
                .loginWithPwd(RequestUtils.createJsonBody(pwdLoginParam.toString()))
                .enqueue(new BaseCallBack<AlphaUserInfo>() {
                    @Override
                    protected void dispatchHttpSuccess(Call<AlphaUserInfo> call, Response<AlphaUserInfo> response) {
                        if (response.body() != null && response.body().succeed) {
                            onSuccess(call, response);
                        } else {
                            onFailure(call,
                                    response.body() != null ?
                                            new ResponseException(-2, response.body().message)
                                            : new ResponseException(-1, "响应为null"));
                        }
                    }

                    @Override
                    public void onSuccess(Call<AlphaUserInfo> call, Response<AlphaUserInfo> response) {
                        dismissLoadingDialog();
                        if (response.body() != null) {
                            AlphaClient.setOfficeId(response.body().getOfficeId());
                            AlphaClient.setToken(response.body().getToken());

                            //下一步 im的token
                            getChatEaseAccount(response.body());
                        } else {
                            dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<AlphaUserInfo> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 微信登陆
     */
    protected final void loginWithWeiXin() {
        doOauth(SHARE_MEDIA.WEIXIN);
    }

}
