package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.AppVersionEntity;
import com.icourt.alpha.entity.bean.CommentEntity;
import com.icourt.alpha.entity.bean.ContactDeatilBean;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.DefaultRepoEntity;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.entity.bean.GroupBean;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.ItemPageEntity;
import com.icourt.alpha.entity.bean.LoginIMToken;
import com.icourt.alpha.entity.bean.MsgConvert2Task;
import com.icourt.alpha.entity.bean.PageEntity;
import com.icourt.alpha.entity.bean.ProjectDetailEntity;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.RepoAdmin;
import com.icourt.alpha.entity.bean.ProjectProcessesEntity;
import com.icourt.alpha.entity.bean.RepoIdResEntity;
import com.icourt.alpha.entity.bean.RepoMatterEntity;
import com.icourt.alpha.entity.bean.SFileLinkInfoEntity;
import com.icourt.alpha.entity.bean.SFileShareUserInfo;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.entity.bean.SelectGroupBean;
import com.icourt.alpha.entity.bean.TaskAttachmentEntity;
import com.icourt.alpha.entity.bean.TaskCheckItemEntity;
import com.icourt.alpha.entity.bean.TaskCountEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.entity.bean.TaskMemberWrapEntity;
import com.icourt.alpha.entity.bean.TaskReminderEntity;
import com.icourt.alpha.entity.bean.TimeEntity;
import com.icourt.alpha.entity.bean.TimingCountEntity;
import com.icourt.alpha.entity.bean.UserDataEntity;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.http.httpmodel.ResEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
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
public interface ApiAlphaService {


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
     * 修改律师电话信息
     *
     * @param phone 手机号码 不包含+86国际代码的字符串
     * @return
     */
    @Deprecated
    @POST("ilaw/api/v1/auth/update")
    @FormUrlEncoded
    Call<ResEntity<String>> updateUserPhone(@Field("phone") String phone);

    /**
     * 修改律师邮箱信息
     *
     * @param email
     * @return
     */
    @Deprecated
    @POST("ilaw/api/v1/auth/update")
    @FormUrlEncoded
    Call<ResEntity<String>> updateUserEmail(@Field("email") String email);

    /**
     * 微信登陆
     * <p>
     * 将"opneid" "unionid" "uniqueDevice"="device"; "deviceType"="android" 组合成json
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/weixinlogin-api/getTokenByOpenidAndUnionidUsingPOST
     *
     * @return
     */
    @POST("ilaw/v2/weixinlogin/getTokenByOpenidAndUnionid")
    Call<ResEntity<AlphaUserInfo>> loginWithWeiXin(@Body RequestBody info);

    /**
     * 账号密码登陆
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/getTokenUsingPOST
     *
     * @param info json请求体
     * @return
     */
    @POST("ilaw/api/v1/auth/login")
    Call<AlphaUserInfo> loginWithPwd(@Body RequestBody info);

    /**
     * 获取云信登陆的token
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/chat-api/getTokenUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v2/chat/msg/token")
    @Deprecated
    Call<ResEntity<LoginIMToken>> getChatToken();

    /**
     * 刷新登陆refreshToken过时
     * 注意请求的key是 refreshToekn
     * 注意这个api 不支持post
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/refreshTokenUsingGET
     *
     * @param refreshToken 已经登陆的refreshToken
     * @return
     */
    @GET("ilaw/api/v1/auth/refresh")
    Call<ResEntity<AlphaUserInfo>> refreshToken(@Query("refreshToekn") String refreshToken);

    /**
     * 获取团队联系人列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/getAllLawyerByOfficeIdUsingGET
     *
     * @param officeId 在登陆信息中有
     * @return
     */
    @Deprecated
    @GET("ilaw/api/v1/auth/q/allByOfficeId/{officeId}")
    Call<ResEntity<List<GroupContactBean>>> getGroupContacts(@Path("officeId") String officeId);


    /**
     * 根据不同类型获取文件列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/chat-api/findFileMsgUsingGET
     *
     * @param type     TYPE_ALL_FILE = 0;  TYPE_MY_FILE = 1;
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/chat/msg/findFileMsg")
    @Deprecated
    Call<ResEntity<List<IMMessageCustomBody>>> getFilesByType(
            @Query("type") int type,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );


    /**
     * 获取搜索引擎列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/site-api/getSiteListUsingGET
     */
    @GET("ilaw/api/v2/site/getSiteList")
    Call<ResEntity<List<SearchEngineEntity>>> getSearchEngines();

    /**
     * 获取客户列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getContactListUsingGET
     *
     * @param pagesize
     * @return
     */
    @GET("ilaw/api/v2/contact")
    Call<ResEntity<List<CustomerEntity>>> getCustomers(@Query("pagesize") int pagesize);

    /**
     * 获取客户列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getContactListUsingGET
     *
     * @param pageindex
     * @param pagesize
     * @param isView    是否关注的 关注==1
     * @return
     */
    @GET("ilaw/api/v2/contact")
    Call<ResEntity<List<CustomerEntity>>> getCustomers(@Query("pageindex") int pageindex,
                                                       @Query("pagesize") int pagesize,
                                                       @Query("isView") int isView);

