package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.Md5Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TestActivity extends BaseAppUpdateActivity {

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, TestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        getData(true);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("test");
        registerClick(R.id.bt_demo);
        registerClick(R.id.bt_login);
        registerClick(R.id.bt_db);
        registerClick(R.id.bt_bugs);
        registerClick(R.id.bt_about);
        registerClick(R.id.bt_json);
        registerClick(R.id.bt_fragment);
    }

    public static final String URL_DOC = "https://test.alphalawyer.cn/ilaw/api/v1/documents/download?filePath=%2FiCourt%2Fzhaolu%EF%BC%8D%E4%B8%93%E7%94%A8one&fileName=%25E6%25B5%258B%25E8%25AF%2595%25E6%25B5%258B%25E8%25AF%2595%25E6%25B5%258B%25E8%25AF%2595%25E6%25B5%258B%25E8%25AF%2595-54.docx";

    public String token = "eyJhbGciOiJIUzI1NiJ9.eyJvZmZpY2VfaWQiOiI0ZDc5MmUzMTZhMDUxMWU2YWE3NjAwMTYzZTE2MmFkZCIsImRldmljZVR5cGUiOiJhbmRyb2lkIiwib2ZmaWNlX25hbWUiOiJpQ291cnQiLCJ1c2VyX2lkIjoiNTM4QkM5QzhGQ0IzMTFFNjg0MzM3MDEwNkZBRUNFMkUiLCJ1c2VyX25hbWUiOiLotbXlsI_mvZ4iLCJpc3MiOiJpTGF3LmNvbSIsImV4cCI6MTQ5MjY5MTg2MjIzNSwiaWF0IjoxNDkyMDg3MDYyMjM1fQ.1wjJgpvZmrV7q4OG8FGapcH6uehtXV3XhHb4N-WdUKM";

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        checkAppUpdate(getContext());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_demo:
                testDownload();
                //shareDemo();
                // DemoActivity.launch(getContext());
                break;
            case R.id.bt_json:
                test();
                break;
            case R.id.bt_login: {
                doOauth(SHARE_MEDIA.WEIXIN);
                /*if (isInstall(SHARE_MEDIA.WEIXIN)) {
                    doOauth(SHARE_MEDIA.WEIXIN);
                } else {
                    showTopSnackBar(R.string.umeng_wexin_uninstalled);
                }*/
            }
            break;
            case R.id.bt_db:
                DemoRealmActivity.launch(getActivity());
                break;
            case R.id.bt_bugs:
                BugtagsDemoActivity.launch(getContext());
                break;
            case R.id.bt_about:
                AboutActivity.launch(getContext());
                break;
            case R.id.bt_fragment:
                DemoViewPagerActivity.launch(getContext());
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void testDownload() {
        if (Environment.isExternalStorageEmulated()) {
            String ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            String token = "eyJhbGciOiJIUzI1NiJ9.eyJvZmZpY2VfaWQiOiI0ZDc5MmUzMTZhMDUxMWU2YWE3NjAwMTYzZTE2MmFkZCIsImRldmljZVR5cGUiOiJhbmRyb2lkIiwib2ZmaWNlX25hbWUiOiJpQ291cnQiLCJ1c2VyX2lkIjoiNTM4QkM5QzhGQ0IzMTFFNjg0MzM3MDEwNkZBRUNFMkUiLCJ1c2VyX25hbWUiOiLotbXlsI_mvZ4iLCJpc3MiOiJpTGF3LmNvbSIsImV4cCI6MTQ5MjY5MTg2MjIzNSwiaWF0IjoxNDkyMDg3MDYyMjM1fQ.1wjJgpvZmrV7q4OG8FGapcH6uehtXV3XhHb4N-WdUKM";
            FileDownloader
                    .getImpl()
                    .create(URL_DOC + "&token?" + token)
                    .addHeader("token", token)
                    .addHeader("Accept-Encoding", "identity")
                    .setPath(ROOTPATH + Md5Utils.md5(URL_DOC, URL_DOC) + ".apk")
                    .setListener(new FileDownloadLargeFileListener() {
                        @Override
                        protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                            task.getLargeFileTotalBytes();

                            log("-------------->pending:soFarBytes:" + soFarBytes + " totalBytes:" + totalBytes + "  ntask.getLargeFileTotalBytes():" + task.getLargeFileTotalBytes() + "     task.getSmallFileTotalBytes():" + task.getSmallFileTotalBytes());
                        }

                        @Override
                        protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                            log("-------------->progress:soFarBytes:" + soFarBytes + " totalBytes:" + totalBytes);

                        }

                        @Override
                        protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {

                        }

                        @Override
                        protected void error(BaseDownloadTask task, Throwable e) {
                            log("-------------->error:" + e);

                        }

                        @Override
                        protected void warn(BaseDownloadTask task) {
                            log("-------------->warn:");

                        }
                    }).start();
        } else {
            showTopSnackBar("sd卡不可用!");
        }
    }

    static class NetRes extends ResEntity<JsonElement> {
    }

    private void test() {
        String json1 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"message\": \"错误1\",\n" +
                "  \"result\": {},\n" +
                "  \"succeed\": true\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json1, NetRes.class);
            LogUtils.d("---------->netRes1:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常1:" + e);
        }


        String json2 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"message\": \"错误2\",\n" +
                "  \"result\": {},\n" +
                "  \"succeed\": \"1\"\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json2, NetRes.class);
            LogUtils.d("---------->netRes2:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常2:" + e);
        }
        String json3 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"message\": \"错误3\",\n" +
                "  \"result\": {},\n" +
                "  \"succeed\": \"0\"\n" +
                "}";

        try {
            NetRes netRes = JsonUtils.Gson2Bean(json3, NetRes.class);
            LogUtils.d("---------->netRes3:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常3:" + e);
        }

        String json4 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误4\",\n" +
                "  \"result\": {},\n" +
                "  \"succeed\": \"1\"\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json4, NetRes.class);
            LogUtils.d("---------->netRes4:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常4:" + e);
        }

        String json5 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误5\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": \"1\"\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json5, NetRes.class);
            LogUtils.d("---------->netRes5:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常5:" + e);
        }

        String json6 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误6\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": \"0\"\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json6, NetRes.class);
            LogUtils.d("---------->netRes6:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常6:" + e);
        }

        String json7 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误7\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": true\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json7, NetRes.class);
            LogUtils.d("---------->netRes7:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常7:" + e);
        }

        String json8 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误8\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": false\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json8, NetRes.class);
            LogUtils.d("---------->netRes8:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常8:" + e);
        }

        String json9 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误9\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": 0\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json9, NetRes.class);
            LogUtils.d("---------->netRes9:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常9:" + e);
        }

        String json10 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误10\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": 1\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json10, NetRes.class);
            LogUtils.d("---------->netRes10:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常10:" + e);
        }

        String json11 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误11\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": \"\"\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json11, NetRes.class);
            LogUtils.d("---------->netRes11:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常11:" + e);
        }

        String json12 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误12\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\": null\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json12, NetRes.class);
            LogUtils.d("---------->netRes12:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常12:" + e);
        }

        String json13 = "{\n" +
                "  \"detail\": \"返回错误\",\n" +
                "  \"resultMess\": \"错误12\",\n" +
                "  \"result\": {},\n" +
                "  \"resultCode\":\n" +
                "}";
        try {
            NetRes netRes = JsonUtils.Gson2Bean(json13, NetRes.class);
            LogUtils.d("---------->netRes13:" + netRes);
        } catch (JsonParseException e) {
            LogUtils.d("-------->解析异常13:" + e);
        }
    }
}
