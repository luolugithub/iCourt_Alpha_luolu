package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.ProgressLayout;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icourt.alpha.utils.FileUtils.isFileExists;

/**
 * Description  文件下载页面
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/16
 * version 2.0.0
 */

public class FileBoxDownloadActivity extends BaseActivity {
    private static final int CODE_PERMISSION_FILE = 1009;
    public static final int TASK_DOWNLOAD_FILE_ACTION = 1;//任务下载附件
    public static final int IM_DOWNLOAD_FILE_ACTION = 2;//享聊下载附件
    public static final int PROJECT_DOWNLOAD_FILE_ACTION = 3;//项目下载附件

    String authToken, seaFileRepoId, rootName, fileName, filePath, imPath;
    int action;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.file_icon_view)
    ImageView fileIconView;
    @BindView(R.id.file_name_view)
    TextView fileNameView;
    @BindView(R.id.progressbar)
    ProgressLayout progressbar;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.save_matter_view)
    TextView saveMatterView;
    @BindView(R.id.send_im_view)
    TextView sendImView;
    @BindView(R.id.open_view)
    TextView openView;
    @BindView(R.id.share_view)
    TextView shareView;
    @BindView(R.id.activity_download_file_meau_layout)
    LinearLayout activityDownloadFileMeauLayout;
    @BindView(R.id.download_tv)
    TextView downloadTv;

    @IntDef({TASK_DOWNLOAD_FILE_ACTION,
            IM_DOWNLOAD_FILE_ACTION, PROJECT_DOWNLOAD_FILE_ACTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DOWNLOAD_ACTION {

    }

    public static void launch(@NonNull Context context, @NonNull String authToken, @NonNull String seaFileRepoId, @NonNull String rootName, @DOWNLOAD_ACTION int action) {
        if (context == null) return;
        if (TextUtils.isEmpty(seaFileRepoId)) return;
        Intent intent = new Intent(context, FileBoxDownloadActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("authToken", authToken);
        intent.putExtra("seaFileRepoId", seaFileRepoId);
        intent.putExtra("rootName", rootName);
        context.startActivity(intent);
    }

    public static void launchIMFile(@NonNull Context context, @NonNull String path, @NonNull String seaFileRepoId, @NonNull String rootName, @DOWNLOAD_ACTION int action) {
        if (context == null) return;
        if (TextUtils.isEmpty(seaFileRepoId)) return;
        Intent intent = new Intent(context, FileBoxDownloadActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("path", path);
        intent.putExtra("seaFileRepoId", seaFileRepoId);
        intent.putExtra("rootName", rootName);
        context.startActivity(intent);
    }

    public static void launchClearTop(Context context, Intent intent) {
        if (context == null) return;
        if (intent == null) return;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_box_download_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        action = getIntent().getIntExtra("action", -1);
        authToken = getIntent().getStringExtra("authToken");
        imPath = getIntent().getStringExtra("path");
        seaFileRepoId = getIntent().getStringExtra("seaFileRepoId");
        rootName = getIntent().getStringExtra("rootName");
        progressbar.setMaxProgress(100);
        if (!TextUtils.isEmpty(rootName)) {
            if (rootName.contains("/")) {
                fileName = rootName.substring(rootName.lastIndexOf("/") + 1);
            } else {
                fileName = rootName;
            }
            setTitle(fileName);
            fileNameView.setText(fileName);
            fileIconView.setImageResource(FileUtils.getFileIcon40(fileName));
            checkPermissionOrDownload();
        }
    }

    @OnClick({R.id.save_matter_view, R.id.send_im_view, R.id.open_view, R.id.share_view})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!FileUtils.fileIsExists(filePath)) return;
        LogUtils.e("filePath ---  " + filePath);
        switch (v.getId()) {
            case R.id.save_matter_view://保存到项目
                ProjectSelectActivity.launch(this, authToken, seaFileRepoId, filePath);
                break;
            case R.id.send_im_view://发送到享聊
                showContactShareDialogFragment(filePath);
                break;
            case R.id.open_view://打开
                if (IMUtils.isPIC(filePath)) {
                    ImagePagerActivity.launch(this,
                            Arrays.asList(filePath));
                } else {
                    openOrShareFile(new File(filePath), Intent.ACTION_VIEW);
                }
                break;
            case R.id.share_view://分享
                shareFile(new File(filePath));
                break;
        }
    }

    /**
     * 打开文件
     *
     * @param file
     */
    private void openOrShareFile(File file, String action) {
        try {
            if (file == null || !file.exists()) {
                showTopSnackBar("文件不存在");
                return;
            }
            if (TextUtils.equals(FileUtils.getFileType(file.getName()), ".key")) {
                showTopSnackBar("此文件类型不支持打开");
                return;
            }
            log("-------------->file path:" + file.getAbsolutePath());
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(action);
            //获取文件file的MIME类型
            String type = FileUtils.getMIMEType(file);
            //设置intent的data和Type属性。
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            log("-------------->open uri:" + uri);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (action.equals(Intent.ACTION_VIEW)) {
                intent.setDataAndType(uri, type);
                startActivity(intent);     //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
            } else if (action.equals(Intent.ACTION_SEND)) {
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("*/*");
                startActivity(Intent.createChooser(intent, "Alpha Share"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            bugSync("外部打开文件失败", e);
        }
    }

    /**
     * 分享文件
     *
     * @param file
     */
    private void shareFile(File file) {
        try {
            if (file == null || !file.exists()) {
                showTopSnackBar("文件不存在");
                return;
            }
            log("-------------->file path:" + file.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的data和Type属性。
            Uri uri = Uri.fromFile(file);
            log("-------------->share uri:" + uri);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("*/*");
            startActivity(Intent.createChooser(intent, "Alpha Share"));
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("外部分享文件失败", e);
        }
    }

    /**
     * 展示联系人转发对话框
     *
     * @param filePath
     */
    public void showContactShareDialogFragment(String filePath) {
        String tag = ContactShareDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactShareDialogFragment.newInstanceFile(filePath,true)
                .show(mFragTransaction, tag);
    }

    /**
     * 检查权限或者下载
     */
    private void checkPermissionOrDownload() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (TextUtils.isEmpty(authToken)) {
                if (action == PROJECT_DOWNLOAD_FILE_ACTION || action == TASK_DOWNLOAD_FILE_ACTION) {
                    getFileBoxToken();
                } else if (action == IM_DOWNLOAD_FILE_ACTION) {
                    getData(true);
                }

            } else if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(seaFileRepoId))
                getData(true);

        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "下载文件需要文件写入权限!", CODE_PERMISSION_FILE);
        }
    }

    /**
     * 获取文档token
     */
    private void getFileBoxToken() {
        getApi().projectQueryFileBoxToken().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        if (response.body().has("authToken")) {
                            JsonElement element = response.body().get("authToken");
                            if (!TextUtils.isEmpty(element.toString()) && !TextUtils.equals("null", element.toString())) {
                                authToken = element.getAsString();
                                getData(true);
                            } else {
                                onFailure(call, new retrofit2.HttpException(response));
                            }
                        }
                    }
                } else {
                    onFailure(call, new retrofit2.HttpException(response));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                showTopSnackBar("获取文档token失败");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_PERMISSION_FILE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showTopSnackBar("文件写入权限被拒绝!");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);

        if (!TextUtils.isEmpty(seaFileRepoId)) {
            if (action == PROJECT_DOWNLOAD_FILE_ACTION || action == TASK_DOWNLOAD_FILE_ACTION) {
                getProjectOrTaskUrl();
            } else if (action == IM_DOWNLOAD_FILE_ACTION) {
                getIMFileUrl();
            }
        }
    }

    /**
     * 获取任务附件或者项目文件 下载url
     */
    private void getProjectOrTaskUrl() {
        String p = "/" + rootName;
        if (p.contains("//")) {
            p = p.replace("//", "/");
        }
        getSFileApi().fileboxDownloadUrlQuery("Token " + authToken, seaFileRepoId, p).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    String downloadUrl = response.body().getAsString();
                    downloadFile(downloadUrl);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                showTopSnackBar("下载失败");
            }
        });
    }

    /**
     * 获取im文件下载地址
     */
    private void getIMFileUrl() {
        getChatApi().fileUrlQuery(seaFileRepoId, imPath, rootName).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                if (response.body() != null) {
                    String downloadUrl = response.body().message;
                    downloadFile(downloadUrl);
                }
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void downloadFile(String url) {
        filePath = getPicSavePath();
        if (TextUtils.isEmpty(url)) {
            showTopSnackBar("下载地址为null");
            return;
        }
        if (!FileUtils.sdAvailable()) {
            showTopSnackBar("sd卡不可用!");
            return;
        }
        if (isFileExists(filePath)) {
            loadingLayout.setVisibility(View.GONE);
            if (IMUtils.isPIC(filePath)) {
                ImagePagerActivity.launch(FileBoxDownloadActivity.this,
                        Arrays.asList(filePath));
            }
//            showMeau();
            activityDownloadFileMeauLayout.setVisibility(View.VISIBLE);
            return;
        } else {
            activityDownloadFileMeauLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        }

        FileDownloader
                .getImpl()
                .create(url)
                .setPath(filePath)
                .setListener(picDownloadListener).start();
    }

    private String getPicSavePath() {
        StringBuilder pathBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        pathBuilder.append(File.separator);
        pathBuilder.append(ActionConstants.FILE_DOWNLOAD_PATH);
        pathBuilder.append(File.separator);
        pathBuilder.append(fileName);
        return pathBuilder.toString();
    }

    private FileDownloadListener picDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            progressbar.setMaxProgress(100);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            int total = totalBytes;
            if (total <= 0) {
                if (soFarBytes > 0) {
                    total = soFarBytes * 3;
                } else {
                    total = 100;
                }
            }
            int progress = (int) ((soFarBytes * 1.0f / total * 1.0f) * 100);
            progressbar.setMaxProgress(100);
            downloadTv.setText("下载中...(" + progress + "%)");
            progressbar.setCurrentProgress(progress);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            if (task != null && !TextUtils.isEmpty(task.getPath())) {
                try {
                    loadingLayout.setVisibility(View.GONE);
                    if (IMUtils.isPIC(task.getPath())) {
                        ImagePagerActivity.launch(FileBoxDownloadActivity.this,
                                Arrays.asList(task.getPath()));
                    }
//                    showMeau();
                    activityDownloadFileMeauLayout.setVisibility(View.VISIBLE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    bugSync("下载文件失败", e);
                }
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            showTopSnackBar(String.format("下载异常!" + StringUtils.throwable2string(e)));
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };

    private void showMeau() {
        activityDownloadFileMeauLayout.setVisibility(View.VISIBLE);
        switch (action) {
            case TASK_DOWNLOAD_FILE_ACTION:
                saveMatterView.setVisibility(View.GONE);
                sendImView.setVisibility(View.VISIBLE);
                openView.setVisibility(View.VISIBLE);
                shareView.setVisibility(View.VISIBLE);
                break;
            case IM_DOWNLOAD_FILE_ACTION:
                saveMatterView.setVisibility(View.VISIBLE);
                sendImView.setVisibility(View.GONE);
                openView.setVisibility(View.VISIBLE);
                shareView.setVisibility(View.VISIBLE);
                break;
            case PROJECT_DOWNLOAD_FILE_ACTION:
                saveMatterView.setVisibility(View.GONE);
                sendImView.setVisibility(View.VISIBLE);
                openView.setVisibility(View.VISIBLE);
                shareView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