    /**
     * 获取所有任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskByDueUsingGET
     *
     * @return
     */
    @Deprecated
    @GET("ilaw/api/v2/taskflow/queryTaskByDue")
    Call<ResEntity<PageEntity<TaskEntity>>> getAllTask();


    /**
     * 获取所有任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskByDueUsingGET
     *
     * @return
     */
    @Deprecated
    @GET("ilaw/api/v2/taskflow/queryTaskByDue")
    Call<ResEntity<PageEntity<TaskEntity.TaskItemEntity>>> getAllTask(@Query("dueStart") String dueStart,
                                                                      @Query("dueEnd") String dueEnd,
                                                                      @Query("assignTos") List<String> assignTos,
                                                                      @Query("attentionType") int attentionType);

    /**
     * 消息转任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/chat-api/addTaskUsingPOST
     *
     * @param content
     * @return
     */
    @POST("ilaw/api/v2/chat/msg/analysisTask")
    @FormUrlEncoded
    Call<ResEntity<MsgConvert2Task>> msgConvert2Task(@Field("content") String content);


    /**
     * 群组文件上传
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/file-api/uploadUsingPOST_1
     *
     * @param groupId
     * @param params
     * @return
     */
    @POST("ilaw/api/v2/file/upload")
    @Multipart
    Call<ResEntity<JsonElement>> groupUploadFile(@Query("groupId") String groupId,
                                                 @PartMap Map<String, RequestBody> params
    );

    /**
     * 项目列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getMatterListUsingGET
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
    @GET("ilaw/api/v1/matters")
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
     * 获取选择项目列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getMatterKeyValueListUsingGET
     *
     * @param status 项目状态：[0:预立案 2:进行中 4:已完结 7:已搁置]，多个以英文逗号分隔
     * @return
     */
    @GET("ilaw/api/v1/matters/keyValue")
    Call<ResEntity<List<ProjectEntity>>> projectSelectListQuery(@Query("status") String status);

    /**
     * 获取选择项目列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getMatterListWithRightUsingGET
     *
     * @param pmsStr MAT:matter.document:readwrite 文档读写权限
     * @return
     */
    @GET("ilaw/api/v1/matters/getWithRight")
    Call<ResEntity<List<ProjectEntity>>> projectPmsSelectListQuery(@Query("pmsStr") String pmsStr);

    /**
     * 创建／编辑任务 选择项目列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/getMatterListUsingGET_1
     *
     * @param status
     * @param word
     * @return
     */
    @GET("ilaw/api/v2/taskflow/getMatterList")
    Call<ResEntity<List<ProjectEntity>>> projectSelectByTask(@Query("status") String status,
                                                             @Query("word") String word);

    /**
     * 计时项目列表搜索
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/getMatterListUsingGET_2
     *
     * @param myStar
     * @param status
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/getMatterList")
    Call<ResEntity<List<ProjectEntity>>> timingProjectQuery(
            @Query("myStar") int myStar,
            @Query("status") String status
    );

    /**
     * 计时项目列表搜索
     * pms接口独有
     * <p>
     * 文档地址：文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/getMatterListUsingGET_2
     *
     * @param myStar
     * @param status
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/getMatterList")
    Call<ResEntity<List<ProjectEntity>>> timingProjectQuery(
            @Query("myStar") int myStar,
            @Query("status") String status,
            @Query("word") String word
    );

    /**
     * 获取项目概览
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getMatterByIdUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v1/matters/{id}")
    Call<ResEntity<List<ProjectDetailEntity>>> projectDetail(@Path("id") String id);

    /**
     * 项目添加关注
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/addMatterIsViewUsingPUT
     *
     * @param matterPkid
     * @return
     */
    @PUT("ilaw/api/v1/matters/addStar")
    Call<ResEntity<JsonElement>> projectAddStar(@Query("matterPkid") String matterPkid);

    /**
     * 项目取消关注
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/deleteMatterIsViewUsingDELETE
     *
     * @param matterPkid
     * @return
     */
    @DELETE("ilaw/api/v1/matters/deleteStar")
    Call<ResEntity<JsonElement>> projectDeleteStar(@Query("matterPkid") String matterPkid);

    /**
     * 更新用户信息
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/updateUsingPOST
     *
     * @param id
     * @param phone
     * @param email
     * @return
     */
    @POST("ilaw/api/v1/auth/update")
    Call<ResEntity<JsonElement>> updateUserInfo(@Query("id") String id, @Query("phone") String phone, @Query("email") String email);

    /**
     * 项目下计时列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/findALlTimingByMatterIdUsingGET
     *
     * @param matterId
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/findByMatterId")
    Call<ResEntity<TimeEntity>> projectQueryTimerList(@Query("matterId") String matterId, @Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);


    /**
     * 获取项目详情文档id
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/getRepoIdUsingGET
     *
     * @param projectId
     * @return
     */
    @GET("ilaw/api/v2/documents/getRepo/{projectId}")
    Call<RepoIdResEntity> projectQueryDocumentId(@Path("projectId") String projectId);

