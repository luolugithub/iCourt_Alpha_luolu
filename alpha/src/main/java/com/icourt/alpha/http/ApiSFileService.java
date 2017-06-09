package com.icourt.alpha.http;

import com.google.gson.JsonElement;
import com.icourt.alpha.entity.bean.FileBoxBean;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
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
    Call<JsonElement> projectUploadFile(@Header("Authorization") String authToken,
                                        @Url String url,
                                        @PartMap Map<String, RequestBody> params);

    /**
     * 获取上传文件url
     *
     */
    @GET("api2/repos/{seaFileRepoId}/upload-link/")
    Call<JsonElement> projectUploadUrlQuery(@Header("Authorization") String authToken, @Path("seaFileRepoId") String seaFileRepoId);

    /**
     * 获取项目下文档列表
     *
     * @param authToken
     * @param seaFileRepoId
     * @param rootName
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/dir/")
    Call<List<FileBoxBean>> projectQueryFileBoxByDir(@Header("Authorization") String authToken, @Path("seaFileRepoId") String seaFileRepoId, @Query("p") String rootName);

    /**
     * 获取文件下载地址
     *
     * @param seaFileRepoId
     * @param rootName
     * @return
     */
    @GET("api2/repos/{seaFileRepoId}/file/")
    Call<JsonElement> fileboxDownloadUrlQuery(@Header("Authorization") String authToken, @Path("seaFileRepoId") String seaFileRepoId, @Query("p") String rootName);
}
