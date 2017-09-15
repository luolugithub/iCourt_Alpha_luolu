package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSaveFileDialogFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.ProgressLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 文件下载界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/24
 * version 2.1.0
 */
public class FileDownloadActivity extends BaseActivity {

    private static final String KEY_SEA_FILE_REPOID = "key_sea_file_repoid";
    private static final String KEY_SEA_FILE_TITLE = "key_sea_file_title";
    private static final String KEY_SEA_FILE_SIZE = "key_sea_file_size";
    private static final String KEY_SEA_FILE_FULL_PATH = "key_sea_file_full_path";
    private static final String KEY_SEA_FILE_VERSION_ID = "key_sea_file_commit_id";
    private static final int CODE_PERMISSION_FILE = 1009;
    @BindView(R.id.file_open_tv)
    TextView fileOpenTv;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.file_type_icon)
    ImageView fileTypeIcon;
    @BindView(R.id.file_name_tv)
    TextView fileNameTv;
    @BindView(R.id.file_size_tv)
    TextView fileSizeTv;
    @BindView(R.id.download_progressbar)
    ProgressLayout downloadProgressbar;
    @BindView(R.id.download_layout)
    LinearLayout downloadLayout;
    @BindView(R.id.download_continue_tv)
    TextView downloadContinueTv;
    @BindView(R.id.download_cancel_tv)
    ImageView downloadCancelTv;

    private String fileCachePath = "";//保存路径
    private String fileDownloadUrl = "";//下载的地址

    /**
     * @param context
     * @param seaFileRepoId   文件对应仓库id
     * @param fileTitle       文件对应标题
     * @param fileSize        文件大小
     * @param seaFileFullPath 文件全路径 eg. "/aa/bb.doc"
     * @param versionId       文件历史版本提交的id 可空
     */
    public static void launch(@NonNull Context context,
                              @NonNull String seaFileRepoId,
                              @NonNull String fileTitle,
                              long fileSize,
                              @NonNull String seaFileFullPath,
                              @Nullable String versionId) {
        if (context == null) return;
        Intent intent = new Intent(context, FileDownloadActivity.class);
        intent.putExtra(KEY_SEA_FILE_REPOID, seaFileRepoId);
        intent.putExtra(KEY_SEA_FILE_TITLE, fileTitle);
        intent.putExtra(KEY_SEA_FILE_SIZE, fileSize);
        intent.putExtra(KEY_SEA_FILE_FULL_PATH, seaFileFullPath);
        intent.putExtra(KEY_SEA_FILE_VERSION_ID, versionId);
        context.startActivity(intent);
    }

    String seaFileRepoId;
    String fileTitle;
    long fileSize;
    String seaFileFullPath;
    String versionId;

    private FileDownloadListener fileDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (downloadProgressbar != null) {
                downloadProgressbar.setMaxProgress(100);
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (downloadProgressbar != null) {
                int total = totalBytes;
                if (total <= 0) {
                    if (soFarBytes > 0) {
                        total = soFarBytes * 3;
                    } else {
                        total = 100;
                    }
                }
                int progress = (int) ((soFarBytes * 1.0f / total * 1.0f) * 100);
                downloadProgressbar.setMaxProgress(100);
                downloadProgressbar.setCurrentProgress(progress);
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            if (downloadProgressbar != null) {
                downloadProgressbar.setCurrentProgress(100);
                updateViewState(2);
                openImageView();
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            log("------------->下载异常:" + StringUtils.throwable2string(e));
            showTopSnackBar(String.format("下载异常!" + StringUtils.throwable2string(e)));
            bugSync("下载异常", e);
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        seaFileRepoId = getIntent().getStringExtra(KEY_SEA_FILE_REPOID);
        fileTitle = getIntent().getStringExtra(KEY_SEA_FILE_TITLE);
        fileSize = getIntent().getLongExtra(KEY_SEA_FILE_SIZE, 0);
        seaFileFullPath = getIntent().getStringExtra(KEY_SEA_FILE_FULL_PATH);
        versionId = getIntent().getStringExtra(KEY_SEA_FILE_VERSION_ID);
        fileCachePath = buildSavePath();

        setTitle(fileTitle);
        fileSizeTv.setText(String.format("(%s)", FileUtils.bFormat(fileSize)));
        fileNameTv.setText(fileTitle);
        fileTypeIcon.setImageResource(FileUtils.getSFileIcon(fileTitle));
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }

        //可能有bug
        if (FileUtils.isFileExists(fileCachePath)) {
            openImageView();
            updateViewState(2);
        } else {
            updateViewState(0);
            //检查文件写入权限
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                getData(true);
            } else {
                reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "下载文件需要文件写入权限!", CODE_PERMISSION_FILE);
            }
        }
    }

    private void openImageView() {
        if (IMUtils.isPIC(fileCachePath)) {
            ArrayList<String> smallImageUrl = new ArrayList<>();
            smallImageUrl.add(fileCachePath);
            ImageViewerActivity.launch(
                    getContext(),
                    smallImageUrl,
                    null,
                    0
            );
            finish();
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        getSFileApi().sFileDownloadUrlQuery(
                seaFileRepoId,
                seaFileFullPath,
                versionId)
                .enqueue(new SFileCallBack<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        fileDownloadUrl = response.body();
                        if (isDestroyOrFinishing()) return;
                        downloadFile(fileDownloadUrl);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * @param state 0下载中 1暂停中 2:下载完成
     */
    private void updateViewState(int state) {
        switch (state) {
            case 0:
                titleAction.setEnabled(false);
                titleAction.setImageResource(R.mipmap.more_disabled);
                fileOpenTv.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.VISIBLE);
                downloadContinueTv.setVisibility(View.GONE);
                break;
            case 1:
                titleAction.setEnabled(false);
                titleAction.setImageResource(R.mipmap.more_disabled);
                fileOpenTv.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.GONE);
                downloadContinueTv.setVisibility(View.VISIBLE);
                break;
            case 2:
                titleAction.setEnabled(true);
                titleAction.setImageResource(R.mipmap.header_icon_more);
                fileOpenTv.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.GONE);
                downloadContinueTv.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void downloadFile(String url) {
        if (!FileUtils.sdAvailable()) {
            showTopSnackBar(R.string.str_sd_unavailable);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            showTopSnackBar("下载地址为null");
            return;
        }

        FileDownloader
                .getImpl()
                .create(url)
                .setPath(fileCachePath)
                .setListener(fileDownloadListener)
                .start();
    }

    /**
     * 文件有历史版本
     * 默认  文件名字_versionId(hashcode) 位数比较少
     *
     * @return
     */
    private String buildSavePath() {
        StringBuilder pathBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        pathBuilder.append(File.separator);
        pathBuilder.append(ActionConstants.FILE_DOWNLOAD_PATH);
        pathBuilder.append(File.separator);
        if (TextUtils.isEmpty(versionId)) {
            pathBuilder.append(fileTitle);
        } else {
            String fileNameWithoutSuffix = FileUtils.getFileNameWithoutSuffix(fileTitle);
            String fileSuffix = FileUtils.getFileSuffix(fileTitle);
            pathBuilder.append(String.format("%s_%s%s", fileNameWithoutSuffix, versionId.hashCode(), fileSuffix));
        }
        return pathBuilder.toString();
    }


    @OnClick({R.id.download_continue_tv,
            R.id.file_open_tv,
            R.id.download_cancel_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                showBottomMenuDialog();
                break;
            case R.id.download_continue_tv:
                updateViewState(0);
                //地址失效 断点下载有问题
                getData(true);
                break;
            case R.id.download_cancel_tv:
                updateViewState(1);
                pauseDownload();
                break;
            case R.id.file_open_tv:
                showOpenableThirdPartyAppDialog();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 展示更多菜单
     */
    private void showBottomMenuDialog() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("转发给同事", "保存到项目资料库", "用其他应用打开"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                showContactShareDialogFragment(fileCachePath);
                                break;
                            case 1:
                                showProjectSaveFileDialogFragment(fileCachePath);
                                break;
                            case 2:
                                showOpenableThirdPartyAppDialog();
                                break;
                        }
                    }
                }).show();
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
        ContactShareDialogFragment.newInstanceFile(filePath, true)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示项目转发对话框
     *
     * @param filePath
     */
    public void showProjectSaveFileDialogFragment(String filePath) {
        String tag = ProjectSaveFileDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectSaveFileDialogFragment.newInstance(filePath, ProjectSaveFileDialogFragment.ALPHA_TYPE)
                .show(mFragTransaction, tag);
    }

    /**
     * 用其他程序打开
     */
    private void showOpenableThirdPartyAppDialog() {
        if (TextUtils.isEmpty(fileCachePath)) {
            showTopSnackBar("文件不存在");
            return;
        }
        File file = new File(fileCachePath);
        if (!file.exists()) {
            showTopSnackBar("文件不存在");
            return;
        }
        if (TextUtils.equals(FileUtils.getFileType(file.getName()), ".key")) {
            showTopSnackBar("此文件类型不支持打开");
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //获取文件file的MIME类型
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            String type = FileUtils.getMIMEType(file);
            //设置intent的data和Type属性。
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, type);
            startActivity(intent);     //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("打开文件失败", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults == null) return;
        if (grantResults.length <= 0) return;
        switch (requestCode) {
            case CODE_PERMISSION_FILE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showToast("文件写入权限被拒绝!");
                    finish();
                } else {
                    getData(true);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        pauseDownload();
        super.onDestroy();
    }

    private void pauseDownload() {
        if (fileDownloadListener != null) {
            try {
                FileDownloader
                        .getImpl()
                        .pause(fileDownloadListener);
            } catch (Exception e) {
            }
        }
    }
}