    /**
     * 获取项目详情文档id
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/getRepoIdUsingGET
     *
     * @param projectId
     * @return
     */
    @GET("ilaw/api/v2/documents/getRepo/{projectId}")
    Observable<RepoIdResEntity> projectQueryDocumentIdObservable(@Path("projectId") String projectId);


    /**
     * 项目下任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryMatterTaskFlowsUsingGET
     *
     * @param projectId
     * @param stateType 全部任务:－1    已完成:1     未完成:0
     * @param type      任务和任务组：-1;    任务：0;    任务组：1;
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/taskflow/queryMatterTask")
    Call<ResEntity<TaskEntity>> projectQueryTaskList(@Query("assignTos") String assignTos,
                                                     @Query("matterId") String projectId,
                                                     @Query("stateType") int stateType,
                                                     @Query("type") int type,
                                                     @Query("pageIndex") int pageIndex,
                                                     @Query("pageSize") int pageSize);

    /**
     * 项目下任务组列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryFlowByMatterIdUsingGET
     *
     * @param projectId
     * @return
     */
    @GET("ilaw/api/v2/flowmatter/flowbyMatterId")
    Call<ResEntity<List<TaskGroupEntity>>> projectQueryTaskGroupList(@Query("matterId") String projectId);

    /**
     * 新建任务组
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addTaskFlowUsingPOST
     *
     * @param msg
     * @return
     */
    @POST("ilaw/api/v2/taskflow")
    Call<ResEntity<TaskGroupEntity>> taskGroupCreate(@Body RequestBody msg);

    /**
     * 修改任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/updateTaskFlowUsingPUT
     *
     * @param msg
     * @return
     * @see {taskUpdateNew}
     */
    @Deprecated
    @PUT("ilaw/api/v2/taskflow")
    Call<ResEntity<JsonElement>> taskUpdate(@Body RequestBody msg);

    /**
     * 修改任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/updateTaskFlowUsingPUT
     *
     * @param msg
     * @return
     */
    @PUT("ilaw/api/v2/taskflow")
    Call<ResEntity<TaskEntity.TaskItemEntity>> taskUpdateNew(@Body RequestBody msg);

    /**
     * 获取任务详情
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/findTaskFlowUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/taskflow/{id}")
    Call<ResEntity<TaskEntity.TaskItemEntity>> taskQueryDetail(@Path("id") String id);

    /**
     * 更新计时
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/updateTimingUsingPUT
     *
     * @return
     */
    @PUT("ilaw/api/v2/timing/timing/update")
    Call<ResEntity<JsonElement>> timingUpdate(@Body RequestBody body);

    /**
     * 新建计时
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/addTimingUsingPOST
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/timing/timing/add")
    @Deprecated
    Call<ResEntity<String>> timingAdd(@Body RequestBody body);


    /**
     * 开始计时
     * 文档地址: https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/startTimingUsingPOST
     *
     * @param name
     * @param matterPkId
     * @param taskPkId
     * @param workTypeId
     * @param clientId
     * @param manual     如果手动指定参数开始，请将manual置为1
     * @param startTime  手动指定的开始时间，只能小于当前系统时间
     * @return
     */
    @POST("ilaw/api/v2/timing/timing/startTiming")
    @FormUrlEncoded
    Call<ResEntity<TimeEntity.ItemEntity>> timingStart(
            @Field("name") String name,
            @Field("matterPkId") String matterPkId,
            @Field("taskPkId") String taskPkId,
            @Field("workTypeId") String workTypeId,
            @Field("clientId") String clientId,
            @Field("manual") int manual,
            @Field("startTime") long startTime);


    /**
     * 停止计时
     * 文档地址:https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/stopTimingUsingPOST
     *
     * @return
     */
    @POST("ilaw/api/v2/timing/timing/stopTiming")
    @FormUrlEncoded
    Call<ResEntity<TimeEntity.ItemEntity>> timingStop(@Field("pkId") String pkId);

    /**
     * 获取任务下检查项列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskFlowsUsingGET
     *
     * @param taskId
     * @return
     */
    @GET("ilaw/api/v2/taskflow/taskitem")
    Call<ResEntity<TaskCheckItemEntity>> taskCheckItemQuery(@Query("taskId") String taskId);

    /**
     * 修改任务下检查项
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/updateTaskItemUsingPUT
     *
     * @param body
     * @return
     */
    @PUT("ilaw/api/v2/taskflow/taskitem")
    Call<ResEntity<JsonElement>> taskCheckItemUpdate(@Body RequestBody body);

    /**
     * 删除任务下检查项
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/deleteTaskItemUsingDELETE
     *
     * @param id
     * @return
     */
    @DELETE("ilaw/api/v2/taskflow/taskitem/{id}")
    Call<ResEntity<JsonElement>> taskCheckItemDelete(@Path("id") String id);

    /**
     * 添加任务下检查项
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addTaskItemUsingPOST
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/taskflow/taskitem")
    Call<ResEntity<JsonElement>> taskCheckItemCreate(@Body RequestBody body);

    /**
     * 任务添加关注
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addTaskMemberUsingPOST_1
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/taskflow/attention")
    Call<ResEntity<JsonElement>> taskAddStar(@Body RequestBody body);

    /**
     * 任务取消关注
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/deleteTaskFlowUsingDELETE
     *
     * @param id
     * @return
     */
    @DELETE("ilaw/api/v2/taskflow/attention/{id}")
    Call<ResEntity<JsonElement>> taskDeleteStar(@Path("id") String id);

