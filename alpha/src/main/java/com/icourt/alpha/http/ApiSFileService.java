package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.entity.bean.FileVersionCommits;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileSearchPage;
import com.icourt.alpha.entity.bean.SFileShareUserInfo;
import com.icourt.alpha.entity.bean.SeaFileTrashPageEntity;

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
 * Description  sfile api
 * 注意如果用的是Observable api必须以Observable结尾
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/9
 * version 1.0.0
 */
public interface ApiSFileService {

    /**
     * 获取项目详情文档列表
     *
     * @param seaFileRepoId
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Call<List<FileBoxBean>> projectQueryFileBoxList(@Path("seaFileRepoId") String seaFileRepoId);

    /**
     * 获取项目详情文档列表
     *
     * @param seaFileRepoId
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Observable<List<FolderDocumentEntity>> projectQueryFileBoxListObservable(@Path("seaFileRepoId") String seaFileRepoId);

    /**
     * sfile上传文件
     *
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Call<JsonElement> sfileUploadFile(@Url String url,
                                      @PartMap Map<String, RequestBody> params);

    /**
     * sfile上传文件
     *
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Observable<JsonElement> sfileUploadFileObservable(@Url String url,
                                                      @PartMap Map<String, RequestBody> params);

    /**
     * 获取上传文件url
     */
    @GET("api2/repos/{seaFileRepoId}/upload-link/")
    Call<JsonElement> projectUploadUrlQuery(@Path("seaFileRepoId") String seaFileRepoId);

    /**
     * 获取项目下文档列表
     *
     * @param seaFileRepoId
     * @param rootName
     * @return
     */
    @Deprecated
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Call<List<FileBoxBean>> projectQueryFileBoxByDir(@Path("seaFileRepoId") String seaFileRepoId,
                                                     @Query("p") String rootName);

    /**
     * 获取项目下文档列表
     *
     * @param seaFileRepoId
     * @param rootName
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Call<List<FolderDocumentEntity>> projectQueryFileBoxByDir2(@Path("seaFileRepoId") String seaFileRepoId,
                                                               @Query("p") String rootName);

    /**
     * 获取文件下载地址
     *
     * @param seaFileRepoId
     * @param fullPath      全路径
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/file/")
    Call<String> sFileDownloadUrlQuery(@Path("seaFileRepoId") String seaFileRepoId, @Query("p") String fullPath);


    /**
     * 获取历史版本文件下载地址
     *
     * @param seaFileRepoId
     * @param fullPath      全路径
     * @param commit_id     历史版本提交的id
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/file/")
    Call<String> sFileDownloadUrlQuery(
            @Path("seaFileRepoId") String seaFileRepoId,
            @Query("p") String fullPath,
            @Query("commit_id") String commit_id);

    /**
     * 获取资料库
     *
     * @param page
     * @param per_page
     * @return
     */
    @GET("api/v2.1/alpha-box-repos/")
    Call<List<RepoEntity>> documentRootQuery(@Query("page") int page,
                                             @Query("per_page") int per_page,
                                             @Query("not_shared_from") String not_shared_from,
                                             @Query("shared_from") String shared_from,
                                             @Query("type") String type);

    /**
     * 获取资料库
     *
     * @param page
     * @param per_page
     * @return
     */
    @GET("api/v2.1/alpha-box-repos/")
    Observable<List<RepoEntity>> documentRootQueryObservable(@Query("page") int page,
                                                             @Query("per_page") int per_page,
                                                             @Query("not_shared_from") String not_shared_from,
                                                             @Query("shared_from") String shared_from,
                                                             @Query("type") String type);

    /**
     * 获取所有资料库
     *
     * @return
     */
    @GET("api2/repos/")
    Call<List<RepoEntity>> repoQueryAll();


    /**
     * 获取律所律所资料库
     *
     * @return
     */
    @GET("api2/repos/public/")
    Call<List<RepoEntity>> documentRootQuery();

    /**
     * 获取律所律所资料库
     *
     * @return
     */
    @GET("api2/repos/public/")
    Observable<List<RepoEntity>> documentRootQueryObservable();

    /**
     * 创建资料库
     *
     * @param name
     * @return
     */
    @POST("api2/repos/")
    @FormUrlEncoded
    Call<RepoEntity> documentRootCreate(@Field("name") String name);

    /**
     * 资料库更改名字
     *
     * @param name
     * @param op   op=rename
     * @return
     */
    @POST("api2/repos/{documentRootId}/")
    @FormUrlEncoded
    Call<String> documentRootUpdateName(@Path("documentRootId") String documentRootId,
                                        @Query("op") String op,
                                        @Field("repo_name") String name);


    /**
     * 删除资料库
     *
     * @param documentRootId
     * @return
     */
    @DELETE("api2/repos/{documentRootId}/")
    Call<String> documentRootDelete(@Path("documentRootId") String documentRootId);


