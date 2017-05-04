package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.GroupDetailEntity;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.bean.GroupMemberEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.LoginIMToken;
import com.icourt.alpha.entity.bean.MsgConvert2Task;
import com.icourt.alpha.entity.bean.PageEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.httpmodel.ResEntity;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author xuanyouwu
 * @email xuanyouwu@163.com
 * @time 2016-06-02 14:26
 * <p>
 * 分页公共参数 整形  请大家按照这个【顺序】写
 * @Query("pageNum") int pageNum,
 * @Query("pageSize") int pageSize
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
    @Deprecated
    @GET("api/v1/auth/q/allByOfficeId/{officeId}")
    Call<ResEntity<List<GroupContactBean>>> getGroupContacts(@Path("officeId") String officeId);

    /***
     * 获取匹配联系人
     * @param name
     * @return
     */
    @GET("api/v1/auth/up/getAllLawyerByName")
    Call<ResEntity<List<GroupMemberEntity>>> queryGroupContacts(@Query("name") String name);

    /**
     * 获取机器人
     *
     * @return
     */
    @GET("api/v1/auth/up/getRobot")
    Call<ResEntity<List<GroupContactBean>>> getRobos();


    /**
     * 根据不同类型获取文件列表
     *
     * @param type     TYPE_ALL_FILE = 0;  TYPE_MY_FILE = 1;
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET("api/v2/chat/msg/findFileMsg")
    Call<ResEntity<List<IMMessageCustomBody>>> getFilesByType(
            @Query("type") int type,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    /**
     * 获取  @我  的消息
     * 【注意 这个接口只能post】
     *
     * @param pageNum  第n页
     * @param pageSize 每页获取条目数量
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/ats")
    Call<ResEntity<List<IMMessageCustomBody>>> getAtMeMsg(@Query("pageNum") int pageNum,
                                                          @Query("pageSize") int pageSize);

    /**
     * 获取我收藏的消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param pageNum  第n页
     * @param pageSize 每页获取条目数量
     * @return
     */
    @GET("api/v2/chat/msg/getStarSign")
    Call<ResEntity<List<IMMessageCustomBody>>> getMyCollectedMessages(@Query("pageNum") int pageNum,
                                                                      @Query("pageSize") int pageSize);

    /**
     * 获取钉的消息
     * 文档地址 https://www.showdoc.cc/1620156?page_id=14899073
     *
     * @param ope
     * @param to
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/pins")
    Call<ResEntity<List<IMMessageCustomBody>>> getDingMessages(@Query("ope") @Const.CHAT_TYPE int ope,
                                                               @Query("to") String to);

    /**
     * 获取搜索引擎列表
     */
    @GET("api/v2/site/getSiteList")
    Call<ResEntity<List<SearchEngineEntity>>> getSearchEngines();

    /**
     * 获取客户列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET("api/v2/contact")
    Call<ResEntity<List<CustomerEntity>>> getCustomers(@Query("pageNum") int pageNum,
                                                       @Query("pageSize") int pageSize);

    /**
     * 获取客户列表
     *
     * @param pageNum
     * @param pageSize
     * @param isView   是否关注的 关注==1
     * @return
     */
    @GET("api/v2/contact")
    Call<ResEntity<List<CustomerEntity>>> getCustomers(@Query("pageNum") int pageNum,
                                                       @Query("pageSize") int pageSize,
                                                       @Query("isView") int isView);

    /**
     * 获取所有任务
     *
     * @return
     */
    @GET("api/v2/taskflow/queryTaskByDue")
    Call<ResEntity<PageEntity<TaskEntity>>> getAllTask();

    /**
     * 获取我加入的讨论组
     * <p>
     * 新版 groupQueryAll()
     *
     * @return
     */
    @Deprecated
    @GET("api/v2/chat/group/inGroup")
    Call<ResEntity<List<GroupEntity>>> getMyJoinedGroups();

    /**
     * 获取所有的讨论组
     *
     * @return
     */
    @Deprecated
    @GET("api/v2/chat/group/LawyerGroup")
    Call<ResEntity<List<GroupEntity>>> getAllGroups();


    /**
     * 搜索我加入的讨论组
     *
     * @return
     */
    @GET("api/v2/chat/group/inGroup")
    Call<ResEntity<List<GroupEntity>>> searchInMyJoinedGroup(@Query("name") String groupName);

    /**
     * 搜索 全部的讨论组
     *
     * @return
     */
    @GET("api/v2/chat/group/LawyerGroup")
    Call<ResEntity<List<GroupEntity>>> searchInAllGroup(@Query("name") String groupName);

    /**
     * 获取 讨论组详情
     *
     * @param tid
     * @return
     */
    @Deprecated
    @GET("api/v2/chat/group/findGroupByTid")
    Call<ResEntity<GroupDetailEntity>> getGroupByTid(@Query("tid") String tid);

    /**
     * 获取 讨论组成员列表
     *
     * @param tid
     * @return
     */
    @GET("api/v2/chat/group/mems/{tid}")
    Call<ResEntity<List<GroupMemberEntity>>> getGroupMemeber(@Path("tid") String tid);


    /**
     * 创建群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/groups")
    Call<ResEntity<JsonElement>> groupCreate(@Body RequestBody groupInfo);


    /**
     * 更新 群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param groupInfo
     * @return
     */
    @PUT("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{tid}")
    Call<ResEntity<JsonElement>> groupUpdate(@Path("tid") String tid,
                                             @Body RequestBody groupInfo);


    /**
     * 加入讨论组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param group_tid
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{group_tid}/members/joined")
    Call<ResEntity<Boolean>> groupJoin(@Path("group_tid") String group_tid);

    /**
     * 退出讨论组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param group_tid
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{group_tid}/members/quit")
    Call<ResEntity<Boolean>> groupQuit(@Path("group_tid") String group_tid);

    /**
     * 获取群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param run_status 0：正常 1：归档 ，不传为所有
     * @param is_private true: 公开；false:私密 ，不传为所有
     * @param joined     true: 加入的；false:未加入的 ，不传为已加入的和未加入公开的
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQuery(@Query("run_status") int run_status,
                                                   @Query("is_private") boolean is_private,
                                                   @Query("joined") boolean joined
    );

    /**
     * 获取群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param run_status 0：正常 1：归档 ，不传为所有
     * @param is_private true: 公开；false:私密 ，不传为所有
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQuery(@Query("run_status") int run_status,
                                                   @Query("is_private") boolean is_private
    );

    /**
     * 获取我加入的群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param run_status
     * @param joined
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQueryJoind(@Query("run_status") int run_status,
                                                        @Query("joined") boolean joined
    );

    /**
     * 获取所有群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQueryAll();

    /**
     * 获取 群组 详情
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{tid}")
    Call<ResEntity<GroupDetailEntity>> groupQueryDetail(@Path("tid") String tid);


    /**
     * 群组添加成员
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param groupId 群组id 非云信id
     * @param members {members":["xx1","xx2","xx3"] msg_id":12321 //当前群组的最新消息id,获取不到则不传}
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{groupId}/members")
    Call<ResEntity<JsonElement>> groupMemberAdd(@Path("groupId") String groupId,
                                                @Body RequestBody members);

    /**
     * 群组 移除成员
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param groupId
     * @param userId
     * @return
     */
    @DELETE("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{groupId}/members/{userId}")
    Call<ResEntity<JsonElement>> groupMemberRemove(@Path("groupId") String groupId,
                                                   @Path("userId") String userId);


    /**
     * 批量移除成员
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param group_tid
     * @param body
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/groups/{group_tid}/members/delete")
    Call<ResEntity<JsonElement>> groupMemberRemoves(@Path("group_tid") String group_tid,
                                                    @Body RequestBody body);

    /**
     * 添加 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msg 消息体
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/msgs")
    Call<ResEntity<Boolean>> msgAdd(@Body RequestBody msg);

    /**
     * 收藏 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msgId
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/stars/{msgId}")
    Call<ResEntity<Boolean>> msgCollect(@Path("msgId") String msgId,
                                        @Query("ope") @Const.CHAT_TYPE int ope,
                                        @Query("to") String to);

    /**
     * 取消收藏 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msgId
     * @return
     */
    @DELETE("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/stars/{msgId}")
    Call<ResEntity<Boolean>> msgCollectCancel(@Path("msgId") String msgId,
                                              @Query("ope") @Const.CHAT_TYPE int ope,
                                              @Query("to") String to);

    /**
     * 获取收藏的消息ids
     * 接口地址:https://www.showdoc.cc/1620156?page_id=14899067
     *
     * @param ope
     * @param to
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/stars/ids")
    Call<ResEntity<List<String>>> msgQueryAllCollectedIds(@Query("ope") @Const.CHAT_TYPE int ope,
                                                          @Query("to") String to);

    /**
     * 获取所有钉的消息id
     *
     * @param ope
     * @param to
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/pins/ids")
    Call<ResEntity<List<String>>> msgQueryAllDingIds(@Query("ope") @Const.CHAT_TYPE int ope,
                                                     @Query("to") String to);

    /**
     * 撤回 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msgId
     * @return
     */
    @DELETE("http://10.25.115.31:8083/ilaw/api/v3/im/msgs/{msgId}")
    Call<ResEntity<JsonElement>> msgRevoke(@Path("msgId") String msgId);


    /**
     * 消息转任务
     *
     * @param content
     * @return
     */
    @POST("api/v2/chat/msg/analysisTask")
    @FormUrlEncoded
    Call<ResEntity<MsgConvert2Task>> msgConvert2Task(@Field("content") String content);


    /**
     * 查询网络消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param type
     * @param size
     * @param mgs_id
     * @param ope
     * @param to
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/msgs")
    Call<ResEntity<List<IMMessageCustomBody>>> msgQueryAll(@Query("type") String type,
                                                           @Query("size") int size,
                                                           @Query("mgs_id") String mgs_id,
                                                           @Query("ope") @Const.CHAT_TYPE int ope,
                                                           @Query("to") String to
    );

    /**
     * 查询所有联系人【客户端理解为联系人】
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/users")
    Call<ResEntity<List<GroupContactBean>>> usersQuery();

    /**
     * 获取置顶的会话ids
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14902507
     *
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/chats/sticks/ids")
    Call<ResEntity<List<String>>> sessionQueryAllsetTopIds();

    /**
     * 会话置顶
     *
     * @param ope
     * @param to
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/chats/sticks")
    @FormUrlEncoded
    Call<ResEntity<Boolean>> sessionSetTop(@Field("ope") @Const.CHAT_TYPE int ope,
                                           @Field("to") String to);

    /**
     * 会话取消置顶
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14902507
     *
     * @param ope
     * @param to
     * @return
     */
    @DELETE("http://10.25.115.31:8083/ilaw/api/v3/im/chats/sticks")
    Call<ResEntity<Boolean>> sessionSetTopCancel(@Query("ope") @Const.CHAT_TYPE int ope,
                                                 @Query("to") String to);

    /**
     * 获取所有会话免打扰id
     *
     * @return
     */
    @GET("http://10.25.115.31:8083/ilaw/api/v3/im/chats/nodisturbing/ids")
    Call<ResEntity<List<String>>> sessionQueryAllNoDisturbingIds();

    /**
     * 会话免打扰
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14902507
     *
     * @param ope
     * @param to
     * @return
     */
    @POST("http://10.25.115.31:8083/ilaw/api/v3/im/chats/nodisturbing")
    @FormUrlEncoded
    Call<ResEntity<Boolean>> sessionNoDisturbing(@Const.CHAT_TYPE @Field("ope") int ope,
                                                 @Field("to") String to);

    /**
     * 取消会话免打扰
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14902507
     *
     * @param ope
     * @param to
     * @return
     */
    @DELETE("http://10.25.115.31:8083/ilaw/api/v3/im/chats/nodisturbing")
    Call<ResEntity<Boolean>> sessionNoDisturbingCancel(@Query("ope") @Const.CHAT_TYPE int ope,
                                                       @Query("to") String to);

    /**
     * 群组文件上传
     *
     * @param groupId
     * @param params
     * @return
     */
    @POST("api/v2/file/upload")
    @Multipart
    Call<ResEntity<JsonElement>> groupUploadFile(@Query("groupId") String groupId,
                                                 @PartMap Map<String, RequestBody> params
    );

    /**
     * 项目列表
     *
     * @param pageindex
     * @param pagesize
     * @param orderby
     * @param ordertype
     * @param status
     * @param matterType
     * @param attorneyType
     * @param myStar
     * @return
     */
    @GET("api/v1/matters")
    Call<ResEntity<List<ProjectEntity>>> projectQueryAll(@Query("pageindex") int pageindex,
                                                         @Query("pagesize") int pagesize,
                                                         @Query("orderby") String orderby,
                                                         @Query("ordertype") String ordertype,
                                                         @Query("status") String status,
                                                         @Query("matterType") String matterType,
                                                         @Query("attorneyType") String attorneyType,
                                                         @Query("myStar") String myStar
    );

    /**
     * 获取项目概览
     *
     * @param id
     * @return
     */
    @GET("api/v1/matters/{id}")
    Call<ResEntity<List<ProjectDetailEntity>>> projectDetail(@Path("id") String id);
}