    /**
     * 删除任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/deleteTaskFlowUsingDELETE_1
     *
     * @param id
     * @return
     */
    @DELETE("ilaw/api/v2/taskflow/{id}")
    Call<ResEntity<JsonElement>> taskDelete(@Path("id") String id);

    /**
     * 任务添加评论
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/comment-api/addCommentUsingPOST
     *
     * @param hostType 被评论的对象类型:100为任务
     * @param hostId   被评论的对象id
     * @param content  评论的内容
     * @return
     */
    @POST("ilaw/api/v2/comment")
    Call<ResEntity<JsonElement>> commentCreate(@Query("hostType") int hostType,
                                               @Query("hostId") String hostId,
                                               @Query("content") String content);

    /**
     * 获取评论列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/comment-api/queryCommentsUsingGET
     *
     * @param hostType  被评论的对象类型:100为任务
     * @param hostId    被评论的对象id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/comment")
    Call<ResEntity<CommentEntity>> commentListQuery(@Query("hostType") int hostType,
                                                    @Query("hostId") String hostId,
                                                    @Query("pageIndex") int pageIndex,
                                                    @Query("pageSize") int pageSize);

    /**
     * 任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskFlowsUsingGET_1
     *
     * @param assignedByMe  0：所有； 1：我分配的
     * @param assignTos     分配给谁的，用户的id序列
     * @param attentionType 全部:0    我关注的:1
     * @param orderBy       按指定类型排序或分组；matterId表示按项目排序;createTime表示按日期排序(默认);parentId表示按清单;assignTo表示按负责人排序
     * @param stateType     全部任务:－1    已完成:1     未完成:0
     * @param type          任务和任务组：-1;    任务：0;    任务组：1;
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/taskflow")
    Call<ResEntity<TaskEntity>> taskListQuery(@Query("assignedByMe") int assignedByMe,
                                              @Query("assignTos") String assignTos,
                                              @Query("stateType") int stateType,
                                              @Query("attentionType") int attentionType,
                                              @Query("orderBy") String orderBy,
                                              @Query("pageIndex") int pageIndex,
                                              @Query("pageSize") int pageSize,
                                              @Query("type") int type);

    /**
     * 任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskFlowsUsingGET_1
     *
     * @param assignTos     分配给谁的，用户的id序列
     * @param attentionType 全部:0    我关注的:1
     * @param orderBy       按指定类型排序或分组；matterId表示按项目排序;createTime表示按日期排序(默认);parentId表示按清单;assignTo表示按负责人排序
     * @param stateType     全部任务:－1    已完成:1     未完成:0
     * @param type          任务和任务组：-1;    任务：0;    任务组：1;
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/taskflow")
    Call<ResEntity<TaskEntity>> taskListItemQuery(@Query("assignTos") String assignTos,
                                                  @Query("stateType") int stateType,
                                                  @Query("attentionType") int attentionType,
                                                  @Query("orderBy") String orderBy,
                                                  @Query("pageIndex") int pageIndex,
                                                  @Query("pageSize") int pageSize,
                                                  @Query("type") int type);

    /**
     * 项目下任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskFlowsUsingGET_1
     *
     * @param stateType 全部任务:－1    已完成:1     未完成:0
     * @param matterId
     * @param type      任务和任务组：-1;    任务：0;    任务组：1;
     * @return
     */
    @GET("ilaw/api/v2/taskflow")
    Call<ResEntity<TaskEntity>> taskListQueryByMatterId(
            @Query("stateType") int stateType,
            @Query("orderBy") String orderBy,
            @Query("matterId") String matterId,
            @Query("type") int type,
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize);

    /**
     * 获取任务下的附件列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/getAttachmentsUsingGET
     *
     * @param taskId
     * @return
     */
    @GET("ilaw/api/v2/task/{taskId}/attachments")
    Call<ResEntity<List<TaskAttachmentEntity>>> taskAttachMentListQuery(@Path("taskId") String taskId);

    /**
     * 任务上传附件
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addFromFileUsingPOST
     *
     * @param taskId
     * @param params
     * @return
     */
    @Multipart
    @POST("ilaw/api/v2/task/{taskId}/attachment/addFromFile")
    Call<ResEntity<JsonElement>> taskAttachmentUpload(@Path("taskId") String taskId, @PartMap Map<String, RequestBody> params);

    /**
     * 任务上传附件
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addFromFileUsingPOST
     *
     * @param taskId
     * @param params
     * @return
     */
    @Multipart
    @POST("ilaw/api/v2/task/{taskId}/attachment/addFromFile")
    Observable<JsonElement> taskAttachmentUploadObservable(@Path("taskId") String taskId, @PartMap Map<String, RequestBody> params);

