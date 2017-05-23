package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.GroupDetailEntity;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
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

/**
 * Description  聊天API
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/10
 * version 1.0.0
 */
public interface ApiChatService {

    /**
     * 创建群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @return
     */
    @POST("im/v1/groups")
    Call<ResEntity<JsonElement>> groupCreate(@Body RequestBody groupInfo);


    /**
     * 更新 群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param groupInfo
     * @return
     */
    @PUT("im/v1/groups/{tid}")
    Call<ResEntity<JsonElement>> groupUpdate(@Path("tid") String tid,
                                             @Body RequestBody groupInfo);


    /**
     * 加入讨论组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param group_tid
     * @return
     */
    @POST("im/v1/groups/{group_tid}/members/joined")
    Call<ResEntity<Boolean>> groupJoin(@Path("group_tid") String group_tid);

    /**
     * 退出讨论组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param group_tid
     * @return
     */
    @POST("im/v1/groups/{group_tid}/members/quit")
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
    @GET("im/v1/groups")
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
    @GET("im/v1/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQuery(@Query("run_status") int run_status,
                                                   @Query("is_private") boolean is_private
    );


    /**
     * 获取讨论组成员id列表
     *
     * @param groupTid
     * @return
     */
    @GET("im/v1/groups/{groupTid}/members/ids")
    Call<ResEntity<List<String>>> groupQueryAllMemberIds(@Path("groupTid") String groupTid);

    /**
     * 获取我加入的群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param run_status
     * @param joined
     * @return
     */
    @GET("im/v1/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQueryJoind(@Query("run_status") int run_status,
                                                        @Query("joined") boolean joined
    );

    /**
     * 转让讨论组管理员
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14913324
     *
     * @param groupTid
     * @param adminId
     * @return
     */
    @PUT("im/v1/groups/{groupTid}/admin/{adminId}")
    Call<ResEntity<Boolean>> groupTransferAdmin(@Path("groupTid") String groupTid,
                                                @Path("adminId") String adminId);

    /**
     * 获取所有群组
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @return
     */
    @GET("im/v1/groups")
    Call<ResEntity<List<GroupEntity>>> groupsQueryAll();

    /**
     * 获取 群组 详情
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @return
     */
    @GET("im/v1/groups/{tid}")
    Call<ResEntity<GroupDetailEntity>> groupQueryDetail(@Path("tid") String tid);


    /**
     * 群组添加成员
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param groupId 群组id 非云信id
     * @param members {members":["xx1","xx2","xx3"] msg_id":12321 //当前群组的最新消息id,获取不到则不传}
     * @return
     */
    @POST("im/v1/groups/{groupId}/members")
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
    @DELETE("im/v1/groups/{groupId}/members/{userId}")
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
    @POST("im/v1/groups/{group_tid}/members/delete")
    Call<ResEntity<JsonElement>> groupMemberRemoves(@Path("group_tid") String group_tid,
                                                    @Body RequestBody body);

    /**
     * 添加 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msg 消息体
     * @return
     */
    @POST("im/v1/msgs")
    Call<ResEntity<IMMessageCustomBody>> msgAdd(@Body RequestBody msg);

    /**
     * 消息批量转发
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param body
     * @return
     */
    @POST("im/v1/msgs/trans")
    Call<ResEntity<JsonElement>> msgTrans(@Body RequestBody body);


    /**
     * 文件消息发送
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14909461
     *
     * @param params
     * @return
     */
    @Multipart
    @POST("im/v1/msgs/files")
    Call<ResEntity<IMMessageCustomBody>> msgImageAdd(@PartMap Map<String, RequestBody> params);


    /**
     * 收藏 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msgId
     * @return
     */
    @POST("im/v1/msgs/stars/{msgId}")
    Call<ResEntity<Boolean>> msgCollect(@Path("msgId") long msgId,
                                        @Query("ope") @Const.CHAT_TYPE int ope,
                                        @Query("to") String to);

