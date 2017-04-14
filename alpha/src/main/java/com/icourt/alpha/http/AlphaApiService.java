package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMSessionDontDisturbEntity;
import com.icourt.alpha.entity.bean.LoginIMToken;
import com.icourt.alpha.http.httpmodel.ResEntity;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-02 14:26
 * <p>
 * 分页公共参数 整形  请大家按照这个【顺序】写
 * @Field("start") int start,
 * @Field("limit") int limit,
 * @Field("maxId") int maxId,
 */
public interface AlphaApiService {


    /**
     * 获取新版本app
     * 文档参考 https://fir.im/docs/version_detection
     *
     * @param url fir地址
     * @return
     */
    @GET
    Call<AppVersionEntity> getNewVersionAppInfo(
            @Url String url);

    /**
     * demo 数据获取[非分页]
     *
     * @param id
     * @return
     */
    @GET("api/v1/auth/groups")
    Call<ResEntity<String>> getData(@Query("id") int id);


    /**
     * demo 分页数据获取
     *
     * @param id
     * @return
     */
    @GET("api/v1/auth/groups")
    Call<ResEntity<List<String>>> getPageData(@Query("id") int id);

    /**
     * 修改律师电话信息
     *
     * @param phone 手机号码 不包含+86国际代码的字符串
     * @return
     */
    @POST("api/v1/auth/update")
    @FormUrlEncoded
    Call<ResEntity<String>> updateUserPhone(@Field("phone") String phone);

    /**
     * 修改律师邮箱信息
     *
     * @param email
     * @return
     */
    @POST("api/v1/auth/update")
    @FormUrlEncoded
    Call<ResEntity<String>> updateUserEmail(@Field("email") String email);

    /**
     * 微信登陆
     * <p>
     * 将"opneid" "unionid" "uniqueDevice"="device"; "deviceType"="android" 组合成json
     *
     * @return
     */
    @POST("v2/weixinlogin/getTokenByOpenidAndUnionid")
    Call<ResEntity<AlphaUserInfo>> loginWithWeiXin(@Body RequestBody info);

    /**
     * 账号密码登陆
     *
     * @param info json请求体
     * @return
     */
    @POST("api/v1/auth/login")
    Call<AlphaUserInfo> loginWithPwd(@Body RequestBody info);

    /**
     * 获取云信登陆的token
     *
     * @return
     */
    @GET("api/v2/chat/msg/token")
    Call<ResEntity<LoginIMToken>> getChatToken();

    /**
     * 刷新登陆refreshToken过时
     * 注意请求的key是 refreshToekn
     * 注意这个api 不支持post
     *
     * @param refreshToken 已经登陆的refreshToken
     * @return
     */
    @GET("api/v1/auth/refresh")
    Call<ResEntity<AlphaUserInfo>> refreshToken(@Query("refreshToekn") String refreshToken);

    /**
     * 获取团队联系人列表
     *
     * @param officeId 在登陆信息中有
     * @return
     */
    @GET("api/v1/auth/q/allByOfficeId/{officeId}")
    Call<ResEntity<List<GroupContactBean>>> getGroupContacts(@Path("officeId") String officeId);

    /**
     * 获取机器人
     *
     * @return
     */
    @GET("api/v1/auth/up/getRobot")
    Call<ResEntity<List<GroupContactBean>>> getRobos();

    /**
     * 获取消息免打扰列表  非免打扰的team 不返回
     * 【注意】 目前单聊没有免打扰 是群聊免打扰
     *
     * @return
     */
    @GET("api/v2/chat/group/getNoDisturbing")
    Call<ResEntity<List<IMSessionDontDisturbEntity>>> getDontDisturbs();

    /**
     * 是否置顶
     *
     * @param p2pId
     * @return
     */
    @GET("api/v2/chat/group/isStarred")
    Call<ResEntity<Integer>> isSetTop(@Query("p2pId") String p2pId);


    /**
     * 置顶
     *
     * @param p2pId
     * @return
     */
    @GET("api/v2/chat/group/setStarred")
    Call<ResEntity<Integer>> setTop(@Query("p2pId") String p2pId);
    /**
     * 设置消息免打扰
     *
     * @param groupId 群组id
     * @return
     */
    @POST("api/v2/chat/group/setNoDisturbing")
    @FormUrlEncoded
    Call<ResEntity<JsonElement>> setNoDisturbing(@Field("groupId") String groupId);
}
