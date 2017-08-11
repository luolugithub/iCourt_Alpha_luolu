package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.entity.bean.FileBoxBean;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Description  sfile api
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/6/9
 * version 1.0.0
 */
public interface ApiSFileService {

    /**
     * 获取项目详情文档列表
     *
     * @param authToken
     * @param seaFileRepoId
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Call<List<FileBoxBean>> projectQueryFileBoxList(@Header("Authorization") String authToken, @Path("seaFileRepoId") String seaFileRepoId);

    /**
     * 项目下上传文件
     *
     * @param authToken
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Call<JsonElement> sfileUploadFile(@Header("Authorization") String authToken,
                                      @Url String url,
                                      @PartMap Map<String, RequestBody> params);

    /**
     * 获取上传文件url
     */
    @GET("api2/repos/{seaFileRepoId}/upload-link/")
    Call<JsonElement> projectUploadUrlQuery(@Header("Authorization") String authToken,
                                            @Path("seaFileRepoId") String seaFileRepoId);

    /**
     * 获取项目下文档列表
     *
     * @param authToken
     * @param seaFileRepoId
     * @param rootName
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Call<List<FileBoxBean>> projectQueryFileBoxByDir(@Header("Authorization") String authToken,
                                                     @Path("seaFileRepoId") String seaFileRepoId,
                                                     @Query("p") String rootName);

    /**
     * 获取文件下载地址
     *
     * @param seaFileRepoId
     * @param rootName
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/file/")
    Call<JsonElement> fileboxDownloadUrlQuery(@Header("Authorization") String authToken, @Path("seaFileRepoId") String seaFileRepoId, @Query("p") String rootName);

    /**
     * 获取资料库
     *
     * @param page
     * @param per_page
     * @return
     */
    @GET("api/v2.1/alpha-box-repos/")
    Call<List<DocumentRootEntity>> documentRootQuery(
            @Header("Authorization") String authToken,
            @Query("page") int page,
            @Query("per_page") int per_page,
            @Query("not_shared_from") String not_shared_from,
            @Query("shared_from") String shared_from,
            @Query("type") String type);


    /**
     * 获取律所律所资料库
     *
     * @param authToken
     * @return
     */
    @GET("api2/repos/public/")
    Call<List<DocumentRootEntity>> documentRootQuery(@Header("Authorization") String authToken);

    /**
     * 创建资料库
     *
     * @param authToken
     * @param name
     * @return
     */
    @POST("api2/repos/")
    @FormUrlEncoded
    Call<DocumentRootEntity> documentRootCreate(@Header("Authorization") String authToken,
                                                @Field("name") String name);

    /**
     * 资料库更改名字
     *
     * @param authToken
     * @param name
     * @param op        op=rename
     * @return
     */
    @POST("api2/repos/{documentRootId}/")
    @FormUrlEncoded
    Call<String> documentRootUpdateName(@Header("Authorization") String authToken,
                                        @Path("documentRootId") String documentRootId,
                                        @Query("op") String op,
                                        @Field("repo_name") String name);


    /**
     * 删除资料库
     *
     * @param authToken
     * @param documentRootId
     * @return
     */
    @DELETE("api2/repos/{documentRootId}/")
    Call<String> documentRootDelete(@Header("Authorization") String authToken,
                                    @Path("documentRootId") String documentRootId);


    /**
     * 文档目录查询
     *
     * @param authToken
     * @param documentRootId
     * @param p              p=/代表资料库根目录   p=/+目录1 代表资料库一级目录
     * @return
     */
    @GET("api2/repos/{documentRootId}/dir/")
    Call<List<FolderDocumentEntity>> documentDirQuery(@Header("Authorization") String authToken,
                                                      @Path("documentRootId") String documentRootId,
                                                      @Query("p") String p);

    /**
     * 获取sfile 上传文件url
     * op_type=upload 上传
     *
     * @param authToken
     * @param op_type
     * @param path
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/upload-link/")
    Call<String> sfileUploadUrlQuery(@Header("Authorization") String authToken,
                                     @Path("seaFileRepoId") String seaFileRepoId,
                                     @Query("op_type") String op_type,
                                     @Query("path") String path);


    /**
     * 创建文件夹
     *
     * @param authToken
     * @param p
     * @return https://testbox.alphalawyer.cn/api/v2.1/repos/d4f82446-a37f-478c-b6b5-ed0e779e1768/dir/?p=%2F22222
     */
    @POST("api/v2.1/repos/{seaFileRepoId}/dir/")
    Call<DocumentRootEntity> folderCreate(@Header("Authorization") String authToken,
                                          @Path("seaFileRepoId") String seaFileRepoId,
                                          @Query("p") String p,
                                          @Body RequestBody body);
}