    /**
     * 获取指定时间段的计时
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/queryTimingUsingGET
     *
     * @param createUserId
     * @param startTime    017-05-09
     * @param endTime      017-05-15
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/search")
    Call<ResEntity<TimeEntity>> timingListQueryByTime(@Query("createUserId") String createUserId,
                                                      @Query("startTime") String startTime,
                                                      @Query("endTime") String endTime,
                                                      @Query("pageIndex") int pageIndex,
                                                      @Query("pageSize") int pageSize);

    /**
     * 获取指定时间段的计时统计
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/getTimingCountByTimeUsingGET
     *
     * @param workStartDate 2015-05-03
     * @param workEndDate   2015-05-10
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/timingCountByTime")
    Call<ResEntity<ItemPageEntity<TimingCountEntity>>> queryTimingCountByTime(@Query("workStartDate") String workStartDate,
                                                                              @Query("workEndDate") String workEndDate);


    /**
     * 获取项目下的工作类型
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/getTimingWorkTypeUsingGET
     *
     * @param matterId
     * @return
     */
    @GET("ilaw/api/v2/timing/workTypes")
    Call<ResEntity<List<WorkType>>> queryWorkTypes(@Query("matterId") String matterId);


    /**
     * 获取项目参与人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getMatterAttorneyListUsingGET
     *
     * @param project
     * @return
     */
    @GET("ilaw/api/v1/matters/attorney")
    Call<ResEntity<List<TaskEntity.TaskItemEntity.AttendeeUserEntity>>> taskOwerListQuery(@Query("id") String project,
                                                                                          @Query("name") String name);

    /**
     * 删除计时
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/deleteTimingUsingDELETE
     *
     * @param timerId
     * @return
     */
    @DELETE("ilaw/api/v2/timing/timing/delete/{timerId}")
    Call<ResEntity<JsonElement>> timingDelete(@Path("timerId") String timerId,
                                              @Query("clientId") String clientId);

    /**
     * 新建任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addTaskFlowUsingPOST
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/taskflow")
    Call<ResEntity<TaskEntity.TaskItemEntity>> taskCreate(@Body RequestBody body);


    /**
     * 计时查询
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/queryTimingUsingGET
     *
     * @param pageIndex
     * @param pageSize
     * @param state
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/search")
    Call<ResEntity<PageEntity<TimeEntity.ItemEntity>>> timerQuery(@Query("pageIndex") int pageIndex,
                                                                  @Query("pageSize") int pageSize,
                                                                  @Query("state") int state);


    /**
     * 获取正在计时
     * 文档地址:https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/getRunningTimingUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/getRunningTiming")
    Call<ResEntity<TimeEntity.ItemEntity>> timerRunningQuery();

    /**
     * 获得token里律师所属的团队信息
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/getGroupByTokenUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v1/auth/groups/q/groupByToken")
    Call<ResEntity<List<GroupBean>>> lawyerGroupListQuery();

    /**
     * 获取联系人详情
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getContactDetailUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/contact/detail/{id}")
    Call<ResEntity<List<ContactDeatilBean>>> customerDetailQuery(@Path("id") String id);

    /**
     * 获取联系人详情
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getContactDetailUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/contact/detail/{id}")
    Observable<ResEntity<List<ContactDeatilBean>>> customerDetailQueryObservable(@Path("id") String id);

    /**
     * 获取企业联络人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getRelatedPersonUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/contact/relatedperson/{id}")
    Call<ResEntity<List<ContactDeatilBean>>> customerLiaisonsQuery(@Path("id") String id);

    /**
     * 获取企业联络人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getRelatedPersonUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/contact/relatedperson/{id}")
    Observable<ResEntity<List<ContactDeatilBean>>> customerLiaisonsQueryObservable(@Path("id") String id);

    /**
     * 联系人添加关注
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/addContactStarUsingPUT
     *
     * @param id
     * @return
     */
    @PUT("ilaw/api/v2/contact/addStar/{id}")
    Call<ResEntity<JsonElement>> customerAddStar(@Path("id") String id);

    /**
     * 联系人删除关注
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/deleteContactStarUsingDELETE
     *
     * @param id
     * @return
     */
    @DELETE("ilaw/api/v2/contact/deleteStar/{id}")
    Call<ResEntity<JsonElement>> customerDeleteStar(@Path("id") String id);

    /**
     * 获取所在律所中的团队列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/permission-department-api/getListAllSubDepartmentUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v2/permission/department/allDepartmentList")
    Call<ResEntity<List<SelectGroupBean>>> officeGroupsQuery();

    /**
     * 修改联系人所属团队
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/editContactGroupUsingPUT
     *
     * @param body
     * @return
     */
    @PUT("ilaw/api/v2/contact/group")
    Call<ResEntity<JsonElement>> customerGroupInfoUpdate(@Body RequestBody body);

    /**
     * 获取联系人的联络人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getRelatedPersonUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/contact/relatedperson/{id}")
    Call<ResEntity<List<ContactDeatilBean>>> liaisonsQuery(@Path("id") String id);

    /**
     * 修改联系人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/updateContactByContactIdForMobleUsingPUT
     *
     * @param body
     * @return
     */
    @PUT("ilaw/api/v2/contact/mobile")
    Call<ResEntity<List<ContactDeatilBean>>> customerUpdate(@Body RequestBody body);