    /**
     * 取消收藏 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msgId
     * @return
     */
    @DELETE("im/v1/msgs/stars/{msgId}")
    Call<ResEntity<Boolean>> msgCollectCancel(@Path("msgId") long msgId,
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
    @GET("im/v1/msgs/stars/ids")
    Call<ResEntity<List<Long>>> msgQueryAllCollectedIds(@Query("ope") @Const.CHAT_TYPE int ope,
                                                        @Query("to") String to);

    /**
     * 获取所有钉的消息id
     *
     * @param ope
     * @param to
     * @return
     */
    @GET("im/v1/msgs/pins/ids")
    Call<ResEntity<List<Long>>> msgQueryAllDingIds(@Query("ope") @Const.CHAT_TYPE int ope,
                                                   @Query("to") String to);

    /**
     * 撤回 消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @param msgId
     * @return
     */
    @DELETE("im/v1/msgs/{msgId}")
    Call<ResEntity<JsonElement>> msgRevoke(@Path("msgId") long msgId);


    /**
     * 获取我的文件
     * 文档地址：https://www.showdoc.cc/1620156?page_id=14909461
     *
     * @param msg_id
     * @return
     */
    @GET("im/v1/msgs/files/me")
    Call<ResEntity<List<IMMessageCustomBody>>> getMyFiles(@Query("msg_id") long msg_id);

    /**
     * 获取我的所有文件
     * 文档地址：https://www.showdoc.cc/1620156?page_id=14909461
     *
     * @param msg_id
     * @return
     */
    @GET("im/v1/msgs/files/all")
    Call<ResEntity<List<IMMessageCustomBody>>> getMyAllFiles(@Query("msg_id") long msg_id);

    /**
     * 获取  @我  的消息
     * 【注意 这个接口只能post】
     *
     * @param msg_id
     * @return
     */
    @GET("im/v1/msgs/ats")
    Call<ResEntity<List<IMMessageCustomBody>>> getAtMeMsg(@Query("msg_id") long msg_id);

    /**
     * 获取我收藏的消息
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14892528
     *
     * @param msg_id
     * @return
     */
    @GET("im/v1/msgs/stars")
    Call<ResEntity<List<IMMessageCustomBody>>> getMyCollectedMessages(@Query("msg_id") long msg_id);

    /**
     * 获取钉的消息
     * 文档地址 https://www.showdoc.cc/1620156?page_id=14899073
     *
     * @param ope
     * @param to
     * @param msg_id
     * @return
     */
    @GET("im/v1/msgs/pins")
    Call<ResEntity<List<IMMessageCustomBody>>> getDingMessages(@Query("ope") @Const.CHAT_TYPE int ope,
                                                               @Query("to") String to,
                                                               @Query("msg_id") long msg_id);


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
    @GET("im/v1/msgs")
    Call<ResEntity<List<IMMessageCustomBody>>> msgQueryAll(@Query("type") String type,
                                                           @Query("size") int size,
                                                           @Query("mgs_id") long mgs_id,
                                                           @Query("ope") @Const.CHAT_TYPE int ope,
                                                           @Query("to") String to
    );

    /**
     * 获取文件消息列表
     *
     * @param ope
     * @param to
     * @return
     */
    @GET("im/v1/msgs/files")
    Call<ResEntity<List<IMMessageCustomBody>>> msgQueryFiles(@Query("ope") @Const.CHAT_TYPE int ope,
                                                             @Query("to") String to,
                                                             @Query("msg_id") long msg_id);

    /**
     * 查询所有联系人【客户端理解为联系人】
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14893618
     *
     * @return
     */
    @GET("im/v1/users")
    Call<ResEntity<List<GroupContactBean>>> usersQuery();

    /**
     * 获取置顶的会话ids
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14902507
     *
     * @return
     */
    @GET("im/v1/chats/sticks/ids")
    Call<ResEntity<List<String>>> sessionQueryAllsetTopIds();

    /**
     * 会话置顶
     *
     * @param ope
     * @param to
     * @return
     */
    @POST("im/v1/chats/sticks")
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
    @DELETE("im/v1/chats/sticks")
    Call<ResEntity<Boolean>> sessionSetTopCancel(@Query("ope") @Const.CHAT_TYPE int ope,
                                                 @Query("to") String to);

    /**
     * 获取所有会话免打扰id
     *
     * @return
     */
    @GET("im/v1/chats/nodisturbing/ids")
    Call<ResEntity<List<String>>> sessionQueryAllNoDisturbingIds();

    /**
     * 会话免打扰
     * 文档地址:https://www.showdoc.cc/1620156?page_id=14902507
     *
     * @param ope
     * @param to
     * @return
     */
    @POST("im/v1/chats/nodisturbing")
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
    @DELETE("im/v1/chats/nodisturbing")
    Call<ResEntity<Boolean>> sessionNoDisturbingCancel(@Query("ope") @Const.CHAT_TYPE int ope,
                                                       @Query("to") String to);

    /**
     * 获取我的最新信息
     *
     * @return
     */
    @GET("im/v1/users/me")
    Call<ResEntity<AlphaUserInfo>> userInfoQuery();


    /**
     * 获取文件下载地址
     *
     * @param repo_id
     * @param path
     * @param name
     * @return
     */
    @GET("im/v1/msgs/files/download")
    Call<ResEntity<JsonElement>> fileUrlQuery(@Query("repo_id") String repo_id,
                                              @Query("path") String path,
                                              @Query("name") String name);

}