    /**
     * 文档目录查询
     *
     * @param documentRootId
     * @param p              p=/代表资料库根目录   p=/+目录1 代表资料库一级目录
     * @return
     */
    @GET("api2/repos/{documentRootId}/dir/")
    Call<List<FolderDocumentEntity>> documentDirQuery(@Path("documentRootId") String documentRootId,
                                                      @Query("p") String p);

    /**
     * 获取sfile 上传文件url
     * op_type=upload 上传
     *
     * @param op_type
     * @param path
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/upload-link/")
    Call<String> sfileUploadUrlQuery(@Path("seaFileRepoId") String seaFileRepoId,
                                     @Query("op_type") String op_type,
                                     @Query("path") String path);

    /**
     * 获取sfile 上传文件url
     * op_type=upload 上传
     *
     * @param op_type
     * @param path
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/upload-link/")
    Observable<String> sfileUploadUrlQueryObservable(@Path("seaFileRepoId") String seaFileRepoId,
                                                     @Query("op_type") String op_type,
                                                     @Query("path") String path);


    /**
     * 创建文件夹
     *
     * @param p
     * @return https://testbox.alphalawyer.cn/api/v2.1/repos/d4f82446-a37f-478c-b6b5-ed0e779e1768/dir/?p=%2F22222
     */
    @POST("api/v2.1/repos/{seaFileRepoId}/dir/")
    Call<RepoEntity> folderCreate(@Path("seaFileRepoId") String seaFileRepoId,
                                  @Query("p") String p,
                                  @Body RequestBody body);

    /**
     * 删除文件夹
     *
     * @param p
     * @return
     */
    @DELETE("api/v2.1/repos/{seaFileRepoId}/dir/")
    Call<JsonObject> folderDelete(@Path("seaFileRepoId") String seaFileRepoId,
                                  @Query("p") String p);

    /**
     * 删除文件夹
     *
     * @param p
     * @return
     */
    @DELETE("api/v2.1/repos/{seaFileRepoId}/dir/")
    Observable<JsonObject> folderDeleteObservable(@Path("seaFileRepoId") String seaFileRepoId,
                                                  @Query("p") String p);

    /**
     * 删除文件
     *
     * @param p
     * @return
     */
    @DELETE("api/v2.1/repos/{seaFileRepoId}/file/")
    Call<JsonObject> fileDelete(@Path("seaFileRepoId") String seaFileRepoId,
                                @Query("p") String p);

    /**
     * 删除文件
     *
     * @param p
     * @return
     */
    @DELETE("api/v2.1/repos/{seaFileRepoId}/file/")
    Observable<JsonObject> fileDeleteObservable(@Path("seaFileRepoId") String seaFileRepoId,
                                                @Query("p") String p);


    /**
     * 文件夹/文件移动
     *
     * @param fromRepoId 文件源仓库id
     * @param fromDir    文件源仓库路径
     * @param fileNames  文件名字 多个名字以":"分割
     * @param dstRepoId  目标仓库id
     * @param dstDir     目标仓库路径
     * @return
     */
    @POST("api2/repos/{seaFileRepoId}/fileops/move/")
    @FormUrlEncoded
    Call<JsonElement> fileMove(@Path("seaFileRepoId") String fromRepoId,
                               @Query("p") String fromDir,
                               @Field("file_names") String fileNames,
                               @Field("dst_repo") String dstRepoId,
                               @Field("dst_dir") String dstDir);

    /**
     * 文件夹/文件复制
     *
     * @param fromRepoId 文件源仓库id
     * @param fromDir    文件源仓库路径
     * @param fileNames  文件名字 多个名字以":"分割
     * @param dstRepoId  目标仓库id
     * @param dstDir     目标仓库路径
     * @return
     */
    @POST("api2/repos/{seaFileRepoId}/fileops/copy/")
    @FormUrlEncoded
    Call<JsonElement> fileCopy(@Path("seaFileRepoId") String fromRepoId,
                               @Query("p") String fromDir,
                               @Field("file_names") String fileNames,
                               @Field("dst_repo") String dstRepoId,
                               @Field("dst_dir") String dstDir);

    /**
     * 文件重命名
     *
     * @param p
     * @param operation rename
     * @param newname
     * @return
     */
    @POST("api/v2.1/repos/{seaFileRepoId}/file/")
    @FormUrlEncoded
    Call<FolderDocumentEntity> fileRename(@Path("seaFileRepoId") String fromRepoId,
                                          @Query("p") String p,
                                          @Field("operation") String operation,
                                          @Field("newname") String newname);

    /**
     * 文件夹重命名
     *
     * @param p
     * @param operation rename
     * @param newname
     * @return
     */
    @POST("api2/repos/{seaFileRepoId}/dir/")
    @FormUrlEncoded
    Call<String> folderRename(@Path("seaFileRepoId") String fromRepoId,
                              @Query("p") String p,
                              @Field("operation") String operation,
                              @Field("newname") String newname);

    /**
     * 垃圾回收站
     */
    @GET("api/v2.1/repos/{seaFileRepoId}/trash/")
    Call<SeaFileTrashPageEntity<FolderDocumentEntity>> folderTrashQuery(@Path("seaFileRepoId") String fromRepoId,
                                                                        @Query("path") String p,
                                                                        @Query("per_page") int per_page,
                                                                        @Query("scan_stat") String scan_stat);