    /**
     * 添加联系人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/addContactUsingPOST
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/contact")
    Call<ResEntity<List<ContactDeatilBean>>> customerCreate(@Body RequestBody body);

    /**
     * 检测企业联系人是否重复
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getContactListUsingGET
     *
     * @param accuratename
     * @return
     */
    @GET("ilaw/api/v2/contact")
    Call<ResEntity<List<CustomerEntity>>> companyCheckReName(@Query("accuratename") String accuratename);


    /**
     * 添加企业联系人
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/addCompanyContactUsingPOST
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/contact/company")
    Call<ResEntity<List<ContactDeatilBean>>> customerCompanyCreate(@Body RequestBody body);


    /**
     * 查看任务的成员 对应的成员(有权限)
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/getMembersUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v1/auth/get/members")
    Call<ResEntity<List<TaskMemberWrapEntity>>> getPremissionTaskMembers();

    /**
     * 查看任务的成员 对应的成员(无权限)
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/auth-api/getGroupByTokenUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v1/auth/groups/q/groupByToken")
    Call<ResEntity<List<TaskMemberWrapEntity>>> getUnPremissionTaskMembers();


    /**
     * 获取某人今日计时、本月计时、本月完成任务、本月总任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTimingAndTaskUsingGET
     *
     * @param userId
     * @return
     */
    @GET("ilaw/api/v2/taskflow/getTimingAndTask")
    Call<ResEntity<UserDataEntity>> getUserData(@Query("userId") String userId);


    /**
     * 搜索项目列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getMatterListUsingGET
     *
     * @param queryString
     * @param myStar
     * @return
     */
    @GET("ilaw/api/v1/matters")
    Call<ResEntity<List<ProjectEntity>>> projectQueryByName(@Query("queryString") String queryString,
                                                            @Query("myStar") int myStar);


    /**
     * 搜索任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryMobileTaskUsingGET
     *
     * @param assignTos
     * @param name
     * @param stateType 0:未完成；1：已完成；2：已删除
     * @param queryType 0:全部；1：新任务；2：我关注的；3我部门的
     * @return
     */
    @GET("ilaw/api/v2/taskflow/queryMobileTask")
    Call<ResEntity<TaskEntity>> taskQueryByName(@Query("assignTos") String assignTos,
                                                @Query("name") String name,
                                                @Query("stateType") int stateType,
                                                @Query("queryType") int queryType);


    /**
     * 搜索项目下任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryMobileTaskUsingGET
     *
     * @param assignTos
     * @param name
     * @param stateType 0:未完成；1：已完成；2：已删除
     * @param queryType 0:全部；1：新任务；2：我关注的；3我部门的
     * @return
     */
    @GET("ilaw/api/v2/taskflow/queryMobileTask")
    Call<ResEntity<TaskEntity>> taskQueryByName(@Query("assignTos") String assignTos,
                                                @Query("name") String name,
                                                @Query("stateType") int stateType,
                                                @Query("queryType") int queryType,
                                                @Query("matterId") String matterId);

    /**
     * 搜索项目下任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryMobileTaskUsingGET
     *
     * @param name
     * @param stateType 0:未完成；1：已完成；2：已删除
     * @param queryType 0:全部；1：新任务；2：我关注的；3我部门的
     * @return
     */
    @GET("ilaw/api/v2/taskflow/queryMobileTask")
    Call<ResEntity<TaskEntity>> taskQueryByNameFromMatter(@Query("name") String name,
                                                          @Query("stateType") int stateType,
                                                          @Query("queryType") int queryType,
                                                          @Query("matterId") String matterId);

    /**
     * 搜索已完成任务列表
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryMobileTaskUsingGET
     *
     * @param assignTos
     * @param name
     * @param stateType 0:未完成；1：已完成；2：已删除
     * @param queryType 0:全部；1：新任务；2：我关注的；3我部门的
     * @return
     */
    @GET("ilaw/api/v2/taskflow/queryMobileTask")
    Call<ResEntity<TaskEntity>> taskQueryByName(@Query("assignTos") String assignTos,
                                                @Query("name") String name,
                                                @Query("stateType") int stateType,
                                                @Query("queryType") int queryType,
                                                @Query("pageIndex") int pageIndex,
                                                @Query("pageSize") int pageSize);
    /**************************权限模块**************************/
    /**
     * 获取各个模块是否有权限 接口真烂
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/permission-department-api/getUserViewModuleUsingGET
     *
     * @param userId
     * @param moduleType //MAT,CON,KM,HR,DEP
     * @return
     */
    @GET("ilaw/api/v2/permission/department/getUserViewModule")
    Call<ResEntity<Boolean>> permissionQuery(@Query("userId") String userId,
                                             @Query("moduleType") String moduleType);

    /**
     * 获取是否有新建任务/联系人查看编辑等权限
     * 聚合权限
     * <p>
     * 文档地址：swagger上暂无
     *
     * @param uid
     * @param type      //MAT,CON,KM,HR,DEP
     * @param subjectid
     * @return
     */
    @GET("ilaw/api/v2/permission/engine/{uid}/getPmsStrings")
    Call<ResEntity<List<String>>> permissionQuery(@Path("uid") String uid,
                                                  @Query("type") String type,
                                                  @Query("subjectid") String subjectid);

    /**
     * 获取是否有新建任务/联系人查看编辑等权限
     * 聚合权限
     * <p>
     * 文档地址：swagger上暂无
     *
     * @param uid
     * @param type      //MAT,CON,KM,HR,DEP
     * @param subjectid
     * @return
     */
    @GET("ilaw/api/v2/permission/engine/{uid}/getPmsStrings")
    Observable<ResEntity<List<String>>> permissionQueryObservable(@Path("uid") String uid,
                                                                  @Query("type") String type,
                                                                  @Query("subjectid") String subjectid);

    /**
     * 获取任务详情(返回权限)
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/findTaskFlowWithRightUsingGET
     *
     * @param id
     * @return
     */
    @GET("ilaw/api/v2/taskflow/getWithRight/{id}")
    Call<ResEntity<TaskEntity.TaskItemEntity>> taskQueryDetailWithRight(@Path("id") String id);

    /**
     * 删除任务评论
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/comment-api/deleteCommentUsingDELETE
     *
     * @param id
     * @return
     */
    @DELETE("ilaw/api/v2/comment/{id}")
    Call<ResEntity<JsonElement>> taskDeleteComment(@Path("id") String id);

    /**
     * 获取项目总计时
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/getSumByMatterIdUsingGET
     *
     * @param matterId
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/getSumByMatterId")
    Call<ResEntity<JsonElement>> getSumTimeByMatterId(@Query("matterId") String matterId);

    /**
     * 新任务修改为已读任务
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/newTaskAfterReadingIsUsingPUT
     *
     * @param ids
     * @return
     */
    @PUT("ilaw/api/v2/taskflow/newTaskAfterReadingIs")
    Call<ResEntity<JsonElement>> checkAllNewTask(@Query("ids") List<String> ids);

    /**
     * 删除任务附件
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/unbindUsingPUT
     *
     * @param taskId
     * @param filePath
     * @return
     */
    @PUT("ilaw/api/v2/task/{taskId}/attachment/unbind")
    Call<ResEntity<JsonElement>> taskDocumentDelete(@Path("taskId") String taskId,
                                                    @Query("filePath") String filePath);


    /**
     * 获取联系人
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/contact-api-v2/getContactListUsingGET
     *
     * @param name
     * @return
     */
    @GET("ilaw/api/v2/contact")
    Call<ResEntity<JsonElement>> contactSearch(@Query("pinyininitial") String name);


    @POST("ilaw/api/v2/sha256/getUrl")
    @FormUrlEncoded
    Call<ResEntity<String>> getSha256Url(@Field("id") int id,
                                         @Field("value") String value);

    /**
     * 查询任务提醒
     * 文档地址：https://test.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskRemindersUsingGET
     *
     * @param taskId
     * @return
     */
    @GET("ilaw/api/v2/tasks/{taskId}/reminders")
    Call<ResEntity<TaskReminderEntity>> taskReminderQuery(@Path("taskId") String taskId);

    /**
     * 新增任务提醒
     * 文档地址：https://test.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/addTaskRemindersUsingPOST
     *
     * @param taskId
     * @return
     */
    @POST("ilaw/api/v2/tasks/{taskId}/reminders")
    Call<ResEntity<TaskReminderEntity>> taskReminderAdd(@Path("taskId") String taskId,
                                                        @Body RequestBody body);


    /**************资料库*****************/

    /**
     * sfile 文档token
     * <p>
     * 文档地址：http://testpms.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/getAuthTokenUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v2/documents/getToken")
    Call<SFileTokenEntity<String>> sFileTokenQuery();


    /**
     * 获取律所 管理员
     * 文档地址: https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/getOfficeAdminUsingGET
     *
     * @param userId
     * @return
     */
    @GET("ilaw/api/v2/documents/getOfficeAdmin")
    Call<String> getOfficeAdmin(@Query("userId") String userId);

    /**
     * 返回某个律所资料库对应的管理员uids
     *
     * @param repoId
     * @return
     */
    @GET("ilaw/api/v2/documents/officeLibs/{repoId}/admins")
    Call<ResEntity<List<RepoAdmin>>> getOfficeAdmins(@Path("repoId") String repoId);


    /**
     * 获取sfile分享链接
     * 文档地址:https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/listPathShareLinkInfoUsingGET
     *
     * @param path
     * @param repoId
     * @param type   共享链接类型 0下载 1上传
     * @return
     */
    @GET("ilaw/api/v2/documents/shareLinks")
    Call<SFileLinkInfoEntity> fileShareLinkQuery(
            @Query("repoId") String repoId,
            @Query("path") String path,
            @Query("type") int type);

    /**
     * 文档地址:https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/createShareLinkUsingPOST
     *
     * @param body
     * @return
     */
    @POST("ilaw/api/v2/documents/shareLinks")
    Call<SFileLinkInfoEntity> fileShareLinkCreate(@Body RequestBody body);

    /**
     * 删除文件链接
     * 文档地址:https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/documents-api/deleteShareLinkUsingDELETE
     *
     * @param shareLinkId
     * @return
     */
    @DELETE("ilaw/api/v2/documents/shareLinks/{shareLinkId}")
    Call<ResEntity<JsonElement>> fileShareLinkDelete(@Path("shareLinkId") String shareLinkId);