    /**
     * 文件恢复
     *
     * @param fromRepoId
     * @param p
     * @param commit_id
     * @return
     */
    @PUT("api2/repos/{seaFileRepoId}/file/revert/")
    @FormUrlEncoded
    Call<JsonObject> fileRevert(@Path("seaFileRepoId") String fromRepoId,
                                @Field("p") String p,
                                @Field("commit_id") String commit_id);

    /**
     * 文件恢复
     *
     * @param fromRepoId
     * @param p
     * @param commit_id
     * @param operation  revert
     * @return
     */
    @PUT("api2/repos/{seaFileRepoId}/file/revert/")
    @FormUrlEncoded
    Call<JsonObject> fileRevertEdit(@Path("seaFileRepoId") String fromRepoId,
                                    @Field("p") String p,
                                    @Field("commit_id") String commit_id,
                                    @Field("operation") String operation);

    /**
     * 文件夹恢复
     *
     * @param fromRepoId
     * @param p
     * @param commit_id
     * @return
     */
    @PUT("api2/repos/{seaFileRepoId}/dir/revert/")
    @FormUrlEncoded
    Call<JsonObject> folderRevert(@Path("seaFileRepoId") String fromRepoId,
                                  @Field("p") String p,
                                  @Field("commit_id") String commit_id);

    /**
     * 文件版本查询
     *
     * @param fromRepoId
     * @param p
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/file/history/")
    Call<FileVersionCommits> fileVersionQuery(@Path("seaFileRepoId") String fromRepoId,
                                              @Query("p") String p);

    /**
     * 文件版本回退
     *
     * @param fromRepoId
     * @param p
     * @param commit_id
     * @param operation  revert
     * @return
     */
    @POST("api/v2.1/repos/{seaFileRepoId}/file/")
    @FormUrlEncoded
    Call<JsonObject> fileRetroversion(@Path("seaFileRepoId") String fromRepoId,
                                      @Query("p") String p,
                                      @Field("commit_id") String commit_id,
                                      @Field("operation") String operation);


    /**
     * 获取文件／文件夹详情
     *
     * @param fromRepoId
     * @param p
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/file/detail/")
    Call<FolderDocumentEntity> fileDetailsQuery(@Path("seaFileRepoId") String fromRepoId,
                                                @Query("p") String p);


    /**
     * 获取文件夹 分享的用户列表
     *
     * @param fromRepoId
     * @param p
     * @param share_type "user"
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/shared_items/")
    Call<List<SFileShareUserInfo>> folderSharedUserQuery(@Path("seaFileRepoId") String fromRepoId,
                                                         @Query("p") String p,
                                                         @Query("share_type") String share_type);


    /**
     * 分享的用户文件夹权限
     *
     * @param fromRepoId
     * @param p
     * @param share_type "user"
     * @return
     */
    @PUT("api2/repos/{seaFileRepoId}/dir/shared_items/")
    @FormUrlEncoded
    Call<JsonObject> folderShareUserPermission(@Path("seaFileRepoId") String fromRepoId,
                                               @Query("p") String p,
                                               @Field("permission") String permission,
                                               @Field("share_type") String share_type,
                                               @Field("username") String username);

    /**
     * 改变分享的用户文件夹权限
     *
     * @param fromRepoId
     * @param p
     * @param share_type "user"
     * @return
     */
    @POST("api2/repos/{seaFileRepoId}/dir/shared_items/")
    @FormUrlEncoded
    Call<JsonObject> folderShareUserChangePermission(@Path("seaFileRepoId") String fromRepoId,
                                                     @Query("p") String p,
                                                     @Field("permission") String permission,
                                                     @Query("share_type") String share_type,
                                                     @Query("username") String username);

    /**
     * 删除分享的用户文件夹
     *
     * @param fromRepoId
     * @param p
     * @param share_type "user"
     * @return
     */
    @DELETE("api2/repos/{seaFileRepoId}/dir/shared_items/")
    Call<JsonObject> folderShareUserDelete(@Path("seaFileRepoId") String fromRepoId,
                                           @Query("p") String p,
                                           @Query("share_type") String share_type,
                                           @Query("username") String username);

    /**
     * 资料库查询
     *
     * @param fromRepoId
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/")
    Call<RepoEntity> repoDetailsQuery(@Path("seaFileRepoId") String fromRepoId);

    @GET("api2/search/")
    Call<SFileSearchPage> fileSearch(@Query("page") int page,
                                     @Query("q") String q,
                                     @Query("search_ftypes") String search_ftypes,
                                     @Query("search_repo") String search_repo);

    /**
     * 资料库解密
     *
     * @param password
     * @return
     */
    @POST("api2/repos/{seaFileRepoId}/")
    @FormUrlEncoded
    Call<String> repoDecrypt(
            @Path("seaFileRepoId") String seaFileRepoId,
            @Field("password") String password);


}