    /**
     * 获取sfile 用户账号
     *
     * @param path uid 或者uid,uid2,uid3 组合字符串
     * @return
     */
    @GET("ilaw/api/v2/documents/getUserList")
    Call<List<String>> sfileUserInfosQuery(@Query("path") String path);


    /**
     * 我的资料库 修改历史
     *
     * @return
     */
    @GET("docnotice/api/notices/docs/labs/me")
    Call<ResEntity<List<FileChangedHistoryEntity>>> repoMineChangeHistory(@Query("page") int page,
                                                                          @Query("repoId") String repoId);

    /**
     * 共享给我的资料库修改历史
     *
     * @return
     */
    @GET("docnotice/api/notices/docs/labs/shared")
    Call<ResEntity<List<FileChangedHistoryEntity>>> repoSharedChangeHistory(@Query("page") int page,
                                                                            @Query("repoId") String repoId);

    /**
     * 项目资料库修改历史
     *
     * @return
     */
    @GET("docnotice/api/notices/docs/matters")
    Call<ResEntity<List<FileChangedHistoryEntity>>> repoProjectChangeHistory(@Query("page") int page,
                                                                             @Query("matterId") String matterId);


    /**
     * 获取seafile matteid
     *
     * @param seaFileRepoId
     * @return
     */
    @GET("ilaw/api/v2/documents/getmatter/{seaFileRepoId}")
    Call<RepoMatterEntity> repoMatterIdQuery(@Path("seaFileRepoId") String seaFileRepoId);

    /**
     * 我的默认资料库
     *
     * @return
     */
    @GET("ilaw/api/v2/library/mime/default")
    Call<ResEntity<DefaultRepoEntity>> repoDefaultQuery();

    /**
     * 获取文件夹 分享的用户列表
     *
     * @param fromRepoId
     * @param path
     * @return
     */
    @GET("ilaw/api/v2/documents/{seaFileRepoId}/dir/shareUsers")
    Call<ResEntity<List<SFileShareUserInfo>>> folderSharedUserQuery(@Path("seaFileRepoId") String fromRepoId,
                                                                    @Query("path") String path);


    /**
     * 获取各个状态的任务数量
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryTaskStateCountUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v2/taskflow/state/count")
    Call<ResEntity<TaskCountEntity>> taskStateCountQuery();

    /**
     * 获取各个状态、类型的项目数量
     * 文档地址:"https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/queryMatterStateCountUsingGET
     *
     * @param matterTypes 项目类型: [0:争议解决 1:非诉专项 2:常年顾问 3:内部事务], 多个以英文逗号分隔
     * @return
     */
    @GET("ilaw/api/v1/matters/state/count")
    Call<ResEntity<JsonElement>> matterStateCountQuery(@Query("matterTypes") String matterTypes);

    /**
     * 获取新任务数量
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/queryNewTasksUsingGET
     *
     * @return
     */
    @GET("ilaw/api/v2/taskflow/newtasks")
    Call<ResEntity<List<String>>> newTasksCountQuery();

    /**
     * 恢复已删除的任务
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/returnTaskFlowUsingPUT
     *
     * @param taskId
     * @return
     */
    @PUT("ilaw/api/v2/taskflow/revivalTaskFlowById")
    Call<ResEntity<JsonElement>> taskRecoverById(@Query("id") String taskId);

    /**
     * 清空所有已删除的任务
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/taskflow-api/deleteTaskFlowByIdsUsingDELETE
     *
     * @param ids
     * @return
     */
    @DELETE("ilaw/api/v2/taskflow/clearTaskFlowByIds")
    Call<ResEntity<JsonElement>> clearDeletedTask(@Query("ids") List<String> ids);

    /**
     * 获取任务下计时列表
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/timing-api/findTimingByTaskIdUsingGET
     *
     * @param taskId
     * @return
     */
    @GET("ilaw/api/v2/timing/timing/findByTaskId")
    Call<ResEntity<TimeEntity>> taskTimesByIdQuery(@Query("taskId") String taskId);

    /**
     * 任务附件添加 从资料库中选择
     *
     * @param requestBody
     * @return
     */
    @POST("ilaw/api/v2/task/attachment/addFromLibrary")
    Call<ResEntity<String>> taskAddAttachmentFromRepo(@Body RequestBody requestBody);

    /**
     * 设置 持续计时过久提醒 关闭
     *
     * @return
     */
    @PUT("ilaw/api/v2/timing/timing/{id}/bubble")
    Call<ResEntity<String>> timerOverTimingRemindClose(@Path("id") String id, @Query("operType") int operType, @Query("clientId") String clientId);

    /**
     * 获取项目下程序信息
     * 文档地址：https://dev.alphalawyer.cn/ilaw/swagger/index.html#!/matters-api/getProcessListUsingGET_1
     *
     * @param matterId
     * @return
     */
    @GET("ilaw/api/v1/matters/{matterId}/processes")
    Call<ResEntity<List<ProjectProcessesEntity>>> projectProcessesQuery(@Path("matterId") String matterId);
}


